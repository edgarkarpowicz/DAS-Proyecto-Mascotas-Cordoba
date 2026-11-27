import { HttpParams } from '@angular/common/http';

export interface IPageRequest {
  pagina?: number;
  tamanioPagina?: number;
}

export interface IPageResponse<T> {
  items: T[];
  pagina: number;
  tamanioPagina: number;
  totalElementos: number;
  totalPaginas: number;
}

export function toPageHttpParams(request?: IPageRequest): HttpParams {
  let params = new HttpParams();
  if (request?.pagina != null) {
    params = params.set('pagina', String(request.pagina));
  }
  if (request?.tamanioPagina != null) {
    params = params.set('tamanioPagina', String(request.tamanioPagina));
  }
  return params;
}
