export type PerfilUsuario =
  | 'SUPER_ADMIN'
  | 'ADMIN_RESTAURANTE'
  | 'OPERADOR_CAIXA'
  | 'OPERADOR_COZINHA';

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  usuarioId: string;
  nome: string;
  email: string;
  perfil: PerfilUsuario;
  restauranteId: string | null;
  restauranteNome: string | null;
}

export interface UsuarioLogado {
  usuarioId: string;
  nome: string;
  email: string;
  perfil: PerfilUsuario;
  restauranteId: string | null;
  restauranteNome: string | null;
}
