<p align="center">
  <h1>🔍 DPI Changer</h1>
  <p>Control de DPI por aplicación mediante hooks en <code>ResourcesImpl</code>.</p>
</p>

<div align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/LSPosed-000000?style=flat&logo=xda-developers&logoColor=white"/>
</div>

---

## ¿Cómo funciona?

Android no expone control de DPI por app de forma nativa — el sistema aplica un único valor global en `ResourcesImpl.updateConfiguration()` al iniciar cada proceso.

Este módulo intercepta ese método antes de que se ejecute y sustituye el `densityDpi` con el valor configurado para ese paquete. La comunicación entre la app gestora y el hook se hace vía `ContentProvider`, que expone las preferencias guardadas en `SharedPreferences` al proceso de la app objetivo. El hook cachea el valor tras la primera consulta para no golpear el provider en cada ciclo de recursos.

## Requisitos

- Magisk / KernelSU / APatch
- LSPosed activo

## Instalación

1. Descarga el APK desde [Releases](../../releases)
2. Activa el módulo en LSPosed con alcance **Sistema Android** (`android`)
3. Abre la app, selecciona una aplicación y asigna el DPI deseado
4. Reinicia la app objetivo para que tome efecto

## ⚠️ Limitaciones

- El DPI se aplica al arrancar el proceso, no en caliente — hay que reiniciar la app objetivo tras cada cambio
- Apps con DRM de hardware (Widevine L1) pueden comportarse de forma inesperada
- No se inyecta en su propio proceso para evitar bucles con el ContentProvider
