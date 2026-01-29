const root = document.body;
const BACKEND_URL =
  root.dataset.backendUrl || "http://localhost:8080/api/predict";
const HEALTH_URL = root.dataset.healthUrl || "http://localhost:8080/api/health";

const $ = (id) => document.getElementById(id);

let currentPage = 1;
const totalPages = 50;
const page_size = 10;

function toTitle(v) {
  return String(v ?? "—")
    .toLowerCase()
    .replaceAll("_", " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

function updatePagination() {
  const currentPageText = document.getElementById("currentPage");
  const btnPrev = document.getElementById("btnPrev");
  const btnNext = document.getElementById("btnNext");

  currentPageText.textContent = `Página ${currentPage}`;

  // Deshabilitar/activar botones
  btnPrev.disabled = currentPage === 1;
  btnNext.disabled = currentPage === totalPages;
}

function showAlert(type, msg) {
  const box = $("alertBox");

  // Verifica si es un error de "fetch"
  if (msg === "Failed to fetch") {
    msg =
      "El servicio está temporalmente fuera de servicio. Por favor intenta nuevamente más tarde."; // Mensaje personalizado
  }

  box.className = `alert alert-${type}`;
  box.textContent = msg;
  box.classList.remove("d-none");
}

function hideAlert() {
  $("alertBox").classList.add("d-none");
}

function setLoading(isLoading) {
  $("btnPredict").disabled = isLoading;
  $("btnSpinner").classList.toggle("d-none", !isLoading);
}

function setResultEmpty(isEmpty) {
  const empty = document.getElementById("resultEmpty");
  const content = document.getElementById("resultContent");
  if (!empty || !content) return;

  empty.classList.toggle("d-none", !isEmpty);
  content.classList.toggle("d-none", isEmpty);
}

function resetResultUI() {
  const ids = [
    "outCustomer",
    "outPrevision",
    "outProb",
    "outLabel",
    "riskText",
    "riskHint",
    "debugInfo",
  ];
  ids.forEach((id) => {
    const el = document.getElementById(id);
    if (!el) return;

    // ✅ debugInfo sin placeholder
    if (id === "debugInfo") {
      el.textContent = "";
      return;
    }

    el.textContent = "—";
  });

  const bar = document.getElementById("riskBar");
  if (bar) bar.style.width = "0%";
}

function clearAll() {
  // ocultar alert si existe
  const alertBox = document.getElementById("alertBox");
  if (alertBox) alertBox.classList.add("d-none");

  // reset form
  const form = document.getElementById("predictForm");
  if (form) {
    form.reset();
    form.classList.remove("was-validated");
  }

  // reset result
  resetResultUI();
  setResultEmpty(true);

  // foco al primer campo
  document.getElementById("customer_id")?.focus();
}

function formatProb(p) {
  if (p === undefined || p === null || Number.isNaN(Number(p))) return "—";
  const n = Number(p);
  return `${n.toFixed(3)} (${(n * 100).toFixed(1)}%)`;
}

function planFee(plan) {
  const map = { Basic: 8.99, Standard: 13.99, Premium: 17.99 };
  return map[plan];
}

function calcAvgWatchTimePerDay() {
  const watchEl = $("watch_hours");
  const daysEl = $("last_login_days");
  const avgEl = $("avg_watch_time_per_day");

  if (!watchEl || !daysEl || !avgEl) return;

  const watchHours = Number(watchEl.value);
  const lastLoginDays = Number(daysEl.value);

  if (
    !Number.isFinite(watchHours) ||
    watchHours < 0 ||
    !Number.isFinite(lastLoginDays) ||
    lastLoginDays < 0
  ) {
    avgEl.value = "";
    return;
  }

  // fórmula: watch_hours / (last_login_days + 1)
  const avg = watchHours / (lastLoginDays + 1);

  // opcional: limitar a 24 por seguridad visual
  const capped = Math.min(24, avg);

  avgEl.value = capped.toFixed(2);
}

// ✅ Ping PRO: el frontend consulta el health del BACKEND,
// y el backend decide si el ML está UP o DOWN.
async function pingHealth() {
  try {
    const r = await fetch(HEALTH_URL, { method: "GET" });
    if (!r.ok) throw new Error("Health not ok");

    const h = await r.json();
    // esperado: { status, backend, ml, ... }

    if (h.backend === "UP") {
      $("status-backend").innerHTML =
        `<i class="bi bi-circle-fill text-success"></i> Backend: OK`;
    } else {
      $("status-backend").innerHTML =
        `<i class="bi bi-circle-fill text-danger"></i> Backend: OFF`;
    }

    if (h.ml === "UP") {
      $("status-ml").innerHTML =
        `<i class="bi bi-circle-fill text-success"></i> ML: OK`;
    } else {
      // DEGRADED: backend UP pero ML DOWN
      $("status-ml").innerHTML =
        `<i class="bi bi-circle-fill text-danger"></i> ML: OFF`;
    }
  } catch {
    $("status-backend").innerHTML =
      `<i class="bi bi-circle-fill text-danger"></i> Backend: OFF`;
    $("status-ml").innerHTML =
      `<i class="bi bi-circle-fill text-danger"></i> ML: ?`;
  }
}

function randInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randFloat(min, max, decimals = 1) {
  const v = Math.random() * (max - min) + min;
  const p = Math.pow(10, decimals);
  return Math.round(v * p) / p;
}

function fillAuto() {
  // Escenario: 40% alto churn, 40% bajo churn, 20% mixto
  const r = Math.random();
  const scenario = r < 0.4 ? "high" : r < 0.8 ? "low" : "mixed";

  // Plan (validación adicional)
  const plans = ["Basic", "Standard", "Premium"];
  const planIndex = randInt(0, plans.length - 1); // Seguridad adicional aquí
  const plan = plans[planIndex] || "Basic"; // Si falla por alguna razón, "Basic" por defecto
  $("subscription_type").value = plan;

  // Fee consistente con el plan
  $("monthly_fee").value = planFee(plan).toFixed(2);

  // ID válido
  $("customer_id").value = `cli-${randInt(10000, 99999)}`;

  const select = $("payment_method");
  const values = [...select.options]
    .map((o) => o.value)
    .filter((v) => v !== "");

  select.value = values[randInt(0, values.length - 1)];

  // Escenarios

  // High churn (alto riesgo): 0–6 hrs, 20–60 días sin entrar, 1–2 perfiles
  //Low churn (bajo riesgo): 15–45 hrs, 1–5 días, 2–5 perfiles
  //Mixed: 6–25 hrs, 3–20 días, 1–4 perfiles
if (scenario === "high") {
  // Poco uso
  $("watch_hours").value = randFloat(0.0, 6.0, 1);
  $("last_login_days").value = randInt(20, 60);
  $("number_of_profiles").value = randInt(1, 2);

} else if (scenario === "low") {
  // Uso alto 
  $("watch_hours").value = randFloat(18.0, 45.0, 1);
  $("last_login_days").value = randInt(1, 5);
  $("number_of_profiles").value = randInt(2, 5);

} else {
  // Mixto
  $("watch_hours").value = randFloat(6.0, 25.0, 1);
  $("last_login_days").value = randInt(3, 20);
  $("number_of_profiles").value = randInt(1, 4);
}





  // ✅ Recalcular promedio diario
  calcAvgWatchTimePerDay();

  // UX: quitar marcas de validación previas
  $("predictForm").classList.remove("was-validated");
}

function readPayload() {
  const subscription = $("subscription_type").value;

  return {
    customer_id: $("customer_id").value.trim(),
    features: {
      subscription_type: subscription,
      payment_method: $("payment_method").value,
      watch_hours: Number($("watch_hours").value),
      last_login_days: Number($("last_login_days").value),
      monthly_fee: Number($("monthly_fee").value),
      number_of_profiles: Number($("number_of_profiles").value),
      avg_watch_time_per_day: Number($("avg_watch_time_per_day").value),
    },
  };
}

function labelToPrevision(label) {
  const v = String(label || "")
    .trim()
    .toLowerCase();
  if (v === "will_churn") return "Va a cancelar";
  if (v === "will_continue") return "Va a continuar";
  return "—";
}

function updateRiskMeter(probability) {
  const bar = document.getElementById("riskBar");
  const txt = document.getElementById("riskText");
  const hint = document.getElementById("riskHint");

  // Resetear el estado de la barra
  bar.style.width = "0%";
  bar.className = "progress-bar";
  txt.textContent = "—";
  hint.textContent = "—";

  // Validar la probabilidad
  if (
    probability === undefined ||
    probability === null ||
    Number.isNaN(Number(probability))
  ) {
    return "Riesgo no calculado"; // En caso de error, asignar un valor predeterminado
  }

  const churnRisk = Math.max(0, Math.min(1, Number(probability)));
  const churnPct = Math.round(churnRisk * 100);
  bar.style.width = `${churnPct}%`;

  let level = "Bajo";
  let klass = "bg-success";

  // Definir los niveles de riesgo y los colores de la barra
  if (churnRisk > 0.7) {
    level = "Alto";
    klass = "bg-danger";
  } else if (churnRisk >= 0.3) {
    level = "Moderado";
    klass = "bg-warning";
  } else {
    level = "Bajo";
    klass = "bg-success";
  }

  // Actualizar la clase de la barra
  bar.classList.add(klass);
  txt.textContent = `${level} (${churnPct}%)`;

  // Mensajes adicionales según el nivel de riesgo
  if (level === "Bajo") {
    hint.textContent = "Riesgo bajo de abandono según el modelo.";
  } else if (level === "Moderado") {
    hint.textContent =
      "Riesgo medio: conviene monitorear y aplicar retención ligera.";
  } else if (level === "Alto") {
    hint.textContent = "Riesgo alto: recomendar acción inmediata de retención.";
  }

  // Devolvemos el valor del riesgo para usarlo en el campo 'outPrevision'
  return level; // Regresa el valor del riesgo (Alto, Moderado, Bajo)
}

let LAST_HISTORY = [];

function renderResult(apiResponse) {
  // ✅ Mostrar contenido y ocultar estado vacío
  setResultEmpty(false);
  const data = apiResponse.data ?? {};
  const label = data.prediction?.label;
  const prob = data.prediction?.probability;

  $("outCustomer").textContent = data.customer_id ?? "—";
  $("outLabel").textContent = label ?? "—";
  $("outProb").textContent = formatProb(prob);

  const riskLevel = updateRiskMeter(prob);
  $("outPrevision").textContent = getPredictionText(label);

  // Cambiar el color de la previsión según su valor (continuará o cancelará)
  changePredictionColor($("outPrevision"), label);

  const riskBadgeEl = document.getElementById("riskBadgeMain");
  if (riskBadgeEl && riskLevel) {
    riskBadgeEl.innerHTML = riskBadge(riskLevel);
  }

  $("debugInfo").textContent =
    `Status: ${apiResponse.status} | Path: ${apiResponse.path}`;

  // === BOTÓN DETALLE ===
  const detailContainer = document.getElementById("resultDetailContainer");
  if (detailContainer) {
    detailContainer.innerHTML = `
      <button
        class="btn btn-sm btn-outline-secondary mt-2 mx-auto d-block"
        onclick="openCurrentPredictionDetail('${riskLevel}')">
        <i class="bi bi-eye me-1"></i> Ver detalles de esta predicción
      </button>
    `;
  }
}

function changePredictionColor(element, label) {
  if (label === "will_continue") {
    element.style.backgroundColor = "transparent";
    element.style.color = "var(--bs-success)";
  } else if (label === "will_churn") {
    element.style.backgroundColor = "transparent";
    element.style.color = "var(--bs-danger)";
  } else {
    element.style.backgroundColor = "transparent";
    element.style.color = "var(--bs-secondary)";
  }
}

function getPredictionText(label) {
  if (label === "will_continue") {
    return "Va a continuar";
  } else if (label === "will_churn") {
    return "Va a cancelar";
  }
  return "—"; // En caso de que no haya etiqueta
}

function openCurrentPredictionDetail(riskLevel) {
  // Convertir "Moderado" a "medio" solo para predictionLabel
  const modalRiskLabel = (() => {
    if (riskLevel === "Moderado") return "medio";
    return riskLevel ? riskLevel.toLowerCase() : null;
  })();

  const h = {
    customerId: $("customer_id").value,
    subscriptionType: $("subscription_type").value,
    paymentMethod: $("payment_method").value,
    monthlyFee: Number($("monthly_fee").value),
    watchHours: Number($("watch_hours").value),
    lastLoginDays: Number($("last_login_days").value),
    numberOfProfiles: Number($("number_of_profiles").value),
    avgWatchTimePerDay: Number($("avg_watch_time_per_day").value),

    // Guardamos la etiqueta real del modelo, no la "previsión" legible
    label:
      $("outLabel").textContent === "—"
        ? null
        : $("outLabel").textContent.trim(),

    //Usar modalRiskLabel para que sea "Riesgo medio" cuando sea moderado
    predictionLabel: modalRiskLabel ? "Riesgo " + modalRiskLabel : null,

    risk: riskLevel ?? "—",

    // Probabilidad como número entre 0 y 1
    probability: Number($("outProb").textContent.split("(")[0]),

    createdAt: new Date().toISOString(),
  };

  openHistoryDetail({
    dataset: { history: encodeURIComponent(JSON.stringify(h)) },
  });
}

document.getElementById("btnPrev").addEventListener("click", () => {
  if (currentPage > 1) {
    currentPage--; // Decrementa la página
    updatePagination(); // Actualiza la UI de paginación
    loadHistory(); // Carga los datos de la nueva página
  }
});

document.getElementById("btnNext").addEventListener("click", () => {
  if (currentPage < totalPages) {
    currentPage++; // Incrementa la página
    updatePagination(); // Actualiza la UI de paginación
    loadHistory(); // Carga los datos de la nueva página
  }
});

// ================= HISTORIAL =================

async function loadHistory() {
  const customerId =
    document.getElementById("historyCustomerId")?.value?.trim() || "";
  const start = document.getElementById("historyStartDate")?.value || ""; // YYYY-MM-DD
  const end = document.getElementById("historyEndDate")?.value || ""; // YYYY-MM-DD

  const page = currentPage - 1; // Usar la página dinámica
  const size = page_size; // Número de registros por página

  let url = "";
  let list = [];

  try {
    // Caso A: Fechas (requiere start y end)
    if (start && end) {
      const startDate = `${start} 00:00:00`;
      const endDate = `${end} 23:59:59`;

      const params = new URLSearchParams({
        startDate,
        endDate,
        page,
        size,
      });

      url = `http://localhost:8080/api/history/filter?${params.toString()}`;

      console.log("[HISTORY] usando FILTER (fechas)", url);

      const res = await fetch(url);
      if (!res.ok)
        throw new Error("Error al consultar historial (filtro por fecha)");

      const data = await res.json();
      list = Array.isArray(data?.content) ? data.content : data || [];

      // Si además hay customerId, filtramos en frontend
      if (customerId) {
        const cid = customerId.toLowerCase();
        list = list.filter((h) =>
          String(h.customerId || "")
            .toLowerCase()
            .includes(cid),
        );
      }
    }

    // Caso B: Solo cliente
    else if (customerId) {
      const params = new URLSearchParams({ page, size });
      url = `http://localhost:8080/api/history/${encodeURIComponent(customerId)}?${params.toString()}`;

      console.log("[HISTORY] usando POR CLIENTE", url);

      const res = await fetch(url);
      if (!res.ok)
        throw new Error("Error al consultar historial (por cliente)");

      const data = await res.json();
      list = Array.isArray(data?.content) ? data.content : data || [];
    }

    // Caso C: Sin filtros (últimos 20)
    else {
      const params = new URLSearchParams({ page, size });
      url = `http://localhost:8080/api/history?${params.toString()}`;

      console.log("[HISTORY] usando ULTIMOS", url);

      const res = await fetch(url);
      if (!res.ok) throw new Error("Error al consultar historial");

      const data = await res.json();
      list = Array.isArray(data?.content) ? data.content : data || [];
    }

    LAST_HISTORY = list;
    renderHistory(list);

    // Verificar si no hay registros en la página actual
    if (list.length === 0) {
      // Deshabilitar el botón "Siguiente" si no hay datos
      document.getElementById("btnNext").disabled = true;
      showAlert("warning", "No hay registros para mostrar en esta página.");
    } else {
      // Habilitar el botón "Siguiente" si hay datos
      document.getElementById("btnNext").disabled = false;
    }

    // Verificar si hay más registros en la siguiente página antes de habilitar "Siguiente"
    if (list.length < size) {
      // Si el número de registros es menor al tamaño de la página, deshabilitamos el botón "Siguiente"
      document.getElementById("btnNext").disabled = true;
    }

    // Verificar si estamos en la primera página y deshabilitar el botón "Anterior"
    document.getElementById("btnPrev").disabled = currentPage === 1;
  } catch (e) {
    console.error("[HISTORY] ERROR", e);
    showAlert("danger", e.message);
  }
}

function renderHistory(list) {
  const table = document.getElementById("historyTable");
  const body = document.getElementById("historyTbody");
  const empty = document.getElementById("historyEmpty");
  const startIndex = (currentPage - 1) * page_size;

  body.innerHTML = "";

  if (!list || list.length === 0) {
    table.classList.add("d-none");
    empty.classList.remove("d-none");
    return;
  }

  function riskBadge(label) {
    if (!label) return "-";
    if (label.toLowerCase().includes("alto"))
      return `<span class="badge bg-danger">Riesgo alto</span>`;
    if (label.toLowerCase().includes("medio"))
      return `<span class="badge bg-warning text-dark">Riesgo medio</span>`;
    return `<span class="badge bg-success">Riesgo bajo</span>`;
  }

  function resultClass(label) {
    if (!label) return "text-secondary";
    if (label === "will_churn") return "text-warning"; // más suave que rojo
    if (label === "will_continue") return "text-info"; // más suave que verde
    return "text-secondary";
  }

  function resultTextClass(label) {
    if (label === "will_churn") return "text-warning opacity-75 fw-semibold";
    if (label === "will_continue") return "text-info opacity-75 fw-semibold";
    return "text-secondary";
  }

  empty.classList.add("d-none");
  table.classList.remove("d-none");

  list.forEach((h, i) => {
    const safe = encodeURIComponent(JSON.stringify(h)); // ✅ NUEVA LÍNEA
    const rowNumber = startIndex + i + 1;
    const tr = document.createElement("tr");
    const d = new Date(h.createdAt);
    const fecha = d.toLocaleDateString("es-CL"); // 15-01-2026 (según config)
    const hora = d.toLocaleTimeString("es-CL", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    });

    tr.innerHTML = `
    <td>${rowNumber}</td>
    <td class="font-monospace">${h.customerId}</td>
    <td> <div>${fecha}</div> <small class="text-secondary">${hora}</small></td>
    <td>${riskBadge(h.predictionLabel)}</td>

    <td>
    <span class="${resultTextClass(h.label)}">
    ${labelToPrevision(h.label)}
    </span>
    </td>
  
   
    <td>${toTitle(h.subscriptionType)}</td>
    <td>${toTitle(h.paymentMethod)}</td>
    <td class="text-end">${(h.probability * 100).toFixed(1)}%</td>

    <td class="text-center">
    <button
      class="btn btn-sm btn-outline-secondary px-3"
      data-history="${safe}"
      onclick="openHistoryDetail(this)">
      <i class="bi bi-eye me-1"></i> Detalle
    </button>
    </td>
  `;
    body.appendChild(tr);
  });
}

function openHistoryDetail(btn) {
  const h = JSON.parse(decodeURIComponent(btn.dataset.history));

  const body = document.getElementById("historyDetailBody");

  const dt = new Date(h.createdAt);

  // Fecha compacta (1 línea)
  const whenStr =
    dt.toLocaleDateString("es-CL", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    }) +
    " · " +
    dt.toLocaleTimeString("es-CL", { hour: "2-digit", minute: "2-digit" });

  // Helpers de formato (si existen)
  const plan =
    typeof toTitle === "function"
      ? toTitle(h.subscriptionType)
      : (h.subscriptionType ?? "—");

  const pay =
    typeof toTitle === "function"
      ? toTitle(h.paymentMethod)
      : (h.paymentMethod ?? "—");

  const resultText =
    typeof labelToPrevision === "function"
      ? labelToPrevision(h.label)
      : (h.label ?? "—");

  const risk = h.predictionLabel ?? "—";
  const prob = (Number(h.probability ?? 0) * 100).toFixed(1) + "%";

  // Badge Resultado (suave)
  const resultBadgeClass =
    h.label === "will_continue"
      ? "badge-soft bg-success text-white"
      : h.label === "will_churn"
        ? "badge-soft bg-danger text-white"
        : "badge-soft bg-secondary text-white";

  // Badge Riesgo (alto/medio/bajo)
  const riskBadgeClass = (() => {
    const v = String(risk || "").toLowerCase();
    if (v.includes("alto")) return "badge-soft bg-danger text-white";
    if (v.includes("medio")) return "badge-soft badge-risk-medium";
    if (v.includes("bajo")) return "badge-soft bg-success text-white";
    return "badge-soft bg-secondary text-white";
  })();

  // Iconitos (si tienes Bootstrap Icons)
  const iconResult =
    h.label === "will_continue"
      ? "bi bi-check2-circle"
      : h.label === "will_churn"
        ? "bi bi-x-circle"
        : "bi bi-info-circle";

  const iconRisk = "bi bi-shield-exclamation";

  // Valores seguros
  const customerId = h.customerId ?? "—";
  const fee = h.monthlyFee ?? "—";

  body.innerHTML = `
  <!-- Header -->
  <div class="d-flex justify-content-between align-items-start gap-3 mb-3">
    <div>
      <div class="text-secondary small">Cliente</div>
      <div class="fs-3 fw-semibold">${customerId}</div>



    </div>

    <div class="text-end">
      <div class="text-secondary small">Fecha</div>
      <div class="text-secondary small">${whenStr}</div>
    </div>
  </div>

  <hr class="border-secondary my-3">

  <!-- 3 tarjetas -->
  <div class="row g-3">
    <!-- Suscripción -->
    <div class="col-md-4">
      <div class="p-3 rounded-3 border border-secondary-subtle bg-black bg-opacity-10 h-100">
        <div class="text-secondary small mb-2">Suscripción</div>

        <div class="d-flex justify-content-between align-items-center mb-2">
          <div class="text-secondary">Plan</div>
          <div class="fw-semibold">${plan}</div>
        </div>

        <div class="d-flex justify-content-between align-items-center mb-2">
          <div class="text-secondary">Tarifa</div>
          <div class="fw-semibold">$${fee}</div>
        </div>

        <div class="d-flex justify-content-between align-items-center">
          <div class="text-secondary">Pago</div>
          <div class="fw-semibold">${pay}</div>
        </div>
      </div>
    </div>

    <!-- Predicción -->
    <div class="col-md-4">
      <div class="p-3 rounded-3 border border-secondary-subtle bg-black bg-opacity-10 h-100">
        <div class="text-secondary small mb-2">Predicción</div>

        <!-- Resultado -->
        <div class="d-flex flex-wrap gap-2 align-items-center mb-3">
          <span class="${resultBadgeClass}">
            <i class="${iconResult} me-1"></i>${resultText}
          </span>
        </div>

        <!-- Riesgo -->
        <div class="text-secondary small mb-1">Riesgo</div>
        <div class="mb-3">
          <span class="${riskBadgeClass}">
            <i class="${iconRisk} me-1"></i>${risk}
          </span>
        </div>

        <!-- Probabilidad -->
        <div class="text-secondary small">Probabilidad</div>
        <div class="fs-3 fw-semibold">${prob}</div>
      </div>
    </div>

    <!-- Actividad -->
    <div class="col-md-4">
      <div class="p-3 rounded-3 border border-secondary-subtle bg-black bg-opacity-10 h-100">
        <div class="text-secondary small mb-2">Actividad</div>

        <div class="row g-2">
          <div class="col-6">
            <div class="text-secondary small">Horas vistas</div>
            <div class="fw-semibold">${h.watchHours ?? "—"}</div>
          </div>
          <div class="col-6">
            <div class="text-secondary small">Días sin acceso</div>
            <div class="fw-semibold">${h.lastLoginDays ?? "—"}</div>
          </div>
          <div class="col-6">
            <div class="text-secondary small">Perfiles</div>
            <div class="fw-semibold">${h.numberOfProfiles ?? "—"}</div>
          </div>
          <div class="col-6">
            <div class="text-secondary small">Prom. diario</div>
            <div class="fw-semibold">${h.avgWatchTimePerDay ?? "—"}</div>
          </div>
        </div>
      </div>
    </div>
  </div>

      <!-- Espacio para separación -->
    <div class="mt-3"></div>

  <!-- ================= SEÑALES DE COMPORTAMIENTO Y ACCIÓN SUGERIDA ================= -->
  <div id="actionSuggestedSenal" class="mb-4">
    <div class="p-3 rounded-3 border border-secondary-subtle bg-black bg-opacity-10">
      <h6 class="fw-semibold mb-2">Señales de comportamiento</h6>
      <ul id="riskFactorsList" class="list-group list-group-flush mb-3">
        <li class="list-group-item bg-dark text-secondary">
          Cargando señales de comportamiento ...
        </li>
      </ul>
    </div>

    <!-- Espacio para separación -->
    <div class="mt-3"></div>

    <div id="actionSuggestedSection" class="mb-4">
    <div class="p-3 rounded-3 border border-secondary-subtle bg-black bg-opacity-10">
      <h6 class="fw-semibold mb-1">Acción sugerida</h6>
      <p id="riskSuggestedAction" class="text-secondary mb-3">—</p>
    </div>
  </div>
  </div>
`;

  // Cargar señales de comportamiento
  loadRiskFactors(h.customerId);

  new bootstrap.Modal(document.getElementById("historyDetailModal")).show();
}

