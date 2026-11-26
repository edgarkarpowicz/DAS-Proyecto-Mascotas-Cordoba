import { Component, inject } from '@angular/core';

import { LoaderService } from '../../services/loader-service';

@Component({
    selector: 'app-loader',
    imports: [],
    templateUrl: './loader.component.html',
    styleUrls: ['./loader.component.css']
})
export class LoaderComponent {

  private readonly _service = inject(LoaderService);
  readonly isLoading = this._service.loading;

}
