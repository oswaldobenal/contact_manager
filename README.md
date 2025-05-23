# Gestión de Contactos

**Gestión de Contactos** es una aplicación de escritorio desarrollada en Java para gestionar información de contactos de manera eficiente y multilingüe. Construida con una arquitectura Modelo-Vista-Controlador (MVC), ofrece una interfaz gráfica moderna, persistencia de datos en SQLite, soporte para serialización/deserialización en JSON, e internacionalización en español, inglés y francés. La aplicación utiliza programación concurrente para garantizar una experiencia fluida y gestiona dependencias con Maven, asegurando versiones estables y exclusión de dependencias transitivas.

## Características

- **Gestión de Contactos**: Crear, editar, eliminar y buscar contactos con campos para nombre, correo, teléfono y tipo (trabajo, familia, personal).
- **Favoritos**: Marcar/desmarcar contactos como favoritos, con íconos dinámicos (`favorite.png`/`not_favorite.png`).
- **Búsqueda en Tiempo Real**: Filtrar contactos por nombre, correo, teléfono o tipo.
- **Internacionalización**: Soporte para español, inglés y francés mediante `ResourceBundle`.
- **Exportación**: Generar archivos CSV y JSON con los datos de contactos.
- **Importación**: Cargar contactos desde archivos JSON externos, validando correos únicos.
- **Interfaz Moderna**: Diseño con FlatLaf y paleta de colores `#4A90E2` (azul), `#F5F7FA` (gris claro), `#2D3748` (gris oscuro).
- **Concurrencia**: Uso de `SwingWorker` para operaciones asíncronas y `ReentrantLock` para acceso seguro a recursos.
- **Gestión de Dependencias**: Configuración de versiones estables en Maven con exclusión de dependencias transitivas.

## Requisitos

- **Java Development Kit (JDK)**: Versión 17 (recomendado) o 24.
- **Maven**: Versión 3.6.0 o superior para gestionar dependencias.
- **Sistema Operativo**: Windows, macOS, o Linux.
- **Entorno de Desarrollo**: IntelliJ IDEA (recomendado) o cualquier IDE compatible con Java y Maven.
- **Dependencias**:
  - `flatlaf:3.6` (tema visual).
  - `sqlite-jdbc:3.46.1` (base de datos).
  - `jackson-databind:2.18.0`, `jackson-core:2.18.0`, `jackson-annotations:2.18.0` (manejo de JSON).
  - Maven Compiler Plugin 3.13.0.

## Instalación

1. **Clonar el Repositorio**:
   ```bash
   git clone https://github.com/oswaldobenal/contact-manager.git
   cd contact-manager
   ```

2. **Configurar el Proyecto**:
   - Abre el proyecto en IntelliJ IDEA (`File > Open > Selecciona la carpeta del proyecto`).
   - Sincroniza las dependencias de Maven:
     - Haz clic en **Maven > Reload All Maven Projects** en el panel de Maven.
     - O ejecuta:
       ```bash
       mvn clean install
       ```

3. **Configurar el JDK**:
   - Ve a `File > Project Structure > Project > SDK` y selecciona JDK 17.
   - Si usas JDK 24, añade la bandera `--enable-native-access=ALL-UNNAMED` en:
     - `Run > Edit Configurations > VM options`.

4. **Verificar Recursos**:
   - Asegúrate de que los archivos de propiedades (`messages_es.properties`, `messages_en.properties`, `messages_fr.properties`) estén en `src/main/resources/`.
   - Confirma que los íconos (`save.png`, `edit.png`, `delete.png`, `clear.png`, `favorite.png`, `not_favorite.png`, `export.png`, `import.png`) estén en `src/main/resources/icons/`.

## Ejecución

1. **Compilar y Ejecutar**:
   - En IntelliJ, haz clic en `Run > Run 'ContactManagerApp'`.
   - O desde la terminal:
     ```bash
     mvn exec:java -Dexec.mainClass="com.juliandev.ContactManagerApp"
     ```

