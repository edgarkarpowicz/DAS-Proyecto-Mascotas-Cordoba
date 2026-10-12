import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  cuil: string = '27-12345678-9';
  clave: string = 'secret123';
  error: string | null = null;

  constructor(private authService: AuthService) {}

  onSubmit(): void {
    if (!this.cuil.trim() || !this.clave.trim()) {
      this.error = 'Por favor complete todos los campos';
      return;
    }

    const success = this.authService.login(this.cuil, this.clave);
    if (!success) {
      this.error = 'Credenciales inválidas';
    }
  }

}
