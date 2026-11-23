# Análisis SOA: integraciones entre sistemas

Todo lo que se presenta del análisis de integraciones está en esta carpeta.

## identificadas/

Las tres integraciones que identificamos. Es lo que se expone.

- **Integraciones - Mascotas Cordoba.pdf**: informe breve (11 páginas). Por cada integración: descripción, diagrama y contrato.
- **Anexo - Analisis SOA detallado - Mascotas Cordoba.pdf**: el mismo contenido con criterios de diseño y lectura paso a paso de cada diagrama (25 páginas).
- **1 - Veterinarias (RF06 y RF09)/**: contrato (DOCX) y diagrama (PDF) de cada proceso por lote.
- **2 - CiDi (RF15)/**: contrato y diagrama.
- **3 - IA generativa (RF21)/**: contrato y diagrama.
- **versiones_anteriores/**: versión previa del anexo.

Los DOCX y PDF por RF son copias de la versión vigente (`renovado10`; RF15 en `renovado8`). El historial de versiones sigue en `obligatorios/rf_XX/` y `no_obligatorios/rf_XX/`.

## candidatas/

Puntos que podrían considerarse integraciones y que no tomamos. Material de respaldo para la defensa.

- **Integraciones candidatas - Mascotas Cordoba.pdf**: informe (18 páginas). Por cada candidata: por qué podría ser una integración, por qué no la tomamos, cómo sería, diagrama y contrato.
- **A a F**: contrato (DOCX) y diagrama (PDF) de cada candidata.
- **G - RF07**: actualización de mascotas desde la veterinaria (copia de `renovado8`).

| | Candidata | RF | Tipo |
| --- | --- | --- | --- |
| A | Refugios: animales bajo cuidado | RF12 | Por lote |
| B | Refugios: publicaciones de adopción | RF13 | Por lote |
| C | Identificación del personal municipal | RF15 | En línea |
| D | Terceros autorizados: validar carnet | RF11 | En línea |
| E | La veterinaria envía mascotas | RF06 | Por lote |
| F | La veterinaria envía información sanitaria | RF09 | Por lote |
| G | Actualización de mascotas | RF07 | Por lote |

## Los demás RF

Los RF que no son integraciones (RF01-05, RF08, RF10-14, RF16-20) tienen su diagrama de proceso interno y su ficha "Por qué no es una integración" en `../obligatorios/rf_XX/renovado10/` y `../no_obligatorios/rf_XX/renovado10/`.
