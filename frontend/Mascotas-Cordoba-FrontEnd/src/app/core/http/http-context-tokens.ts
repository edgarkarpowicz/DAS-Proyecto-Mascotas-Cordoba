import { HttpContextToken } from '@angular/common/http';

/**
 * Permite omitir el loader global en llamadas interactivas o en segundo plano
 * (por ejemplo autocompletado o chat del asistente virtual RF21).
 */
export const SKIP_GLOBAL_LOADER = new HttpContextToken<boolean>(() => false);
