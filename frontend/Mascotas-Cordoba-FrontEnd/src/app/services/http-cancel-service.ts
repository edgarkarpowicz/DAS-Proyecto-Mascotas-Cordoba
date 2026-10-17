import { inject, Injectable } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';
import { Subject, filter } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpCancelService {

  private _router = inject(Router);
  private _subject = new Subject<void>();

  constructor() {
    this._router.events
      .pipe(
        filter(event => event instanceof NavigationStart)
      )
      .subscribe(() => {
        this.cancelPendingRequests();
      });
  }

  get pendingHTTPRequests$() {
    return this._subject.asObservable();
  }

  cancelPendingRequests(): void {
    this._subject.next();
  }

}
