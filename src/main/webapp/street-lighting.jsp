<%@ page contentType="text/html;charset=UTF-8"
         language="java"
         isELIgnored="true" %>
<%
    String contextPath = request.getContextPath();
    request.setAttribute("activePage", "streetlights");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartCity360 | Street Lighting</title>

    <link rel="stylesheet" href="<%= contextPath %>/css/living-city.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/street-lighting.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/dashboard.css">
</head>
<body>

<div class="app-layout">

    <jsp:include page="/includes/sidebar.jsp" />

    <main class="main-shell">

        <header class="topbar">
            <div class="topbar-heading">
                <span class="breadcrumb">SMART CITY / FLYWEIGHT PATTERN</span>
                <h1>Street lighting</h1>
            </div>

            <div class="topbar-actions">
                <div class="environment-chip" id="streetlightConnectionIndicator">
                    <span class="status-dot"></span>
                    CONNECTING TO DATABASE
                </div>

                <button class="help-button"
                        type="button"
                        onclick="showHelp()"
                        aria-label="Street lighting help">?</button>
            </div>
        </header>

        <div class="content-scroll">
            <div class="page-content street-page">

                <section class="street-hero">
                    <div class="street-hero-content">
                        <span class="street-kicker">Flyweight architecture · Part D</span>
                        <h2>Intelligent street lighting</h2>
                        <p>
                            Manage a large network of city lights using reusable configuration
                            objects. Share common lighting data while keeping each streetlight's
                            location and operating state independent.
                        </p>
                    </div>

                    <div class="street-hero-actions">
                        <button class="street-btn street-btn-primary"
                                type="button"
                                onclick="focusAddForm()">
                            + Add streetlight
                        </button>

                        <button class="street-btn street-btn-ghost"
                                type="button"
                                onclick="runFlyweightDemo()">
                            Run Flyweight demo ↗
                        </button>
                    </div>
                </section>

                <div class="street-notice">
                    <div class="street-notice-icon">i</div>
                    <div>
                        <strong>Database-backed streetlight management</strong>
                        <span>
                            Streetlight records and shared configurations are loaded from the
                            SmartCity360 database. New streetlight instances are saved through
                            the application service and reuse existing Flyweight configurations.
                        </span>
                    </div>
                </div>

                <div id="streetApiError"
                     role="alert"
                     aria-live="assertive"
                     style="display:none;"></div>

                <section class="street-metrics" aria-label="Street lighting summary">

                    <article class="street-metric">
                        <div class="street-metric-top">
                            <span class="street-metric-label">REGISTERED LIGHTS</span>
                            <span class="street-metric-icon">✳</span>
                        </div>
                        <div class="street-metric-value" id="metricLights">—</div>
                        <div class="street-metric-foot">Individual streetlight records</div>
                    </article>

                    <article class="street-metric">
                        <div class="street-metric-top">
                            <span class="street-metric-label">SHARED CONFIGURATIONS</span>
                            <span class="street-metric-icon">⚙</span>
                        </div>
                        <div class="street-metric-value" id="metricConfigs">—</div>
                        <div class="street-metric-foot">Reusable Flyweight configurations</div>
                    </article>

                    <article class="street-metric">
                        <div class="street-metric-top">
                            <span class="street-metric-label">ACTIVE LIGHTS</span>
                            <span class="street-metric-icon">☼</span>
                        </div>
                        <div class="street-metric-value" id="metricActive">—</div>
                        <div class="street-metric-foot">Currently active streetlights</div>
                    </article>

                    <article class="street-metric">
                        <div class="street-metric-top">
                            <span class="street-metric-label">CONFIGURATION REUSE</span>
                            <span class="street-metric-icon">⟳</span>
                        </div>
                        <div class="street-metric-value" id="metricReuse">—</div>
                        <div class="street-metric-foot">Average lights per used configuration</div>
                    </article>

                </section>

                <section class="street-section">
                    <div class="street-section-heading">
                        <div>
                            <span class="street-eyebrow">01 / FLYWEIGHT OBJECT POOL</span>
                            <h3>Reusable lighting configurations</h3>
                        </div>

                        <span class="street-muted" id="configurationCount">
                            Loading configurations...
                        </span>
                    </div>

                    <div class="street-panel">
                        <div class="street-panel-header">
                            <div>
                                <span class="street-eyebrow">INTRINSIC STATE</span>
                                <h3>Common properties stored once</h3>
                            </div>
                            <span class="street-tag">SHARED OBJECTS</span>
                        </div>

                        <div class="street-config-grid" id="configurationGrid">
                            <div class="street-empty">
                                Loading shared configurations...
                            </div>
                        </div>
                    </div>
                </section>

                <section class="street-section">
                    <div class="street-section-heading">
                        <div>
                            <span class="street-eyebrow">02 / OBJECT STRUCTURE</span>
                            <h3>Shared configuration vs individual state</h3>
                        </div>
                    </div>

                    <div class="street-panel">
                        <div class="street-state-grid">

                            <div class="street-state-card">
                                <div class="street-state-icon">⚙</div>
                                <div>
                                    <strong>Shared configuration · Intrinsic state</strong>
                                    <p>
                                        Common data such as lighting type, wattage, brightness,
                                        colour temperature and sensor settings. Multiple
                                        streetlights reference the same configuration object.
                                    </p>
                                    <span class="street-state-label">REUSED BY MANY LIGHTS</span>
                                </div>
                            </div>

                            <div class="street-state-card unique">
                                <div class="street-state-icon">⌖</div>
                                <div>
                                    <strong>Individual streetlight · Extrinsic state</strong>
                                    <p>
                                        Unique information such as streetlight ID, district,
                                        installation location and current operating status.
                                        This information belongs to each individual light.
                                    </p>
                                    <span class="street-state-label">UNIQUE PER STREETLIGHT</span>
                                </div>
                            </div>

                        </div>
                    </div>
                </section>

                <section class="street-section" id="flyweightDemo">
                    <div class="street-section-heading">
                        <div>
                            <span class="street-eyebrow">03 / MEMORY EFFICIENCY</span>
                            <h3>Flyweight factory demonstration</h3>
                        </div>

                        <button class="street-btn street-btn-light"
                                type="button"
                                onclick="runFlyweightDemo()">
                            Recalculate ↻
                        </button>
                    </div>

                    <div class="street-panel">
                        <div class="street-memory-layout">

                            <div class="street-memory-visual">
                                <h4>How configuration reuse works</h4>

                                <div class="street-memory-flow">
                                    <div class="street-memory-node">
                                        <strong>Streetlight objects</strong>
                                        <span>Unique IDs, locations and status</span>
                                    </div>

                                    <div class="street-memory-arrow">→</div>

                                    <div class="street-memory-node shared">
                                        <strong>Flyweight factory</strong>
                                        <span>Returns an existing shared configuration</span>
                                    </div>
                                </div>

                                <div class="street-memory-flow" style="margin-top:10px;">
                                    <div class="street-memory-node">
                                        <strong>Individual light</strong>
                                        <span>Its own location and status</span>
                                    </div>

                                    <div class="street-memory-arrow">↘</div>

                                    <div class="street-memory-node shared">
                                        <strong>Shared configuration</strong>
                                        <span>One reusable configuration object</span>
                                    </div>
                                </div>

                                <p class="street-memory-caption">
                                    The Flyweight factory checks whether a configuration already
                                    exists. If it does, the existing object is reused instead of
                                    creating another identical configuration.
                                </p>
                            </div>

                            <div>
                                <div class="street-memory-stats">
                                    <div class="street-memory-stat">
                                        <span>LIGHT INSTANCES</span>
                                        <strong id="demoInstanceCount">—</strong>
                                        <small>Individual streetlights</small>
                                    </div>

                                    <div class="street-memory-stat">
                                        <span>SHARED OBJECTS</span>
                                        <strong id="demoSharedCount">—</strong>
                                        <small>Configurations used by lights</small>
                                    </div>

                                    <div class="street-memory-stat">
                                        <span>CONFIGURATION CREATIONS</span>
                                        <strong id="demoCreationCount">—</strong>
                                        <small>Distinct configurations in use</small>
                                    </div>

                                    <div class="street-memory-stat">
                                        <span>REUSE RATE</span>
                                        <strong id="demoReuseRate">—</strong>
                                        <small>Calculated from loaded records</small>
                                    </div>
                                </div>

                                <p class="street-memory-caption">
                                    These figures are calculated from database records.
                                    They illustrate configuration reuse and are not a measured
                                    JVM memory benchmark.
                                </p>
                            </div>

                        </div>
                    </div>
                </section>

                <section class="street-section">
                    <div class="street-section-heading">
                        <div>
                            <span class="street-eyebrow">04 / INDIVIDUAL OBJECTS</span>
                            <h3>Streetlight registry</h3>
                        </div>

                        <span class="street-muted" id="registryCount">
                            Loading records...
                        </span>
                    </div>

                    <div class="street-panel">
                        <div class="street-panel-header">
                            <div>
                                <h3>Installed streetlights</h3>
                                <span class="street-muted">
                                    Select a record to inspect its unique state and shared configuration.
                                </span>
                            </div>

                            <span class="street-tag">LIVE REGISTRY</span>
                        </div>

                        <div class="street-registry-tools">
                            <div class="street-search">
                                <input type="search"
                                       id="lightSearch"
                                       placeholder="Search by ID, street or district..."
                                       aria-label="Search streetlights">
                            </div>

                            <div class="street-filter">
                                <select id="statusFilter" aria-label="Filter by status">
                                    <option value="all">All statuses</option>
                                    <option value="Active">Active</option>
                                    <option value="Inactive">Offline</option>
                                </select>
                            </div>
                        </div>

                        <div class="street-table-wrap">
                            <table class="street-table">
                                <thead>
                                <tr>
                                    <th>Generated Streetlight ID</th>
                                    <th>Location</th>
                                    <th>District</th>
                                    <th>Configuration</th>
                                    <th>Status</th>
                                </tr>
                                </thead>

                                <tbody id="streetRegistryBody">
                                <tr>
                                    <td colspan="5" class="street-empty">
                                        Loading streetlight records...
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </section>

                <section class="street-section">
                    <div class="street-section-heading">
                        <div>
                            <span class="street-eyebrow">05 / OBJECT INSPECTOR</span>
                            <h3>Inspect and manage streetlight instances</h3>
                        </div>
                    </div>

                    <div class="street-lower-grid">

                        <article class="street-panel">
                            <div class="street-panel-header">
                                <div>
                                    <span class="street-eyebrow">SELECTED OBJECT</span>
                                    <h3>Streetlight inspector</h3>
                                </div>

                                <span class="street-tag">DETAIL VIEW</span>
                            </div>

                            <div class="street-inspector">
                                <div class="street-inspector-top">
                                    <strong id="inspectorTitle">Select a streetlight</strong>
                                    <span class="street-status" id="inspectorStatus">—</span>
                                </div>

                                <div class="street-inspector-grid">
                                    <div class="street-inspector-item">
                                        <span>Streetlight ID</span>
                                        <strong id="inspectId">—</strong>
                                    </div>

                                    <div class="street-inspector-item">
                                        <span>District</span>
                                        <strong id="inspectDistrict">—</strong>
                                    </div>

                                    <div class="street-inspector-item">
                                        <span>Street / Location</span>
                                        <strong id="inspectLocation">—</strong>
                                    </div>

                                    <div class="street-inspector-item">
                                        <span>Shared configuration</span>
                                        <strong id="inspectConfig">—</strong>
                                    </div>

                                    <div class="street-inspector-item">
                                        <span>Wattage</span>
                                        <strong id="inspectWattage">—</strong>
                                    </div>

                                    <div class="street-inspector-item">
                                        <span>Individual status</span>
                                        <strong id="inspectStatus">—</strong>
                                    </div>
                                    <div class="street-inspector-item">
                                        <span>Installation date</span>
                                        <strong id="inspectInstallationDate">—</strong>
                                    </div>

                                    <div class="street-inspector-item">
                                        <span>Latitude</span>
                                        <strong id="inspectLatitude">—</strong>
                                    </div>

                                    <div class="street-inspector-item">
                                        <span>Longitude</span>
                                        <strong id="inspectLongitude">—</strong>
                                    </div>
                                </div>
                            </div>

                            <p class="street-memory-caption">
                                The selected light owns its location and status while referencing
                                a reusable configuration.
                            </p>
                            <div class="street-form-actions street-record-actions">
                                <button class="street-btn street-btn-primary"
                                        id="editLightButton"
                                        type="button"
                                        onclick="editSelectedStreetlight()"
                                        disabled>
                                    Edit selected
                                </button>


                            </div>
                        </article>

                        <article class="street-panel" id="addLightPanel">
                            <div class="street-panel-header">
                                <div>
                                    <span class="street-eyebrow">CREATE INSTANCE</span>
                                    <h3 id="lightFormTitle">Add a streetlight</h3>
                                </div>

                                <span class="street-tag" id="lightFormTag">CREATE RECORD</span>
                            </div>

                            <form id="addLightForm">
                                <div class="street-form-grid">

                                    <div class="street-form-field">
                                        <label for="newDisplayId">Generated Streetlight ID</label>
                                        <input type="text"
                                               id="newDisplayId"
                                               value="Assigned after saving"
                                               readonly
                                               aria-readonly="true">
                                        <small class="street-optional">Automatically generated and stored as pole_code, e.g. SL-DEMO-001. This field cannot be edited.</small>
                                    </div>

                                    <div class="street-form-field">
                                        <label for="newDistrict">District</label>
                                        <select id="newDistrict" required>
                                            <option value="">Select district</option>
                                        </select>
                                    </div>

                                    <div class="street-form-field full">
                                        <label for="newLocation">Street / Location</label>
                                        <select id="newLocation" required disabled>
                                            <option value="">Select district first</option>
                                        </select>
                                    </div>

                                    <div class="street-form-field full">
                                        <label for="newConfig">Reuse configuration</label>
                                        <select id="newConfig" required>
                                            <option value="">Loading configurations...</option>
                                        </select>
                                    </div>

                                    <div class="street-form-field">
                                        <label for="newInstallationDate">Installation date <span class="street-optional">(optional)</span></label>
                                        <input type="date" id="newInstallationDate">
                                    </div>

                                    <div class="street-form-field">
                                        <label for="newStatus">Operational status</label>
                                        <select id="newStatus" required>
                                            <option value="">Loading statuses...</option>
                                        </select>
                                    </div>

                                    <div class="street-form-field">
                                        <label for="newLatitude">Latitude <span class="street-optional">(optional)</span></label>
                                        <input type="number" id="newLatitude" min="-90" max="90" step="any" placeholder="e.g. 6.9271000">
                                    </div>

                                    <div class="street-form-field">
                                        <label for="newLongitude">Longitude <span class="street-optional">(optional)</span></label>
                                        <input type="number" id="newLongitude" min="-180" max="180" step="any" placeholder="e.g. 79.8612000">
                                    </div>

                                </div>

                                <div class="street-form-actions">
                                    <button class="street-btn street-btn-primary"
                                            id="createLightButton"
                                            type="submit">
                                        + Create streetlight
                                    </button>

                                    <button class="street-btn street-btn-light"
                                            id="cancelEditButton"
                                            type="button"
                                            onclick="resetStreetlightForm()"
                                            style="display:none;">
                                        Cancel edit
                                    </button>

                                    <button class="street-btn street-btn-light"
                                            id="clearLightButton"
                                            type="reset">
                                        Clear
                                    </button>
                                </div>

                                <div id="addLightMessage"
                                     class="street-form-message"
                                     role="status"
                                     aria-live="polite"></div>
                            </form>
                        </article>

                    </div>
                </section>

                <footer class="street-page-footer">
                    <span>SMARTCITY360 · CITY INFRASTRUCTURE</span>
                    <span>Flyweight Pattern · Part D · Database-backed module</span>
                </footer>

            </div>
        </div>
    </main>
