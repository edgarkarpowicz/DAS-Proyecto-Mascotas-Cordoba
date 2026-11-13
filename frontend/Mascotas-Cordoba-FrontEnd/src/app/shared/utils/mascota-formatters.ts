import { environment } from '../../../environments/environment';

const DEFAULT_CAT_PHOTO =
  'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=800&q=80';
const DEFAULT_DOG_PHOTO =
  'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=800&q=80';

export function getDefaultFotoMascota(especie?: string): string {
  const normalized = (especie || '').toLowerCase();
  if (normalized.includes('gato') || normalized.includes('gata') || normalized.includes('felin')) {
    return DEFAULT_CAT_PHOTO;
  }
  return DEFAULT_DOG_PHOTO;
}

export function getFotoMascotaUrl(foto?: string, especie?: string): string {
  const cleanFoto = foto?.trim();
  if (!cleanFoto || cleanFoto.includes('mascotas.cordoba.gob.ar')) {
    return getDefaultFotoMascota(especie);
  }
  if (cleanFoto.startsWith('/uploads/')) {
    return `${environment.apiUrl}${cleanFoto}`;
  }
  return cleanFoto;
}

export function applyFallbackFoto(event: Event, especie?: string, explicitFallback?: string): void {
  const target = event.target as HTMLImageElement | null;
  if (target && !target.dataset['fallback']) {
    target.dataset['fallback'] = 'true';
    target.src = explicitFallback?.trim() || getDefaultFotoMascota(especie);
  }
}

export function formatFotoCaption(nombreMascota?: string, especie?: string): string {
  const nombre = nombreMascota?.trim() || 'Mascota';
  const cleanEspecie = especie?.trim();
  return cleanEspecie ? `${nombre} · ${cleanEspecie}` : nombre;
}

export function getEdadTexto(edadAproximada?: number, anoNacimiento?: number): string {
  if (edadAproximada && edadAproximada > 0) {
    return `${edadAproximada} ${edadAproximada === 1 ? 'año' : 'años'}`;
  }
  if (anoNacimiento && anoNacimiento > 0) {
    const edadCalc = new Date().getFullYear() - anoNacimiento;
    const years = edadCalc > 0 ? edadCalc : 1;
    return `${years} ${years === 1 ? 'año' : 'años'}`;
  }
  return '2 años';
}

export function getSexoLabel(sexo?: string): 'Hembra' | 'Macho' {
  const normalized = (sexo || '').trim().toUpperCase();
  if (normalized === 'H' || normalized === 'F' || normalized === 'HEMBRA') {
    return 'Hembra';
  }
  return 'Macho';
}

export function formatFechaPublicacion(fecha?: string): string {
  if (!fecha || !fecha.trim()) {
    return 'Sin fecha registrada';
  }
  const isoDateMatch = fecha.trim().match(/^(\d{4})-(\d{2})-(\d{2})/);
  if (isoDateMatch) {
    const [, year, month, day] = isoDateMatch;
    return `${day}/${month}/${year}`;
  }
  return fecha.trim();
}

