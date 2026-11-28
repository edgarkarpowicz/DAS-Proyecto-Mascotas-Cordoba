import { Component, ElementRef, HostListener, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

export interface INavItem {
  label: string;
  route: string;
  exact: boolean;
}

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {

  private readonly _el = inject(ElementRef<HTMLElement>);
  private readonly authService = inject(AuthService);
  readonly currentUser = this.authService.usuario;
  showDropdown = false;

  readonly navItems = computed<INavItem[]>(() => {
    const items: INavItem[] = [
      { label: 'Portal', route: '/', exact: true },
      { label: 'Veterinarias', route: '/veterinarias', exact: false },
      { label: 'Refugios', route: '/refugios', exact: false }
    ];
    const perfil = this.currentUser()?.perfil?.trim().toUpperCase();
    if (perfil === 'REFUGIO') {
      items.push({ label: 'Mis publicaciones', route: '/publicaciones', exact: false });
    }
    return items;
  });

  readonly subtituloUsuario = computed(() => {
    const usuario = this.currentUser();
    if (!usuario) {
      return '';
    }
    return usuario.nombreRefugio || usuario.nombreVeterinaria || usuario.perfil;
  });

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

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (this.showDropdown && !this._el.nativeElement.contains(event.target as Node)) {
      this.showDropdown = false;
    }
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
