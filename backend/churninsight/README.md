# 🎬 ChurnInsight
## Backend

API REST de ChurnInsight para predicción de churn de clientes, historial de predicciones, KPIs y factores de riesgo. Incluye documentación interactiva con Swagger.

---

### 🧠 Descripción

ChurnInsight Backend es un servicio REST que permite a empresas de suscripción analizar el riesgo de cancelación de sus clientes. Sus principales funcionalidades son:

- Generar predicciones de churn por cliente usando un modelo de Machine Learning externo (FastAPI).
- Consultar historial de predicciones y filtrar por cliente o rango de fechas.
- Consultar KPIs generales de churn (total evaluados, riesgo alto/medio/bajo, tasa de churn).
- Obtener explicaciones de factores de riesgo y sugerencias de acción personalizadas.
- Monitorear el estado del backend y sus servicios dependientes mediante health checks.

El backend está diseñado para integrarse fácilmente con otros servicios o aplicaciones y ofrece documentación Swagger para probar los endpoints de manera interactiva.

---

### 🏗️ Ubicación dentro del repositorio
```
Hackthon_NoCountry_65/
└── backend/
    └── churninsight/ ← Proyecto Spring Boot
```
---

### 🛠️ Stack tecnológico

#### Tecnologías principales
- **Java 21** – Lenguaje principal del backend (requerido por Spring Boot 4.x)
- **Spring Boot 4.0.1** – Framework para el desarrollo de la API REST
    - **Spring Web** – Para exponer endpoints REST
    - **Spring Data JPA** – Acceso a base de datos
    - **Spring Validation** – Validación de requests
    - **WebClient (Spring WebFlux)** – Cliente HTTP para consumir servicios externos
- **Maven 3.8+** – Gestión de dependencias y compilación
- **PostgreSQL 14+** – Base de datos relacional (usando Supabase)
- **Jackson** – Serialización y deserialización de JSON
- **Lombok 1.18+** – Generación automática de getters, setters y constructores
- **Swagger / OpenAPI 3.0.1** – Documentación interactiva de la API
- **FastAPI (Python, servicio externo)** – Servicio de Machine Learning consumido por el backend para generar predicciones de churn.

#### Tecnologías de testing
- **JUnit 5 / Spring Test** – Pruebas unitarias e integración
- **Mockito** – Mocking de servicios y dependencias
- **MockWebServer 4.12.0** – Simulación de servicios externos en tests

---

### 📁 Estructura del proyecto backend
```
backend/
├── src/main/java/com/hackathon/churninsight
│   ├── controller   # Endpoints REST
│   ├── domain       # Entidades JPA y enums
│   ├── dto          # DTOs de request y response
│   ├── exception    # Excepciones y manejo global de errores
│   ├── repository   # Repositorios JPA
│   ├── service      # Lógica de negocio, predicción, KPIs, factores de riesgo
│   └── config       # Configuraciones (CORS, Jackson, WebClient, Swagger)
├── src/main/resources
│   └── application.properties
└── test             # Pruebas unitarias y de integración
```
---

### ▶️ Cómo ejecutar el backend

#### Requisitos previos

Antes de instalar y ejecutar **ChurnInsight Backend**, asegúrate de tener:

- **Java 21** instalado y configurado (JAVA_HOME apuntando al JDK correcto).
- **Maven 3.8+** para compilación y gestión de dependencias.
- **PostgreSQL 14+** (o Supabase) corriendo y accesible.
- **Git** para clonar el repositorio.
- **Servicio de Machine Learning (FastAPI)** levantado y accesible en la URL configurada (`ML_SERVICE_URL`). Este servicio es requerido para que los endpoints de predicción y factores de riesgo funcionen correctamente.
- Variables de entorno configuradas según `application.properties` (DB, ML_SERVICE_URL).
- *(Opcional)* **Postman** o **Insomnia** para probar los endpoints.

---

#### 🔹 Pasos para ejecutar

1. Posicionarse en la carpeta del proyecto backend:
```
cd backend/churninsight
```
2. Ejecutar la aplicación:

- Windows
```
mvnw spring-boot:run
```

- Linux / macOS
```
./mvnw spring-boot:run
```

3. Esperar a que el proyecto compile y se levante correctamente.

🔹 **Acceso a la aplicación**

Por defecto, el backend se ejecuta en:
```
http://localhost:8080
```
🔹 **Verificación rápida**

