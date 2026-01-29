"""
Punto de entrada principal de la aplicación.
"""
import uvicorn
from app.main import app
from app.config import settings


if __name__ == "__main__":
    uvicorn.run(
        app, 
        host=settings.host, 
        port=settings.port,
        log_level=settings.log_level.lower()
    )