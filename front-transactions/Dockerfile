# Etapa 1: Construcción
FROM node:20-alpine AS build
WORKDIR /app

# Instalar dependencias primero para aprovechar el caché
COPY package*.json ./
RUN npm install

# Copiar el resto del código e ignorar lo que está en .dockerignore
COPY . .
RUN npm run build

# Etapa 2: Servidor Nginx para servir el contenido estático
FROM nginx:stable-alpine
# Eliminar los archivos por defecto de nginx
RUN rm -rf /usr/share/nginx/html/*
# Copiar el build de la etapa anterior
COPY --from=build /app/dist /usr/share/nginx/html

# Configuración de Nginx para SPAs (React)
RUN printf 'server {\n\
    listen 80;\n\
    location / {\n\
        root /usr/share/nginx/html;\n\
        index index.html;\n\
        try_files $uri $uri/ /index.html;\n\
    }\n\
}\n' > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
