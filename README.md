# Bank Clientes Project

Este proyecto es una aplicación completa de gestión de clientes y transacciones bancarias, con un backend en **Spring Boot** (Java), un frontend en **React + Vite**, y una base de datos **PostgreSQL**.

## Prerrequisitos

Asegúrate de tener instalados los siguientes componentes:
- [Docker](https://www.docker.com/products/docker-desktop/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Cómo ejecutar el proyecto

Para iniciar toda la infraestructura (Base de datos, Backend y Frontend), ejecuta el siguiente comando en la raíz del proyecto:

```bash
docker-compose up -d --build
```

### Servicios Disponibles

Una vez que los contenedores estén en ejecución, podrás acceder a los servicios en las siguientes URLs:

- **Frontend (Interfaz de Usuario):** [http://localhost:3000](http://localhost:3000)
- **Backend (API REST):** [http://localhost:8080](http://localhost:8080)
- **Documentación Swagger (OpenAPI):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Base de Datos (PostgreSQL):** `localhost:5432` (Usuario: `postgres`, Password: `admin`, DB: `tr_bank`)

## Estructura del Proyecto

- `/back-transactions`: Microservicio Spring Boot con arquitectura hexagonal.
- `/front-transactions`: Aplicación React con Vite y componentes de gestión.
- `docker-compose.yml`: Orquestación de contenedores.

## Detener el proyecto

Para detener y eliminar los contenedores, usa:

```bash
docker-compose down
```
