import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);

  carregando = false;

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]]
  });

  entrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();

      this.notificationService.alerta(
        'Preencha corretamente o e-mail e a senha para continuar.'
      );

      return;
    }

    this.carregando = true;

    this.authService.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.carregando = false;

        this.notificationService.sucesso(
          'Login realizado com sucesso.'
        );

        this.router.navigate(['/admin/dashboard']);
      },
      error: (erro) => {
        this.carregando = false;

        const mensagem =
          erro?.error?.mensagem ||
          'Não foi possível realizar o login. Verifique suas credenciais.';

        this.notificationService.erro(mensagem);
      }
    });
  }

  campoInvalido(campo: 'email' | 'senha'): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && (controle.dirty || controle.touched);
  }
}
