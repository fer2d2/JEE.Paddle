# Tecnología Spring: Proyecto Padel
#### Back-end con Tecnologías de Código Abierto (SPRING)
#### [Máster en Ingeniería Web por la U.P.M.](http://miw.etsisi.upm.es)

### Datos del alumno:

- Nombre: Fernando Moro Hernández
- Matrícula: BH0611
- Grupo: FdS

### Tareas realizadas

#### Resumen

- ECP1. Persistencia (30%)
    - A, B, C.
- ECP1. Api (50%)
    - A, B, C.
- ECP1. Web (20%)
    - A.

#### Detalle
- ECP1. Persistencia (30%)
    - Se modifica la capa de persistencia para que los token caduquen en una hora.
    - Se añaden tests para la funcionalidad solicitada.
    - Se amplía la capa de persistencia para ofrecer un servicio de clases de Pádel.
    - Se añaden tests para la funcionalidad solicitada.
- ECP1. Api (50%)
    - Se modifica la capa de negocio para hacer uso de la mejora de caducidad. Se implementa el comportamiento a través de la clase `UserDetailsServiceImpl`, utilizando la variable `credentialsNonExpired` del usuario de Spring.
    - Se añaden tests para la funcionalidad solicitada.
    - Se amplía la capa de negocio con una interfaz REST para los entrenamientos. Se exponen las opciones CRUD para un entrenamiento, y así como las opciones de añadir y eliminar alumno. Se controla mediante excepciones cualquier comportamiento anómalo detectado. Se configura la autorización de acceso para las rutas involucradas.
    - Se añaden tests para la funcionalidad solicitada.
- ECP1. Web (20%)
    - Utilizando la tecnología JSP, se generan pantallas web para listado, alta y borrado de pistas y usuarios, pudiendo activar o desactivar desde la pantalla de listado en el caso de las pistas. Se utilizan *flash parameters* para mostrar los mensajes que notifican al usuario que el proceso de inserción ha sido correcto. Para acceder a la *home* de la vista web, se facilita [el siguiente enlace](http://localhost:8080/JEE.Paddle.0.0.1-SNAPSHOT/home).
