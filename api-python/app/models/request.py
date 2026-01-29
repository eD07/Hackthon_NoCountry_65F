from pydantic import BaseModel, Field
from typing import Literal


class CustomerFeatures(BaseModel):
    """Características del cliente para predicción de churn"""
    
    subscription_type: Literal["Basic", "Standard", "Premium"] = Field(
        ..., 
        description="Tipo de suscripción del cliente"
    )
    watch_hours: float = Field(
        ..., 
        ge=0, 
        description="Horas totales vistas"
    )
    last_login_days: int = Field(
        ..., 
        ge=0, 
        description="Días desde el último login"
    )
    monthly_fee: float = Field(
        ..., 
        ge=0, 
        description="Tarifa mensual"
    )
    number_of_profiles: int = Field(
        ..., 
        ge=1, 
        le=5, 
        description="Número de perfiles (1-5)"
    )
    avg_watch_time_per_day: float = Field(
        ..., 
        ge=0, 
        le=24, 
        description="Tiempo promedio de visualización por día en horas"
    )
    payment_method: Literal["Credit Card", "Debit Card", "PayPal", "Gift Card", "Crypto"] = Field(
        ..., 
        description="Método de pago"
    )


class PredictRequest(BaseModel):
    """Request para predicción de churn"""
    
    customer_id: str = Field(
        ..., 
        min_length=1, 
        description="ID único del cliente"
    )
    features: CustomerFeatures
