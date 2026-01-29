"""
Aplicación FastAPI para predicción de churn de clientes Netflix.
Arquitectura modular con separación de responsabilidades.
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager

from app.config import settings, logger
from app.services import prediction_service
from app.routers import health, prediction


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Gestor del ciclo de vida de la aplicación.
    Ejecuta código en startup y shutdown.
    """
    # Startup
    logger.info(f"🚀 Iniciando {settings.app_name} v{settings.app_version}")
    logger.info(f"Host: {settings.host}, Puerto: {settings.port}")
    logger.info(f"Orígenes CORS permitidos: {settings.cors_origins_list}")
    
    # Cargar modelo de ML
    prediction_service.load_model()
    
    yield
    
    # Shutdown
    logger.info("🛑 Deteniendo el servicio...")


# Crear aplicación FastAPI
app = FastAPI(
    title=settings.app_name,
    description="API de predicción de abandono de clientes (churn) para Netflix",
    version=settings.app_version,
    lifespan=lifespan
)

# Configurar CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins_list,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Registrar routers
app.include_router(health.router)
app.include_router(prediction.router)


@app.get("/")
async def root():
    """Endpoint raíz con información básica del servicio"""
    return {
        "service": settings.app_name,
        "version": settings.app_version,
        "status": "running",
        "docs": "/docs",
        "health": "/health"
    }
