import pickle
from pathlib import Path
from typing import Optional
import pandas as pd
from fastapi import HTTPException

from app.config import settings, logger
from app.models import PredictRequest, PredictResponse, PredictionResult


class PredictionService:
    """
    Servicio encargado de realizar predicciones de churn.
    Maneja la carga del modelo y la lógica de inferencia.
    """
    
    # Columnas esperadas por el modelo en el orden correcto
    MODEL_COLUMNS = [
        "subscription_type", 
        "watch_hours", 
        "last_login_days", 
        "monthly_fee", 
        "number_of_profiles", 
        "avg_watch_time_per_day", 
        "payment_method"
    ]
    
    MODEL_VERSION = "v1.0.0"
    
    def __init__(self):
        self.model: Optional[object] = None
        self.model_path = Path(settings.model_path)
    
    def load_model(self) -> None:
        """Carga el modelo de ML desde el disco"""
        try:
            if not self.model_path.exists():
                raise FileNotFoundError(f"Modelo no encontrado en: {self.model_path}")
            
            with open(self.model_path, 'rb') as f:
                self.model = pickle.load(f)
            
            logger.info(f"✅ Modelo cargado exitosamente desde {self.model_path}")
            logger.info(f"Tipo de modelo: {type(self.model).__name__}")
            
        except Exception as e:
            logger.error(f"❌ Error crítico al cargar el modelo: {e}")
            raise
    
    def is_model_loaded(self) -> bool:
        """Verifica si el modelo está cargado"""
        return self.model is not None
    
    def get_model_info(self) -> dict:
        """Retorna información del modelo"""
        return {
            "version": self.MODEL_VERSION,
            "features": self.MODEL_COLUMNS,
            "model_type": type(self.model).__name__ if self.model else None,
            "loaded": self.is_model_loaded()
        }
    
    def predict(self, request: PredictRequest) -> PredictResponse:
        """
        Realiza la predicción de churn para un cliente.
        
        Args:
            request: Datos del cliente
            
        Returns:
            PredictResponse con la predicción
            
        Raises:
            HTTPException: Si el modelo no está disponible o hay un error en la predicción
        """
        if not self.is_model_loaded():
            logger.error("Intento de predicción sin modelo cargado")
            raise HTTPException(
                status_code=503, 
                detail="Modelo no disponible. El servicio está iniciando."
            )
        
        try:
            # Convertir features a diccionario
            input_dict = request.features.model_dump()
            
            # Crear DataFrame con el orden correcto de columnas
            data_df = pd.DataFrame([input_dict])[self.MODEL_COLUMNS]
            
            logger.debug(f"Datos de entrada para {request.customer_id}: {input_dict}")
            
            # Realizar predicción
            proba = self.model.predict_proba(data_df)[0, 1]
            
            # Determinar etiqueta
            label = "will_churn" if proba >= 0.5 else "will_continue"
            
            # Logging
            logger.info(
                f"Predicción - Cliente: {request.customer_id} | "
                f"Prob: {proba:.4f} | Label: {label}"
            )
            
            return PredictResponse(
                customer_id=request.customer_id,
                prediction=PredictionResult(
                    label=label,
                    probability=round(float(proba), 3)
                )
            )
            
        except ValueError as e:
            logger.error(f"Error de validación de datos para {request.customer_id}: {e}")
            raise HTTPException(
                status_code=422, 
                detail=f"Error en los datos de entrada: {str(e)}"
            )
        except Exception as e:
            logger.error(
                f"Error durante la inferencia para {request.customer_id}: {e}", 
                exc_info=True
            )
            raise HTTPException(
                status_code=500, 
                detail=f"Error procesando la predicción: {str(e)}"
            )


# Instancia global del servicio
prediction_service = PredictionService()
