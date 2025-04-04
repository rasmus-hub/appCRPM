# Sistema de Control en Tiempo Real con ESP32

Este proyecto consiste en el desarrollo de un sistema mecatrónico que permite el control remoto de servomotores mediante una aplicación Android, utilizando un microcontrolador ESP32 como núcleo de procesamiento y comunicación. Fue desarrollado como parte del curso **Taller de Diseño y Desarrollo de Soluciones** de la carrera de Ingeniería en Informática.

## 🛠️ Tecnologías utilizadas

- **ESP32** (Bluetooth)
- **Servomotores MG996R**
- **Android Studio (Java)**
- **Python (servidor intermedio)**
- **Arduino IDE**
- **Firebase** (para autenticación y almacenamiento)
- **SQLite** (base de datos local en Android)

## 📱 Funcionalidades principales

- Inicio de sesión seguro mediante autenticación.
- Emparejamiento y conexión Bluetooth con ESP32.
- Control en tiempo real de servomotores desde la aplicación móvil.
- Visualización en vivo de cámara para captura de movimientos.
- Interfaz gráfica intuitiva con distintas pantallas:
  - Inicio
  - Control y captura
  - Calibración
  - Configuración del sistema
- Configuración de parámetros como sensibilidad y calibración personalizada.

## 🧪 Plan de pruebas

El sistema fue validado a través de diversas pruebas:

- Pruebas funcionales e integrales.
- Evaluación de latencia, precisión y estabilidad energética.
- Verificación de conectividad y desempeño del sistema en condiciones reales.

## 🧰 Instalación

### Requisitos

- Placa ESP32
- Servomotores MG996R
- Fuente de alimentación estable (recomendada externa)
- Smartphone Android
- Cableado y componentes de conexión

### Pasos

1. Clona este repositorio.
2. Cargar el código del microcontrolador desde Arduino IDE.
3. Configurar el servidor intermedio en Python (si se usa).
4. Abrir y ejecutar el proyecto Android en Android Studio.
5. Realiza la calibración inicial y emparejamiento Bluetooth.
