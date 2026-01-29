# 📜 Contrato de Integración – Predicción de Churn
### 🎯 Objetivo

Definir el estándar de comunicación entre el Backend (Java / Spring Boot) y el servicio de Data Science (Python / FastAPI) para la predicción de Churn en la plataforma Netflix.

Este contrato establece:

- El formato de intercambio de datos
- Las responsabilidades de cada equipo
- Las reglas de validación
- El manejo de errores y excepciones

---

### 🛠️ Responsabilidades por Equipo

- **Backend (Java):** 
    - Gestionar y validar el `customer_id`
    - Validar rangos y tipos de datos
    - Manejar errores de red (timeouts) 
    - Asegurar la disponibilidad del API frente al cliente final.    
    

- **Data Science (Python):**  
    - Transformar variables categóricas
    - Ejecutar el modelo de inferencia y
    - Garantizar que la probabilidad retornada esté en el rango [0, 1].

---

### 🔗 Endpoint de Predicción

- URL: http://localhost:8000/predict
- Método: POST
- Content-Type: application/json

---

### 📥 Solicitud (Backend → Data Science)

El Backend debe enviar los datos respetando exactamente las mayúsculas y minúsculas del dataset original (case sensitive).

```json
{
  "customer_id": "4d71f6ce-fca9-4ff7-8afa-197ac24de14b",
  "features": {
    "subscription_type": "Standard",
    "watch_hours": 16.32,
    "last_login_days": 10,
    "monthly_fee": 13.99,
    "number_of_profiles": 2,
    "avg_watch_time_per_day": 1.48,
    "payment_method": "Crypto"
  }
}
```

### 📋 Diccionario de Datos y Reglas (Validación)

| Campo                    | Tipo    | Valores Permitidos / Reglas                                  | Descripción                                 |
| ------------------------ | ------- | ------------------------------------------------------------ | ------------------------------------------- |
| `subscription_type`      | String  | `Basic`, `Standard`, `Premium`                               | Plan contratado (sensible a mayúsculas).    |
| `watch_hours`            | Double  | Valor ≥ 0.0                                                  | Total de horas de visualización acumuladas. |
| `last_login_days`        | Integer | Valor ≥ 0                                                    | Días desde el último acceso.                |
| `monthly_fee`            | Double  | `8.99`, `13.99`, `17.99`                                     | Costo mensual según el plan.                |
| `payment_method`         | String  | `Credit Card`, `Debit Card`, `PayPal`, `Gift Card`, `Crypto` | Método de pago registrado.                  |
| `number_of_profiles`     | Integer | Rango de 1 a 5                                               | Perfiles creados en la cuenta.              |
| `avg_watch_time_per_day` | Double  | Valor ≥ 0.0                                                  | Promedio diario de uso de la plataforma.    |

---

### 📤 Respuesta (Data Science → Backend)
El servicio de ML responde con la predicción calculada y su probabilidad asociada.

```json
{
  "customer_id": "4d71f6ce-fca9-4ff7-8afa-197ac24de14b",
  "prediction": {
    "label": "will_churn",
    "probability": 0.91
  }
}
```
### 📌 Definiciones de Salida
- **label:** Resultado categórico de la predicción.  
  Valores permitidos:
    - `will_churn`
    - `will_continue`

- **probability:** Probabilidad asociada a la predicción.  
  Rango válido: `0.0` – `1.0`

---

### 🛑 Protocolo de Errores

El cumplimiento de estos códigos HTTP es **obligatorio** para garantizar la estabilidad de la integración entre **Backend (Java)** y **Machine Learning (Python)**.

| Código HTTP | Uso                                            |
| ----------- | ---------------------------------------------- |
| 400         | Solicitud inválida o predicción no procesable. |
