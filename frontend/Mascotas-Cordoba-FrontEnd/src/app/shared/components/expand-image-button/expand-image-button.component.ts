import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-expand-image-button',
  standalone: true,
  templateUrl: './expand-image-button.component.html',
  styleUrls: ['./expand-image-button.component.css']
})
export class ExpandImageButtonComponent {

  ariaLabel = input<string>('Ampliar imagen');
  tooltip = input<string>('Ver imagen completa');

  expand = output<void>();

  onClick(event: MouseEvent): void {
    event.stopPropagation();
    this.expand.emit();
  }

}

