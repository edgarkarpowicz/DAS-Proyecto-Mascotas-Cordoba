import { IUsuarioRefugio } from './i-usuario-refugio';

export interface ILoginResponse {
  codigoRespuesta: number;
  mensajeRespuesta?: string;
  mensaje?: string;
  token?: string;
  usuario?: IUsuarioRefugio;
}

