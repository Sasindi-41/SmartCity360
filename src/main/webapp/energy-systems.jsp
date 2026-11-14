<%@ page contentType="text/html;charset=UTF-8"
         language="java"
         isELIgnored="true" %>
<%
    String contextPath = request.getContextPath();
    request.setAttribute("activePage", "energy");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartCity360 | Energy Systems</title>

    <link rel="stylesheet" href="<%= contextPath %>/css/living-city.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/energy-systems.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/dashboard.css">
</head>

<body>
<div class="app-layout">

    <jsp:include page="/includes/sidebar.jsp" />

    <main class="main-shell">

        <header class="topbar">
            <div class="topbar-heading">
                <span class="breadcrumb">SMARTCITY360 / ENERGY / DECORATOR</span>
                <h1>Energy systems</h1>
            </div>

            <div class="topbar-actions">

                <div class="environment-chip" id="connectionIndicator">
                    <span class="status-dot" id="connectionDot"></span>
                    <span id="connectionText">CONNECTING TO DATABASE</span>
                </div>


                <button class="help-button"
                        type="button"
                        onclick="showHelp()"
                        aria-label="Help">?</button>
            </div>
        </header>

        <div class="content-scroll">
            <div class="page-content energy-page">

                <section class="energy-intro">

                    <div class="energy-intro-copy">
                        <div class="energy-eyebrow">
                            <span class="energy-eyebrow-dot"></span>
                            PART A · DECORATOR DESIGN PATTERN
                        </div>

                        <h2>Smart energy monitoring</h2>

                        <p>
                            Monitor registered energy meters and securely process
                            incoming energy readings using independently configurable
                            Decorator layers.
                        </p>

                        <div class="energy-intro-tags">
                            <span>LIVE METER MONITORING</span>
                            <span>SECURE STREAM PROCESSING</span>
                            <span>DATA EFFICIENCY</span>
                        </div>
                    </div>

                    <div class="energy-intro-visual">
                        <div class="energy-orbit orbit-one"></div>
                        <div class="energy-orbit orbit-two"></div>

                        <div class="energy-core">
                            <span class="energy-bolt">ϟ</span>
                            <small>ENERGY</small>
                        </div>

                        <span class="orbit-node node-one"></span>
                        <span class="orbit-node node-two"></span>
                        <span class="orbit-node node-three"></span>

                        <div class="visual-caption">
                            <span class="energy-live-dot"></span>
                            STREAM PROTECTION
                        </div>
                    </div>

                </section>

                <section class="energy-metrics">

                    <article class="energy-metric-card">
                        <div class="energy-metric-heading">
                            <span>REGISTERED METERS</span>
                            <span class="energy-metric-icon">⌁</span>
                        </div>

                        <strong id="meterCount">—</strong>

                        <div class="energy-metric-foot">
                            <span class="metric-indicator"></span>
                            Registered in the city database
                        </div>
                    </article>

                    <article class="energy-metric-card">
                        <div class="energy-metric-heading">
                            <span>ACTIVE METERS</span>
                            <span class="energy-metric-icon">ϟ</span>
                        </div>

                        <strong id="activeMeterCount">—</strong>

                        <div class="energy-metric-foot">
                            Currently marked as operational
                        </div>
                    </article>

                    <article class="energy-metric-card">
                        <div class="energy-metric-heading">
                            <span>SECURITY LAYERS</span>
                            <span class="energy-metric-icon">⬡</span>
                        </div>

                        <strong id="layerCount">03</strong>

                        <div class="energy-metric-foot">
                            Authentication · Compression · Encryption
                        </div>
                    </article>

                    <article class="energy-metric-card">
                        <div class="energy-metric-heading">
                            <span>PIPELINE STATUS</span>
                            <span class="energy-metric-icon">◉</span>
                        </div>

                        <strong class="metric-status-text" id="pipelineStatus">
                            LOADING
                        </strong>

                        <div class="energy-metric-foot">
                            <span class="metric-indicator"></span>
                            <span id="pipelineStatusCaption">Checking energy service</span>
                        </div>
                    </article>

                </section>

                <section class="energy-workspace">

                    <article class="energy-panel meter-panel">

                        <div class="energy-panel-heading">
                            <div>
                                <span class="energy-section-label">DEVICE NETWORK</span>
                                <h3>Energy meter directory</h3>
                                <p>Live meter information from the city database.</p>
                            </div>

                            <div class="meter-heading-actions">
                                <span class="energy-count-badge" id="meterDirectoryBadge">
                                    DATABASE
                                </span>
                                <button type="button" class="energy-add-meter-button"
                                        id="toggleMeterFormButton">
                                    <span>＋</span> Add energy meter
                                </button>
                            </div>
                        </div>

                        <div class="meter-modal-backdrop" id="meterModalBackdrop" hidden>
                            <section class="meter-modal" role="dialog" aria-modal="true"
                                     aria-labelledby="meterModalTitle">
                                <form class="meter-registration-form" id="meterRegistrationForm">
                                    <div class="meter-registration-heading">
                                        <div>
                                            <span class="energy-section-label">DEVICE MANAGEMENT</span>
                                            <strong id="meterModalTitle">Register a new energy meter</strong>
                                            <small id="meterModalSubtitle">Serial number and secure credentials are generated automatically.</small>
                                        </div>
                                        <button type="button" class="meter-form-close" id="closeMeterModalButton" aria-label="Close">×</button>
                                    </div>
                                    <input type="hidden" id="meterFormMode" value="create">
                                    <input type="hidden" id="editingMeterId">
                                    <div class="meter-registration-grid">
                                        <div class="energy-form-field">
                                            <label for="meterSerialDisplay">SERIAL NUMBER</label>
                                            <input type="text" id="meterSerialDisplay" value="Generated automatically" readonly>
                                            <small>Format: EM-SC360-001, EM-SC360-002, and so on.</small>
                                        </div>
                                        <div class="energy-form-field">
                                            <label for="newMeterType">METER TYPE</label>
                                            <select id="newMeterType" required>
                                                <option value="">Loading meter types...</option>
                                            </select>
                                        </div>
                                        <div class="energy-form-field">
                                            <label for="newMeterBuilding">ASSIGNED BUILDING</label>
                                            <select id="newMeterBuilding" required>
                                                <option value="">Loading buildings...</option>
                                            </select>
                                        </div>
                                        <div class="energy-form-field">
                                            <label for="newMeterDate">INSTALLATION DATE <span>(OPTIONAL)</span></label>
                                            <input type="date" id="newMeterDate">
                                        </div>
                                        <div class="energy-form-field meter-status-field" id="meterStatusField" hidden>
                                            <label for="newMeterStatus">METER STATUS</label>
                                            <select id="newMeterStatus">
                                                <option value="">Loading statuses...</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="meter-registration-footer">
                                        <span id="meterModalHint">New meters are registered as ACTIVE. A secure token is generated and stored safely.</span>
                                        <div class="meter-modal-actions">
                                            <button type="button" class="meter-cancel-button" id="cancelMeterModalButton">Cancel</button>
                                            <button type="submit" class="energy-add-meter-submit" id="registerMeterButton">Register meter</button>
                                        </div>
                                    </div>
                                    <div class="meter-registration-message" id="meterRegistrationMessage" role="status" aria-live="polite" hidden></div>
                                </form>
                            </section>
                        </div>

                        <div class="meter-toolbar">

                            <div class="energy-search">
                                <span>⌕</span>
                                <input type="search"
                                       id="meterSearch"
                                       placeholder="Search meter or location..."
                                       autocomplete="off">
                            </div>

                            <select id="meterFilter" aria-label="Filter meters">
                                <option value="all">All meters</option>
                                <option value="active">Active</option>
                                <option value="warning">Warning</option>
                                <option value="offline">Offline</option>
                            </select>

                        </div>

                        <div class="meter-table-wrap">
                            <table class="meter-table">

                                <thead>
                                <tr>
                                    <th>METER</th>
                                    <th>LOCATION</th>
                                    <th>TYPE</th>
                                    <th>STATUS</th>
                                    <th>ACTIONS</th>
                                </tr>
                                </thead>

                                <tbody id="meterTableBody">
                                <tr>
                                    <td colspan="5" class="meter-empty">
                                        Loading registered meters...
                                    </td>
                                </tr>
                                </tbody>

                            </table>

                            <div class="meter-empty" id="meterEmpty" hidden>
                                No meters match your search.
                            </div>
                        </div>

                        <div class="meter-panel-footer">
                            <span id="meterFooterCount">Loading meter records...</span>
                            <span>Source: Energy meter database</span>
                        </div>

                    </article>

                    <article class="energy-panel security-panel">

                        <div class="energy-panel-heading">
                            <div>
                                <span class="energy-section-label">PROTECTION CONTROL</span>
                                <h3>Security configuration</h3>
                                <p>Configure and execute the stream Decorators.</p>
                            </div>

                            <span class="configuration-icon">⌘</span>
                        </div>

                        <form id="energyProcessForm">
                            <div class="stream-result"
                                 id="processMessage"
                                 role="alert"
                                 aria-live="assertive"
                                 hidden>
                                <span class="result-indicator"></span>
                                <span id="processMessageText"></span>
                            </div>
                            <div class="energy-form-fields">

                                <div class="energy-form-field">
                                    <label for="meterSelect">ENERGY METER</label>

                                    <select id="meterSelect" required>
                                        <option value="">Loading registered meters...</option>
                                    </select>

                                    <small>Select the meter sending the reading.</small>
                                </div>

                                <div class="energy-form-field">
                                    <label for="meterToken">AUTHENTICATION TOKEN</label>

                                    <input type="password"
                                           id="meterToken"
                                           placeholder="Select an energy meter to load its token"
                                           autocomplete="new-password"
                                           readonly
                                           required>

                                    <small id="meterTokenHelp">
                                        Select a registered meter. Its protected
                                        authentication token will load automatically.
                                    </small>
                                </div>

                                <div class="energy-reading-fields">

                                    <div class="energy-form-field">
                                        <label for="readingValue">ENERGY READING</label>

                                        <input type="number"
                                               id="readingValue"
                                               min="0"
                                               max="9999999999.9999"
                                               step="0.0001"
                                               placeholder="e.g. 125.7500"
                                               required>
                                    </div>

                                    <div class="energy-form-field">
                                        <label for="readingUnit">UNIT</label>

                                        <select id="readingUnit">
                                            <option value="kWh">kWh</option>
                                            <option value="Wh">Wh</option>
                                            <option value="MWh">MWh</option>
                                        </select>
                                    </div>

                                </div>

                            </div>


                            <div class="security-layer-list">

                                <label class="security-layer-option mandatory-layer">
                                    <span class="layer-icon authentication-icon">✓</span>

                                    <span class="layer-copy">
                                        <strong>Authentication</strong>
                                        <small>
                                            Verify the identity and source of meter data.
                                        </small>
                                        <em>AUTHENTICATION DECORATOR · ALWAYS ACTIVE</em>
                                    </span>

                                    <span class="required-label">REQUIRED</span>
                                </label>

                                <label class="security-layer-option">
                                    <span class="layer-icon compression-icon">⇣</span>

                                    <span class="layer-copy">
                                        <strong>Compression</strong>
                                        <small>
                                            Reduce the amount of data using DEFLATE.
                                        </small>
                                        <em>COMPRESSION DECORATOR</em>
                                    </span>

                                    <input type="checkbox"
                                           id="compression"
                                           checked>

                                    <span class="custom-switch"></span>
                                </label>

                                <label class="security-layer-option">
                                    <span class="layer-icon encryption-icon">⌑</span>

                                    <span class="layer-copy">
                                        <strong>Encryption</strong>
                                        <small>
                                            Protect stream contents using AES-256-GCM.
                                        </small>
                                        <em>ENCRYPTION DECORATOR</em>
                                    </span>

                                    <input type="checkbox"
                                           id="encryption"
                                           checked>

                                    <span class="custom-switch"></span>
                                </label>

                            </div>


                            <div class="security-selection-summary">
                                <div>
                                    <span>ACTIVE DECORATOR LAYERS</span>
                                    <strong id="selectedLayerCount">3 layers enabled</strong>
                                </div>

                                <span class="selection-shield">⬡</span>
                            </div>


                            <button class="energy-primary-button"
                                    type="submit"
                                    id="processButton">

                                <span>Process energy stream</span>
                                <span>→</span>

                            </button>

                            <div class="stream-result"
                                 id="processMessage"
                                 role="status"
                                 aria-live="polite"
                                 hidden>
                                <span class="result-indicator"></span>
                                <span id="processMessageText"></span>
                            </div>

                            <p class="configuration-note">
                                Authentication is mandatory. Compression and encryption
                                can be enabled or disabled before processing.
                            </p>

                        </form>

                    </article>

                </section>

                <section class="energy-panel pipeline-panel">

                    <div class="energy-panel-heading pipeline-heading">
                        <div>
                            <span class="energy-section-label">DECORATOR ARCHITECTURE</span>
                            <h3>Stream security pipeline</h3>
                            <p>
                                Each selected Decorator adds behaviour around the
                                original energy stream.
                            </p>
                        </div>

                        <span class="pipeline-mode" id="pipelineMode">
                            3 DECORATORS ACTIVE
                        </span>
                    </div>


                    <div class="pipeline-flow" id="pipelineFlow">

                        <div class="pipeline-node source-node">
                            <span class="pipeline-node-icon">ϟ</span>
                            <strong>Energy meter</strong>
                            <small>Original data source</small>
                            <span class="pipeline-node-type">CORE COMPONENT</span>
                        </div>

                        <span class="pipeline-connector"></span>

                        <div class="pipeline-node decorator-node layer-enabled"
                             data-layer="authentication">

                            <span class="pipeline-node-icon">✓</span>
                            <strong>Authentication</strong>
                            <small>Verify source identity</small>
                            <span class="pipeline-node-type">REQUIRED</span>

                        </div>

                        <span class="pipeline-connector"></span>

                        <div class="pipeline-node decorator-node layer-enabled"
                             data-layer="compression">

                            <span class="pipeline-node-icon">⇣</span>
                            <strong>Compression</strong>
                            <small>DEFLATE data</small>
                            <span class="pipeline-node-type">DECORATOR</span>

                        </div>

                        <span class="pipeline-connector"></span>

                        <div class="pipeline-node decorator-node layer-enabled"
                             data-layer="encryption">

                            <span class="pipeline-node-icon">⌑</span>
                            <strong>Encryption</strong>
                            <small>AES-256-GCM</small>
                            <span class="pipeline-node-type">DECORATOR</span>

                        </div>

                        <span class="pipeline-connector"></span>

                        <div class="pipeline-node output-node">
                            <span class="pipeline-node-icon">◎</span>
                            <strong>Protected stream</strong>
                            <small>Database storage</small>
                            <span class="pipeline-node-type">OUTPUT</span>
                        </div>

                    </div>

                    <div class="pipeline-caption">
                        <span class="caption-dot"></span>
                        <span id="pipelineDescription">
                            Energy meter → Authentication → Compression → Encryption → Protected stream.
                        </span>
                    </div>

                </section>

                <section class="energy-lower-grid">

                    <article class="energy-panel stream-test-panel">

                        <div class="energy-panel-heading">
                            <div>
                                <span class="energy-section-label">STREAM LABORATORY</span>
                                <h3>Processing result</h3>
                                <p>
                                    Actual output returned by the Java Decorator service.
                                </p>
                            </div>

                            <span class="lab-icon">⌁</span>
                        </div>


                        <div class="stream-source-card">

                            <div class="stream-source-top">
                                <span>PROCESSED STREAM</span>
                                <span class="stream-sample-tag" id="resultTag">
                                    AWAITING DATA
                                </span>
                            </div>

                            <code id="streamPayload">
                                No energy stream has been processed yet.
                            </code>

                            <div class="stream-source-meta">
                                <span id="resultMeter">Source: —</span>
                                <span id="resultRecord">Record: —</span>
                            </div>

                        </div>


                        <div class="stream-processing-list" id="processingSteps">

                            <div class="processing-empty">
                                Submit an energy reading to see the actual
                                Decorator execution steps.
                            </div>

                        </div>


                        <div class="stream-result" id="streamResult">
                            <span class="result-indicator"></span>
                            <span>
                                Waiting for the first stream processing operation.
                            </span>
                        </div>

                    </article>

                    <article class="energy-panel efficiency-panel">

                        <div class="energy-panel-heading">
                            <div>
                                <span class="energy-section-label">DATA EFFICIENCY</span>
                                <h3>Compression overview</h3>
                                <p>Actual input and output sizes from the latest execution.</p>
                            </div>

                            <span class="efficiency-icon">⇣</span>
                        </div>


                        <div class="compression-comparison">

                            <div class="compression-row">
                                <div class="compression-label">
                                    <span>Original stream</span>
                                    <strong id="originalSize">—</strong>
                                </div>

                                <div class="compression-track">
                                    <span class="compression-bar original-bar"
                                          id="originalSizeBar"></span>
                                </div>
                            </div>


                            <div class="compression-row">
                                <div class="compression-label">
                                    <span>Processed stream</span>
                                    <strong id="finalSize">—</strong>
                                </div>

                                <div class="compression-track">
                                    <span class="compression-bar compressed-bar"
                                          id="finalSizeBar"></span>
                                </div>
                            </div>

                        </div>


                        <div class="efficiency-result">
                            <div>
                                <span>SIZE REDUCTION</span>
                                <strong id="sizeReduction">—</strong>
                            </div>

                            <span class="efficiency-result-icon">↘</span>
                        </div>

                        <p class="illustrative-note" id="compressionNote">
                            Actual compression measurements will appear after
                            a stream is processed.
                        </p>

                    </article>

                </section>

                <section class="energy-lower-grid information-grid">

                    <article class="energy-panel activity-panel">

                        <div class="energy-panel-heading">
                            <div>
                                <span class="energy-section-label">DATABASE ACTIVITY</span>
                                <h3>Recent activity</h3>
                                <p>Latest stream records saved by the processing service.</p>
                            </div>

                            <span class="activity-counter" id="activityCounter">00</span>
                        </div>

                        <div class="energy-activity-list" id="energyActivityList">

                            <div class="processing-empty">
                                Loading stream history...
                            </div>

                        </div>

                    </article>

                    <article class="energy-panel architecture-panel">

                        <div class="energy-panel-heading">
                            <div>
                                <span class="energy-section-label">DESIGN EXPLANATION</span>
                                <h3>How the Decorator works</h3>
                                <p>
                                    Additional behaviour without modifying the core component.
                                </p>
                            </div>

                            <span class="architecture-symbol">⬡</span>
                        </div>


                        <div class="architecture-content">

                            <div class="architecture-concept">
                                <span class="concept-number">01</span>
                                <div>
                                    <strong>Component</strong>
                                    <p>
                                        The common EnergyStream interface defines
                                        the processing operation.
                                    </p>
                                </div>
                            </div>

                            <div class="architecture-concept">
                                <span class="concept-number">02</span>
                                <div>
                                    <strong>Concrete component</strong>
                                    <p>
                                        BasicEnergyStream provides the original
                                        energy-stream behaviour.
                                    </p>
                                </div>
                            </div>

                            <div class="architecture-concept">
                                <span class="concept-number">03</span>
                                <div>
                                    <strong>Concrete Decorators</strong>
                                    <p>
                                        Authentication, compression and encryption
                                        wrap the stream with additional behaviour.
                                    </p>
                                </div>
                            </div>

                        </div>


                        <div class="architecture-footer">
                            <span class="architecture-footer-dot"></span>
                            Add or remove optional processing layers without
                            changing the core stream class.
                        </div>

                    </article>

                </section>

                <section class="energy-implementation-note">

                    <span class="implementation-icon">i</span>

                    <div>
                        <strong>Implementation status</strong>
                        <p id="implementationMessage">
                            Energy meters, stream processing and activity are
                            connected to the SmartCity360 backend. Authentication
                            is mandatory; optional Decorators are applied according
                            to the selected configuration.
                        </p>
                    </div>

                    <span class="implementation-status" id="implementationStatus">
                        DATABASE CONNECTED
                    </span>

                </section>

                <footer class="page-footer energy-footer">
                    <span>SMARTCITY360</span>
                    <span>Energy systems · Decorator pattern · Part A</span>
                </footer>

            </div>
        </div>

    </main>
