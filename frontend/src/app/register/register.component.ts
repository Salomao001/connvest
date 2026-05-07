import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class RegisterComponent {
  name = '';
  email = '';
  password = '';
  errorMessage = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  get emailValid(): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email);
  }

  get passwordValid(): boolean {
    return this.password.length >= 6;
  }

  onRegister() {
    this.errorMessage = '';

    if (!this.name.trim()) {
      this.errorMessage = 'Nome é obrigatório.';
      return;
    }
    if (!this.emailValid) {
      this.errorMessage = 'Informe um e-mail válido.';
      return;
    }
    if (!this.passwordValid) {
      this.errorMessage = 'A senha deve ter ao menos 6 caracteres.';
      return;
    }

    this.loading = true;
    this.authService.register(this.name, this.email, this.password).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/onboarding']);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        if (err.status === 409) {
          this.errorMessage = 'Este e-mail já está cadastrado.';
        } else {
          this.errorMessage = 'Erro ao criar conta. Tente novamente.';
        }
      }
    });
  }
}
