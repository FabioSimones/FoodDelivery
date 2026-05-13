import { Routes } from '@angular/router';

import { LoginComponent } from './features/auth/login/login';
import { DashboardComponent } from './features/admin/dashboard/dashboard';
import { RestauranteList } from './features/admin/restaurantes/restaurante-list/restaurante-list';
import { CategoriaList } from './features/admin/categorias/categoria-list/categoria-list';
import { ProdutoList } from './features/admin/produtos/produto-list/produto-list';
import { UsuarioList } from './features/admin/usuarios/usuario-list/usuario-list';
import { CaixaPedidos } from './features/caixa/caixa-pedidos/caixa-pedidos';
import { CozinhaPedidos } from './features/cozinha/cozinha-pedidos/cozinha-pedidos';

import { AdminLayoutComponent } from './layout/admin-layout/admin-layout';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'admin/dashboard',
        component: DashboardComponent
      },
      {
        path: 'admin/restaurantes',
        component: RestauranteList
      },
      {
        path: 'admin/usuarios',
        component: UsuarioList
      },
      {
        path: 'admin/categorias',
        component: CategoriaList
      },
      {
        path: 'admin/produtos',
        component: ProdutoList
      },
      {
        path: 'caixa/pedidos',
        component: CaixaPedidos
      },
      {
        path: 'cozinha/pedidos',
        component: CozinhaPedidos
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'admin/dashboard'
      }
    ]
  },

  {
    path: '**',
    redirectTo: 'login'
  }
];
