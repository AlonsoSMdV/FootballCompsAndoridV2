# **Futbol Fan** 🏆  

Bienvenido a **Futbol Fan**, una aplicación desarrollada en Android Studio que permite gestionar ligas, equipos y jugadores de fútbol, además de ofrecer diversas funcionalidades adicionales. Aún se encuentra en desarrollo, por lo que hay mejoras y correcciones pendientes.  

---

## **Características principales**  

### **1. Autenticación de usuario**  
- Registro de usuario con **username, email y contraseña**.
- Inicio de sesión con **username y contraseña**.
- Visualización de perfil con opción para cerrar sesión.

### **2. Gestión de Ligas**  
- Creación y eliminación de ligas personalizadas o profesionales.
- Interfaz para visualizar las ligas disponibles.

### **3. Gestión de Equipos**  
- Al seleccionar una liga, se mostrarán los equipos asociados.
- Creación y eliminación de equipos.
- **Problema conocido:** La lista de equipos no recarga bien al volver de la lista de jugadores debido a la falta del Id de la competición.

### **4. Gestión de Jugadores**  
- Registro de jugadores con datos como **nombre, número, posición, nacionalidad y fecha de nacimiento**.
- Creación y eliminación de jugadores.
- Visualización de detalles de cada jugador.
- **Problema conocido:** Similar al de los equipos, la lista no se no recarga bien al volver del detalle de un jugador debido a la falta del Id del equipo.

### **5. Calendario de Partidos**  
- Consulta de próximos partidos con información sobre **hora y ubicación**.
- Implementación de un mapa para visualizar la localización del encuentro.
- Comparte los partidos próximos con tus amigos para que no se los pierdan

### **6. Otras funcionalidades**  
- **Cambio de tema (claro/oscuro)** utilizando **DataStore Preferences**.
- **Interceptor HTTP** para adjuntar un mensaje si la solicitud contiene un token.
- **Navegación optimizada** entre fragmentos.
- **Notificaciones** mediante **Hilt y WorkManager**.
- **Sección de favoritos**, donde se pueden marcar ligas, equipos y jugadores preferidos.
- **Subida de imágenes** a la base de datos de Strapi.

---

## **Desarrollo y desafíos enfrentados**  

### **Decisiones de diseño importantes**  
- **DataStore Preferences** para el almacenamiento eficiente de preferencias del usuario.
- **Hilt para la inyección de dependencias**, facilitando la gestión de componentes y optimizando el rendimiento.
- **WorkManager** para tareas en segundo plano, como la gestión de notificaciones.
- **Room Database** para almacenamiento local de datos.
- **Material Design** utilizado para crear una iterfaz fácil de acceder y uso de divesos elementos estilizados, todo ello con la intención de mejorar la experenciencia del usaurio en la app

### **Desafíos encontrados y soluciones aplicadas**  

| Desafío | Solución |
|---------|----------|
| Manejo de tokens en solicitudes HTTP. | Se implementó un **Interceptor HTTP** para gestionar automáticamente los tokens en las peticiones. |
| Persistencia de datos de usuario y configuración de la app. | Se optó por **DataStore en lugar de SharedPreferences**, ya que ofrece mejor rendimiento y soporte para datos más complejos. |

---

## **Próximas mejoras y funcionalidades**  
- Corrección del problema de actualización automática en listas de equipos y jugadores.
- Implementación de un sistema de estadísticas para equipos y jugadores.
- Implementacionas varias en la sección de partidos
- Implementación de la posibilidad de editar el perfil

---

## **Conclusión**  

**Futbol Fan** es una aplicación en desarrollo con un gran potencial para convertirse en una plataforma completa para los amantes del fútbol. Aunque aún tiene errores por corregir, su estructura y funcionalidad avanzan hacia una experiencia fluida e intuitiva. ¡Gracias por probarla y contribuir a su mejora! ⚽🔥


    
        
 
