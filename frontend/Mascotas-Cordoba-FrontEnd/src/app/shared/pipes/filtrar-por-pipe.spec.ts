import { FiltrarPorPipe } from './filtrar-por-pipe';

describe('FiltrarPorPipe', () => {
  const pipe = new FiltrarPorPipe();
  const sample = [
    { nombreMascota: 'Rocco', especie: 'Perro' },
    { nombreMascota: 'Mía', especie: 'Gato' },
    { nombreMascota: 'Thor', especie: 'Perro' }
  ];

  it('should return all items when filter is empty', () => {
    expect(pipe.transform(sample, 'nombreMascota', '')).toEqual(sample);
  });

  it('should filter items case-insensitively by field', () => {
    const result = pipe.transform(sample, 'nombreMascota', 'roc');
    expect(result.length).toBe(1);
    expect(result[0].nombreMascota).toBe('Rocco');
  });

  it('should handle regex special characters safely', () => {
    expect(() => pipe.transform(sample, 'nombreMascota', '([*')).not.toThrow();
    expect(pipe.transform(sample, 'nombreMascota', '([*')).toEqual([]);
  });
});