async function loadRiskFactors(customerId) {
  try {
    const res = await fetch(
      `http://localhost:8080/api/risk-factors/${customerId}`,
    );

    if (!res.ok) {
      throw new Error("No se pudieron obtener señales de comportamiento");
    }

    const data = await res.json();
    renderRiskFactors(data);
  } catch (e) {
    console.error(e);
    renderRiskFactors(null);
  }
}

function renderRiskFactors(data) {
  const list = document.getElementById("riskFactorsList");
  const action = document.getElementById("riskSuggestedAction");

  if (!list || !action) return;

  list.innerHTML = "";

  if (!data || !data.riskFactors || data.riskFactors.length === 0) {
    list.innerHTML = `
      <li class="list-group-item bg-dark text-secondary">
        No se identificaron factores relevantes
      </li>
    `;
    action.textContent = data?.suggestedAction ?? "—";
    return;
  }

  data.riskFactors.forEach((factor) => {
    const li = document.createElement("li");
    li.className = "list-group-item bg-dark text-white";
    li.textContent = factor;
    list.appendChild(li);
  });

  action.textContent = data.suggestedAction ?? "—";
}

function exportHistoryCSV() {
  if (!LAST_HISTORY || LAST_HISTORY.length === 0) {
    showAlert(
      "warning",
      "No hay datos para exportar. Primero presiona Buscar.",
    );
    return;
  }

  const rows = LAST_HISTORY.map((h) => ({
    customerId: h.customerId,
    createdAt: h.createdAt,
    subscriptionType: h.subscriptionType,
    paymentMethod: h.paymentMethod,
    monthlyFee: h.monthlyFee,
    watchHours: h.watchHours,
    lastLoginDays: h.lastLoginDays,
    numberOfProfiles: h.numberOfProfiles,
    avgWatchTimePerDay: h.avgWatchTimePerDay,
    label: h.label,
    predictionLabel: h.predictionLabel,
    probability: h.probability,
  }));

  const headers = Object.keys(rows[0]);
  const csv = [
    headers.join(","),
    ...rows.map((r) =>
      headers.map((k) => `"${String(r[k]).replaceAll('"', '""')}"`).join(","),
    ),
  ].join("\n");

  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);

  const a = document.createElement("a");
  a.href = url;
  a.download = `historial_churn_${new Date().toISOString().slice(0, 10)}.csv`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