Se puede verificar el estado del backend accediendo a los endpoints de health o Swagger.
```
http://localhost:8080/api/health/basic
```

o consumiendo los endpoints expuestos mediante una herramienta de pruebas HTTP
(Postman, Insomnia, curl, etc.).

> **Nota:** El proyecto puede ejecutarse desde cualquier IDE Java compatible.  
> Se recomienda **IntelliJ IDEA**, abriendo la carpeta `backend/churninsight` como proyecto Maven.  
> El proyecto incluye **Maven Wrapper (mvnw)**, por lo que no es necesario tener Maven instalado globalmente.

---

#### ▶️ Ejecutar desde IntelliJ IDEA

1. Abrir **IntelliJ IDEA**.
2. Seleccionar **File → Open** y abrir la carpeta: `backend/churninsight`
3. Esperar a que IntelliJ importe el proyecto como **Maven Project**.
4. Verificar que el **JDK 21** esté configurado:
- File → Project Structure → Project SDK → Java 21
5. Ubicar la clase principal: `ChurnInsightApplication.java`
6. Ejecutar haciendo clic derecho → **Run 'ChurnInsightApplication'**.

La aplicación se levantará por defecto en: `http://localhost:8080`

---

## Endpoints
### 1. Health Endpoints

| Endpoint             | Método | Descripción                                                         |
|---------------------|--------|---------------------------------------------------------------------|
| /api/health          | GET    | Health completo: valida estado del backend y la conectividad con el servicio ML.         |
| /api/health/basic    | GET    | Health básico: retorna estado del backend sin validar servicios externos |

### 2. KPIs Endpoints

| Endpoint   | Método | Descripción                                           |
|-----------|--------|-------------------------------------------------------|
| /api/kpis | GET    | Retorna KPIs generales de churn: total de clientes evaluados, cantidad por nivel de riesgo (alto, medio, bajo) y tasa de churn (%). |


### 3. Predicción de Churn

| Endpoint     | Método | Descripción                                                                 |
|-------------|--------|-----------------------------------------------------------------------------|
| /api/predict | POST   | Genera predicción de churn usando ML. Recibe un objeto con customer_id y features, y retorna un response con la predicción y su probabilidad. |

**Ejemplo de request:**
```json
{
  "customer_id": "abc-123",
  "features": {
    "subscription_type": "Basic",
    "watch_hours": 3,
    "last_login_days": 5,
    "monthly_fee": 8.99,
    "number_of_profiles": 2,
    "avg_watch_time_per_day": 0.5,
    "payment_method": "Credit Card"
  }
}
```
> **Nota:** features debe coincidir con las variables que espera el servicio ML externo.


Ejemplo de Response
```json
{
  "timestamp": "2026-01-21T12:06:31.9273135",
  "status": 200,
  "message": "Predicción generada correctamente",
  "data": {
    "customer_id": "abc-123",
    "prediction": {
      "label": "will_churn",
      "probability": 0.604
    },
    "prevision": "Va a cancelar"
  },
  "path": "/api/predict"
}
```
> "prevision" es un mensaje interpretativo en lenguaje humano de la predicción generada.

---

### 4. Historial de Predicciones

| Endpoint                     | Método | Descripción                           |
|-------------------------------|--------|---------------------------------------|
| /api/history                  | GET    | Lista historial de predicciones (paginado) |
| /api/history/{customerId}     | GET    | Historial por cliente (paginado)      |
| /api/history/filter           | GET    | Filtra historial por rango de fechas usando query params `startDate` y `endDate`. |
| /api/history                  | DELETE | Elimina todo el historial             |

### 5. Factores de Riesgo

| Endpoint                        | Método | Descripción                                                                 |
|---------------------------------|--------|-----------------------------------------------------------------------------|
| /api/risk-factors/{customerId} | GET    | Retorna factores de riesgo y acción sugerida según la última predicción     |

## Swagger / OpenAPI

Swagger permite probar los endpoints directamente desde el navegador sin herramientas externas.

- Documentación interactiva: http://localhost:8080/swagger-ui.html
- Si no funciona, probar: http://localhost:8080/swagger-ui/index.html
- API Docs JSON: http://localhost:8080/v3/api-docs

## Tests

Pruebas unitarias y de integración en `src/test/`.

Ejecutar tests
```
mvn test
```
Ejecutar tests específicos
```
mvn test -Dtest=NombreDeTest
```
