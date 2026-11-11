import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filtrarPor',
  standalone: true
})
export class FiltrarPorPipe implements PipeTransform {

  transform<T>(values: T[], filters: keyof T | (keyof T)[], filter: string): T[] {
    if (!values || !filter || !filter.trim()) {
      return values || [];
    }

    const keys = Array.isArray(filters) ? filters : [filters];
    const escaped = filter.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const regex = new RegExp(escaped, 'i');

    return values.filter(item =>
      keys.some((keyName) => {
        const val = item?.[keyName];
        return val !== null && val !== undefined && regex.test(String(val));
      })
    );
  }

}