function validateForm() {
  const form = $("predictForm");
  form.classList.add("was-validated");
  return form.checkValidity();
}

async function predict() {
  hideAlert();

  if (!validateForm()) {
    showAlert(
      "danger",
      "Revisa los campos marcados. Hay valores faltantes o inválidos.",
    );
    return;
  }

  setLoading(true);

  const payload = readPayload();

  try {
    const r = await fetch(BACKEND_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const json = await r.json();

    if (!r.ok) {
      const details = JSON.stringify(json.details ?? json, null, 2);
      showAlert(
        "danger",
        `Error (${json.status ?? r.status}): ${
          json.error ?? "Bad Request"
        }. Detalles: ${details}`,
      );
      return;
    }

    showAlert("success", "OK: predicción recibida correctamente.");
    renderResult(json);

    // ✅ refresca estado real (desde backend)
    await pingHealth();

    // ✅ refrescar historial (para que el nuevo salga primero)
    currentPage = 1;
    updatePagination();
    await loadHistory();
  } catch (e) {
    showAlert("warning", `No se pudo conectar al backend. (${e.message})`);
  } finally {
    setLoading(false);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  // ✅ Estado inicial del panel de Resultado
  setResultEmpty(true);
  resetResultUI();

  $("badgeEndpoint").textContent = `Endpoint: POST ${BACKEND_URL}`;
  $("endpointLabel").textContent = BACKEND_URL.replace(
    "http://localhost:8080",
    "",
  );

  // Llamada inicial a la paginación
  updatePagination();
  loadHistory();

  // Manejo de eventos
  document.getElementById("btnHistorySearch")?.addEventListener("click", () => {
    currentPage = 1;
    updatePagination();
    loadHistory();
  });

  document
    .getElementById("btnHistoryExport")
    ?.addEventListener("click", exportHistoryCSV);

  document.getElementById("btnClear")?.addEventListener("click", clearAll);

  pingHealth();
  setInterval(pingHealth, 5000);

  $("btnAuto").addEventListener("click", fillAuto);

  $("subscription_type").addEventListener("change", (e) => {
    const fee = planFee(e.target.value);
    if (fee !== undefined) $("monthly_fee").value = fee;
  });

  // ✅ recalcular promedio diario cuando cambian inputs
  $("watch_hours").addEventListener("input", calcAvgWatchTimePerDay);
  $("last_login_days").addEventListener("input", calcAvgWatchTimePerDay);

  // ✅ calcular al cargar por si hay valores precargados
  calcAvgWatchTimePerDay();

  document.getElementById("customer_id")?.focus();

  $("predictForm").addEventListener("submit", (e) => {
    e.preventDefault();
    predict();
  });

  async function loadKPIs() {
    try {
      const res = await fetch("http://localhost:8080/api/kpis");
      if (!res.ok) throw new Error("Error loading KPIs");
      const data = await res.json();

      $("kpiTotal").textContent = data.totalEvaluated ?? "—";
      $("kpiHigh").textContent = data.highRisk ?? "—";
      $("kpiMedium").textContent = data.mediumRisk ?? "—";
      $("kpiLow").textContent = data.lowRisk ?? "—";
      $("kpiChurnRate").textContent = data.churnRate?.toFixed(2) ?? "—";
    } catch (e) {
      console.error(e);
    }
  }

  // Llamar al cargar DOM
  loadKPIs();
});
