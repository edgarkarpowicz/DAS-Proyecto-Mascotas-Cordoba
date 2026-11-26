import { Component, HostListener, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {

  private readonly authService = inject(AuthService);
  readonly currentUser = this.authService.usuario;
  showDropdown = false;

  readonly iniciales = computed(() => {
    const usuario = this.currentUser();
    const letras = [usuario?.nombre, usuario?.apellido]
      .map(parte => parte?.trim().charAt(0) ?? '')
      .join('');
    return letras.toUpperCase() || '?';
  });

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown;
  }

  @HostListener('document:keydown.escape')
  cerrarMenu(): void {
    this.showDropdown = false;
  }

  logout(): void {
    this.showDropdown = false;
    this.authService.logout();
  }

}
