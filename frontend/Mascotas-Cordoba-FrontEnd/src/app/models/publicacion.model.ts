export interface Publicacion {
  nroPublicacion: number;
  nroRegMunicipal: string;
  nombreMascota: string;
  especie: string;
  sexo: 'Hembra' | 'Macho';
  edad: string;
  descripcion: string;
  foto: string;
  estadoPublicacion: 'Activa' | 'Pausada' | 'Finalizada';
}
