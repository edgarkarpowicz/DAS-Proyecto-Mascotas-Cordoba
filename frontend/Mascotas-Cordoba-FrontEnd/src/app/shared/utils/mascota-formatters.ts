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

interface ParsedCalendarDate {
  year: number;
  month: number;
  day: number;
}

function parseIsoCalendarDate(fecha?: string): ParsedCalendarDate | null {
  if (!fecha || !fecha.trim()) {
    return null;
  }
  const isoDateMatch = fecha.trim().match(/^(\d{4})-(\d{2})-(\d{2})/);
  if (!isoDateMatch) {
    return null;
  }
  const year = Number(isoDateMatch[1]);
  const month = Number(isoDateMatch[2]);
  const day = Number(isoDateMatch[3]);
  const probe = new Date(year, month - 1, day);
  if (
    probe.getFullYear() !== year ||
    probe.getMonth() !== month - 1 ||
    probe.getDate() !== day
  ) {
    return null;
  }
  return { year, month, day };
}

export function getEdadTexto(edadAproximada?: number, anoNacimiento?: number, fechaNacimiento?: string): string {
  const parsedBirthDate = parseIsoCalendarDate(fechaNacimiento);
  if (parsedBirthDate) {
    const today = new Date();
    const totalMonths =
      (today.getFullYear() - parsedBirthDate.year) * 12 +
      (today.getMonth() - (parsedBirthDate.month - 1)) -
      (today.getDate() < parsedBirthDate.day ? 1 : 0);

    if (totalMonths <= 0) {
      return 'Menos de 1 mes';
    }
    if (totalMonths < 12) {
      return `${totalMonths} ${totalMonths === 1 ? 'mes' : 'meses'}`;
    }
    const years = Math.floor(totalMonths / 12);
    return `${years} ${years === 1 ? 'año' : 'años'}`;
  }

  if (typeof edadAproximada === 'number' && edadAproximada >= 0) {
    if (edadAproximada === 0) {
      return 'Menos de 1 año';
    }
    return `${edadAproximada} ${edadAproximada === 1 ? 'año' : 'años'}`;
  }

  if (anoNacimiento && anoNacimiento > 0) {
    const edadCalc = new Date().getFullYear() - anoNacimiento;
    if (edadCalc <= 0) {
      return 'Menos de 1 año';
    }
    return `${edadCalc} ${edadCalc === 1 ? 'año' : 'años'}`;
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

export function formatFechaNacimiento(fecha?: string): string | null {
  const parsed = parseIsoCalendarDate(fecha);
  if (!parsed) {
    return null;
  }
  const day = String(parsed.day).padStart(2, '0');
  const month = String(parsed.month).padStart(2, '0');
  return `${day}/${month}/${parsed.year}`;
}

export function formatFechaPublicacion(fecha?: string): string {
  if (!fecha || !fecha.trim()) {
    return 'Sin fecha registrada';
  }
  return formatFechaNacimiento(fecha) ?? fecha.trim();
}

