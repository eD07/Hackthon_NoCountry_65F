from pydantic import BaseModel


class PredictionResult(BaseModel):
    """Resultado de la predicción"""
    label: str
    probability: float


class PredictResponse(BaseModel):
    """Response de predicción de churn"""
    customer_id: str
    prediction: PredictionResult


class HealthCheckResponse(BaseModel):
    """Response para health check"""
    status: str
    model_loaded: bool
    service: str


class ModelInfoResponse(BaseModel):
    """Información del modelo"""
    version: str
    features: list[str]
    model_type: str
    loaded: bool
