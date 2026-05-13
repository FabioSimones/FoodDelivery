import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, UsuarioLogado } from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly tokenKey = 'totem_fastfood_access_token';
  private readonly usuarioKey = 'totem_fastfood_usuario';

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/login`, request)
      .pipe(
        tap((response) => this.salvarSessao(response))
      );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.usuarioKey);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  estaAutenticado(): boolean {
    return !!this.getToken();
  }

  getUsuarioLogado(): UsuarioLogado | null {
    const usuarioJson = localStorage.getItem(this.usuarioKey);

    if (!usuarioJson) {
      return null;
    }

    try {
      return JSON.parse(usuarioJson) as UsuarioLogado;
    } catch {
      this.logout();
      return null;
    }
  }

  private salvarSessao(response: LoginResponse): void {
    localStorage.setItem(this.tokenKey, response.accessToken);

    const usuario: UsuarioLogado = {
      usuarioId: response.usuarioId,
      nome: response.nome,
      email: response.email,
      perfil: response.perfil,
      restauranteId: response.restauranteId,
      restauranteNome: response.restauranteNome
    };

    localStorage.setItem(this.usuarioKey, JSON.stringify(usuario));
  }
}