2. **Interfaz**:
   - La aplicación se abrirá con una interfaz gráfica donde puedes:
     - Crear/editar/eliminar contactos.
     - Marcar contactos como favoritos.
     - Buscar contactos en tiempo real.
     - Cambiar el idioma (Archivo > Idioma).
     - Exportar a CSV/JSON (Archivo > Exportar CSV).
     - Importar desde JSON (Archivo > Importar JSON).
     

## Uso

### Crear un Contacto
- Completa los campos Nombre, Correo, Teléfono (opcional) y Tipo.
- Haz clic en **Guardar**.
- Nota: El correo debe ser único y válido (formato `nombre@dominio.com`).

### Importar Contactos
- Ve a **Archivo > Importar JSON**.
- Selecciona un archivo JSON con el siguiente formato:
  ```json
  [
      {
          "id": 0,
          "name": "Juan Pérez",
          "email": "juan.perez@example.com",
          "phone": "+34 612 345 678",
          "contactType": "Trabajo",
          "favorite": true
      }
  ]
  ```
- Los contactos se añadirán a la base de datos, ignorando correos duplicados.

### Exportar Contactos
- Ve a **Archivo > Exportar CSV**.
- Selecciona una ubicación para guardar `contacts.csv`.
- Un archivo `contacts.json` se generará automáticamente en la misma carpeta.

### Cambiar Idioma
- Selecciona **Archivo > Idioma** y elige entre Español, Inglés o Francés.
- La interfaz se actualizará automáticamente.

## Estructura del Proyecto

```
contact-manager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/juliandev/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Contact.java
│   │   │   │   │   ├── ContactDAO.java
│   │   │   │   ├── view/
│   │   │   │   │   ├── ContactView.java
│   │   │   │   ├── controller/
│   │   │   │   │   ├── ContactController.java
│   │   │   │   ├── ContactManagerApp.java
│   │   ├── resources/
│   │   │   ├── icons/
│   │   │   │   ├── save.png
│   │   │   │   ├── edit.png
│   │   │   │   ├── delete.png
│   │   │   │   ├── clear.png
│   │   │   │   ├── favorite.png
│   │   │   │   ├── not_favorite.png
│   │   │   │   ├── export.png
│   │   │   │   ├── import.png
│   │   │   ├── messages_es.properties
│   │   │   ├── messages_en.properties
│   │   │   ├── messages_fr.properties
├── pom.xml
├── README.md
```

## Resolución de Problemas

- **Error: "Icono no encontrado"**:
  - Verifica que los íconos estén en `src/main/resources/icons/`.
  - Descarga íconos desde [Flaticon](https://www.flaticon.com/) si faltan.
- **Error: "MissingResourceException"**:
  - Asegúrate de que los archivos `messages_*.properties` estén en `src/main/resources/`.
  - Limpia el caché de IntelliJ: `File > Invalidate Caches / Restart`.
  - Reconstruye el proyecto: `mvn clean install`.
- **Error al Importar JSON**:
  - Valida el formato del archivo JSON con [JSONLint](https://jsonlint.com/).
  - Revisa la consola para excepciones de Jackson.
- **Interfaz Lenta**:
  - Confirma que todas las operaciones de base de datos usen `SwingWorker`.
  - Revisa los logs en `ContactController` para cuellos de botella.
- **Advertencias en JDK 24**:
  - Cambia a JDK 17 o añade `--enable-native-access=ALL-UNNAMED` en las opciones de la JVM.

## Contribuir

1. **Fork** el repositorio.
2. Crea una rama para tu funcionalidad:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. Realiza tus cambios y haz commit:
   ```bash
   git commit -m "Añadir nueva funcionalidad"
   ```
4. Sube los cambios:
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
5. Abre un **Pull Request** en GitHub.

Por favor, sigue las convenciones de código (formato Java, comentarios claros) y añade pruebas para nuevas funcionalidades.

## Licencia

Este proyecto está licenciado bajo la [Licencia MIT](LICENSE). Consulta el archivo `LICENSE` para más detalles.

## Contacto

Para dudas o sugerencias, contacta a [juliandev26@gmail.com](mailto:tu.email@example.com) o abre un issue en el repositorio.

---

Desarrollado por **JuliánDev** | 2025