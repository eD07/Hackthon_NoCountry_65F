
# 🚀 Hackthon NoCountry 65 – ChurnInsight

![Python](https://img.shields.io/badge/Python-3.10%2B-blue?logo=python)
![FastAPI](https://img.shields.io/badge/FastAPI-green?logo=fastapi)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?logo=postgresql)
![Status](https://img.shields.io/badge/status-active-success)

Proyecto desarrollado para **Hackathon NoCountry 65**, con arquitectura **multi-backend** orientada a la **predicción de churn**.

---

## 🧠 Arquitectura

```text
Frontend
   ↓
Spring Boot (Java) ──────────► PostgreSQL (Supabase)
   ↓
FastAPI (Python) ──► Modelo ML (scikit-learn)
```

---

## 🧩 Componentes

- 🐍 **API Python (FastAPI)**  
  Predicción utilizando un modelo de Machine Learning (scikit-learn)

- ☕ **Backend Java (Spring Boot)**  
  Lógica de negocio, orquestación y persistencia de datos

- 🗄️ **PostgreSQL (Supabase)**  
  Almacenamiento del historial de predicciones

---

## 📦 Requisitos

### General
- Git
- Conexión a Internet

### Python
- Python **3.10+** (recomendado 3.11 o 3.12)
- pip

### Java
- Java **21**
- Maven **NO requerido** (se usa `mvnw`)

---

## 🚀 Levantar el proyecto (después de clonar)

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/VillaltaE/Hackthon_NoCountry_65.git
cd Hackthon_NoCountry_65
```

---

## 🐍 API Python – FastAPI (Puerto 8000)

### 📂 Ir a la carpeta

```bash
cd api-python
```

### 🧱 Crear entorno virtual (solo la primera vez)

```bash
python -m venv .venv
```

### ▶️ Activar entorno virtual

#### Windows (PowerShell)

```powershell
.\.venv\Scripts\Activate.ps1
```

> ⚠️ Si PowerShell bloquea scripts:

```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```

Luego vuelve a activar:

```powershell
.\.venv\Scripts\Activate.ps1
```

### 📥 Instalar dependencias

```bash
pip install -r requirements.txt
```

> Si aparece `ModuleNotFoundError: No module named 'pydantic_settings'`:

```bash
pip install pydantic-settings
```

> Nota: Si el modelo avisa versiones distintas de scikit-learn (warning), el servicio puede funcionar igual. Para igualar versión:

```bash
pip install scikit-learn==1.6.1
```

### ▶️ Levantar API Python

```bash
uvicorn main:app --reload
```

✅ Verifica:
- API: `http://127.0.0.1:8000`
- Health: `http://127.0.0.1:8000/health`

---

## ☕ Backend Java – Spring Boot (Puerto 8080)

### 📂 Ir a la carpeta

```bash
cd backend/churninsight
```

### ▶️ Levantar backend

```powershell
.\mvnw.cmd spring-boot:run
```

> (Opcional: más rápido, sin tests)

```powershell
.\mvnw.cmd spring-boot:run -DskipTests
```

✅ Verifica:
- Backend: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 🔁 Orden recomendado de ejecución

1. Levanta **API Python** (puerto **8000**)
2. Levanta **Spring Boot** (puerto **8080**)

El backend Java consulta `http://localhost:8000/health` y llama `POST /predict`.

---

## 🌐 Levantar el Frontend (index.html)

### 📂 Ir a la carpeta **frontend**

En el **directorio de frontend** con `index.html` \Hackthon_NoCountry_65-main\Hackthon_NoCountry_65_1501\frontend, sigue estos pasos para levantarlo:

1. Abre el archivo **`index.html`** en tu navegador.
   - Si tienes un **servidor web** como **Apache**, **Nginx**, o algo similar, puedes levantarlo con ese servidor.

2. **Si es un proyecto estático**, simplemente abre `index.html` directamente en tu navegador. Esto debería permitirte interactuar con el backend a través de la API expuesta.

> Si tienes algún problema con CORS (Cross-Origin Resource Sharing), asegúrate de que el **Backend Java (Spring Boot)** permita peticiones desde el frontend.

---

## 🧯 Problemas comunes

### ❌ No se puede activar `.venv`
- Asegúrate de haber creado el entorno:
  ```bash
  python -m venv .venv
  ```
- Si PowerShell bloquea scripts:
  ```powershell
  Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
  ```

### ❌ `ModuleNotFoundError`
Ejecuta:
```bash
pip install -r requirements.txt
```
o instala el paquete faltante:
```bash
pip install pydantic-settings
```

### ❌ Spring Boot no conecta a Python
Verifica que Python esté arriba:
- `http://127.0.0.1:8000/health` → debe responder 200

### ❌ Puerto ocupado (8000/8080)
Cierra procesos previos o cambia el puerto.
Ejemplo FastAPI:
```bash
uvicorn main:app --reload --port 8001
```

---

## ✅ Flujo recomendado de Git (equipo)

Antes de trabajar:
```bash
git pull --rebase
```

Subir cambios:
```bash
git add <tus archivos>
git commit -m "mensaje claro"
git push
```

---

## 🧹 Nota sobre archivos locales (ruido en Git)

Es normal ver `node_modules/` como *untracked* si existe localmente.
No debe subirse al repo. Se recomienda ignorarlo con `.gitignore`:

```
node_modules/
```

---

## 🏁 Estado esperado (cuando todo funciona)

- FastAPI arriba en `8000` (health OK)
- Spring Boot arriba en `8080`
- Spring Boot llama a FastAPI `/predict` (200 OK)
- Se guardan predicciones en PostgreSQL (Supabase)

```
Modelo cargado exitosamente
Application startup complete
Started ChurninsightApplication
Predicción exitosa
BUILD SUCCESS
```

