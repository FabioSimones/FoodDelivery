import { Injectable, inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly toastr = inject(ToastrService);

  sucesso(mensagem: string, titulo = 'Sucesso'): void {
    this.toastr.success(mensagem, titulo);
  }

  erro(mensagem: string, titulo = 'Erro'): void {
    this.toastr.error(mensagem, titulo);
  }

  alerta(mensagem: string, titulo = 'Atenção'): void {
    this.toastr.warning(mensagem, titulo);
  }

  informacao(mensagem: string, titulo = 'Informação'): void {
    this.toastr.info(mensagem, titulo);
  }
}
