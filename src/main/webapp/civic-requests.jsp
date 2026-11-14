<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    request.setAttribute("activePage", "complaints");

    if (session.getAttribute("userId") == null) {
        response.sendRedirect(contextPath + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>SmartCity360 | Civic Requests</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/living-city.css">

    <link rel="stylesheet"
          href="<%= contextPath %>/css/civic-requests.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/dashboard.css">
</head>

<body>

<div class="app-layout">

    <jsp:include page="/includes/sidebar.jsp"/>

    <main class="main-shell">

        <header class="topbar">

            <div class="topbar-heading">
                <span class="breadcrumb">
                    SMART CITY / BEHAVIOURAL PATTERNS
                </span>

                <h1>Civic requests</h1>
            </div>

            <div class="topbar-actions">

                <div class="environment-chip" id="civicConnectionIndicator">
                    <span class="status-dot"></span>
                    CONNECTING TO DATABASE
                </div>

                <button class="help-button"
                        type="button"
                        onclick="showHelp()"
                        aria-label="Help">
                    ?
                </button>

            </div>

        </header>

        <div class="content-scroll">

            <div class="page-content civic-page">

                <section class="civic-hero">

                    <div class="hero-content">

                        <div class="hero-kicker">
                            <span class="hero-kicker-line"></span>
                            CHAIN OF RESPONSIBILITY
                        </div>

                        <h2>
                            Every concern deserves<br>
                            <span>the right response.</span>
                        </h2>

                        <p>
                            A structured civic complaint management system
                            that routes civic requests through the right
                            authority based on severity.
                        </p>

                        <div class="hero-actions">

                            <a href="#submit-request"
                               class="civic-primary-btn">
                                Submit a request <span>↗</span>
                            </a>

                            <a href="#handler-chain"
                               class="civic-secondary-btn">
                                Explore the workflow <span>↓</span>
                            </a>

                        </div>

                    </div>

                    <div class="hero-visual">

                        <div class="hero-orbit orbit-one"></div>
                        <div class="hero-orbit orbit-two"></div>

                        <div class="hero-center">
                            <span class="hero-center-icon">⌖</span>
                            <span class="hero-center-label">
                                CITY<br>RESPONSE
                            </span>
                        </div>

                        <div class="orbit-node node-one">
                            <span>01</span>
                            <small>LOCAL</small>
                        </div>

                        <div class="orbit-node node-two">
                            <span>02</span>
                            <small>SUPERVISOR</small>
                        </div>

                        <div class="orbit-node node-three">
                            <span>03</span>
                            <small>MANAGER</small>
                        </div>

                        <div class="hero-visual-caption">
                            CONNECTED HANDLERS
                        </div>

                    </div>

                    <div class="hero-bottom">
                        <span>
                            <i class="mini-dot"></i>
                            CITIZEN-CENTRED SERVICE
                        </span>

                        <span>
                            SMARTCITY360 <b>/</b> CIVIC OPERATIONS
                        </span>
                    </div>

                </section>

                <div class="demo-notice">

                    <span class="notice-icon">i</span>

                    <div>
                        <strong>Connected complaint management</strong>

                        <p>
                            Complaints are submitted to the database and
                            assigned through the Java Chain of Responsibility
                            implementation.
                        </p>
                    </div>

                    <span class="demo-tag">LIVE DATA</span>

                </div>

                <section class="civic-metrics">

                    <article class="civic-metric">

                        <div class="metric-topline">
                            <span class="metric-icon">◷</span>
                            <span class="metric-trend">QUEUE</span>
                        </div>

                        <span class="metric-number" id="openCount">0</span>

                        <span class="metric-name">Open requests</span>

                        <span class="metric-foot">
                            Awaiting initial action
                        </span>

                    </article>


                    <article class="civic-metric">

                        <div class="metric-topline">
                            <span class="metric-icon">⇄</span>
                            <span class="metric-trend">ACTIVE</span>
                        </div>

                        <span class="metric-number" id="progressCount">0</span>

                        <span class="metric-name">In progress</span>

                        <span class="metric-foot">
                            Under authority review
                        </span>

                    </article>


                    <article class="civic-metric">

                        <div class="metric-topline">
                            <span class="metric-icon">✓</span>
                            <span class="metric-trend">COMPLETED</span>
                        </div>

                        <span class="metric-number" id="resolvedCount">0</span>

                        <span class="metric-name">Resolved requests</span>

                        <span class="metric-foot">
                            Successfully completed
                        </span>

                    </article>


                    <article class="civic-metric accent-metric">

                        <div class="metric-topline">
                            <span class="metric-icon">⌁</span>
                            <span class="metric-trend">ROUTING</span>
                        </div>

                        <span class="metric-number">03</span>

                        <span class="metric-name">Handler levels</span>

                        <span class="metric-foot">
                            Local · Supervisor · Manager
                        </span>

                    </article>

                </section>

                <section class="civic-section" id="handler-chain">

                    <div class="section-heading">

                        <div>
                            <span class="section-eyebrow">
                                01 / THE DESIGN PATTERN
                            </span>

                            <h3>Complaint handling chain</h3>

                            <p>
                                Each handler evaluates the request and either
                                handles it or forwards it.
                            </p>
                        </div>

                        <span class="section-status">
                            <i></i> WORKFLOW MODEL
                        </span>

                    </div>


                    <div class="handler-panel">

                        <div class="handler-panel-top">

                            <div>
                                <span class="small-label">
                                    REQUEST FLOW
                                </span>

                                <h4>
                                    From first contact to city-level action
                                </h4>
                            </div>

                            <span class="chain-count">03 HANDLERS</span>

                        </div>


                        <div class="handler-chain">

                            <article class="handler-card">

                                <div class="handler-card-head">
                                    <span class="handler-index">01</span>
                                    <span class="handler-symbol">⌂</span>
                                </div>

                                <span class="handler-level">LEVEL ONE</span>

                                <h4>Local staff</h4>

                                <p>
                                    First point of contact for everyday
                                    neighbourhood concerns.
                                </p>

                                <div class="handler-rule">
                                    <span class="rule-dot"></span>
                                    <span>Handles low severity</span>
                                </div>

                                <div class="handler-footer">
                                    INITIAL REVIEW
                                </div>

                            </article>


                            <div class="chain-connector">
                                <span>IF NOT HANDLED</span>
                                <b>→</b>
                            </div>


                            <article class="handler-card">

                                <div class="handler-card-head">
                                    <span class="handler-index">02</span>
                                    <span class="handler-symbol">◈</span>
                                </div>

                                <span class="handler-level">LEVEL TWO</span>

                                <h4>Supervisor</h4>

                                <p>
                                    Reviews more serious concerns and
                                    unresolved local requests.
                                </p>

                                <div class="handler-rule">
                                    <span class="rule-dot"></span>
                                    <span>Handles medium severity</span>
                                </div>

                                <div class="handler-footer">
                                    SECONDARY REVIEW
                                </div>

                            </article>


                            <div class="chain-connector">
                                <span>IF NOT HANDLED</span>
                                <b>→</b>
                            </div>


                            <article class="handler-card manager-card">

                                <div class="handler-card-head">
                                    <span class="handler-index">03</span>
                                    <span class="handler-symbol">⌂</span>
                                </div>

                                <span class="handler-level">LEVEL THREE</span>

                                <h4>City manager</h4>

                                <p>
                                    Receives high-impact and critical matters
                                    requiring senior attention.
                                </p>

                                <div class="handler-rule">
                                    <span class="rule-dot"></span>
                                    <span>Handles high / critical</span>
                                </div>

                                <div class="handler-footer">
                                    FINAL ESCALATION
                                </div>

                            </article>

                        </div>


                        <div class="chain-explanation">

                            <span class="explanation-icon">↳</span>

                            <p>
                                <strong>Pattern principle:</strong>
                                The sender does not need to know which handler
                                will resolve the complaint. Each handler
                                decides whether to process the request or
                                pass it along the chain.
                            </p>

                        </div>

                    </div>

                </section>

                <section class="civic-workspace">

                    <article class="civic-card request-form-card"
                             id="submit-request">

                        <div class="card-heading">

                            <div>
                                <span class="section-eyebrow">
                                    02 / CIVIC SERVICES
                                </span>

                                <h3>Submit a civic request</h3>

                                <p>
                                    Enter a complaint and preview its
                                    escalation route.
                                </p>
                            </div>

                            <span class="card-icon">＋</span>

                        </div>


                        <form id="requestForm">

                            <div class="civic-field">

                                <label for="complaintTitle">
                                    Complaint title
                                </label>

                                <input
                                        type="text"
                                        id="complaintTitle"
                                        placeholder="e.g. Damaged road near the main junction"
                                        required
                                        maxlength="180">

                            </div>

                            <div class="civic-field">

                                <label for="requestCategory">
                                    Request category
                                </label>

                                <select id="requestCategory" required>

                                    <option value="">
                                        Loading categories...
                                    </option>

                                </select>

                            </div>

                            <div class="civic-field">

                                <label for="requestDistrict">
                                    District
                                </label>

                                <select id="requestDistrict" required>

                                    <option value="">
                                        Select district
                                    </option>

                                </select>

                            </div>

                            <div class="civic-field">

                                <label for="requestStreet">
                                    Street
                                </label>

                                <select id="requestStreet" required disabled>

                                    <option value="">
                                        Select a district first
                                    </option>

                                </select>

                            </div>
                            <div class="civic-field">

                                <label for="requestBuilding">
                                    Building <span style="font-weight:400; opacity:.65;">(Optional)</span>
                                </label>

                                <select id="requestBuilding" disabled>

                                    <option value="">
                                        Select a street first
                                    </option>

                                </select>

                                <span class="field-hint">
        Select a specific building if the complaint relates to one.
        Otherwise, leave this field empty.
    </span>

                            </div>

                            <div class="civic-field">

                                <label for="requestDescription">
                                    Complaint description
                                </label>

                                <textarea
                                        id="requestDescription"
                                        rows="3"
                                        placeholder="Describe the issue and its impact..."
                                        required
                                        maxlength="500"></textarea>

                                <span class="field-hint">
                                    Provide enough detail to help the assigned handler.
                                </span>

                            </div>

                            <div class="civic-field">

                                <label>Severity level</label>

                                <div class="severity-options">

                                    <label class="severity-option severity-low">

                                        <input
                                                type="radio"
                                                name="severity"
                                                value="low"
                                                checked>

                                        <span class="severity-choice">
                                            <b>Low</b>
                                            <small>Routine issue</small>
                                        </span>

                                    </label>


                                    <label class="severity-option severity-medium">

                                        <input
                                                type="radio"
                                                name="severity"
                                                value="medium">

                                        <span class="severity-choice">
                                            <b>Medium</b>
                                            <small>Needs review</small>
                                        </span>

                                    </label>


                                    <label class="severity-option severity-high">

                                        <input
                                                type="radio"
                                                name="severity"
                                                value="high">

                                        <span class="severity-choice">
                                            <b>High</b>
                                            <small>Major impact</small>
                                        </span>

                                    </label>


                                    <label class="severity-option severity-critical">

                                        <input
                                                type="radio"
                                                name="severity"
                                                value="critical">

                                        <span class="severity-choice">
                                            <b>Critical</b>
                                            <small>Urgent issue</small>
                                        </span>

                                    </label>

                                </div>

                            </div>

                            <div id="formMessage"
                                 role="status"
                                 aria-live="polite"
                                 style="display:none; margin:12px 0; padding:12px;
                                        font-size:13px; line-height:1.5;">
                            </div>


                            <button type="submit"
                                    class="civic-submit-btn"
                                    id="submitButton">

                                Submit request <span>↗</span>

                            </button>


                            <p class="form-disclaimer">
                                Your complaint will be saved securely
                                and assigned to the appropriate authority.
                            </p>

                        </form>

                    </article>

                    <article class="civic-card route-card">

                        <div class="card-heading">

                            <div>
                                <span class="section-eyebrow">
                                    03 / PATTERN SIMULATOR
                                </span>

                                <h3>Escalation simulator</h3>

                                <p>
                                    Test how severity changes the selected handler.
                                </p>
                            </div>

                            <span class="card-icon">⌁</span>

                        </div>


                        <div class="simulator-select">

                            <label for="complaintPriority">
                                Choose complaint severity
                            </label>

                            <select id="complaintPriority"
                                    onchange="testEscalation()">

                                <option value="low">
                                    Low — routine issue
                                </option>

                                <option value="medium">
                                    Medium — needs review
                                </option>

                                <option value="high">
                                    High — major impact
                                </option>

                                <option value="critical">
                                    Critical — urgent issue
                                </option>

                            </select>

                        </div>


                        <div class="route-preview" id="routePreview">

                            <div class="route-preview-top">

                                <span>SELECTED ROUTE</span>

                                <span class="route-priority low"
                                      id="routePriority">
                                    LOW
                                </span>

                            </div>

                            <div class="route-steps" id="routeSteps"></div>

                            <div class="route-result"
                                 id="escalationResult"
                                 aria-live="polite">
                                Local staff is the responsible handler for this request.
                            </div>

                        </div>


                        <div class="simulator-note">

                            <span>ⓘ</span>

                            <p>
                                The Java Chain of Responsibility implementation
                                determines the assignment route when a complaint
                                is submitted.
                            </p>

                        </div>

                    </article>

                </section>

                <section class="civic-section queue-section">

                    <div class="section-heading">

                        <div>
                            <span class="section-eyebrow">
                                04 / REQUEST MONITORING
                            </span>

                            <h3>Recent civic requests</h3>

                            <p>
                                Search and monitor submitted civic complaints.
                            </p>
                        </div>

                        <span class="queue-total">
                            <b id="queueCount">00</b> VISIBLE ITEMS
                        </span>

                    </div>


                    <div class="queue-panel">

                        <div class="queue-toolbar">

                            <div class="queue-search">

                                <span>⌕</span>

                                <input
                                        type="search"
                                        id="requestSearch"
                                        placeholder="Search requests..."
                                        oninput="renderRequests()">

                            </div>


                            <select id="statusFilter"
                                    onchange="renderRequests()"
                                    aria-label="Filter by status">

                                <option value="all">All statuses</option>
                                <option value="Open">Open</option>
                                <option value="In Progress">In progress</option>
                                <option value="Resolved">Resolved</option>
                                <option value="Rejected">Rejected</option>

                            </select>

                        </div>


                        <div class="request-table-wrap">

                            <table class="request-table">

                                <thead>
                                <tr>
                                    <th>REQUEST ID</th>
                                    <th>SUBMITTED BY / CATEGORY</th>
                                    <th>LOCATION</th>
                                    <th>SEVERITY</th>
                                    <th>ASSIGNED HANDLER</th>
                                    <th>STATUS</th>
                                </tr>
                                </thead>

                                <tbody id="requestTableBody"></tbody>

                            </table>

                        </div>


                        <div class="empty-requests" id="emptyRequests">

                            <span>⌕</span>

                            <strong>No requests loaded</strong>

                            <p>
                                Database-backed request monitoring
                                will appear here when the queue endpoint
                                is connected.
                            </p>

                        </div>


                        <div class="queue-footer">

                            <span>
                                <i class="mini-dot"></i>
                                DATABASE REQUESTS
                            </span>

                            <span>
                                Complaint monitoring
                            </span>

                        </div>

                    </div>

                </section>

                <section class="civic-bottom-grid">

                    <article class="civic-card activity-card">

                        <div class="card-heading">

                            <div>
                                <span class="section-eyebrow">
                                    05 / ROUTING ACTIVITY
                                </span>

                                <h3>Escalation activity</h3>
                            </div>

                            <span class="live-indicator">
                                <i></i> ROUTING LOG
                            </span>

                        </div>


                        <div class="activity-list" id="activityList">

                            <div class="activity-item">

                                <span class="activity-marker marker-green"></span>

                                <div>
                                    <strong>Ready for database activity</strong>

                                    <p>
                                        Assignment and escalation records
                                        will appear here.
                                    </p>

                                    <small>
                                        WAITING FOR ACTIVITY DATA
                                    </small>
                                </div>

                            </div>

                        </div>

                    </article>



                    <article class="civic-card pattern-info-card">

                        <span class="section-eyebrow">
                            06 / IMPLEMENTATION GUIDE
                        </span>

                        <h3>How the pattern works</h3>

                        <p>
                            The Chain of Responsibility separates the sender
                            of a request from the object that handles it.
                        </p>


                        <div class="pattern-code">

                            <div>
                                <span>01</span>
                                Complaint Request
                            </div>

                            <div>
                                <span>02</span>
                                Local Staff Handler
                            </div>

                            <div>
                                <span>03</span>
                                Supervisor Handler
                            </div>

                            <div>
                                <span>04</span>
                                City Manager Handler
                            </div>

                        </div>


                        <div class="pattern-benefit">

                            <span>✳</span>

                            <p>
                                <strong>Design benefit</strong>

                                New handler levels can be added without
                                changing the complaint sender.
                            </p>

                        </div>

                    </article>

                </section>



                <footer class="page-footer">

                    <span>SMARTCITY360</span>

                    <span>
                        Chain of Responsibility · Civic Operations
                    </span>

                </footer>

            </div>

        </div>

    </main>

</div>


<script>
    const contextPath = "<%= contextPath %>";
    const API_URL = contextPath + "/api/complaints";

    let categoryOptions = [];
    let priorityOptions = [];
    let districtOptions = [];

    let complaintRequests = [];
    let isSubmitting = false;

    const handlerRoutes = {

        low: {
            label: "LOW",
            handler: "Local staff",
            explanation:
                "Local staff is the responsible handler for this request.",
            steps: ["Local staff"]
        },

        medium: {
            label: "MEDIUM",
            handler: "Supervisor",
            explanation:
                "The request is assigned through Local Staff to the Supervisor.",
            steps: ["Local staff", "Supervisor"]
        },

        high: {
            label: "HIGH",
            handler: "City manager",
            explanation:
                "The request is assigned through the chain to the City Manager.",
            steps: ["Local staff", "Supervisor", "City manager"]
        },

        critical: {
            label: "CRITICAL",
            handler: "City manager",
            explanation:
                "Critical severity reaches the City Manager for urgent attention.",
            steps: ["Local staff", "Supervisor", "City manager"]
        }

    };

    function buildRouteSteps(priority) {

        const route = handlerRoutes[priority];
        const container = document.getElementById("routeSteps");

        container.replaceChildren();

        route.steps.forEach((step, index) => {

            const item = document.createElement("div");
            item.className = "route-step";

            const number = document.createElement("span");
            number.className = "route-step-number";
            number.textContent = "0" + (index + 1);

            const details = document.createElement("div");
            details.className = "route-step-details";

            const name = document.createElement("strong");
            name.textContent = step;

            const state = document.createElement("small");

            state.textContent =
                index === route.steps.length - 1
                    ? "RESPONSIBLE HANDLER"
                    : "FORWARD IF NOT HANDLED";

            details.append(name, state);
            item.append(number, details);

            if (index === route.steps.length - 1) {

                const current = document.createElement("span");
                current.className = "route-current";
                current.textContent = "SELECTED";

                item.appendChild(current);
            }

            container.appendChild(item);
        });
    }


    function testEscalation() {

        const priority =
            document.getElementById("complaintPriority").value;

        const route = handlerRoutes[priority];

        if (!route) {
            return;
        }

        const badge = document.getElementById("routePriority");

        badge.textContent = route.label;
        badge.className = "route-priority " + priority;

        buildRouteSteps(priority);

        document.getElementById("escalationResult").textContent =
            route.explanation;
    }

    function showFormMessage(message, success) {

        const messageBox = document.getElementById("formMessage");

        messageBox.style.display = "block";

        messageBox.style.background =
            success ? "#e8f3dd" : "#f8e5e2";

        messageBox.style.color =
            success ? "#315c28" : "#8d3025";

        messageBox.style.borderLeft =
            success
                ? "3px solid #71954B"
                : "3px solid #c75a4a";

        messageBox.textContent = message;
        if (success && typeof showGlobalToast === "function") showGlobalToast(message, "success");

    }


    function hideFormMessage() {

        const messageBox = document.getElementById("formMessage");

        messageBox.style.display = "none";
        messageBox.textContent = "";

    }

    async function fetchJSON(url, options = {}) {

        const response = await fetch(url, {
            credentials: "same-origin",
            ...options
        });

        let data;

        try {
            data = await response.json();
        } catch (error) {
            throw new Error(
                "The server returned an invalid response. Please check the server logs."
            );
        }

        if (!response.ok) {
            throw new Error(
                data.message || "The request could not be completed."
            );
        }

        return data;
    }

    async function loadComplaintOptions() {

        const categorySelect =
            document.getElementById("requestCategory");

        const districtSelect =
            document.getElementById("requestDistrict");

        try {

            const data = await fetchJSON(API_URL);

            categoryOptions = data.categories || [];
            priorityOptions = data.priorities || [];
            districtOptions = data.districts || [];

            categorySelect.replaceChildren();

            addSelectOption(
                categorySelect,
                "",
                "Select category"
            );

            categoryOptions.forEach(category => {

                addSelectOption(
                    categorySelect,
                    category.id,
                    category.name
                );

            });

            districtSelect.replaceChildren();

            addSelectOption(
                districtSelect,
                "",
                "Select district"
            );

            districtOptions.forEach(district => {

                addSelectOption(
                    districtSelect,
                    district.id,
                    district.name
                );

            });


            if (categoryOptions.length === 0) {

                showFormMessage(
                    "No complaint categories were found in the database.",
                    false
                );

            }

            if (priorityOptions.length === 0) {

                showFormMessage(
                    "No priority options were found in the database.",
                    false
                );

            }

        } catch (error) {

            console.error("Complaint options error:", error);

            showFormMessage(
                error.message || "Unable to load complaint options.",
                false
            );

        }
    }


    function addSelectOption(select, value, label) {

        const option = document.createElement("option");

        option.value = value;
        option.textContent = label;

        select.appendChild(option);

    }

    async function loadStreets() {

        const districtSelect =
            document.getElementById("requestDistrict");

        const streetSelect =
            document.getElementById("requestStreet");

        const districtId = districtSelect.value;

        resetBuildingSelect("Select a street first");

        streetSelect.disabled = true;
        streetSelect.replaceChildren();

        if (!districtId) {

            addSelectOption(
                streetSelect,
                "",
                "Select a district first"
            );

            return;
        }

        addSelectOption(
            streetSelect,
            "",
            "Loading streets..."
        );

        try {

            const streets = await fetchJSON(
                API_URL + "?districtId=" +
                encodeURIComponent(districtId)
            );

            streetSelect.replaceChildren();

            addSelectOption(
                streetSelect,
                "",
                "Select street"
            );

            streets.forEach(street => {

                addSelectOption(
                    streetSelect,
                    street.id,
                    street.name
                );

            });

            if (streets.length > 0) {

                streetSelect.disabled = false;

            } else {

                addSelectOption(
                    streetSelect,
                    "",
                    "No streets available"
                );

            }

        } catch (error) {

            console.error("Street loading error:", error);

            streetSelect.replaceChildren();

            addSelectOption(
                streetSelect,
                "",
                "Unable to load streets"
            );

            showFormMessage(error.message, false);

        }
    }

    function resetBuildingSelect(message) {

        const buildingSelect =
            document.getElementById("requestBuilding");

        buildingSelect.replaceChildren();

        addSelectOption(
            buildingSelect,
            "",
            message
        );

        buildingSelect.disabled = true;

    }

    async function loadBuildings() {

        const streetSelect =
            document.getElementById("requestStreet");

        const buildingSelect =
            document.getElementById("requestBuilding");

        const streetId = streetSelect.value;

        resetBuildingSelect("Loading buildings...");

        if (!streetId) {

            resetBuildingSelect("Select a street first");
            return;
        }

        try {

            const buildings = await fetchJSON(
                API_URL + "?streetId=" +
                encodeURIComponent(streetId)
            );

            buildingSelect.replaceChildren();

            addSelectOption(
                buildingSelect,
                "",
                buildings.length > 0
                    ? "No specific building"
                    : "No buildings available"
            );

            buildings.forEach(building => {

                let label = building.name;

                if (building.code) {
                    label += " (" + building.code + ")";
                }

                addSelectOption(
                    buildingSelect,
                    building.id,
                    label
                );

            });

            buildingSelect.disabled = false;

        } catch (error) {

            console.error("Building loading error:", error);

            resetBuildingSelect("Unable to load buildings");

            showFormMessage(
                error.message || "Unable to load buildings.",
                false
            );

        }
    }

    function getSelectedSeverity() {

        const selected = document.querySelector(
            'input[name="severity"]:checked'
        );

        return selected ? selected.value : "low";

    }

    function getPriorityId(priorityName) {

        const selected = priorityOptions.find(priority => {

            return String(priority.name).toLowerCase() ===
                priorityName.toLowerCase();

        });

        return selected ? selected.id : null;

    }

    async function submitRequest(event) {

        event.preventDefault();

        if (isSubmitting) {
            return;
        }

        const form = document.getElementById("requestForm");

        if (!form.reportValidity()) {
            return;
        }

        hideFormMessage();

        const title =
            document.getElementById("complaintTitle").value.trim();

        const categoryId =
            document.getElementById("requestCategory").value;

        const districtId =
            document.getElementById("requestDistrict").value;

        const streetId =
            document.getElementById("requestStreet").value;

        const buildingId =
            document.getElementById("requestBuilding").value;

        const description =
            document.getElementById("requestDescription").value.trim();

        const severity = getSelectedSeverity();

        const priorityId = getPriorityId(severity);


        if (!priorityId) {

            showFormMessage(
                "The selected priority was not found in the database.",
                false
            );

            return;
        }

        if (!streetId) {

            showFormMessage(
                "Please select a street before submitting.",
                false
            );

            return;
        }


        const formData = new URLSearchParams();

        formData.append("title", title);
        formData.append("categoryId", categoryId);
        formData.append("districtId", districtId);
        formData.append("streetId", streetId);
        formData.append("buildingId", buildingId);
        formData.append("description", description);
        formData.append("priorityId", priorityId);


        const submitButton =
            document.getElementById("submitButton");

        isSubmitting = true;

        submitButton.disabled = true;
        submitButton.textContent = "Submitting...";


        try {

            const result = await fetchJSON(API_URL, {

                method: "POST",

                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded;charset=UTF-8"
                },

                body: formData.toString()

            });


            if (!result.success) {

                throw new Error(
                    result.message || "Complaint submission failed."
                );

            }

            document.getElementById("complaintPriority").value = severity;

            testEscalation();

            form.reset();

            const streetSelect =
                document.getElementById("requestStreet");

            streetSelect.replaceChildren();

            addSelectOption(
                streetSelect,
                "",
                "Select a district first"
            );

            streetSelect.disabled = true;

            resetBuildingSelect("Select a street first");


            showFormMessage(
                "Complaint #" + result.complaintId +
                " was successfully saved and assigned.",
                true
            );

            await refreshComplaintData();


        } catch (error) {

            console.error("Complaint submission error:", error);

            showFormMessage(
                error.message || "Something went wrong while submitting.",
                false
            );

        } finally {

            isSubmitting = false;

            submitButton.disabled = false;

            submitButton.innerHTML =
                'Submit request <span>↗</span>';

        }
    }

    function setCivicConnection(connected) {
        const indicator = document.getElementById("civicConnectionIndicator");
        if (!indicator) return;
        indicator.innerHTML = connected
            ? '<span class="status-dot"></span> DATABASE CONNECTED'
            : '<span class="status-dot"></span> CONNECTION ERROR';
        indicator.classList.toggle("connection-error", !connected);
    }

    async function refreshComplaintData() {

        await Promise.allSettled([
            loadDashboard(),
            loadComplaintQueue(),
            loadRecentActivity()
        ]);

    }

    async function loadDashboard() {

        try {

            const data = await fetchJSON(
                API_URL + "?view=dashboard"
            );
            setCivicConnection(true);

            document.getElementById("openCount").textContent =
                Number(data.open || 0);

            document.getElementById("progressCount").textContent =
                Number(data.inProgress || 0);

            document.getElementById("resolvedCount").textContent =
                Number(data.resolved || 0);

        } catch (error) {

            setCivicConnection(false);
            console.error("Dashboard loading error:", error);

        }
    }

    async function loadComplaintQueue() {

        const tableBody =
            document.getElementById("requestTableBody");

        const emptyState =
            document.getElementById("emptyRequests");

        tableBody.replaceChildren();

        emptyState.hidden = false;

        emptyState.querySelector("strong").textContent =
            "Loading civic requests";

        emptyState.querySelector("p").textContent =
            "Retrieving complaint records from the database...";

        try {

            const data = await fetchJSON(
                API_URL + "?view=queue"
            );

            if (!Array.isArray(data)) {
                throw new Error("The complaint queue response is invalid.");
            }

            complaintRequests = data;

            renderRequests();

        } catch (error) {

            console.error("Complaint queue error:", error);

            complaintRequests = [];

            tableBody.replaceChildren();

            document.getElementById("queueCount").textContent = "00";

            emptyState.hidden = false;

            emptyState.querySelector("strong").textContent =
                "Unable to load requests";

            emptyState.querySelector("p").textContent =
                error.message || "Please try again later.";

        }
    }

    function createCell(value, className = "") {

        const cell = document.createElement("td");

        if (className) {
            cell.className = className;
        }

        cell.textContent =
            value === null || value === undefined || value === ""
                ? "—"
                : String(value);

        return cell;

    }


    function createTag(value, className) {

        const tag = document.createElement("span");

        tag.className = className;
        tag.textContent = value || "Unknown";

        return tag;

    }

    function renderRequests() {

        const tableBody =
            document.getElementById("requestTableBody");

        const emptyState =
            document.getElementById("emptyRequests");

        const searchTerm =
            document.getElementById("requestSearch")
                .value.trim().toLowerCase();

        const statusFilter =
            document.getElementById("statusFilter").value;


        tableBody.replaceChildren();


        const filteredRequests = complaintRequests.filter(request => {

            const searchableText = [

                request.id,
                request.title,
                request.category,
                request.priority,
                request.status,
                request.street,
                request.district,
                request.building,
                request.citizen,
                request.assignee

            ].filter(Boolean).join(" ").toLowerCase();


            const matchesSearch =
                searchableText.includes(searchTerm);

            const matchesStatus =
                statusFilter === "all" ||
                String(request.status).toLowerCase() ===
                statusFilter.toLowerCase();

            return matchesSearch && matchesStatus;

        });


        document.getElementById("queueCount").textContent =
            String(filteredRequests.length).padStart(2, "0");


        if (filteredRequests.length === 0) {

            emptyState.hidden = false;

            emptyState.querySelector("strong").textContent =
                complaintRequests.length === 0
                    ? "No civic requests found"
                    : "No matching requests";

            emptyState.querySelector("p").textContent =
                complaintRequests.length === 0
                    ? "Submitted complaints will appear here when they are available."
                    : "Try changing your search term or status filter.";

            return;
        }


        emptyState.hidden = true;


        filteredRequests.forEach(request => {

            const row = document.createElement("tr");

            const idCell = createCell(
                "#" + request.id,
                "request-id-cell"
            );

            const citizenCell = document.createElement("td");

            const citizenWrapper = document.createElement("div");
            citizenWrapper.className = "queue-primary-info";

            const citizenName = document.createElement("strong");
            citizenName.textContent = request.citizen || "Unknown user";

            const categoryName = document.createElement("small");
            categoryName.textContent = request.category || "Uncategorised";

            citizenWrapper.append(citizenName, categoryName);
            citizenCell.appendChild(citizenWrapper);

            const locationCell = document.createElement("td");

            const locationWrapper = document.createElement("div");
            locationWrapper.className = "queue-primary-info";

            const streetName = document.createElement("strong");
            streetName.textContent = request.street || "Unknown street";

            const locationDetails = document.createElement("small");

            const locationParts = [
                request.building,
                request.district
            ].filter(Boolean);

            locationDetails.textContent =
                locationParts.length > 0
                    ? locationParts.join(" · ")
                    : "No additional location";

            locationWrapper.append(streetName, locationDetails);
            locationCell.appendChild(locationWrapper);

            const priority = String(
                request.priority || "Unknown"
            ).toLowerCase();

            const priorityCell = document.createElement("td");

            priorityCell.appendChild(
                createTag(
                    request.priority || "Unknown",
                    "priority-tag priority-" +
                    priority.replace(/[^a-z0-9-]/g, "")
                )
            );

            const assigneeCell = createCell(
                request.assignee || "Not assigned",
                "assignee-cell"
            );

            const status = String(
                request.status || "Unknown"
            );

            const statusClass = status
                .toLowerCase()
                .replace(/[^a-z0-9]+/g, "-");

            const statusCell = document.createElement("td");

            statusCell.appendChild(
                createTag(
                    status,
                    "request-status status-" + statusClass
                )
            );


            row.append(
                idCell,
                citizenCell,
                locationCell,
                priorityCell,
                assigneeCell,
                statusCell
            );

            tableBody.appendChild(row);

        });

    }

    async function loadRecentActivity() {

        const activityList =
            document.getElementById("activityList");

        activityList.replaceChildren();

        try {

            const activities = await fetchJSON(
                API_URL + "?view=activity"
            );

            if (!Array.isArray(activities)) {
                throw new Error("Invalid activity response.");
            }

            renderRecentActivity(activities.slice(0, 3));

        } catch (error) {

            console.error("Activity loading error:", error);

            renderActivityMessage(
                "Unable to load escalation activity.",
                error.message
            );

        }
    }


    function renderRecentActivity(activities) {

        const activityList =
            document.getElementById("activityList");

        activityList.replaceChildren();

        if (activities.length === 0) {

            renderActivityMessage(
                "No escalation activity yet.",
                "Complaint assignment records will appear here."
            );

            return;
        }


        activities.forEach(activity => {

            const item = document.createElement("div");
            item.className = "activity-item";

            const marker = document.createElement("span");
            marker.className = "activity-marker marker-green";

            const details = document.createElement("div");

            const heading = document.createElement("strong");

            heading.textContent =
                (activity.action || "Complaint action") +
                " · Complaint #" + activity.complaintId;

            const description = document.createElement("p");

            const fromName = activity.from || "User";
            const toName = activity.to || "Unassigned";

            description.textContent =
                fromName + " → " + toName +
                " · " + (activity.level || "Handler");

            const remarks = document.createElement("p");
            remarks.textContent = activity.remarks || "";

            const timestamp = document.createElement("small");

            timestamp.textContent =
                formatDateTime(activity.actionAt);

            details.append(
                heading,
                description,
                remarks,
                timestamp
            );

            item.append(marker, details);

            activityList.appendChild(item);

        });

    }


    function renderActivityMessage(title, description) {

        const activityList =
            document.getElementById("activityList");

        activityList.replaceChildren();

        const item = document.createElement("div");
        item.className = "activity-item";

        const marker = document.createElement("span");
        marker.className = "activity-marker marker-green";

        const details = document.createElement("div");

        const heading = document.createElement("strong");
        heading.textContent = title;

        const paragraph = document.createElement("p");
        paragraph.textContent = description;

        details.append(heading, paragraph);
        item.append(marker, details);

        activityList.appendChild(item);

    }

    function formatDateTime(value) {

        if (!value) {
            return "Date unavailable";
        }

        const date = new Date(
            String(value).replace(" ", "T")
        );

        if (Number.isNaN(date.getTime())) {
            return String(value);
        }

        return date.toLocaleString(undefined, {
            year: "numeric",
            month: "short",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });

    }

    function showHelp() {

        alert(
            "CIVIC REQUESTS — CHAIN OF RESPONSIBILITY\n\n" +

            "1. Select a complaint category.\n" +

            "2. Select the district and street.\n" +

            "3. Optionally select a building.\n" +

            "4. Describe the complaint.\n" +

            "5. Select its severity.\n" +

            "6. Submit the request.\n\n" +

            "Your signed-in account is identified automatically " +
            "from your login session.\n\n" +

            "The Java Chain of Responsibility implementation " +
            "assigns the complaint to the appropriate handlers."
        );

    }

    document.addEventListener("DOMContentLoaded", async function () {

        testEscalation();

        document.getElementById("requestDistrict")
            .addEventListener("change", loadStreets);

        document.getElementById("requestStreet")
            .addEventListener("change", loadBuildings);

        document.getElementById("requestForm")
            .addEventListener("submit", submitRequest);

        document.getElementById("requestSearch")
            .addEventListener("input", renderRequests);

        document.getElementById("statusFilter")
            .addEventListener("change", renderRequests);

        await loadComplaintOptions();

        await refreshComplaintData();

    });

</script>

<jsp:include page="/includes/global-toast.jsp" />
</body>
</html>