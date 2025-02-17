# API REST de Alquiler de Productos

Esta API REST permite la gestión de productos para alquiler, con operaciones CRUD (Crear, Leer, Actualizar y Eliminar) y una base de datos MySQL que garantiza la persistencia de los datos. La aplicación está desplegada y gestionada utilizando **Kubernetes**, lo que proporciona escalabilidad, alta disponibilidad y recuperación ante fallos.

## 1. Introducción

Hemos desarrollado una API REST de alquiler de productos con una base de datos MySQL. Esta API permite la gestión de productos mediante operaciones CRUD (Crear, Leer, Actualizar y Eliminar) y garantiza la persistencia de datos en un entorno seguro y escalable.

Para desplegar y administrar nuestra aplicación, utilizamos **Kubernetes**, una plataforma de orquestación de contenedores que nos permite manejar la escalabilidad, la alta disponibilidad y la gestión automatizada de nuestros servicios.

## 2. Arquitectura de la Aplicación

- **API REST** (Backend con Java y Spring Boot)
- **Base de Datos** (MySQL)
- **Servicios en Kubernetes**

## 3. Documentación

### 3.1 ¿Qué es Kubernetes?

Kubernetes es una plataforma de código abierto y extensible diseñada para la gestión de aplicaciones en contenedores. Facilita la automatización del despliegue, la escala y la operación de estas aplicaciones a través de un enfoque declarativo y basado en contenedores.

Kubernetes permite a los usuarios administrar clústeres de contenedores, brindando alta disponibilidad, escalabilidad automática y capacidad de auto-recuperación. Su ecosistema en constante expansión incluye herramientas y extensiones que lo hacen ideal para ejecutar aplicaciones distribuidas, microservicios y cargas de trabajo en entornos de nube.

### 3.2 Conceptos Básicos

- **Contenedor**: Es una unidad de software que empaqueta una aplicación y sus dependencias para ejecutarse de manera aislada.
- **Pod**: Es la unidad más pequeña en Kubernetes. Un pod puede contener uno o varios contenedores que comparten recursos (como red y almacenamiento) y se ejecutan juntos.
- **Node (Nodo)**: Es una máquina (física o virtual) que ejecuta los pods. Puede haber varios nodos en un clúster.
- **Cluster**: Es un conjunto de nodos que ejecutan aplicaciones en contenedores gestionados por Kubernetes.
- **Deployment**: Es una manera de gestionar y asegurar que las aplicaciones se ejecuten de manera deseada, permitiendo el despliegue y la actualización automática de pods.
- **Service**: Es un componente que define cómo acceder a los pods de manera estable, incluso si los pods se crean o eliminan dinámicamente.
- **Namespace**: Es una forma de organizar y aislar recursos dentro de un clúster.
- **ConfigMap & Secret**: Almacenan configuraciones y datos sensibles de las aplicaciones (como contraseñas o claves), sin tener que codificarlos directamente en los contenedores.

### 3.3 ¿Por qué Kubernetes para la API?

- **Escalabilidad automática** según la demanda.
- **Alta disponibilidad** y **recuperación ante fallos**.
- **Despliegue continuo** con actualizaciones sin interrupciones.
- **Gestión centralizada de configuraciones y secretos**.
- **Balanceo de carga** y **enrutamiento de tráfico inteligente**.
- **Portabilidad** de la API entre diferentes entornos y plataformas.
- **Monitoreo y registro** integrado para la observabilidad.

## 4. Endpoints

## 5. Leyendas de Excepciones

- **400 - Bad Request**: La solicitud es incorrecta o mal formada. Puede deberse a parámetros inválidos o falta de información necesaria.
- **403 - Forbidden**: El usuario no tiene permisos suficientes para realizar la acción solicitada.
- **404 - Not Found**: No se ha encontrado el recurso solicitado.
- **409 - Conflict**: Ya existe un recurso con la información solicitada o un conflicto de datos.
- **500 - Internal Server Error**: Error interno del servidor al procesar la solicitud. Puede deberse a fallos en la aplicación o en el servidor.

### Usuarios

