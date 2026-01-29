from fastapi import APIRouter
from app.models import PredictRequest, PredictResponse, ModelInfoResponse
from app.services import prediction_service
from app.config import logger

router = APIRouter(prefix="", tags=["Prediction"])


@router.post("/predict", response_model=PredictResponse)
async def predict(request: PredictRequest):
    """
    Predice si un cliente abandonará el servicio (churn)
    
    Args:
        request: Datos del cliente con sus características
    
    Returns:
        - customer_id: ID del cliente
        - prediction:
            - label: "will_churn" o "will_continue"
            - probability: Probabilidad de churn (0-1)
    """
    logger.info(f"Solicitud de predicción recibida para cliente: {request.customer_id}")
    
    response = prediction_service.predict(request)
    
    logger.info(
        f"Predicción completada para {request.customer_id}: "
        f"{response.prediction.label} ({response.prediction.probability})"
    )
    
    return response


@router.get("/model/info", response_model=ModelInfoResponse)
async def model_info():
    """
    Información del modelo actual
    
    Returns:
        Metadatos del modelo incluyendo versión y características
    """
    info = prediction_service.get_model_info()
    return ModelInfoResponse(**info)
