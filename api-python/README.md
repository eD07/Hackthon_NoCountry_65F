# ChurnInsight - API Python

API de predicción de churn para clientes Netflix desarrollada con FastAPI.

## 🚀 Características

- ✅ Arquitectura modular y escalable
- ✅ Configuración con variables de entorno
- ✅ Logging estructurado
- ✅ Validación robusta con Pydantic
- ✅ Documentación automática (Swagger)
- ✅ Tests completos con pytest
- ✅ CORS configurado
- ✅ Health check endpoint

## 📁 Estructura del Proyecto

```
api-python/
├── app/
│   ├── __init__.py
│   ├── main.py              # Aplicación FastAPI
│   ├── config/
│   │   ├── settings.py      # Configuración con Pydantic Settings
│   │   └── logging_config.py
│   ├── models/
│   │   ├── request.py       # Modelos de request
│   │   └── response.py      # Modelos de response
│   ├── services/
│   │   └── prediction_service.py  # Lógica de ML
│   └── routers/
│       ├── health.py        # Health check
│       └── prediction.py    # Predicciones
├── tests/
│   └── test_api.py
├── .env.example
├── .env
├── requirements.txt
├── modelo.pkl
└── main.py                  # Entry point
```

## 🛠️ Instalación

1. **Crear entorno virtual**:
```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
.\venv\Scripts\activate   # Windows
```

2. **Instalar dependencias**:
```bash
pip install -r requirements.txt
```

3. **Configurar variables de entorno**:
```bash
cp .env.example .env
# Editar .env según necesidades
```

## ▶️ Ejecución

### Modo desarrollo
```bash
python main.py
```

O con uvicorn directamente:
```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### Producción
```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

## 📚 Documentación

- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc
- **OpenAPI JSON**: http://localhost:8000/openapi.json

## 🧪 Tests

Ejecutar tests:
```bash
pytest tests/ -v
```

Con coverage:
```bash
pytest tests/ -v --cov=app
```

## 📡 Endpoints

### Health Check
```http
GET /health
```

### Información del Modelo
```http
GET /model/info
```

### Predicción
```http
POST /predict
Content-Type: application/json

{
  "customer_id": "customer-123",
  "features": {
    "subscription_type": "Premium",
    "watch_hours": 150.5,
    "last_login_days": 5,
    "monthly_fee": 17.99,
    "number_of_profiles": 3,
    "avg_watch_time_per_day": 5.5,
    "payment_method": "Credit Card"
  }
}
```

## 🔐 Variables de Entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `HOST` | Host del servidor | `0.0.0.0` |
| `PORT` | Puerto del servidor | `8000` |
| `MODEL_PATH` | Ruta al modelo ML | `modelo.pkl` |
| `ALLOWED_ORIGINS` | Orígenes CORS permitidos | `http://localhost:8080` |
| `LOG_LEVEL` | Nivel de logging | `INFO` |

## 🐳 Docker (futuro)

```dockerfile
# Dockerfile incluido en próxima iteración
```

## 📝 Licencia

MIT License
