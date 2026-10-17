import { computed, Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoaderService {

  private _pendingRequests = signal(0);

  readonly loading = computed(() =>
    this._pendingRequests() > 0
  );

  start(): void {
    this._pendingRequests.update(count => count + 1);
  }

  complete(): void {
    this._pendingRequests.update(count =>
      Math.max(0, count - 1)
    );
  }

}
