from fastapi import APIRouter
from app.models import HealthCheckResponse
from app.services import prediction_service
from app.config import settings

router = APIRouter(prefix="/health", tags=["Health"])


@router.get("", response_model=HealthCheckResponse)
async def health_check():
    """
    Verifica el estado del servicio y del modelo
    
    Returns:
        Estado del servicio y del modelo ML
    """
    return HealthCheckResponse(
        status="healthy" if prediction_service.is_model_loaded() else "unhealthy",
        model_loaded=prediction_service.is_model_loaded(),
        service=settings.app_name
    )
