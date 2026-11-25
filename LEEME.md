# Plantilla de AppBlocks

Esta carpeta contiene una app Android muy sencilla que solo muestra, a pantalla completa,
la página guardada en `app/src/main/assets/www/index.html`.

AppBlocks usará esta plantilla para crear la APK de cada alumno: cambiará esa página por la app
del alumno y le pondrá su nombre, su icono y un identificador propio.

La plantilla solo hay que compilarla **una vez**. Lo hace GitHub en sus ordenadores, gratis.

---

## Pasos

### 1. Crear el repositorio
1. Entra en github.com con tu cuenta.
2. Arriba a la derecha, pulsa **+** → **New repository**.
3. Nombre: `plantilla-appblocks`. Déjalo en **Public** y pulsa **Create repository**.

### 2. Subir los archivos
1. En la página del repositorio vacío, pulsa el enlace **uploading an existing file**.
2. Abre esta carpeta (`plantilla-appblocks`) en tu ordenador, selecciona **todo lo que hay dentro**
   (las carpetas `.github` y `app` y los archivos sueltos) y arrástralo a la página de GitHub.
3. Espera a que terminen de subirse y pulsa **Commit changes**.

> Comprueba que se ha subido la carpeta **`.github`**: sin ella GitHub no compila nada.
> Si no aparece, mira el apartado *Si algo falla* al final.

### 3. Esperar a que GitHub compile
1. Pulsa la pestaña **Actions** del repositorio.
   Si te pide activarlas, pulsa el botón verde para permitirlo.
2. Verás una tarea llamada **Compilar plantilla APK** con un círculo amarillo girando.
3. Espera unos 5 minutos hasta que salga un **tic verde** ✅.

### 4. Descargar el resultado
1. Pulsa sobre la tarea terminada.
2. Abajo, en **Artifacts**, pulsa **plantilla-appblocks**. Se descarga un zip.
3. Dentro hay:
   - `plantilla-appblocks.apk` → la plantilla para AppBlocks.
   - `PRUEBA-instalar-en-el-movil.apk` → para probarla en un móvil.
   - `contenido.txt`, `manifiesto.txt`, `resumen.txt`, `firma-prueba.txt` → informes técnicos.

### 5. Probarla en el móvil (Android)
1. Pásate `PRUEBA-instalar-en-el-movil.apk` al móvil (por correo, Drive, cable…).
2. Ábrela. El móvil pedirá permiso para **instalar apps desconocidas**: acéptalo.
   Puede salir un aviso de Play Protect: pulsa *Instalar de todas formas*.
3. Abre la app **APPBLOCKS_NOMBRE_DE_LA_APP…** (ese nombre tan raro es a propósito:
   AppBlocks lo cambiará por el de cada app).
4. Prueba los 6 botones: contador, vibrar, GPS, foto, pantalla 2 + botón Atrás y abrir una web.

### 6. Enviárselo a Claude
Pasa a Claude **el zip entero** que descargaste en el paso 4 y cuéntale qué botones funcionaron.

---

## Si algo falla

- **Sale una cruz roja ❌ en Actions:** pulsa sobre la tarea, luego sobre el paso que tenga la cruz,
  copia el texto del error y pásaselo a Claude.
- **No se subió la carpeta `.github`:** en el repositorio pulsa **Add file → Create new file**.
  Como nombre escribe exactamente `.github/workflows/compilar.yml`, pega dentro el contenido del archivo
  `compilar.yml` de esta carpeta y pulsa **Commit changes**. La compilación empezará sola.
- **Volver a compilar:** en **Actions → Compilar plantilla APK → Run workflow**.
