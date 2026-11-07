# ==============================================================================
# Etapa 1: Build de la aplicación Spring Boot con Maven
# ==============================================================================
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar descriptor de dependencias
COPY pom.xml .

# Copiar código fuente
COPY src ./src

# Compilar empaquetando como WAR omitiendo tests en el build de imagen
RUN mvn clean package -DskipTests

# ==============================================================================
# Etapa 2: Runtime con Apache Tomcat 10.1 (Convención Cátedra DAS)
# ==============================================================================
FROM tomcat:10.1-jdk17-temurin
LABEL authors="Mariela Pastarini / DAS UBP"

# Limpiar aplicaciones de ejemplo predeterminadas de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar el WAR compilado como ROOT.war para responder en la raíz del servidor (/)
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Directorio para subida y almacenamiento de fotos de publicaciones
RUN mkdir -p /usr/local/tomcat/uploads/publicaciones

# El puerto interno del contenedor Tomcat es 8080
EXPOSE 8080

CMD ["catalina.sh", "run"]
