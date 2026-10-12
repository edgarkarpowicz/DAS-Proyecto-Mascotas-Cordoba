import { Injectable, signal } from '@angular/core';
import { Publicacion } from '../models/publicacion.model';

@Injectable({
  providedIn: 'root'
})
export class PublicacionesService {

  private _publicaciones = signal<Publicacion[]>([
    {
      nroPublicacion: 1,
      nroRegMunicipal: 'MUN-000125',
      nombreMascota: 'Mora',
      especie: 'Perra',
      sexo: 'Hembra',
      edad: '2 años',
      descripcion: 'Mora es una perrita cariñosa, tranquila y sociable. Busca una familia responsable que pueda brindarle mucho amor.',
      foto: 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&w=800&q=80',
      estadoPublicacion: 'Activa'
    },
    {
      nroPublicacion: 2,
      nroRegMunicipal: 'MUN-000131',
      nombreMascota: 'Toby',
      especie: 'Perro',
      sexo: 'Macho',
      edad: '4 años',
      descripcion: 'Toby es muy juguetón y energético. Se lleva bien con otros perros y disfruta mucho de los paseos.',
      foto: 'https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?auto=format&fit=crop&w=800&q=80',
      estadoPublicacion: 'Activa'
    },
    {
      nroPublicacion: 3,
      nroRegMunicipal: 'MUN-000145',
      nombreMascota: 'Luna',
      especie: 'Gata',
      sexo: 'Hembra',
      edad: '1 año',
      descripcion: 'Luna es una gatita tranquila y muy cariñosa. Actualmente se encuentra en evaluación para adopción.',
      foto: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=800&q=80',
      estadoPublicacion: 'Pausada'
    }
  ]);

  readonly publicaciones = this._publicaciones.asReadonly();

  toggleEstado(nroPublicacion: number): void {
    this._publicaciones.update(lista =>
      lista.map(item => {
        if (item.nroPublicacion === nroPublicacion) {
          const nuevoEstado = item.estadoPublicacion === 'Activa' ? 'Pausada' : 'Activa';
          return { ...item, estadoPublicacion: nuevoEstado };
        }
        return item;
      })
    );
  }

  eliminarPublicacion(nroPublicacion: number): void {
    this._publicaciones.update(lista =>
      lista.filter(item => item.nroPublicacion !== nroPublicacion)
    );
  }

  agregarPublicacion(nueva: Omit<Publicacion, 'nroPublicacion'>): void {
    const nextId = Math.max(0, ...this._publicaciones().map(p => p.nroPublicacion)) + 1;
    const pub: Publicacion = {
      ...nueva,
      nroPublicacion: nextId
    };
    this._publicaciones.update(lista => [pub, ...lista]);
  }

}
