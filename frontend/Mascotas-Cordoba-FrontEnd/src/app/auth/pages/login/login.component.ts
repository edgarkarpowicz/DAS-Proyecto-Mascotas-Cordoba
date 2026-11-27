import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { getHomeRouteForPerfil } from '../../../core/config/perfil-routes';
import { AuthService } from '../../../core/services/auth.service';
import { AutofocusDirective } from '../../../shared/directives/autofocus';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, AutofocusDirective],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  private readonly _authService = inject(AuthService);
  private readonly _router = inject(Router);
  private readonly _fb = inject(FormBuilder);

  readonly form = this._fb.nonNullable.group({
    cuil: ['20-25999000-5', Validators.required],
    clave: ['pass123', Validators.required]
  });

  /** Los errores de los campos se muestran recién al intentar ingresar. */
  readonly enviado = signal(false);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  mostrarError(campo: 'cuil' | 'clave'): boolean {
    const control = this.form.controls[campo];
    return this.enviado() && (control.invalid || !control.value.trim());
  }

  onSubmit(): void {
    this.enviado.set(true);
    this.error.set(null);
    const { cuil, clave } = this.form.getRawValue();
    if (this.form.invalid || !cuil.trim() || !clave.trim()) {
      return;
    }

    this.loading.set(true);
    this._authService.login(cuil, clave)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          if (response && response.codigoRespuesta === 1 && response.usuario) {
            this._router.navigate([getHomeRouteForPerfil(response.usuario.perfil)]);
          } else {
            this.error.set(response?.mensajeRespuesta || response?.mensaje || 'Credenciales inválidas');
          }
        },
        error: (err) => {
          this.error.set(err?.error?.mensajeRespuesta || err?.error?.message || err?.error?.mensaje || 'Error de autenticación. Verifique sus credenciales.');
        }
      });
  }

}
