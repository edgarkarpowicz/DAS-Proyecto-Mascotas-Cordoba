/**
 * Extrae el mensaje legible devuelto por Spring Boot / RFC 7807 / errores HTTP.
 */
export function getHttpErrorMessage(err: unknown, fallback: string): string {
  const errorObj = (err as { error?: unknown })?.error;
  if (typeof errorObj === 'string' && errorObj.trim()) {
    return errorObj.trim();
  }
  if (errorObj && typeof errorObj === 'object') {
    const record = errorObj as Record<string, unknown>;
    for (const key of ['message', 'detail', 'error']) {
      const value = record[key];
      if (typeof value === 'string' && value.trim()) {
        return value.trim();
      }
    }
  }
  const msg = (err as { message?: unknown })?.message;
  if (typeof msg === 'string' && msg.trim()) {
    return msg.trim();
  }
  return fallback;
}
