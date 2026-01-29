import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_root_endpoint():
    """Test del endpoint raíz"""
    response = client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert "service" in data
    assert data["status"] == "running"


def test_health_check():
    """Test del health check"""
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert "status" in data
    assert "model_loaded" in data
    assert data["model_loaded"] is True


def test_model_info():
    """Test de información del modelo"""
    response = client.get("/model/info")
    assert response.status_code == 200
    data = response.json()
    assert "version" in data
    assert "features" in data
    assert "loaded" in data


def test_predict_valid_request():
    """Test de predicción con request válido"""
    payload = {
        "customer_id": "test-123",
        "features": {
            "subscription_type": "Basic",
            "watch_hours": 10.5,
            "last_login_days": 60,
            "monthly_fee": 8.99,
            "number_of_profiles": 1,
            "avg_watch_time_per_day": 0.5,
            "payment_method": "Credit Card"
        }
    }
    
    response = client.post("/predict", json=payload)
    assert response.status_code == 200
    
    data = response.json()
    assert data["customer_id"] == "test-123"
    assert "prediction" in data
    assert "label" in data["prediction"]
    assert "probability" in data["prediction"]
    assert data["prediction"]["label"] in ["will_churn", "will_continue"]
    assert 0 <= data["prediction"]["probability"] <= 1


def test_predict_invalid_subscription_type():
    """Test de predicción con tipo de suscripción inválido"""
    payload = {
        "customer_id": "test-456",
        "features": {
            "subscription_type": "Invalid Type",
            "watch_hours": 100,
            "last_login_days": 5,
            "monthly_fee": 13.99,
            "number_of_profiles": 2,
            "avg_watch_time_per_day": 3.5,
            "payment_method": "PayPal"
        }
    }
    
    response = client.post("/predict", json=payload)
    assert response.status_code == 422  # Validation error


def test_predict_negative_values():
    """Test de predicción con valores negativos"""
    payload = {
        "customer_id": "test-789",
        "features": {
            "subscription_type": "Premium",
            "watch_hours": -10,  # Valor inválido
            "last_login_days": 5,
            "monthly_fee": 17.99,
            "number_of_profiles": 3,
            "avg_watch_time_per_day": 2.5,
            "payment_method": "Credit Card"
        }
    }
    
    response = client.post("/predict", json=payload)
    assert response.status_code == 422


def test_predict_invalid_profiles_count():
    """Test de predicción con número de perfiles fuera de rango"""
    payload = {
        "customer_id": "test-101",
        "features": {
            "subscription_type": "Standard",
            "watch_hours": 50,
            "last_login_days": 10,
            "monthly_fee": 13.99,
            "number_of_profiles": 10,  # Máximo es 5
            "avg_watch_time_per_day": 2.0,
            "payment_method": "Debit Card"
        }
    }
    
    response = client.post("/predict", json=payload)
    assert response.status_code == 422
