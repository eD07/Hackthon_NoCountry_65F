import logging
import sys
from app.config.settings import settings


def setup_logging():
    """
    Configura el sistema de logging de la aplicación
    """
    log_level = getattr(logging, settings.log_level.upper(), logging.INFO)
    
    logging.basicConfig(
        level=log_level,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[
            logging.StreamHandler(sys.stdout)
        ]
    )
    
    # Configurar loggers específicos
    logger = logging.getLogger("app")
    logger.setLevel(log_level)
    
    # Reducir verbosidad de uvicorn
    logging.getLogger("uvicorn.access").setLevel(logging.WARNING)
    
    return logger


# Logger global de la aplicación
logger = setup_logging()