| Método  | Endpoint            | Descripción                                                        | Excepciones         |
|---------|---------------------|--------------------------------------------------------------------|---------------------|
| `POST`  | /usuarios/register   | Crear un nuevo usuario en la plataforma (Registro). [Público]      | `400` - Bad Request<br>`409` - Conflict<br>`500` - Internal Server Error       |
| `POST`  | /usuarios/login      | Iniciar sesión en la plataforma. [Público]                         | `400` - Bad Request<br>`404` - Not Found<br>`500` - Internal Server Error       |
| `GET`   | /usuarios/           | Obtener la información de todos los usuarios. [Admin]              | `400` - Bad Request<br>`403` - Forbidden<br>`500` - Internal Server Error       |
| `GET`   | /usuarios/{id}       | Obtener la información de un usuario específico por su `id`. [Admin] & [Usuario(sólo el suyo)] | `400` - Bad Request<br>`403` - Forbidden<br>`404` - Not Found<br>`500` - Internal Server Error  |
| `PUT`   | /usuarios/{id}       | Actualizar la información de un usuario por su `id`. [Admin] & [Usuario(sólo el suyo)] | `400` - Bad Request<br>`403` - Forbidden<br>`404` - Not Found<br>`409` - Conflict<br>`500` - Internal Server Error |
| `DELETE`| /usuarios/{id}       | Eliminar un usuario de la plataforma por su `id`. [Admin] & [Usuario(sólo el suyo)] | `400` - Bad Request<br>`403` - Forbidden<br>`404` - Not Found<br>`500` - Internal Server Error  |

### Productos

| Método  | Endpoint           | Descripción                                                        | Excepciones         |
|---------|--------------------|--------------------------------------------------------------------|---------------------|
| `POST`  | /productos/         | Crear un nuevo producto disponible para alquiler. [Público]         | `400` - Bad Request<br>`403` - Forbidden<br>`404` - Not Found<br>`500` - Internal Server Error  |
| `GET`   | /productos/         | Obtener la lista de todos los productos disponibles. [Público]     | `400` - Bad Request<br>`403` - Forbidden<br>`500` - Internal Server Error       |
| `GET`   | /productos/{id}     | Obtener detalles de un producto específico por su `id`. [Público]  | `400` - Bad Request<br>`403` - Forbidden<br>`404` - Not Found<br>`500` - Internal Server Error  |
| `PUT`   | /productos/{id}     | Actualizar información de un producto por su `id`. [Admin] & [Usuario(sólo los suyos)] | `400` - Bad Request<br>`404` - Not Found<br>`500` - Internal Server Error  |
| `DELETE`| /productos/{id}     | Eliminar un producto de la plataforma por su `id`. [Admin] & [Usuario(sólo los suyos)] | `400` - Bad Request<br>`404` - Not Found<br>`500` - Internal Server Error  |

### Reservas

| Método  | Endpoint           | Descripción                                                        | Excepciones         |
|---------|--------------------|--------------------------------------------------------------------|---------------------|
| `POST`  | /reservas/          | Crear una nueva reserva para un producto. [Solo autenticados]      | `400` - Bad Request<br>`403` - Forbidden<br>`409` - Conflict<br>`500` - Internal Server Error  |
| `GET`   | /reservas/          | Obtener la lista de todas las reservas disponibles. [Admin]        | `400` - Bad Request<br>`403` - Forbidden<br>`500` - Internal Server Error       |
| `GET`   | /reservas/{id}      | Obtener detalles de una reserva en específico por su `id`. [Admin] & [Usuario(sólo las suyas)] | `400` - Bad Request<br>`403` - Forbidden<br>`404` - Not Found<br>`500` - Internal Server Error  |
| `PUT`   | /reservas/{id}      | Modificar las fechas de una reserva existente. [Admin] & [Usuario(sólo las suyas)] | `400` - Bad Request<br>`403` - Forbidden<br>`404` - Not Found<br>`409` - Conflict<br>`500` - Internal Server Error |
| `DELETE`| /reservas/{id}      | Cancelar una reserva existente. [Admin] & [Usuario(sólo las suyas)] | `400` - Bad Request<br>`403` - Forbidden<br>`404` - Not Found<br>`500` - Internal Server Error  |


## 6. Tecnologías

- **Java** con **Spring Boot** para el desarrollo del backend.
- **MySQL** para la persistencia de datos.
- **Docker** para contenedores.
- **Kubernetes** para orquestación, escalabilidad y gestión de la aplicación.
