import { Component, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { AutofocusDirective } from '../../../shared/directives/autofocus';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, AutofocusDirective],
  templateUrl: './login.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
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
            const perfil = (response.usuario.perfil || '').toUpperCase();
            if (perfil !== 'REFUGIO') {
              this._authService.logout(false);
              this.error.set(`Usuario autenticado con perfil ${perfil || 'desconocido'}. Este módulo es exclusivo para usuarios con perfil REFUGIO.`);
              return;
            }
            this._router.navigate(['/publicaciones']);
          } else {
            this.error.set(response?.mensajeRespuesta || response?.mensaje || 'Credenciales inválidas para el refugio');
          }
        },
        error: (err) => {
          this.error.set(err?.error?.mensajeRespuesta || err?.error?.message || err?.error?.mensaje || 'Error de autenticación. Verifique sus credenciales.');
        }
      });
  }

}
