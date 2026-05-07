import { Component, OnInit, OnDestroy, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { MessageService } from '../services/message.service';
import { AuthService } from '../services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../services/user.service';
import { ChatService } from '../services/chat.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './messages.component.html'
})
export class MessagesComponent implements OnInit, OnDestroy {
  @ViewChild('messagesContainer') private messagesContainer?: ElementRef;

  conversations: any[] = [];
  activeConversation: any = null;
  messages: any[] = [];
  newMessage = '';
  currentUser: any;
  loading = true;
  showChatMobile = false;

  private chatSub?: Subscription;

  constructor(
    private messageService: MessageService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute,
    private userService: UserService,
    private chatService: ChatService,
    private notifService: NotificationService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) return;

    this.chatSub = this.chatService.message$.subscribe(msg => this.onNewMessage(msg));

    const partnerId = this.route.snapshot.queryParamMap.get('partnerId');
    if (partnerId) {
      this.openOrCreateConversation(Number(partnerId));
    } else {
      this.loadConversations();
    }
  }

  ngOnDestroy() {
    this.chatSub?.unsubscribe();
    this.chatService.activePartnerId = null;
  }

  private onNewMessage(msg: any) {
    if (this.activeConversation?.partnerId === msg.senderId) {
      this.messages = [...this.messages, msg];
      this.cdr.detectChanges();
      this.scrollToBottom();
    }

    const conv = this.conversations.find(c => c.partnerId === msg.senderId);
    if (conv) {
      conv.lastMessage = msg.content;
      conv.lastMessageAt = msg.createdAt;
      if (this.activeConversation?.partnerId !== msg.senderId) {
        conv.unreadCount = (conv.unreadCount || 0) + 1;
      }
      const idx = this.conversations.indexOf(conv);
      if (idx > 0) {
        this.conversations.splice(idx, 1);
        this.conversations.unshift(conv);
      }
    } else {
      this.conversations.unshift({
        partnerId: msg.senderId,
        partnerName: msg.senderName,
        partnerPhoto: msg.senderPhoto,
        lastMessage: msg.content,
        lastMessageAt: msg.createdAt,
        unreadCount: 1
      });
    }
  }

  private scrollToBottom() {
    setTimeout(() => {
      if (this.messagesContainer) {
        this.messagesContainer.nativeElement.scrollTop =
          this.messagesContainer.nativeElement.scrollHeight;
      }
    }, 50);
  }

  private openOrCreateConversation(partnerId: number) {
    this.userService.getUser(partnerId).subscribe(partner => {
      this.loadConversations(() => {
        const existing = this.conversations.find(c => c.partnerId === partnerId);
        if (existing) {
          this.openConversation(existing);
        } else {
          const newConv = { partnerId: partner.id, partnerName: partner.name, partnerPhoto: partner.photo, lastMessage: '' };
          this.conversations.unshift(newConv);
          this.openConversation(newConv);
        }
      });
    });
  }

  loadConversations(callback?: () => void) {
    this.messageService.getConversations(this.currentUser.id).subscribe(convs => {
      this.conversations = convs;
      this.loading = false;
      if (callback) {
        callback();
      } else if (convs.length > 0 && !this.activeConversation) {
        this.openConversation(convs[0]);
      }
      this.cdr.detectChanges();
    });
  }

  openConversation(conv: any) {
    this.activeConversation = conv;
    this.chatService.activePartnerId = conv.partnerId;
    const unread = conv.unreadCount || 0;
    conv.unreadCount = 0;
    if (unread > 0) {
      const cur = this.notifService.unreadMsg$.getValue();
      this.notifService.unreadMsg$.next(Math.max(0, cur - unread));
    }
    this.showChatMobile = true;
    this.loadThread(conv.partnerId);
  }

  backToList() {
    this.chatService.activePartnerId = null;
    this.showChatMobile = false;
  }

  loadThread(partnerId: number) {
    this.messages = [];
    this.messageService.getThread(this.currentUser.id, partnerId).subscribe(msgs => {
      this.messages = [...msgs];
      this.cdr.detectChanges();
      this.scrollToBottom();
    });
  }

  send() {
    const content = this.newMessage.trim();
    if (!content || !this.activeConversation) return;

    // Optimistic update: show message immediately
    const optimistic: any = {
      senderId: this.currentUser.id,
      senderName: this.currentUser.name,
      content,
      createdAt: new Date().toISOString(),
      _pending: true
    };
    this.messages = [...this.messages, optimistic];
    this.newMessage = '';
    this.cdr.detectChanges();
    this.scrollToBottom();

    const partnerId = this.activeConversation.partnerId;
    this.messageService.sendMessage(this.currentUser.id, partnerId, content).subscribe({
      next: msg => {
        // Replace optimistic with confirmed server message
        this.messages = this.messages.map(m => m === optimistic ? msg : m);
        const conv = this.conversations.find(c => c.partnerId === partnerId);
        if (conv) {
          conv.lastMessage = content;
          conv.lastMessageAt = msg.createdAt;
        }
        this.cdr.detectChanges();
      },
      error: () => {
        // Remove optimistic message on failure and restore input
        this.messages = this.messages.filter(m => m !== optimistic);
        this.newMessage = content;
        this.cdr.detectChanges();
      }
    });
  }

  isMine(msg: any): boolean {
    return msg.senderId === this.currentUser?.id;
  }

  timeAgo(dateStr: string): string {
    if (!dateStr) return '';
    const diff = Date.now() - new Date(dateStr).getTime();
    const mins = Math.floor(diff / 60000);
    if (mins < 1) return 'agora';
    if (mins < 60) return `${mins}min`;
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) return `${hrs}h`;
    return `${Math.floor(hrs / 24)}d`;
  }

  getInitial(name: string): string {
    return (name || 'U').charAt(0).toUpperCase();
  }
}
