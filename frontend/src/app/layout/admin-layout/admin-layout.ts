import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { PerfilUsuario, UsuarioLogado } from '../../core/models/auth.models';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
  perfis: PerfilUsuario[];
}

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.scss'
})
export class AdminLayoutComponent {
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);

  sidebarFechada = false;
  menuMobileAberto = false;

  usuario: UsuarioLogado | null = this.authService.getUsuarioLogado();

  menuItems: MenuItem[] = [
    {
      label: 'Dashboard',
      icon: 'bi bi-speedometer2',
      route: '/admin/dashboard',
      perfis: ['SUPER_ADMIN', 'ADMIN_RESTAURANTE', 'OPERADOR_CAIXA', 'OPERADOR_COZINHA']
    },
    {
      label: 'Restaurantes',
      icon: 'bi bi-shop',
      route: '/admin/restaurantes',
      perfis: ['SUPER_ADMIN']
    },
    {
      label: 'Usuários',
      icon: 'bi bi-people',
      route: '/admin/usuarios',
      perfis: ['SUPER_ADMIN']
    },
    {
      label: 'Categorias',
      icon: 'bi bi-tags',
      route: '/admin/categorias',
      perfis: ['SUPER_ADMIN', 'ADMIN_RESTAURANTE']
    },
    {
      label: 'Produtos',
      icon: 'bi bi-basket',
      route: '/admin/produtos',
      perfis: ['SUPER_ADMIN', 'ADMIN_RESTAURANTE']
    },
    {
      label: 'Caixa',
      icon: 'bi bi-cash-coin',
      route: '/caixa/pedidos',
      perfis: ['SUPER_ADMIN', 'OPERADOR_CAIXA']
    },
    {
      label: 'Cozinha',
      icon: 'bi bi-cup-hot',
      route: '/cozinha/pedidos',
      perfis: ['SUPER_ADMIN', 'OPERADOR_COZINHA']
    }
  ];

  get menuPermitido(): MenuItem[] {
    if (!this.usuario) {
      return [];
    }

    return this.menuItems.filter((item) =>
      item.perfis.includes(this.usuario!.perfil)
    );
  }

  alternarSidebar(): void {
    this.sidebarFechada = !this.sidebarFechada;
  }

  alternarMenuMobile(): void {
    this.menuMobileAberto = !this.menuMobileAberto;
  }

  fecharMenuMobile(): void {
    this.menuMobileAberto = false;
  }

  sair(): void {
    this.notificationService.informacao('Sessão encerrada com sucesso.');
    this.authService.logout();
  }
}