</div>

<div class="energy-toast" id="energyToast" role="status"
     aria-live="polite" hidden>
    <span class="energy-toast-icon">✓</span>
    <span id="energyToastText">
        Energy stream saved successfully.
    </span>
</div>
<script>
    const contextPath = "<%= contextPath %>";

    const energyEndpoints = {
        meters: contextPath + "/energy/meters",
        buildings: contextPath + "/energy/buildings",
        register: contextPath + "/energy/register",
        update: contextPath + "/energy/update",
        options: contextPath + "/energy/options",
        token: contextPath + "/energy/token",
        summary: contextPath + "/energy/summary",
        history: contextPath + "/energy/history?limit=3",
        process: contextPath + "/energy/process"
    };

    let meterData = [];
    let historyData = [];
    let latestResult = null;

    let energyToastTimer = null;

    const optionalLayers = ["compression", "encryption"];

    const layerNames = {
        authentication: "Authentication",
        compression: "Compression",
        encryption: "Encryption"
    };

    document.addEventListener("DOMContentLoaded", function () {

        document.getElementById("meterSearch")
            .addEventListener("input", filterMeters);

        document.getElementById("meterRegistrationForm")
            .addEventListener("submit", saveEnergyMeter);

        document.getElementById("toggleMeterFormButton")
            .addEventListener("click", () => openMeterModal("create"));
        document.getElementById("closeMeterModalButton")
            .addEventListener("click", closeMeterModal);
        document.getElementById("cancelMeterModalButton")
            .addEventListener("click", closeMeterModal);
        document.getElementById("meterModalBackdrop")
            .addEventListener("click", event => {
                if (event.target.id === "meterModalBackdrop") closeMeterModal();
            });
        document.addEventListener("keydown", event => {
            if (event.key === "Escape") closeMeterModal();
        });
        document.getElementById("meterTableBody")
            .addEventListener("click", event => {
                const button = event.target.closest("[data-edit-meter]");
                if (button) openMeterModal("edit", Number(button.dataset.editMeter));
            });

        loadBuildingOptions().catch(error => showEnergyToast(error.message));
        loadMeterOptions().catch(error => showEnergyToast(error.message));

        document.getElementById("meterFilter")
            .addEventListener("change", filterMeters);

        document.getElementById("meterSelect")
            .addEventListener("change", loadSelectedMeterToken);

        document.getElementById("compression")
            .addEventListener("change", updateSecurityConfiguration);

        document.getElementById("encryption")
            .addEventListener("change", updateSecurityConfiguration);

        document.getElementById("energyProcessForm")
            .addEventListener("submit", processEnergyStream);

        updateSecurityConfiguration();
        refreshDashboard();

        const formFields = [
            "meterSelect",
            "meterToken",
            "readingValue",
            "readingUnit"
        ];

        formFields.forEach((fieldId) => {
            const field = document.getElementById(fieldId);

            if (!field) return;

            field.addEventListener("input", hideProcessMessage);
            field.addEventListener("change", hideProcessMessage);
        });

    });

    function toggleMeterForm(forceOpen) {
        if (forceOpen === false) closeMeterModal();
        else openMeterModal("create");
    }

    async function openMeterModal(mode, meterId) {
        const backdrop = document.getElementById("meterModalBackdrop");
        const form = document.getElementById("meterRegistrationForm");
        const isEdit = mode === "edit";
        const meter = isEdit ? meterData.find(item => Number(item.id) === Number(meterId)) : null;
        if (isEdit && !meter) {
            showEnergyToast("The selected meter could not be found.");
            return;
        }

        form.reset();
        document.getElementById("meterFormMode").value = isEdit ? "edit" : "create";
        document.getElementById("editingMeterId").value = isEdit ? meter.id : "";
        document.getElementById("meterModalTitle").textContent = isEdit
            ? "Update energy meter" : "Register a new energy meter";
        document.getElementById("meterModalSubtitle").textContent = isEdit
            ? "Update the meter type, building, installation date and status."
            : "Serial number and secure credentials are generated automatically.";
        document.getElementById("meterModalHint").textContent = isEdit
            ? "The serial number and authentication credentials remain unchanged."
            : "New meters are registered as ACTIVE. A secure token is generated and stored safely.";
        document.getElementById("registerMeterButton").textContent = isEdit
            ? "Save changes" : "Register meter";
        document.getElementById("meterSerialDisplay").value = isEdit
            ? meter.serialNumber : "Generated automatically";
        document.getElementById("meterSerialDisplay").placeholder = isEdit
            ? "" : "Generated automatically on save";
        document.getElementById("meterStatusField").hidden = !isEdit;
        document.getElementById("meterRegistrationMessage").hidden = true;

        backdrop.hidden = false;
        document.body.classList.add("meter-modal-open");

        try {
            await Promise.all([loadBuildingOptions(), loadMeterOptions()]);
            if (isEdit) {
                document.getElementById("newMeterType").value = meter.meterType || "";
                document.getElementById("newMeterBuilding").value = String(meter.buildingId || "");
                document.getElementById("newMeterDate").value = meter.installationDate || "";
                document.getElementById("newMeterStatus").value = String(meter.statusId || "");
            } else {
                document.getElementById("newMeterType").selectedIndex = 0;
                document.getElementById("newMeterStatus").value = "";
            }
            document.getElementById("newMeterType").focus();
        } catch (error) {
            showMeterRegistrationMessage(error.message || "Unable to load meter form options.", "error");
        }
    }

    function closeMeterModal() {
        const backdrop = document.getElementById("meterModalBackdrop");
        if (!backdrop || backdrop.hidden) return;
        backdrop.hidden = true;
        document.body.classList.remove("meter-modal-open");
    }

    async function loadBuildingOptions() {
        const select = document.getElementById("newMeterBuilding");
        if (!select || select.dataset.loaded === "true") return;
        const data = await fetchJson(energyEndpoints.buildings, { cache: "no-store" });
        select.innerHTML = '<option value="">Select an assigned building</option>';
        (data.buildings || []).forEach(building => {
            const option = document.createElement("option");
            option.value = building.id;
            option.textContent = building.label;
            select.appendChild(option);
        });
        if (!data.buildings || data.buildings.length === 0) {
            select.innerHTML = '<option value="">No buildings found — add a building first</option>';
        }
        select.dataset.loaded = "true";
        if (!select.options.length) throw new Error("No buildings are available.");
    }

    async function loadMeterOptions() {
        const typeSelect = document.getElementById("newMeterType");
        const statusSelect = document.getElementById("newMeterStatus");
        if (typeSelect.dataset.loaded === "true" && statusSelect.dataset.loaded === "true") return;
        const data = await fetchJson(energyEndpoints.options, { cache: "no-store" });
        typeSelect.innerHTML = '<option value="">Select a meter type</option>';
        (data.meterTypes || []).forEach(type => {
            const option = document.createElement("option");
            option.value = type;
            option.textContent = type;
            typeSelect.appendChild(option);
        });
        statusSelect.innerHTML = '<option value="">Select a status</option>';
        (data.statuses || []).forEach(status => {
            const option = document.createElement("option");
            option.value = status.id;
            option.textContent = status.name;
            statusSelect.appendChild(option);
        });
        typeSelect.dataset.loaded = "true";
        statusSelect.dataset.loaded = "true";
    }

    async function saveEnergyMeter(event) {
        event.preventDefault();
        const isEdit = document.getElementById("meterFormMode").value === "edit";
        const button = document.getElementById("registerMeterButton");
        const original = button.textContent;
        const params = new URLSearchParams();
        params.append("meterType", document.getElementById("newMeterType").value);
        params.append("buildingId", document.getElementById("newMeterBuilding").value);
        params.append("installationDate", document.getElementById("newMeterDate").value);
        if (isEdit) {
            params.append("meterId", document.getElementById("editingMeterId").value);
            params.append("statusId", document.getElementById("newMeterStatus").value);
        }
        button.disabled = true;
        button.textContent = isEdit ? "Saving..." : "Registering...";
        try {
            const result = await fetchJson(isEdit ? energyEndpoints.update : energyEndpoints.register, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body: params.toString(),
                cache: "no-store"
            });
            if (!result.success) throw new Error(result.message || "Unable to save this meter.");

            closeMeterModal();
            await Promise.all([loadMeters(), loadSummary()]);
            if (!isEdit && result.meterId) {
                const meterSelect = document.getElementById("meterSelect");
                meterSelect.value = String(result.meterId);
                await loadSelectedMeterToken();
                showEnergyToast("Meter " + result.serialNumber + " registered. Its secure token is ready.");
            } else {
                showEnergyToast("Energy meter updated successfully.");
            }
        } catch (error) {
            showMeterRegistrationMessage(error.message || "Meter save failed.", "error");
        } finally {
            button.disabled = false;
            button.textContent = original;
        }
    }

    function showMeterRegistrationMessage(message, type) {
        const box = document.getElementById("meterRegistrationMessage");
        if (!box) return;
        box.hidden = false;
        box.className = "meter-registration-message " + (type || "info");
        box.textContent = message;
    }

    async function fetchJson(url, options = {}) {

        const response = await fetch(url, options);

        let data;

        try {
            data = await response.json();
        } catch (error) {
            throw new Error("The server returned an invalid response.");
        }

        if (!response.ok) {
            throw new Error(
                data.message || "Request failed. HTTP " + response.status
            );
        }

        return data;
    }

    async function refreshDashboard() {

        const results = await Promise.allSettled([
            loadMeters(),
            loadSummary(),
            loadHistory()
        ]);

        const hasFailure = results.some(
            result => result.status === "rejected"
        );

        if (hasFailure) {
            setConnection(false, "DATABASE CONNECTION ISSUE");

            document.getElementById("pipelineStatus").textContent = "CHECK";
            document.getElementById("pipelineStatusCaption").textContent =
                "Some dashboard data could not be loaded";

            document.getElementById("implementationStatus").textContent =
                "CONNECTION ISSUE";
        } else {
            setConnection(true, "LIVE DATABASE CONNECTED");

            document.getElementById("pipelineStatus").textContent = "READY";
            document.getElementById("pipelineStatusCaption").textContent =
                "Energy processing service available";

            document.getElementById("implementationStatus").textContent =
                "DATABASE CONNECTED";
        }

    }



    function setConnection(connected, message) {

        const indicator =
            document.getElementById("connectionIndicator");

        const dot =
            document.getElementById("connectionDot");

        document.getElementById("connectionText").textContent = message;

        indicator.classList.toggle("connection-error", !connected);

        if (dot) {
            dot.classList.toggle("connection-dot-error", !connected);
        }
    }

    async function loadMeters() {

        const body = document.getElementById("meterTableBody");

        try {

            const data = await fetchJson(energyEndpoints.meters);

            if (data.success === false) {
                throw new Error(data.message || "Unable to load meters.");
            }

            meterData = Array.isArray(data.meters)
                ? data.meters
                : [];

            renderMeters();
            populateMeterSelect();

            return meterData;

        } catch (error) {

            body.innerHTML = `
                <tr>
                    <td colspan="5" class="meter-empty">
                        ${escapeHtml(error.message)}
                    </td>
                </tr>
            `;

            throw error;
        }

    }


    function populateMeterSelect() {

        const select = document.getElementById("meterSelect");
        const previousValue = select.value;

        select.innerHTML =
            '<option value="">Select a registered meter</option>';

        meterData.forEach(meter => {

            const option = document.createElement("option");

            option.value = meter.id;

            option.textContent =
                (meter.serialNumber || "Meter " + meter.id) +
                " — " +
                (meter.buildingName || "Unassigned building");

            select.appendChild(option);

        });

        if (meterData.some(
            meter => String(meter.id) === previousValue
        )) {
            select.value = previousValue;
            loadSelectedMeterToken();
        } else {
            document.getElementById("meterToken").value = "";
            document.getElementById("meterTokenHelp").textContent =
                "Select a registered meter. Its protected authentication token will load automatically.";
        }

        if (meterData.length === 0) {
            select.innerHTML =
                '<option value="">No registered meters found</option>';
        }

    }


    let meterTokenRequestSequence = 0;

    async function loadSelectedMeterToken() {
        const select = document.getElementById("meterSelect");
        const tokenInput = document.getElementById("meterToken");
        const tokenHelp = document.getElementById("meterTokenHelp");
        const meterId = select.value;
        const requestSequence = ++meterTokenRequestSequence;

        tokenInput.value = "";

        if (!meterId) {
            tokenInput.disabled = false;
            tokenInput.placeholder = "Select an energy meter to load its token";
            tokenHelp.textContent =
                "Select a registered meter. Its protected authentication token will load automatically.";
            return;
        }

        tokenInput.disabled = true;
        tokenInput.placeholder = "Loading meter authentication token...";
        tokenHelp.textContent = "Retrieving the selected meter's protected token...";

        try {
            const parameters = new URLSearchParams();
            parameters.append("meterId", meterId);

            const result = await fetchJson(energyEndpoints.token, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                },
                body: parameters.toString(),
                cache: "no-store"
            });

            if (requestSequence !== meterTokenRequestSequence ||
                select.value !== meterId) {
                return;
            }

            if (!result.success || !result.token) {
                throw new Error(result.message || "The meter token could not be loaded.");
            }

            tokenInput.value = result.token;
            tokenInput.placeholder = "Authentication token loaded";
            tokenHelp.textContent = result.newlyIssued
                ? "A new token was issued because this legacy meter had only a one-way hash. Its previous token is now invalid."
                : "The selected meter's encrypted token was loaded. The field remains masked for security.";

        } catch (error) {
            if (requestSequence !== meterTokenRequestSequence) return;
            tokenInput.value = "";
            tokenInput.placeholder = "Token unavailable";
            tokenHelp.textContent = error.message || "Unable to load the meter token.";
            showProcessMessage(tokenHelp.textContent, "warning");

        } finally {
            if (requestSequence === meterTokenRequestSequence) {
                tokenInput.disabled = false;
            }
        }
    }


    function renderMeters() {

        const body = document.getElementById("meterTableBody");

        const search = document.getElementById("meterSearch")
            .value.trim().toLowerCase();

        const filter = document.getElementById("meterFilter").value;

        const filtered = meterData.filter(meter => {

            const status = normalizeStatus(meter.status);

            const searchText = [
                meter.serialNumber,
                meter.meterType,
                meter.buildingName,
                meter.streetName,
                meter.districtName,
                meter.cityName
            ].join(" ").toLowerCase();

            const matchesSearch = searchText.includes(search);

            const matchesFilter =
                filter === "all" ||
                status === filter ||
                (filter === "warning" && status.includes("warning"));

            return matchesSearch && matchesFilter;

        });


        document.getElementById("meterFooterCount").textContent =
            filtered.length + (filtered.length === 1
                ? " meter displayed"
                : " meters displayed");


        document.getElementById("meterDirectoryBadge").textContent =
            meterData.length + " REGISTERED";


        if (filtered.length === 0) {

            body.innerHTML = `
                <tr>
                    <td colspan="5" class="meter-empty">
                        ${meterData.length == 0
                            ? "No energy meters have been registered yet."
                            : "No meters match your search."}
                    </td>
                </tr>
            `;

            return;
        }


        body.innerHTML = filtered.map(meter => {

            const status = normalizeStatus(meter.status);

            const location = [
                meter.buildingName,
                meter.streetName,
                meter.districtName,
                meter.cityName
            ].filter(Boolean).join(", ");


            return `
                <tr data-status="${escapeHtml(status)}">

                    <td>
                        <strong>${escapeHtml(meter.serialNumber || "—")}</strong>
                        <small>ID ${escapeHtml(meter.id)}</small>
                    </td>

                    <td>
                        ${escapeHtml(
                            location || meter.location || "Location unavailable"
                        )}
                    </td>

                    <td>
                        ${escapeHtml(meter.meterType || "—")}
                    </td>

                    <td>
                        <span class="meter-status ${statusClass(status)}">
                            ${escapeHtml(meter.status || "UNKNOWN")}
                        </span>
                    </td>
                    <td class="meter-action-cell">
                        <button type="button" class="meter-edit-button"
                                data-edit-meter="${Number(meter.id)}" aria-label="Edit ${escapeHtml(meter.serialNumber)}">
                            Edit
                        </button>
                    </td>

                </tr>
            `;

        }).join("");

    }


    function filterMeters() {
        renderMeters();
    }


    function normalizeStatus(value) {
        return String(value || "").trim().toLowerCase();
    }


    function statusClass(status) {

        if (status.includes("active")) return "active";

        if (status.includes("warning")) return "warning";

        if (status.includes("offline") ||
            status.includes("inactive")) {
            return "offline";
        }

        return "offline";
    }

    async function loadSummary() {

        const data = await fetchJson(energyEndpoints.summary);

        if (data.success === false) {
            throw new Error(data.message || "Unable to load summary.");
        }

        const summary = data.summary || data;

        document.getElementById("meterCount").textContent =
            getFirstValue(summary, [
                "totalMeters",
                "registeredMeters",
                "meterCount"
            ], meterData.length);


        document.getElementById("activeMeterCount").textContent =
            getFirstValue(summary, [
                "activeMeters",
                "activeMeterCount"
            ], "—");

    }


    function getFirstValue(object, keys, fallback) {

        for (const key of keys) {

            if (object &&
                object[key] !== undefined &&
                object[key] !== null) {

                return object[key];
            }

        }

        return fallback;
    }

    function getEnabledLayers() {

        return [
            "authentication",
            ...optionalLayers.filter(id =>
                document.getElementById(id).checked
            )
        ];

    }


    function updateSecurityConfiguration() {

        const enabled = getEnabledLayers();

        const count = enabled.length;

        document.getElementById("layerCount").textContent =
            String(count).padStart(2, "0");

        document.getElementById("selectedLayerCount").textContent =
            count + (count === 1
                ? " layer enabled"
                : " layers enabled");

        document.getElementById("pipelineMode").textContent =
            count + (count === 1
                ? " DECORATOR ACTIVE"
                : " DECORATORS ACTIVE");

        document.getElementById("compression").closest(
            ".security-layer-option"
        ).classList.toggle(
            "layer-option-disabled",
            !document.getElementById("compression").checked
        );

        document.getElementById("encryption").closest(
            ".security-layer-option"
        ).classList.toggle(
            "layer-option-disabled",
            !document.getElementById("encryption").checked
        );

        updatePipelineVisual();

    }

    function updatePipelineVisual() {

        const enabled = getEnabledLayers();

        document.querySelectorAll(".decorator-node").forEach(node => {

            const layer = node.dataset.layer;

            const active = enabled.includes(layer);

            node.classList.toggle("layer-enabled", active);
            node.classList.toggle("layer-disabled", !active);

        });


        const connectors = document.querySelectorAll(".pipeline-connector");

        connectors.forEach(connector => {
            connector.classList.remove("connector-muted");
        });


        document.getElementById("pipelineDescription").textContent =
            "Energy meter → " +
            enabled.map(id => layerNames[id]).join(" → ") +
            " → Protected stream.";

    }

    async function processEnergyStream(event) {

        event.preventDefault();

        const meterId =
            document.getElementById("meterSelect").value;

        const token =
            document.getElementById("meterToken").value.trim();

        const readingValue =
            document.getElementById("readingValue").value;

        const unit =
            document.getElementById("readingUnit").value;

        const compression =
            document.getElementById("compression").checked;

        const encryption =
            document.getElementById("encryption").checked;


        if (!meterId) {
            showProcessMessage(
                "Please select an energy meter.",
                "warning"
            );
            return;
        }

        if (!token) {
            showProcessMessage(
                "Enter the meter authentication token.",
                "warning"
            );
            return;
        }

        if (
            readingValue === "" ||
            !Number.isFinite(Number(readingValue)) ||
            Number(readingValue) < 0 ||
            Number(readingValue) > 9999999999.9999
        ) {

            showProcessMessage(
                "Enter a valid non-negative energy reading.",
                "warning"
            );

            return;
        }


        const button = document.getElementById("processButton");

        button.disabled = true;

        button.innerHTML =
            "<span>Processing stream...</span><span>◌</span>";

        showProcessMessage(
            "Sending the energy stream to the Java processing service...",
            "loading"
        );


        const parameters = new URLSearchParams();

        parameters.append("meterId", meterId);
        parameters.append("token", token);
        parameters.append("readingValue", readingValue);
        parameters.append("unit", unit);
        parameters.append("compression", String(compression));
        parameters.append("encryption", String(encryption));


        try {

            const result = await fetchJson(
                energyEndpoints.process,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded;charset=UTF-8"
                    },

                    body: parameters.toString()
                }
            );


            console.log("ENERGY PROCESSING RESPONSE:", result);

            displayResult(result);

            console.log("Processing result rendered successfully.");


            if (result.success !== true) {

                showProcessMessage(
                    result.message || "The stream could not be processed.",
                    "warning"
                );

                addEnergyActivity(
                    "Stream processing failed",
                    result.message || "The processing service rejected the stream.",
                    "warning"
                );

                return;
            }


            showProcessMessage(
                result.message ||
                "Energy stream processed and saved successfully.",
                "success"
            );




            addEnergyActivity(
                "Energy stream processed",
                "Record #" + (result.streamRecordId || "—") +
                " · " +
                (result.encryptionAlgorithm || "No encryption") +
                " · " +
                (result.compressionAlgorithm || "No compression"),
                "success"
            );


            await Promise.allSettled([
                loadSummary(),
                loadHistory()
            ]);


        } catch (error) {

            showProcessMessage(error.message, "warning");

            addEnergyActivity(
                "Processing request failed",
                error.message,
                "warning"
            );

            console.error("Energy stream processing error:", error);

        } finally {

            button.disabled = false;

            button.innerHTML =
                "<span>Process energy stream</span><span>→</span>";

        }

    }

    function showProcessMessage(message, type) {

        const container = document.getElementById("processMessage");

        container.hidden = false;

        container.className = "stream-result";

        if (type === "warning" || type === "error") {
            container.classList.add("warning");
        }

        const text = document.getElementById("processMessageText");

        text.textContent = message;

    }

    function displayResult(result) {

        if (!result || typeof result !== "object") {
            console.error("Invalid processing result:", result);
            return;
        }

        latestResult = result;

        const successful = result.success === true;

        const tag = document.getElementById("resultTag");

        tag.textContent = successful
            ? "PROCESSING SUCCESSFUL"
            : "PROCESSING FAILED";

        tag.classList.toggle("result-success", successful);
        tag.classList.toggle("result-failed", !successful);

        const selectedMeter =
            document.getElementById("meterSelect")
                .selectedOptions[0];

        document.getElementById("resultMeter").textContent =
            "Meter: " +
            (result.serialNumber ||
                selectedMeter?.textContent ||
                "—");

        document.getElementById("resultRecord").textContent =
            "Record: #" + (result.streamRecordId ?? "—");

        document.getElementById("streamPayload").textContent =
            result.processedPayload ||
            result.message ||
            "No processed payload available.";

        renderProcessingSteps(result);

        renderCompressionResult(result);

        const resultBox =
            document.getElementById("streamResult");

        resultBox.className = "stream-result";

        if (!successful) {
            resultBox.classList.add("warning");
        }

        resultBox.replaceChildren();

        const indicator = document.createElement("span");
        indicator.className = "result-indicator";

        const message = document.createElement("span");

        message.textContent =
            result.message ||
            (successful
                ? "Energy stream processed successfully."
                : "Energy stream processing failed.");

        resultBox.append(indicator, message);

    }

    function renderProcessingSteps(result) {

        const container =
            document.getElementById("processingSteps");

        container.replaceChildren();

        const logs = Array.isArray(result.logs)
            ? result.logs
            : [];


        if (logs.length === 0) {

            const empty = document.createElement("div");

            empty.className = "processing-empty";

            empty.textContent =
                "No decorator execution details were returned.";

            container.appendChild(empty);

            return;
        }


        logs.forEach((log, index) => {

            const row = document.createElement("div");

            row.className = "processing-step";


            const number = document.createElement("span");

            number.className = "step-number";

            number.textContent =
                String(index + 1).padStart(2, "0");


            const check = document.createElement("span");

            check.className = "step-check";

            check.textContent = log.success ? "✓" : "!";


            const title = document.createElement("span");

            title.textContent =
                log.decoratorName ||
                log.operationName ||
                "Processing step";


            const state = document.createElement("small");

            state.textContent =
                log.success ? "SUCCESS" : "FAILED";


            row.append(number, check, title, state);

            container.appendChild(row);

        });

    }

    function renderCompressionResult(result) {

        const original = Number(result.originalSize);
        const finalSize = Number(result.finalSize);

        const validSizes =
            Number.isFinite(original) &&
            Number.isFinite(finalSize) &&
            original >= 0 &&
            finalSize >= 0;


        if (!validSizes) {

            document.getElementById("originalSize").textContent = "—";
            document.getElementById("finalSize").textContent = "—";
            document.getElementById("sizeReduction").textContent = "—";

            document.getElementById("originalSizeBar").style.width = "0%";
            document.getElementById("finalSizeBar").style.width = "0%";

            document.getElementById("compressionNote").textContent =
                "Size information was not returned by this processing operation.";

            return;
        }


        document.getElementById("originalSize").textContent =
            formatBytes(original);

        document.getElementById("finalSize").textContent =
            formatBytes(finalSize);


        const reduction = original > 0
            ? ((original - finalSize) / original) * 100
            : 0;


        document.getElementById("sizeReduction").textContent =
            reduction.toFixed(2) + "%";


        const maximum = Math.max(original, finalSize, 1);

        document.getElementById("originalSizeBar").style.width =
            ((original / maximum) * 100) + "%";

        document.getElementById("finalSizeBar").style.width =
            ((finalSize / maximum) * 100) + "%";


        if (result.compressionAlgorithm) {

            document.getElementById("compressionNote").textContent =
                "Measured from the latest execution using " +
                result.compressionAlgorithm + ".";

        } else {

            document.getElementById("compressionNote").textContent =
                "No compression algorithm was applied to this stream.";

        }

    }

    async function loadHistory() {

        const container =
            document.getElementById("energyActivityList");

        try {

            const data = await fetchJson(energyEndpoints.history);

            if (data.success === false) {
                throw new Error(
                    data.message || "Unable to load stream history."
                );
            }

            historyData = Array.isArray(data.history)
                ? data.history
                : [];

            renderHistory();

            return historyData;

        } catch (error) {

            container.innerHTML =
                '<div class="processing-empty">' +
                escapeHtml(error.message) +
                '</div>';

            throw error;
        }

    }


    function renderHistory() {

        const container =
            document.getElementById("energyActivityList");

        document.getElementById("activityCounter").textContent =
            String(historyData.length).padStart(2, "0");


        if (historyData.length === 0) {

            container.innerHTML =
                '<div class="processing-empty">' +
                'No stream records have been processed yet.' +
                '</div>';

            return;
        }


        container.innerHTML = historyData.map(item => {

            const status = String(
                item.processingStatus || ""
            ).toUpperCase();

            const successful =
                status === "SUCCESS" ||
                item.success === true ||
                item.success === 1;


            const serial =
                item.serialNumber ||
                item.meterSerialNumber ||
                ("Meter #" + (item.energyMetersId || item.meterId || "—"));


            const recordId =
                item.id || item.streamRecordId || "—";


            const date =
                item.receivedAt ||
                item.processedAt ||
                item.recordedAt ||
                item.createdAt ||
                "";


            const description = [
                item.processingStatus || "UNKNOWN",
                "Record #" + recordId,
                item.encryptionAlgorithm || "No encryption",
                item.compressionAlgorithm || "No compression"
            ].join(" · ");


            return `
                <div class="energy-activity-item">

                    <span class="activity-marker ${successful ? "" : "warning"}"></span>

                    <div>
                        <strong>${escapeHtml(serial)}</strong>
                        <small>${escapeHtml(description)}</small>
                    </div>

                    <time>${escapeHtml(formatDate(date))}</time>

                </div>
            `;

        }).join("");

    }

    function formatBytes(value) {

        if (!Number.isFinite(value)) return "—";

        if (value < 1024) {
            return value + " B";
        }

        return (value / 1024).toFixed(2) + " KB";

    }


    function formatDate(value) {

        if (!value) return "—";

        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return String(value);
        }

        return date.toLocaleString();

    }


    function escapeHtml(value) {

        return String(value ?? "").replace(/[&<>"']/g, character => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        })[character]);

    }

    function showHelp() {

        alert(
            "SMARTCITY360 — ENERGY SYSTEMS\n\n" +
            "This module demonstrates the Decorator design pattern.\n\n" +
            "1. Energy meter data enters the stream processor.\n" +
            "2. Authentication verifies the meter token.\n" +
            "3. Compression optionally reduces the stream size.\n" +
            "4. Encryption optionally protects the stream using AES-256-GCM.\n" +
            "5. The processed stream and reading are saved in the database.\n\n" +
            "Authentication is always required. Compression and encryption " +
            "can be configured before processing."
        );

    }

    function validateEnergyForm() {

        const meterSelect = document.getElementById("meterSelect");
        const tokenInput = document.getElementById("meterToken");
        const readingInput = document.getElementById("readingValue");
        const unitSelect = document.getElementById("readingUnit");

        const meterId = meterSelect.value;
        const token = tokenInput.value.trim();
        const readingText = readingInput.value.trim();
        const unit = unitSelect.value;

        [
            meterSelect,
            tokenInput,
            readingInput,
            unitSelect
        ].forEach(field => field.setCustomValidity(""));

        if (!meterId) {
            showProcessMessage("Please select an energy meter.", "error");
            meterSelect.focus();
            return false;
        }

        const meterExists = meterData.some(
            meter => String(meter.id) === String(meterId)
        );

        if (!meterExists) {
            showProcessMessage(
                "The selected meter is not available in the registered meter list. Refresh the page and try again.",
                "error"
            );
            meterSelect.focus();
            return false;
        }

        if (!token) {
            showProcessMessage(
                "Enter the authentication token for the selected energy meter.",
                "error"
            );
            tokenInput.focus();
            return false;
        }

        if (!readingText) {
            showProcessMessage(
                "Enter an energy reading before processing the stream.",
                "error"
            );
            readingInput.focus();
            return false;
        }

        const reading = Number(readingText);

        if (!Number.isFinite(reading) ||
            reading < 0 ||
            reading > 9999999999.9999) {

            showProcessMessage(
                "The energy reading must be a valid number between 0 and 9,999,999,999.9999.",
                "error"
            );
            readingInput.focus();
            return false;
        }

        const decimalPart = readingText.split(".")[1] || "";

        if (decimalPart.length > 4) {
            showProcessMessage(
                "The energy reading can contain a maximum of four decimal places.",
                "error"
            );
            readingInput.focus();
            return false;
        }

        if (!["kWh", "Wh", "MWh"].includes(unit)) {
            showProcessMessage(
                "Please select a valid energy reading unit.",
                "error"
            );
            unitSelect.focus();
            return false;
        }

        return true;
    }

    function showEnergyToast(message) {

        const toast = document.getElementById("energyToast");
        const toastText = document.getElementById("energyToastText");

        if (!toast || !toastText) return;

        if (energyToastTimer) {
            clearTimeout(energyToastTimer);
        }

        toastText.textContent = message ||
            "Energy stream processed and saved successfully.";

        toast.hidden = false;

        energyToastTimer = setTimeout(() => {
            toast.hidden = true;
            energyToastTimer = null;
        }, 4500);
    }

    async function processEnergyStream(event) {

        event.preventDefault();

        hideProcessMessage();

        if (!validateEnergyForm()) {
            return;
        }

        const meterId =
            document.getElementById("meterSelect").value;

        const token =
            document.getElementById("meterToken").value.trim();

        const readingValue =
            document.getElementById("readingValue").value.trim();

        const unit =
            document.getElementById("readingUnit").value;

        const compression =
            document.getElementById("compression").checked;

        const encryption =
            document.getElementById("encryption").checked;

        const button = document.getElementById("processButton");

        const originalButtonContent =
            '<span>Process energy stream</span><span>→</span>';

        button.disabled = true;

        button.innerHTML =
            "<span>Processing stream...</span><span>◌</span>";

        showProcessMessage(
            "Sending the energy stream to the Java processing service...",
            "loading"
        );

        const parameters = new URLSearchParams();

        parameters.append("meterId", meterId);
        parameters.append("token", token);
        parameters.append("readingValue", readingValue);
        parameters.append("unit", unit);
        parameters.append("compression", String(compression));
        parameters.append("encryption", String(encryption));

        try {

            const result = await fetchJson(
                energyEndpoints.process,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded;charset=UTF-8"
                    },

                    body: parameters.toString()
                }
            );

            console.log("ENERGY PROCESSING RESPONSE:", result);

            displayResult(result);

            if (result.success !== true) {

                showProcessMessage(
                    result.message ||
                    "The energy stream could not be processed. Check the meter token and try again.",
                    "error"
                );

                addEnergyActivity(
                    "Stream processing failed",
                    result.message ||
                    "The processing service rejected the stream.",
                    "warning"
                );

                return;
            }

            hideProcessMessage();

            showEnergyToast(
                result.message ||
                "Energy stream processed and saved successfully."
            );

            addEnergyActivity(
                "Energy stream processed",
                "Record #" + (result.streamRecordId || "—") +
                " · " +
                (result.encryptionAlgorithm || "No encryption") +
                " · " +
                (result.compressionAlgorithm || "No compression"),
                "success"
            );

            const refreshResults = await Promise.allSettled([
                loadSummary(),
                loadHistory()
            ]);

            const refreshFailed = refreshResults.some(
                item => item.status === "rejected"
            );

            if (refreshFailed) {
                console.warn(
                    "The stream was processed, but some dashboard data could not be refreshed."
                );
            }

        } catch (error) {

            showProcessMessage(
                error.message ||
                "An unexpected error occurred while processing the energy stream.",
                "error"
            );

            addEnergyActivity(
                "Processing request failed",
                error.message || "Unexpected processing error.",
                "warning"
            );

            console.error("Energy stream processing error:", error);

        } finally {

            button.disabled = false;
            button.innerHTML = originalButtonContent;

        }
    }

    function showProcessMessage(message, type) {

        const container = document.getElementById("processMessage");
        const text = document.getElementById("processMessageText");

        if (!container || !text) return;

        container.hidden = false;

        container.className = "stream-result";

        if (type === "error" || type === "warning") {
            container.classList.add("error");
        } else if (type === "success") {
            container.classList.add("success");
        } else if (type === "loading") {
            container.classList.add("loading");
        }

        text.textContent = message;
    }


    function hideProcessMessage() {
        const message = document.getElementById("processMessage");

        if (!message) return;

        message.hidden = true;
        message.textContent = "";
        message.className = "stream-result";
    }

    function addEnergyActivity(title, description, type) {

        const container =
            document.getElementById("energyActivityList");

        const emptyMessage =
            container.querySelector(".processing-empty");

        if (emptyMessage) {
            container.replaceChildren();
        }

        const item = document.createElement("div");
        item.className = "energy-activity-item";

        const marker = document.createElement("span");
        marker.className = "activity-marker";

        if (type === "warning") {
            marker.classList.add("warning");
        }

        const content = document.createElement("div");

        const heading = document.createElement("strong");
        heading.textContent = title;

        const detail = document.createElement("small");
        detail.textContent = description;

        content.append(heading, detail);

        const time = document.createElement("time");
        time.textContent = new Date().toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit"
        });

        item.append(marker, content, time);

        container.prepend(item);

        while (container.children.length > 10) {
            container.lastElementChild.remove();
        }

        document.getElementById("activityCounter").textContent =
            String(container.children.length).padStart(2, "0");
    }
</script>

</body>
</html>