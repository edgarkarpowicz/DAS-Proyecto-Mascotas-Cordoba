# ==============================================================================
# Etapa 1: Compilación de la aplicación Angular (Producción)
# ==============================================================================
FROM node:20-alpine AS builder
WORKDIR /app

# Copiar manifiestos de paquetes
COPY package*.json ./

# Instalar dependencias
RUN npm install

# Copiar código fuente
COPY . .

# Compilar para producción
RUN npm run build -- --configuration production

# ==============================================================================
# Etapa 2: Servidor Web Nginx liviano para servir la SPA
# ==============================================================================
FROM nginx:alpine
LABEL authors="DAS UBP - Mascotas Córdoba"

# Copiar configuración con soporte para rutas HTML5 de Angular
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar los archivos estáticos generados por el build
COPY --from=builder /app/dist/mascotas-cordoba-front-end/browser /usr/share/nginx/html

# Exponer el puerto estándar HTTP
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
