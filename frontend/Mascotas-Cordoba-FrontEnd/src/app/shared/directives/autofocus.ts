import { afterNextRender, booleanAttribute, Directive, ElementRef, inject, input } from '@angular/core';

@Directive({
  selector: '[appAutofocus]',
  standalone: true
})
export class AutofocusDirective {

  private readonly _el = inject(ElementRef<HTMLElement>);

  appAutofocus = input(true, { transform: booleanAttribute });

  constructor() {
    afterNextRender(() => {
      this.tryFocus();
    });
  }

  private tryFocus(): void {
    if (!this.appAutofocus()) return;

    this._el.nativeElement.focus({ preventScroll: true });
  }

}