</div>

<script>
    "use strict";

    const API_URL = "<%= contextPath %>/api/streetlights";

    let streetlights = [];
    let configurations = [];
    let districts = [];
    let streets = [];
    let statuses = [];

    let selectedLightId = null;
    let isSaving = false;
    let editingLightId = null;

    const el = {
        metricLights: document.getElementById("metricLights"),
        metricConfigs: document.getElementById("metricConfigs"),
        metricActive: document.getElementById("metricActive"),
        metricReuse: document.getElementById("metricReuse"),

        configurationGrid: document.getElementById("configurationGrid"),
        configurationCount: document.getElementById("configurationCount"),

        demoInstanceCount: document.getElementById("demoInstanceCount"),
        demoSharedCount: document.getElementById("demoSharedCount"),
        demoCreationCount: document.getElementById("demoCreationCount"),
        demoReuseRate: document.getElementById("demoReuseRate"),

        registryBody: document.getElementById("streetRegistryBody"),
        registryCount: document.getElementById("registryCount"),
        lightSearch: document.getElementById("lightSearch"),
        statusFilter: document.getElementById("statusFilter"),

        inspectorTitle: document.getElementById("inspectorTitle"),
        inspectorStatus: document.getElementById("inspectorStatus"),
        inspectId: document.getElementById("inspectId"),
        inspectDistrict: document.getElementById("inspectDistrict"),
        inspectLocation: document.getElementById("inspectLocation"),
        inspectConfig: document.getElementById("inspectConfig"),
        inspectWattage: document.getElementById("inspectWattage"),
        inspectStatus: document.getElementById("inspectStatus"),
        inspectInstallationDate: document.getElementById("inspectInstallationDate"),
        inspectLatitude: document.getElementById("inspectLatitude"),
        inspectLongitude: document.getElementById("inspectLongitude"),
        editLightButton: document.getElementById("editLightButton"),

        addLightForm: document.getElementById("addLightForm"),
        newDisplayId: document.getElementById("newDisplayId"),
        newDistrict: document.getElementById("newDistrict"),
        newLocation: document.getElementById("newLocation"),
        newConfig: document.getElementById("newConfig"),
        newInstallationDate: document.getElementById("newInstallationDate"),
        newStatus: document.getElementById("newStatus"),
        newLatitude: document.getElementById("newLatitude"),
        newLongitude: document.getElementById("newLongitude"),
        lightFormTitle: document.getElementById("lightFormTitle"),
        lightFormTag: document.getElementById("lightFormTag"),
        cancelEditButton: document.getElementById("cancelEditButton"),
        clearLightButton: document.getElementById("clearLightButton"),
        addLightMessage: document.getElementById("addLightMessage"),
        createLightButton: document.getElementById("createLightButton"),

        apiError: document.getElementById("streetApiError")
    };

    function escapeHtml(value) {
        return String(value ?? "").replace(/[&<>"']/g, character => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        })[character]);
    }

    function showPageError(message) {
        el.apiError.textContent = message;
        el.apiError.style.display = "block";
    }

    function clearPageError() {
        el.apiError.textContent = "";
        el.apiError.style.display = "none";
    }

    function showFormMessage(message, isError = false) {
        el.addLightMessage.textContent = message;
        el.addLightMessage.classList.add("visible");
        el.addLightMessage.style.color = isError ? "#a33c32" : "";
        if (!isError && typeof showGlobalToast === "function") showGlobalToast(message, "success");
    }

    function clearFormMessage() {
        el.addLightMessage.textContent = "";
        el.addLightMessage.classList.remove("visible");
        el.addLightMessage.style.color = "";
    }

    function getStatusName(light) {
        return String(light.statusName ?? light.status ?? "").trim();
    }

    function normalizeStatus(status) {
        const value = String(status ?? "").trim().toLowerCase();

        if (value === "active") return "Active";
        if (value === "inactive" || value === "offline") return "Inactive";

        return value;
    }

    function getStatusDisplay(status) {
        const normalized = normalizeStatus(status);

        if (normalized === "Active") return "Active";
        if (normalized === "Inactive") return "Offline";

        return status || "Unknown";
    }

    function getStatusClass(status) {
        const normalized = normalizeStatus(status).toLowerCase();

        if (normalized === "active") return "active";
        if (normalized.includes("maintenance")) return "maintenance";
        return "offline";
    }

    function getConfigurationId(light) {
        return light.configurationId ??
            light.streetlightConfigurationsId ??
            light.streetlight_configurations_id ??
            null;
    }

    function getConfiguration(light) {
        const id = getConfigurationId(light);

        if (id == null) return null;

        return configurations.find(configuration =>
            Number(configuration.id) === Number(id)
        ) || null;
    }

    function getConfigurationName(light) {
        const configuration = getConfiguration(light);

        return configuration?.configName ||
            light.configName ||
            "Unknown configuration";
    }

    function getWattage(light) {
        const configuration = getConfiguration(light);

        const wattage =
            configuration?.powerRatingWatts ??
            light.powerRatingWatts ??
            light.wattage;

        if (wattage == null || wattage === "") return "—";

        return Number(wattage).toLocaleString() + "W";
    }

    function getConfigurationIcon(name) {
        const value = String(name || "").toLowerCase();

        if (value.includes("residential")) return "☼";
        if (value.includes("highway")) return "✧";
        if (value.includes("park")) return "♧";
        if (value.includes("commercial")) return "▦";

        return "⚙";
    }

    function formatNumber(value) {
        return Number(value || 0).toLocaleString();
    }

    async function apiRequest(url, options = {}) {
        const response = await fetch(url, {
            ...options,
            cache: "no-store",
            headers: {
                "Accept": "application/json",
                ...(options.body ? {
                    "Content-Type": "application/json"
                } : {}),
                ...(options.headers || {})
            }
        });

        const responseText = await response.text();

        let data;

        try {
            data = responseText ? JSON.parse(responseText) : {};
        } catch (error) {
            console.error("Invalid API response:", responseText);

            throw new Error(
                "The server returned a non-JSON response. " +
                "Check the Servlet mapping and Payara server log."
            );
        }

        if (!response.ok) {
            throw new Error(
                data.message ||
                "Server request failed. HTTP " + response.status
            );
        }

        if (data.success === false) {
            throw new Error(
                data.message || "The server could not complete the request."
            );
        }

        return data;
    }

    function setStreetlightConnection(connected) {
        const indicator = document.getElementById("streetlightConnectionIndicator");
        if (!indicator) return;
        indicator.innerHTML = connected
            ? '<span class="status-dot"></span> DATABASE CONNECTED'
            : '<span class="status-dot"></span> CONNECTION ERROR';
        indicator.classList.toggle("connection-error", !connected);
    }

    async function loadStreetlightData() {
        console.info("[Street Lighting] Loading database records...");
        console.info("[Street Lighting] API:", API_URL);

        el.registryBody.innerHTML = `
        <tr>
            <td colspan="5" class="street-empty">
                Loading streetlight records...
            </td>
        </tr>
    `;

        el.configurationGrid.innerHTML = `
        <div class="street-empty">
            Loading shared configurations...
        </div>
    `;

        try {
            const data = await apiRequest(API_URL);

            console.info("[Street Lighting] API response:", data);

            const requiredArrays = [
                "streetlights",
                "configurations",
                "districts",
                "streets",
                "statuses"
            ];

            for (const property of requiredArrays) {
                if (!Array.isArray(data[property])) {
                    throw new Error(
                        "Invalid API response: '" + property +
                        "' must be an array."
                    );
                }
            }

            setStreetlightConnection(true);
            streetlights = data.streetlights;
            configurations = data.configurations;
            districts = data.districts;
            streets = data.streets;
            statuses = data.statuses;

            console.info("[Street Lighting] Streetlights:", streetlights.length);
            console.info("[Street Lighting] Configurations:", configurations.length);
            console.info("[Street Lighting] Districts:", districts.length);
            console.info("[Street Lighting] Streets:", streets.length);

            populateDistrictDropdown();
            populateConfigurationDropdown();
            populateStatusDropdown();

            renderConfigurationCards();
            updateMetrics();
            renderRegistry();

            const selectedStillExists = streetlights.some(light =>
                Number(light.id) === Number(selectedLightId)
            );

            if (!selectedStillExists) {
                selectedLightId = streetlights.length > 0
                    ? Number(streetlights[0].id)
                    : null;
            }

            if (selectedLightId !== null) {
                selectStreetlight(selectedLightId);
            } else {
                clearInspector();
            }

            clearPageError();

            if (configurations.length === 0) {
                showPageError(
                    "The API connection succeeded, but no lighting configurations were found."
                );
            } else if (streetlights.length === 0) {
                showPageError(
                    "No streetlight records were found. You can add a streetlight using the form below."
                );
            }

        } catch (error) {
            setStreetlightConnection(false);
            console.error("[Street Lighting] Loading failed:", error);

            showPageError(
                "Unable to load street lighting data. " +
                error.message +
                " API: " + API_URL
            );

            streetlights = [];
            configurations = [];
            districts = [];
            streets = [];
            statuses = [];

            updateMetrics();
            renderConfigurationCards();
            renderRegistry();
            populateDistrictDropdown();
            populateConfigurationDropdown();
            populateStatusDropdown();
            clearInspector();
        }
    }

    function renderConfigurationCards() {
        if (configurations.length === 0) {
            el.configurationGrid.innerHTML = `
            <div class="street-empty">
                No lighting configurations found.
            </div>
        `;

            el.configurationCount.textContent = "0 configurations";
            return;
        }

        el.configurationCount.textContent =
            configurations.length + " shared configurations";

        el.configurationGrid.innerHTML = configurations.map(configuration => {
            const wattage = configuration.powerRatingWatts == null
                ? "Not specified"
                : Number(configuration.powerRatingWatts) + "W";

            const brightness = configuration.brightnessPercent == null
                ? "—"
                : Number(configuration.brightnessPercent) + "%";

            const temperature = Number(configuration.colorTemperature || 0);

            const temperatureLabel = temperature <= 3000
                ? "Warm white"
                : temperature <= 4000
                    ? "Neutral white"
                    : "Cool white";

            const sensorEnabled =
                configuration.motionSensorEnabled === true ||
                Number(configuration.motionSensorEnabled) === 1;

            return `
            <article class="street-config-card">
                <div class="street-config-icon">
                    ${getConfigurationIcon(configuration.configName)}
                </div>

                <h4>${escapeHtml(configuration.configName)}</h4>

                <p>
                    Reusable lighting configuration shared by streetlight instances.
                </p>

                <div class="street-config-specs">
                    <span class="street-spec">LED · ${escapeHtml(wattage)}</span>
                    <span class="street-spec">${temperatureLabel}</span>
                    <span class="street-spec">${escapeHtml(brightness)}</span>
                    <span class="street-spec">
                        Sensor ${sensorEnabled ? "On" : "Off"}
                    </span>
                </div>
            </article>
        `;
        }).join("");
    }

    function updateMetrics() {
        const totalLights = streetlights.length;
        const totalConfigurations = configurations.length;

        const activeLights = streetlights.filter(light =>
            normalizeStatus(getStatusName(light)) === "Active"
        ).length;

        const usedConfigurationIds = new Set();

        streetlights.forEach(light => {
            const id = getConfigurationId(light);

            if (id != null) {
                usedConfigurationIds.add(String(id));
            }
        });

        const usedCount = usedConfigurationIds.size;

        const averageReuse = usedCount > 0
            ? totalLights / usedCount
            : 0;

        const reuseRate = totalLights > 0
            ? ((totalLights - usedCount) / totalLights) * 100
            : 0;

        el.metricLights.textContent = formatNumber(totalLights);
        el.metricConfigs.textContent = formatNumber(totalConfigurations);
        el.metricActive.textContent = formatNumber(activeLights);
        el.metricReuse.textContent = averageReuse.toFixed(1) + "×";

        el.demoInstanceCount.textContent = formatNumber(totalLights);
        el.demoSharedCount.textContent = formatNumber(usedCount);
        el.demoCreationCount.textContent = formatNumber(usedCount);
        el.demoReuseRate.textContent = reuseRate.toFixed(1) + "%";
    }

    function runFlyweightDemo() {
        updateMetrics();

        document.getElementById("flyweightDemo").scrollIntoView({
            behavior: "smooth",
            block: "center"
        });
    }

    function formatStreetlightId(databaseId) {
        return "SL-DEMO-" + String(Number(databaseId)).padStart(3, "0");
    }

    function renderRegistry() {
        const search = el.lightSearch.value.trim().toLowerCase();
        const selectedStatus = el.statusFilter.value;

        const filteredLights = streetlights.filter(light => {
            const searchableText = [
                light.displayId,
                light.poleCode,
                light.streetName,
                light.districtName,
                getConfigurationName(light)
            ].join(" ").toLowerCase();

            const matchesSearch = searchableText.includes(search);

            const matchesStatus =
                selectedStatus === "all" ||
                normalizeStatus(getStatusName(light)) === normalizeStatus(selectedStatus);

            return matchesSearch && matchesStatus;
        });

        el.registryCount.textContent =
            "Showing " + filteredLights.length +
            " of " + streetlights.length + " records";

        if (filteredLights.length === 0) {
            el.registryBody.innerHTML = `
            <tr>
                <td colspan="5" class="street-empty">
                    ${streetlights.length == 0
                        ? "No streetlight records available."
                        : "No matching streetlights found."}
                </td>
            </tr>
        `;
            return;
        }

        el.registryBody.innerHTML = filteredLights.map(light => {
            const selected =
                Number(selectedLightId) === Number(light.id)
                    ? "selected"
                    : "";

            const status = getStatusName(light);

            return `
            <tr class="${selected}"
                data-light-id="${Number(light.id)}"
                tabindex="0"
                role="button"
                aria-label="Inspect ${escapeHtml(light.poleCode)}">

                <td class="street-id">
                    <strong>${escapeHtml(light.poleCode || light.displayId || formatStreetlightId(light.id))}</strong>
                </td>

                <td>${escapeHtml(light.streetName || "—")}</td>

                <td>${escapeHtml(light.districtName || "—")}</td>

                <td>${escapeHtml(getConfigurationName(light))}</td>

                <td>
                    <span class="street-status ${getStatusClass(status)}">
                        ${escapeHtml(getStatusDisplay(status))}
                    </span>
                </td>
            </tr>
        `;
        }).join("");
    }

    function selectStreetlight(databaseId) {
        const light = streetlights.find(item =>
            Number(item.id) === Number(databaseId)
        );

        if (!light) {
            clearInspector();
            return;
        }

        if (editingLightId !== null &&
            Number(editingLightId) !== Number(light.id)) {
            resetStreetlightForm();
        }

        selectedLightId = Number(light.id);

        const status = getStatusName(light);

        el.inspectorTitle.textContent =
            (light.displayId || formatStreetlightId(light.id)) +
            " · " + (light.streetName || "Unknown street");

        el.inspectorStatus.textContent = getStatusDisplay(status);
        el.inspectorStatus.className =
            "street-status " + getStatusClass(status);

        el.inspectId.textContent =
            light.poleCode || light.displayId || formatStreetlightId(light.id);
        el.inspectDistrict.textContent = light.districtName || "—";
        el.inspectLocation.textContent = light.streetName || "—";
        el.inspectConfig.textContent = getConfigurationName(light);
        el.inspectWattage.textContent = getWattage(light);
        el.inspectStatus.textContent = getStatusDisplay(status);
        el.inspectInstallationDate.textContent =
            light.installationDate || "Not provided";
        el.inspectLatitude.textContent =
            light.latitude == null ? "Not provided" : String(light.latitude);
        el.inspectLongitude.textContent =
            light.longitude == null ? "Not provided" : String(light.longitude);

        el.editLightButton.disabled = false;


        renderRegistry();
    }

    function clearInspector() {
        selectedLightId = null;

        el.inspectorTitle.textContent = "No streetlight selected";

        el.inspectorStatus.textContent = "—";
        el.inspectorStatus.className = "street-status";

        el.inspectId.textContent = "—";
        el.inspectDistrict.textContent = "—";
        el.inspectLocation.textContent = "—";
        el.inspectConfig.textContent = "—";
        el.inspectWattage.textContent = "—";
        el.inspectStatus.textContent = "—";
        el.inspectInstallationDate.textContent = "—";
        el.inspectLatitude.textContent = "—";
        el.inspectLongitude.textContent = "—";

        el.editLightButton.disabled = true;
    }

    function populateDistrictDropdown() {
        el.newDistrict.innerHTML =
            '<option value="">Select district</option>';

        districts.forEach(district => {
            const option = document.createElement("option");

            option.value = district.id;
            option.textContent = district.districtName;

            el.newDistrict.appendChild(option);
        });

        el.newLocation.innerHTML =
            '<option value="">Select district first</option>';

        el.newLocation.disabled = true;
    }

    function loadStreetsForDistrict() {
        const districtId = Number(el.newDistrict.value);

        el.newLocation.innerHTML =
            '<option value="">Select street</option>';

        if (!districtId) {
            el.newLocation.disabled = true;
            el.newLocation.innerHTML =
                '<option value="">Select district first</option>';
            return;
        }

        const districtStreets = streets.filter(street =>
            Number(street.districtId) === districtId
        );

        if (districtStreets.length === 0) {
            el.newLocation.disabled = true;
            el.newLocation.innerHTML =
                '<option value="">No streets available</option>';
            return;
        }

        districtStreets.forEach(street => {
            const option = document.createElement("option");

            option.value = street.id;
            option.textContent = street.streetName;

            el.newLocation.appendChild(option);
        });

        el.newLocation.disabled = false;
    }

    function populateConfigurationDropdown() {
        el.newConfig.innerHTML =
            '<option value="">Select configuration</option>';

        configurations.forEach(configuration => {
            const option = document.createElement("option");

            option.value = configuration.id;
            option.textContent = configuration.configName;

            el.newConfig.appendChild(option);
        });

        el.newConfig.disabled = configurations.length === 0;
    }

    function populateStatusDropdown() {
        el.newStatus.innerHTML =
            '<option value="">Select status</option>';

        el.statusFilter.innerHTML =
            '<option value="all">All statuses</option>';

        statuses.forEach(status => {
            const option = document.createElement("option");
            option.value = status.id;
            option.textContent = status.name;
            el.newStatus.appendChild(option);

            const filterOption = document.createElement("option");
            filterOption.value = status.name;
            filterOption.textContent =
                status.name.toLowerCase() === "inactive"
                    ? "Offline"
                    : status.name;
            el.statusFilter.appendChild(filterOption);
        });

        el.newStatus.disabled = statuses.length === 0;
    }

    async function addStreetlight(event) {
        event.preventDefault();

        if (isSaving) return;

        const streetId = Number(el.newLocation.value);
        const configurationId = Number(el.newConfig.value);
        const statusId = Number(el.newStatus.value);
        const latitudeText = el.newLatitude.value.trim();
        const longitudeText = el.newLongitude.value.trim();

        if (!streetId || !configurationId || !statusId) {
            showFormMessage("Please complete all required fields.", true);
            return;
        }

        if ((latitudeText && !longitudeText) ||
            (!latitudeText && longitudeText)) {
            showFormMessage(
                "Enter both latitude and longitude, or leave both empty.",
                true
            );
            return;
        }

        const latitude = latitudeText ? Number(latitudeText) : null;
        const longitude = longitudeText ? Number(longitudeText) : null;

        if (latitude !== null &&
            (!Number.isFinite(latitude) || latitude < -90 || latitude > 90)) {
            showFormMessage("Latitude must be between -90 and 90.", true);
            return;
        }

        if (longitude !== null &&
            (!Number.isFinite(longitude) || longitude < -180 || longitude > 180)) {
            showFormMessage("Longitude must be between -180 and 180.", true);
            return;
        }

        const payload = {
            streetId: streetId,
            configurationId: configurationId,
            installationDate: el.newInstallationDate.value || null,
            latitude: latitude,
            longitude: longitude,
            statusId: statusId
        };

        const isEditing = editingLightId !== null;
        if (isEditing) payload.id = editingLightId;

        isSaving = true;
        el.createLightButton.disabled = true;
        el.createLightButton.textContent = isEditing ? "Updating..." : "Saving...";
        clearFormMessage();

        try {
            const result = await apiRequest(API_URL, {
                method: isEditing ? "PUT" : "POST",
                body: JSON.stringify(payload)
            });

            const savedId = isEditing ? editingLightId : result.id;
            await loadStreetlightData();

            if (savedId !== null && savedId !== undefined) {
                selectedLightId = Number(savedId);
                selectStreetlight(savedId);
            }

            resetStreetlightForm();
            if (!isEditing && result.displayId) {
                el.newDisplayId.value = result.displayId;
            }
            showFormMessage(
                result.message ||
                (isEditing
                    ? "Streetlight updated successfully."
                    : "Streetlight " + (result.displayId || "") + " saved successfully.")
            );

        } catch (error) {
            console.error("[Street Lighting] Save failed:", error);
            showFormMessage(error.message, true);

        } finally {
            isSaving = false;
            el.createLightButton.disabled = false;
            el.createLightButton.textContent = editingLightId !== null
                ? "Save changes"
                : "+ Create streetlight";
        }
    }


    function editSelectedStreetlight() {
        const light = streetlights.find(item =>
            Number(item.id) === Number(selectedLightId)
        );

        if (!light) {
            showFormMessage("Select a streetlight before editing.", true);
            return;
        }

        editingLightId = Number(light.id);

        el.lightFormTitle.textContent = "Edit streetlight";
        el.lightFormTag.textContent = "UPDATE RECORD";
        el.createLightButton.textContent = "Save changes";
        el.cancelEditButton.style.display = "inline-flex";
        el.clearLightButton.style.display = "none";

        el.newDisplayId.value =
            light.poleCode || light.displayId || formatStreetlightId(light.id);

        const selectedStreet = streets.find(street =>
            Number(street.id) === Number(light.streetId)
        );

        if (selectedStreet) {
            el.newDistrict.value = String(selectedStreet.districtId);
            loadStreetsForDistrict();
        }

        el.newLocation.value = String(light.streetId || "");
        el.newConfig.value = String(getConfigurationId(light) || "");
        el.newInstallationDate.value = light.installationDate || "";
        el.newLatitude.value = light.latitude == null ? "" : light.latitude;
        el.newLongitude.value = light.longitude == null ? "" : light.longitude;
        el.newStatus.value = String(light.statusId || "");

        clearFormMessage();

        document.getElementById("addLightPanel").scrollIntoView({
            behavior: "smooth",
            block: "center"
        });

        el.newDistrict.focus();
    }


    function resetStreetlightForm() {
        editingLightId = null;
        el.addLightForm.reset();

        el.lightFormTitle.textContent = "Add a streetlight";
        el.lightFormTag.textContent = "CREATE RECORD";
        el.newDisplayId.value = "Assigned after saving";
        el.createLightButton.textContent = "+ Create streetlight";
        el.cancelEditButton.style.display = "none";
        el.clearLightButton.style.display = "inline-flex";

        el.newLocation.disabled = true;
        el.newLocation.innerHTML =
            '<option value="">Select district first</option>';

        clearFormMessage();
    }


    async function deleteSelectedStreetlight() {
        const light = streetlights.find(item =>
            Number(item.id) === Number(selectedLightId)
        );

        if (!light) {
            alert("Select a streetlight before deleting.");
            return;
        }

        const confirmed = window.confirm(
            "Delete streetlight " + light.poleCode + "?\n\n" +
            "This permanently removes this streetlight record. " +
            "Its shared Flyweight configuration will not be deleted."
        );

        if (!confirmed || isSaving) return;

        isSaving = true;


        try {
            const result = await apiRequest(
                API_URL + "?id=" + encodeURIComponent(light.id),
                { method: "DELETE" }
            );

            selectedLightId = null;
            resetStreetlightForm();
            await loadStreetlightData();
            showFormMessage(result.message || "Streetlight deleted successfully.");

        } catch (error) {
            console.error("[Street Lighting] Delete failed:", error);
            showFormMessage(error.message, true);

        } finally {
            isSaving = false;

        }
    }

    function focusAddForm() {
        resetStreetlightForm();
        document.getElementById("addLightPanel").scrollIntoView({
            behavior: "smooth",
            block: "center"
        });

        el.newDistrict.focus();
    }

    function showHelp() {
        alert(
            "STREET LIGHTING — FLYWEIGHT PATTERN\n\n" +
            "Intrinsic state: shared lighting configuration, wattage, " +
            "brightness, colour temperature and sensor settings.\n\n" +
            "Extrinsic state: generated Streetlight ID, street, district and status.\n\n" +
            "The Flyweight factory reuses common configuration objects."
        );
    }

    el.lightSearch.addEventListener("input", renderRegistry);

    el.statusFilter.addEventListener("change", renderRegistry);

    el.newDistrict.addEventListener(
        "change",
        loadStreetsForDistrict
    );

    el.addLightForm.addEventListener(
        "submit",
        addStreetlight
    );

    el.addLightForm.addEventListener("reset", () => {
        setTimeout(() => {
            if (editingLightId !== null) {
                resetStreetlightForm();
                return;
            }

            el.newLocation.disabled = true;
            el.newLocation.innerHTML =
                '<option value="">Select district first</option>';

            clearFormMessage();
        }, 0);
    });

    el.registryBody.addEventListener("click", event => {
        const row = event.target.closest("tr[data-light-id]");

        if (row) {
            selectStreetlight(Number(row.dataset.lightId));
        }
    });

    el.registryBody.addEventListener("keydown", event => {
        if (event.key !== "Enter" && event.key !== " ") return;

        const row = event.target.closest("tr[data-light-id]");

        if (row) {
            event.preventDefault();
            selectStreetlight(Number(row.dataset.lightId));
        }
    });


    console.info("====================================");
    console.info("SMARTCITY360 - STREET LIGHTING");
    console.info("JavaScript successfully started");
    console.info("API endpoint:", API_URL);
    console.info("====================================");

    loadStreetlightData();

</script>

<jsp:include page="/includes/global-toast.jsp" />
</body>
</html>
