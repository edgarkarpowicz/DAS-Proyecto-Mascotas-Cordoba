import { IUsuario } from './i-usuario';

export interface ILoginResponse {
  codigoRespuesta: number;
  mensajeRespuesta?: string;
  mensaje?: string;
  token?: string;
  usuario?: IUsuario;
}
