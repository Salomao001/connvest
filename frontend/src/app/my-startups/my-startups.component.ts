import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-my-startups',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './my-startups.component.html',
  styleUrl: './my-startups.component.scss'
})
export class MyStartupsComponent implements OnInit {
  startups: any[] = [];
  loading = false;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    this.userService.getUserStartups(user.id).subscribe({
      next: (startups) => {
        this.startups = startups;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
