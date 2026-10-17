import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  private readonly _authService = inject(AuthService);
  private readonly _router = inject(Router);

  cuil: string = '20-25999000-5';
  clave: string = 'pass123';
  error: string | null = null;
  loading: boolean = false;

  onSubmit(): void {
    if (!this.cuil.trim() || !this.clave.trim()) {
      this.error = 'Por favor complete todos los campos';
      return;
    }

    this.error = null;
    this.loading = true;

    this._authService.login(this.cuil, this.clave).subscribe({
      next: (response) => {
        this.loading = false;
        if (response && response.codigoRespuesta === 1) {
          this._router.navigate(['/publicaciones']);
        } else {
          this.error = response?.mensajeRespuesta || response?.mensaje || 'Credenciales inválidas para el refugio';
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || err?.error?.mensaje || 'Error de autenticación. Verifique sus credenciales.';
      }
    });
  }

}

