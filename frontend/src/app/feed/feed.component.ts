import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { PostService } from '../services/post.service';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { FollowService } from '../services/follow.service';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.scss'
})
export class FeedComponent implements OnInit {
  posts: any[] = [];
  newPostContent = '';
  currentUser: any;
  startupPostOptions: any[] = [];
  selectedAuthor = 'PERSONAL';

  isStructuredUpdate = false;
  metrics: any[] = [{ name: '', previousValue: '', currentValue: '' }];

  activeTab: 'ALL' | 'UPDATES' | 'FREE' = 'ALL';

  expandedComments: Set<number> = new Set();
  commentInputs: Record<number, string> = {};
  commentsCache: Record<number, any[]> = {};
  likedPosts: Set<number> = new Set();

  constructor(
    private postService: PostService,
    private authService: AuthService,
    private userService: UserService,
    private followService: FollowService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    this.loadPosts();
    this.loadStartupPostOptions();
  }

  loadPosts() {
    this.postService.getPosts().subscribe(data => {
      this.posts = data;
      this.cdr.detectChanges();
    });
  }

  loadStartupPostOptions() {
    if (!this.currentUser) return;
    this.userService.getUserStartups(this.currentUser.id).subscribe(startups => {
      this.startupPostOptions = startups.filter(s => ['Owner', 'Admin', 'Editor'].includes(s.role));
    });
  }

  get filteredPosts() {
    if (this.activeTab === 'UPDATES') return this.posts.filter(p => p.type === 'STRUCTURED_UPDATE');
    if (this.activeTab === 'FREE') return this.posts.filter(p => p.type === 'FREE_TEXT');
    return this.posts;
  }

  setTab(tab: 'ALL' | 'UPDATES' | 'FREE') { this.activeTab = tab; }

  toggleLike(post: any) {
    if (!this.currentUser) return;
    this.postService.toggleLike(post.id, this.currentUser.id).subscribe(result => {
      post.likesCount = result.count;
      if (result.liked) this.likedPosts.add(post.id);
      else this.likedPosts.delete(post.id);
    });
  }

  isLiked(postId: number): boolean { return this.likedPosts.has(postId); }

  toggleComments(post: any) {
    if (this.expandedComments.has(post.id)) {
      this.expandedComments.delete(post.id);
    } else {
      this.expandedComments.add(post.id);
      if (!this.commentsCache[post.id]) this.loadComments(post.id);
    }
  }

  loadComments(postId: number) {
    this.postService.getComments(postId).subscribe(comments => {
      this.commentsCache[postId] = comments;
      this.cdr.detectChanges();
    });
  }

  submitComment(post: any) {
    const content = (this.commentInputs[post.id] || '').trim();
    if (!content || !this.currentUser) return;
    const comment = {
      authorId: this.currentUser.id,
      authorName: this.currentUser.name,
      authorPhoto: this.currentUser.photo,
      content
    };
    this.postService.addComment(post.id, comment).subscribe(saved => {
      if (!this.commentsCache[post.id]) this.commentsCache[post.id] = [];
      this.commentsCache[post.id].push(saved);
      post.commentsCount = (post.commentsCount || 0) + 1;
      this.commentInputs[post.id] = '';
      this.cdr.detectChanges();
    });
  }

  followStartup(post: any) {
    if (!this.currentUser || !post.startupId) return;
    this.followService.toggleFollow(this.currentUser.id, 'STARTUP', post.startupId).subscribe();
  }

  toggleStructuredUpdate() { this.isStructuredUpdate = !this.isStructuredUpdate; }
  addMetric() { this.metrics.push({ name: '', previousValue: '', currentValue: '' }); }
  removeMetric(i: number) { this.metrics.splice(i, 1); }
  triggerMediaUpload() { alert('Upload de mídia será implementado em breve!'); }

  canPublish(): boolean {
    if (this.newPostContent.trim().length > 0) return true;
    if (this.isStructuredUpdate && this.metrics.length > 0 && this.metrics[0].name.trim().length > 0) return true;
    return false;
  }

  publish() {
    if (!this.canPublish()) return;
    const validMetrics = this.isStructuredUpdate
      ? this.metrics.filter(m => m.name.trim() !== '' || m.currentValue.trim() !== '')
      : [];
    const startupAuthor = this.startupPostOptions.find(s => String(s.startupId) === this.selectedAuthor);
    const post = {
      authorId: startupAuthor ? startupAuthor.startupId : this.currentUser?.id,
      startupId: startupAuthor ? startupAuthor.startupId : null,
      authorName: startupAuthor ? startupAuthor.name : (this.currentUser?.name ?? 'Usuario'),
      authorType: startupAuthor ? 'Startup' : 'Founder',
      authorPhoto: startupAuthor ? (startupAuthor.name?.charAt(0) ?? 'S') : (this.currentUser?.photo || ''),
      type: this.isStructuredUpdate ? 'STRUCTURED_UPDATE' : 'FREE_TEXT',
      content: this.newPostContent,
      metrics: validMetrics,
      badge: this.isStructuredUpdate ? 'Update' : null,
      likesCount: 0,
      commentsCount: 0
    };
    this.postService.createPost(post, startupAuthor ? this.currentUser?.id : undefined).subscribe(savedPost => {
      this.posts.unshift(savedPost);
      this.newPostContent = '';
      this.isStructuredUpdate = false;
      this.metrics = [{ name: '', previousValue: '', currentValue: '' }];
      this.selectedAuthor = 'PERSONAL';
      this.cdr.detectChanges();
    });
  }

  authorInitial(post: any): string {
    return (post.authorName || 'U').charAt(0).toUpperCase();
  }

  timeAgo(dateStr: string): string {
    if (!dateStr) return '';
    const diff = Date.now() - new Date(dateStr).getTime();
    const mins = Math.floor(diff / 60000);
    if (mins < 60) return `há ${mins}min`;
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) return `há ${hrs}h`;
    return `há ${Math.floor(hrs / 24)}d`;
  }
}
