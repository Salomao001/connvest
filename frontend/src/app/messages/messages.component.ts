import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService } from '../services/message.service';
import { AuthService } from '../services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './messages.component.html'
})
export class MessagesComponent implements OnInit {
  conversations: any[] = [];
  activeConversation: any = null;
  messages: any[] = [];
  newMessage = '';
  currentUser: any;
  loading = true;

  constructor(
    private messageService: MessageService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) return;

    const partnerId = this.route.snapshot.queryParamMap.get('partnerId');
    if (partnerId) {
      this.openOrCreateConversation(Number(partnerId));
    } else {
      this.loadConversations();
    }
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
    this.loadThread(conv.partnerId);
  }

  loadThread(partnerId: number) {
    this.messageService.getThread(this.currentUser.id, partnerId).subscribe(msgs => {
      this.messages = msgs;
      this.cdr.detectChanges();
    });
  }

  send() {
    const content = this.newMessage.trim();
    if (!content || !this.activeConversation) return;
    this.messageService.sendMessage(this.currentUser.id, this.activeConversation.partnerId, content).subscribe(msg => {
      this.messages.push(msg);
      this.newMessage = '';
      // Update conversation preview
      const conv = this.conversations.find(c => c.partnerId === this.activeConversation.partnerId);
      if (conv) conv.lastMessage = content;
      this.cdr.detectChanges();
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
