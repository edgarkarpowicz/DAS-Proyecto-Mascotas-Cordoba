import { IUsuario } from '../../../core/api/models/i-usuario';

export interface IResumenPrograma {
  publicacionesActivas: number;
  veterinariasHabilitadas: number;
  refugiosAdheridos: number;
}

export interface IServicioHabilitado {
  codigo: string;
  titulo: string;
  descripcion: string;
  ruta: string;
  disponible: boolean;
  requiereAutenticacion: boolean;
  rfReferencia: string;
}

export interface IPortalInfo {
  nombrePrograma: string;
  descripcionPrograma: string;
  autenticado: boolean;
  perfil: string | null;
  usuario: IUsuario | null;
  resumen: IResumenPrograma;
  serviciosPublicos: IServicioHabilitado[];
  serviciosHabilitados: IServicioHabilitado[];
}
