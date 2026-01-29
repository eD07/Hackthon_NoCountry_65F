from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import List


class Settings(BaseSettings):
    """
    Configuración de la aplicación usando Pydantic Settings.
    Lee variables de entorno o usa valores por defecto.
    """
    
    # Server Configuration
    host: str = "0.0.0.0"
    port: int = 8000
    
    # Model Configuration
    model_path: str = "modelo.pkl"
    
    # CORS Configuration
    allowed_origins: str = "http://localhost:8080"
    
    # Logging
    log_level: str = "INFO"
    
    # Application Metadata
    app_name: str = "Netflix Churn Prediction Service"
    app_version: str = "1.0.0"
    
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding='utf-8',
        case_sensitive=False
    )
    
    @property
    def cors_origins_list(self) -> List[str]:
        """Convierte la cadena de orígenes CORS en una lista"""
        return [origin.strip() for origin in self.allowed_origins.split(",")]


# Instancia global de configuración
settings = Settings()
