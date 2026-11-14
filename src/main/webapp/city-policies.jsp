<%@ page contentType="text/html;charset=UTF-8"
         language="java"
         isELIgnored="true" %>
<%
    String contextPath = request.getContextPath();
    request.setAttribute("activePage", "policies");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartCity360 | City Policies</title>

    <link rel="stylesheet" href="<%= contextPath %>/css/living-city.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/city-policies.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/dashboard.css">
</head>
<body>

<div class="app-layout">

    <jsp:include page="/includes/sidebar.jsp"/>

    <main class="main-shell">

        <header class="topbar">
            <div class="topbar-heading">
                <span class="breadcrumb">SMART CITY / INTERPRETER PATTERN</span>
                <h1>City Policies</h1>
            </div>

            <div class="topbar-actions">
                <div class="environment-chip" id="policyConnectionIndicator">
                    <span class="status-dot"></span>
                    CONNECTING TO DATABASE
                </div>

                <button class="help-button" type="button" onclick="showHelp()">?</button>
            </div>
        </header>

        <div class="content-scroll">
            <div class="page-content policy-page">

                <section class="policy-hero">

                    <div class="hero-copy">
                        <span class="eyebrow">BEHAVIOURAL DESIGN PATTERN · PART E</span>

                        <h2>City policy <span>intelligence.</span></h2>

                        <p>
                            Define operational rules in a simple policy language.
                            Interpret expressions, evaluate city conditions and
                            understand how each decision is reached.
                        </p>

                        <div class="hero-tags">
                            <span><i class="tag-dot"></i> Expression parsing</span>
                            <span><i class="tag-dot"></i> Rule evaluation</span>
                            <span><i class="tag-dot"></i> Logical operators</span>
                        </div>
                    </div>

                    <div class="hero-visual">
                        <div class="visual-orbit orbit-one"></div>
                        <div class="visual-orbit orbit-two"></div>

                        <div class="visual-core">
                            <span class="core-symbol">∑</span>
                            <span>POLICY<br>ENGINE</span>
                        </div>

                        <div class="orbit-node node-one">AND</div>
                        <div class="orbit-node node-two">&gt;</div>
                        <div class="orbit-node node-three">OR</div>
                        <div class="orbit-node node-four">==</div>
                    </div>

                    <div class="hero-index">
                        <span>05</span>
                        <small>DESIGN<br>PATTERNS</small>
                    </div>

                </section>

                <section class="policy-metrics">

                    <div class="metric-card">
                        <div class="metric-top">
                            <span class="metric-icon purple">⌘</span>
                            <span class="metric-caption">WORKSPACE</span>
                        </div>
                        <strong id="metric-evaluations">0</strong>
                        <span class="metric-label">Evaluations performed</span>
                        <div class="metric-foot">Saved in MySQL</div>
                    </div>

                    <div class="metric-card">
                        <div class="metric-top">
                            <span class="metric-icon blue">◇</span>
                            <span class="metric-caption">LIBRARY</span>
                        </div>
                        <strong id="metric-policies">0</strong>
                        <span class="metric-label">Saved city policies</span>
                        <div class="metric-foot">Loaded from MySQL</div>
                    </div>

                    <div class="metric-card">
                        <div class="metric-top">
                            <span class="metric-icon green">✓</span>
                            <span class="metric-caption">RESULTS</span>
                        </div>
                        <strong id="metric-passed">0</strong>
                        <span class="metric-label">Conditions satisfied</span>
                        <div class="metric-foot">True evaluations</div>
                    </div>

                    <div class="metric-card">
                        <div class="metric-top">
                            <span class="metric-icon orange">⚡</span>
                            <span class="metric-caption">ENGINE</span>
                        </div>
                        <strong>9</strong>
                        <span class="metric-label">Supported operators</span>
                        <div class="metric-foot">AND · OR · NOT · comparison</div>
                    </div>

                </section>

                <section class="workspace-grid">

                    <div class="workspace-main">

                        <article class="panel editor-panel">

                            <div class="panel-header editor-header">
                                <div>
                                    <span class="eyebrow">01 / POLICY WORKSPACE</span>
                                    <h3>Expression editor</h3>
                                    <p>Write a rule using the SmartCity360 policy language.</p>
                                </div>

                                <span class="engine-status">
                                    <span></span> INTERPRETER ENGINE
                                </span>
                            </div>

                            <div class="editor-toolbar">
                                <div class="editor-file">
                                    <span class="file-icon">⌘</span>
                                    <span>city-policy.rule</span>
                                </div>

                                <span class="editor-language">SMARTCITY DSL</span>
                            </div>

                            <div class="editor-area">
                                <div class="line-numbers" id="line-numbers">01</div>

                                <textarea
                                        id="policy-expression"
                                        spellcheck="false"
                                        autocomplete="off"
                                        aria-label="Policy expression"
                                        placeholder="NOISE_LEVEL > 60 AND PARKING_HOUR < 8">NOISE_LEVEL > 60 AND PARKING_HOUR < 8</textarea>
                            </div>

                            <div class="editor-footer">
                                <span><i class="footer-dot"></i> Expression input</span>
                                <span id="expression-count">0 characters</span>
                            </div>

                            <div class="operator-toolbar">
                                <span class="toolbar-label">INSERT OPERATOR</span>

                                <button type="button" onclick="insertOperator(' AND ')">AND</button>
                                <button type="button" onclick="insertOperator(' OR ')">OR</button>
                                <button type="button" onclick="insertOperator('NOT ')">NOT</button>
                                <button type="button" onclick="insertOperator(' > ')">&gt;</button>
                                <button type="button" onclick="insertOperator(' < ')">&lt;</button>
                                <button type="button" onclick="insertOperator(' >= ')">&gt;=</button>
                                <button type="button" onclick="insertOperator(' <= ')">&lt;=</button>
                                <button type="button" onclick="insertOperator(' == ')">==</button>
                                <button type="button" onclick="insertOperator(' != ')">!=</button>
                                <button type="button" onclick="insertOperator('(')">(</button>
                                <button type="button" onclick="insertOperator(')')">)</button>
                            </div>

                            <div class="editor-actions">
                                <button type="button" class="secondary-button" onclick="resetWorkspace()">
                                    Reset
                                </button>

                                <button type="button" class="primary-button" onclick="evaluatePolicy()">
                                    <span>Evaluate policy</span>
                                    <span class="button-arrow">→</span>
                                </button>
                            </div>

                            <div class="editor-notice">
                                <span class="notice-icon">i</span>
                                <p>
                                    Expressions are parsed and evaluated by the Java Interpreter
                                    on the server, and every evaluation is saved to the history.
                                </p>
                            </div>

                        </article>

                        <article class="panel context-panel">

                            <div class="panel-header">
                                <div>
                                    <span class="eyebrow">02 / EVALUATION CONTEXT</span>
                                    <h3>City environment values</h3>
                                    <p>Set the current values used when evaluating the expression.</p>
                                </div>

                                <span class="context-count">LIVE INPUTS</span>
                            </div>

                            <div class="variable-grid">

                                <div class="variable-field">
                                    <label for="noise-level">
                                        <span class="variable-symbol">◉</span>
                                        NOISE_LEVEL
                                    </label>
                                    <div class="variable-input-wrap">
                                        <input type="number" id="noise-level" value="75" min="0" max="200">
                                        <span>dB</span>
                                    </div>
                                    <small>Measured environmental noise</small>
                                </div>

                                <div class="variable-field">
                                    <label for="parking-hour">
                                        <span class="variable-symbol">◷</span>
                                        PARKING_HOUR
                                    </label>
                                    <div class="variable-input-wrap">
                                        <input type="number" id="parking-hour" value="6" min="0" max="23">
                                        <span>24H</span>
                                    </div>
                                    <small>Current parking restriction hour</small>
                                </div>

                                <div class="variable-field">
                                    <label for="air-quality">
                                        <span class="variable-symbol">◎</span>
                                        AIR_QUALITY
                                    </label>
                                    <div class="variable-input-wrap">
                                        <input type="number" id="air-quality" value="42" min="0" max="500">
                                        <span>AQI</span>
                                    </div>
                                    <small>Air quality index value</small>
                                </div>

                                <div class="variable-field">
                                    <label for="traffic-level">
                                        <span class="variable-symbol">⌁</span>
                                        TRAFFIC_LEVEL
                                    </label>
                                    <div class="variable-input-wrap">
                                        <input type="number" id="traffic-level" value="68" min="0" max="100">
                                        <span>%</span>
                                    </div>
                                    <small>Traffic congestion percentage</small>
                                </div>

                            </div>

                            <div class="context-bottom">
                                <span>Values are editable for testing.</span>
                                <button type="button" onclick="resetValues()">Restore sample values ↺</button>
                            </div>

                        </article>

                        <article class="panel result-panel" id="result-panel">

                            <div class="panel-header">
                                <div>
                                    <span class="eyebrow">03 / INTERPRETER OUTPUT</span>
                                    <h3>Evaluation result</h3>
                                    <p>See the result produced by the browser-side expression evaluator.</p>
                                </div>

                                <span class="waiting-badge" id="result-status">AWAITING INPUT</span>
                            </div>

                            <div class="result-content" id="result-content">

                                <div class="result-empty">
                                    <div class="empty-symbol">∑</div>
                                    <h4>Ready to interpret</h4>
                                    <p>
                                        Enter a policy expression, set the city values
                                        and evaluate the rule to see its result.
                                    </p>
                                </div>

                            </div>

                        </article>

                    </div>

                    <aside class="workspace-side">

                        <article class="panel library-panel">

                            <div class="panel-header">
                                <div>
                                    <span class="eyebrow">TEMPLATES</span>
                                    <h3>Policy library</h3>
                                </div>
                                <span class="library-count">05</span>
                            </div>

                            <p class="side-description">
                                Select an example rule to load it into the editor.
                            </p>

                            <div class="policy-list">

                                <button class="policy-template selected"
                                        type="button"
                                        onclick="loadTemplate(0)">
                                    <span class="template-icon noise-icon">◉</span>
                                    <span class="template-info">
                                        <strong>Noise restriction</strong>
                                        <small>Environmental control</small>
                                    </span>
                                    <span class="template-arrow">↗</span>
                                </button>

                                <button class="policy-template"
                                        type="button"
                                        onclick="loadTemplate(1)">
                                    <span class="template-icon parking-icon">◷</span>
                                    <span class="template-info">
                                        <strong>Parking restriction</strong>
                                        <small>Urban parking policy</small>
                                    </span>
                                    <span class="template-arrow">↗</span>
                                </button>

                                <button class="policy-template"
                                        type="button"
                                        onclick="loadTemplate(2)">
                                    <span class="template-icon air-icon">◎</span>
                                    <span class="template-info">
                                        <strong>Air quality alert</strong>
                                        <small>Environmental monitoring</small>
                                    </span>
                                    <span class="template-arrow">↗</span>
                                </button>

                                <button class="policy-template"
                                        type="button"
                                        onclick="loadTemplate(3)">
                                    <span class="template-icon traffic-icon">⌁</span>
                                    <span class="template-info">
                                        <strong>Traffic control</strong>
                                        <small>Transport management</small>
                                    </span>
                                    <span class="template-arrow">↗</span>
                                </button>

                                <button class="policy-template"
                                        type="button"
                                        onclick="loadTemplate(4)">
                                    <span class="template-icon combined-icon">⌘</span>
                                    <span class="template-info">
                                        <strong>Combined city rule</strong>
                                        <small>Multiple conditions</small>
                                    </span>
                                    <span class="template-arrow">↗</span>
                                </button>

                            </div>

                        </article>

                        <article class="panel reference-panel">

                            <div class="panel-header">
                                <div>
                                    <span class="eyebrow">LANGUAGE GUIDE</span>
                                    <h3>Operator reference</h3>
                                </div>
                            </div>

                            <div class="operator-reference">
                                <div>
                                    <code>&gt;</code>
                                    <span><strong>Greater than</strong><small>Compare larger values</small></span>
                                </div>

                                <div>
                                    <code>&lt;</code>
                                    <span><strong>Less than</strong><small>Compare smaller values</small></span>
                                </div>

                                <div>
                                    <code>==</code>
                                    <span><strong>Equal to</strong><small>Compare equal values</small></span>
                                </div>

                                <div>
                                    <code>&gt;=</code>
                                    <span><strong>Greater than or equal</strong><small>Value is at least the limit</small></span>
                                </div>

                                <div>
                                    <code>&lt;=</code>
                                    <span><strong>Less than or equal</strong><small>Value is at most the limit</small></span>
                                </div>

                                <div>
                                    <code>!=</code>
                                    <span><strong>Not equal to</strong><small>Value differs from the limit</small></span>
                                </div>

                                <div>
                                    <code>NOT</code>
                                    <span><strong>Logical NOT</strong><small>Reverses the condition after it</small></span>
                                </div>

                                <div>
                                    <code>AND</code>
                                    <span><strong>Logical AND</strong><small>Both conditions must be true</small></span>
                                </div>

                                <div>
                                    <code>OR</code>
                                    <span><strong>Logical OR</strong><small>At least one must be true</small></span>
                                </div>
                            </div>

                        </article>

                        <article class="panel history-panel">

                            <div class="panel-header">
                                <div>
                                    <span class="eyebrow">ACTIVITY</span>
                                    <h3>Recent evaluations</h3>
                                </div>
                                <span class="history-live">LIVE</span>
                            </div>

                            <div id="evaluation-history" class="evaluation-history">
                                <div class="history-empty">
                                    No evaluations yet.
                                    <small>Your recent results will appear here.</small>
                                </div>
                            </div>

                            <button type="button" class="clear-history" onclick="clearHistory()">
                                Clear history
                            </button>

                        </article>

                    </aside>

                </section>

                <section class="panel architecture-panel">

                    <div class="architecture-heading">
                        <div>
                            <span class="eyebrow">INTERPRETER DESIGN PATTERN</span>
                            <h3>From policy language to city decision.</h3>
                            <p>
                                A policy expression is represented as an expression structure.
                                The interpreter evaluates that structure using the current context.
                            </p>
                        </div>

                        <span class="architecture-label">PATTERN FLOW</span>
                    </div>

                    <section class="saved-policy-panel" aria-labelledby="saved-policies-heading">
                        <div class="saved-policy-heading">
                            <div class="saved-policy-heading-copy">
                                <span class="eyebrow">DATABASE RECORDS · MYSQL</span>
                                <h4 id="saved-policies-heading">Saved city policies</h4>
                                <p>Browse policies stored in the database. Select any policy to load its expression into the editor.</p>
                            </div>
                            <span class="library-count" id="saved-policy-count">0</span>
                        </div>

                        <div class="saved-policy-list" id="saved-policy-list" aria-live="polite">
                            <div class="saved-policy-empty">Loading saved policies...</div>
                        </div>
                    </section>

                    <div class="architecture-flow">

                        <div class="architecture-step">
                            <span class="architecture-number">01</span>
                            <div class="architecture-symbol">⌨</div>
                            <strong>Policy expression</strong>
                            <small>Rule written in the city policy language</small>
                            <code>NOISE_LEVEL &gt; 60</code>
                        </div>

                        <div class="flow-connector"><span>→</span></div>

                        <div class="architecture-step">
                            <span class="architecture-number">02</span>
                            <div class="architecture-symbol">⌘</div>
                            <strong>Policy parser</strong>
                            <small>Reads tokens and builds expression objects</small>
                            <code>PolicyParser</code>
                        </div>

                        <div class="flow-connector"><span>→</span></div>

                        <div class="architecture-step">
                            <span class="architecture-number">03</span>
                            <div class="architecture-symbol">◇</div>
                            <strong>Expression tree</strong>
                            <small>Represents comparisons and logical operations</small>
                            <code>Expression</code>
                        </div>

                        <div class="flow-connector"><span>→</span></div>

                        <div class="architecture-step">
                            <span class="architecture-number">04</span>
                            <div class="architecture-symbol">∑</div>
                            <strong>Interpretation</strong>
                            <small>Evaluates the expression against city values</small>
                            <code>interpret(context)</code>
                        </div>

                        <div class="flow-connector"><span>→</span></div>

                        <div class="architecture-step final-step">
                            <span class="architecture-number">05</span>
                            <div class="architecture-symbol">✓</div>
                            <strong>Policy decision</strong>
                            <small>Returns the final Boolean result</small>
                            <code>TRUE / FALSE</code>
                        </div>

                    </div>

                    <div class="architecture-classes">

                        <div>
                            <span class="class-dot purple-dot"></span>
                            <strong>Expression</strong>
                            <small>Common interface</small>
                        </div>

                        <div>
                            <span class="class-dot blue-dot"></span>
                            <strong>ComparisonExpression</strong>
                            <small>Evaluates comparisons</small>
                        </div>

                        <div>
                            <span class="class-dot green-dot"></span>
                            <strong>LogicalExpression</strong>
                            <small>Combines conditions</small>
                        </div>

                        <div>
                            <span class="class-dot orange-dot"></span>
                            <strong>PolicyContext</strong>
                            <small>Stores city values</small>
                        </div>

                        <div>
                            <span class="class-dot pink-dot"></span>
                            <strong>PolicyParser</strong>
                            <small>Builds the expression structure</small>
                        </div>

                    </div>

                </section>

                <footer class="page-footer">
                    <span>SMARTCITY360</span>
                    <span>Interpreter Pattern · Part E · Development Preview</span>
                </footer>

            </div>
        </div>

    </main>
</div>

<script>

    const API_BASE = "<%= contextPath %>/city-policies";

    const templates = [
        {
            name: "Noise restriction",
            expression: "NOISE_LEVEL > 60",
            description: "Checks whether measured noise exceeds the permitted threshold."
        },
        {
            name: "Parking restriction",
            expression: "PARKING_HOUR < 8",
            description: "Checks whether the current hour falls within the restricted period."
        },
        {
            name: "Air quality alert",
            expression: "AIR_QUALITY > 100",
            description: "Checks whether the air quality index exceeds the alert threshold."
        },
        {
            name: "Traffic control",
            expression: "TRAFFIC_LEVEL > 70",
            description: "Checks whether traffic congestion exceeds the control threshold."
        },
        {
            name: "Combined city rule",
            expression: "NOISE_LEVEL > 60 AND PARKING_HOUR < 8",
            description: "Checks two city conditions using a logical AND expression."
        }
    ];

    const defaultValues = {
        NOISE_LEVEL: 75,
        PARKING_HOUR: 6,
        AIR_QUALITY: 42,
        TRAFFIC_LEVEL: 68
    };

    let evaluationCount = 0;
    let passedCount = 0;
    let historyItems = [];

    const expressionInput =
        document.getElementById("policy-expression");

    function getValues() {
        return {
            NOISE_LEVEL: Number(
                document.getElementById("noise-level").value
            ),

            PARKING_HOUR: Number(
                document.getElementById("parking-hour").value
            ),

            AIR_QUALITY: Number(
                document.getElementById("air-quality").value
            ),

            TRAFFIC_LEVEL: Number(
                document.getElementById("traffic-level").value
            )
        };
    }

    function updateEditorInfo() {

        const expression = expressionInput.value;

        document.getElementById("expression-count").textContent =
            expression.length + " characters";

        const lineCount = Math.max(
            1,
            expression.split("\n").length
        );

        document.getElementById("line-numbers").textContent =
            Array.from(
                { length: lineCount },
                (_, index) => String(index + 1).padStart(2, "0")
            ).join("\n");
    }

    expressionInput.addEventListener(
        "input",
        updateEditorInfo
    );

    function insertOperator(operator) {

        const start = expressionInput.selectionStart;
        const end = expressionInput.selectionEnd;

        const current = expressionInput.value;

        expressionInput.value =
            current.substring(0, start)
            + operator
            + current.substring(end);

        expressionInput.focus();

        const cursor = start + operator.length;

        expressionInput.setSelectionRange(cursor, cursor);

        updateEditorInfo();
    }

    function setPolicy(expression) {

        expressionInput.value = expression;

        updateEditorInfo();
    }

    function loadTemplate(index) {

        const template = templates[index];

        if (!template) {
            return;
        }

        setPolicy(template.expression);

        document.querySelectorAll(".policy-template")
            .forEach((button, i) => {
                button.classList.toggle(
                    "selected",
                    i === index
                );
            });

        document.getElementById("result-status").textContent =
            "AWAITING INPUT";

        const panel = document.getElementById("result-panel");

        panel.classList.remove(
            "result-success",
            "result-failure"
        );

        document.getElementById("result-content").innerHTML = `
        <div class="result-empty">
            <div class="empty-symbol">⌘</div>
            <h4>${escapeHtml(template.name)} loaded</h4>
            <p>
                ${escapeHtml(template.description)}
                Evaluate the expression to see the result.
            </p>
        </div>
    `;
    }

    function resetValues() {

        document.getElementById("noise-level").value =
            defaultValues.NOISE_LEVEL;

        document.getElementById("parking-hour").value =
            defaultValues.PARKING_HOUR;

        document.getElementById("air-quality").value =
            defaultValues.AIR_QUALITY;

        document.getElementById("traffic-level").value =
            defaultValues.TRAFFIC_LEVEL;
    }

    function resetWorkspace() {

        setPolicy(templates[4].expression);

        resetValues();

        const panel = document.getElementById("result-panel");

        panel.classList.remove(
            "result-success",
            "result-failure"
        );

        document.getElementById("result-status").textContent =
            "AWAITING INPUT";

        document.getElementById("result-content").innerHTML = `
        <div class="result-empty">
            <div class="empty-symbol">∑</div>
            <h4>Workspace reset</h4>
            <p>
                Your sample expression and city values
                have been restored.
            </p>
        </div>
    `;

        document.querySelectorAll(".policy-template")
            .forEach((button, index) => {
                button.classList.toggle(
                    "selected",
                    index === 4
                );
            });
    }

    async function evaluatePolicy() {

        const expression = expressionInput.value.trim();

        if (!expression) {
            showError("Please enter a policy expression first.");
            return;
        }

        const values = getValues();

        for (const [name, value] of Object.entries(values)) {

            if (!Number.isFinite(value)) {
                showError(
                    "Please enter a valid numeric value for " + name
                );
                return;
            }
        }

        const button = document.querySelector(
            ".editor-actions .primary-button"
        );

        const originalButtonContent = button.innerHTML;

        button.disabled = true;
        button.innerHTML = "<span>Interpreting...</span>";

        try {

            const parameters = new URLSearchParams();

            parameters.append("expression", expression);
            parameters.append("noiseLevel", values.NOISE_LEVEL);
            parameters.append("parkingHour", values.PARKING_HOUR);
            parameters.append("airQuality", values.AIR_QUALITY);
            parameters.append("trafficLevel", values.TRAFFIC_LEVEL);

            const response = await fetch(
                API_BASE + "/evaluate",
                {
                    method: "POST",
                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded;charset=UTF-8"
                    },
                    body: parameters.toString()
                }
            );

            const data = await response.json();

            if (response.status === 401) {
                window.location.href =
                    "<%= contextPath %>/login.jsp";
                return;
            }

            if (!response.ok || !data.success) {
                throw new Error(
                    data.message || "Policy evaluation failed."
                );
            }

            renderResult(
                data.passed,
                data.conditions,
                data
            );

            evaluationCount++;

            if (data.passed) {
                passedCount++;
            }

            updateMetrics();

            await loadHistory();
            await loadSummary();
            await loadPolicies();

        } catch (error) {

            showError(
                error.message || "Unable to connect to the policy engine."
            );

        } finally {

            button.disabled = false;
            button.innerHTML = originalButtonContent;
        }
    }

    function renderResult(result, conditions, data) {

        const panel = document.getElementById("result-panel");
        const status = document.getElementById("result-status");
        const content = document.getElementById("result-content");

        panel.classList.remove(
            "result-success",
            "result-failure"
        );

        panel.classList.add(
            result ? "result-success" : "result-failure"
        );

        status.textContent = result
            ? "TRUE · SATISFIED"
            : "FALSE · NOT SATISFIED";

        if (result && typeof showGlobalToast === "function") {
            showGlobalToast("City policy evaluated successfully: all conditions are satisfied.", "success");
        }

        const conditionHtml = conditions.map(
            (condition, index) => `

        <div class="condition-row">

            <span class="condition-number">
                ${String(index + 1).padStart(2, "0")}
            </span>

            <div class="condition-description">

                <strong>
                    ${escapeHtml(condition.description)}
                </strong>

                <small>
                    Actual: ${condition.actual}
                    &nbsp; · &nbsp;
                    Expected: ${condition.operator}
                    ${condition.expected}
                </small>

            </div>

            <span class="condition-result
                ${condition.result
                    ? "condition-true"
                    : "condition-false"}">

                ${condition.result ? "TRUE" : "FALSE"}

            </span>

        </div>
    `
        ).join("");

        content.innerHTML = `

        <div class="result-summary">

            <div class="result-symbol">
                ${result ? "✓" : "×"}
            </div>

            <div>

                <span class="result-overline">
                    FINAL POLICY DECISION
                </span>

                <h2>${result ? "TRUE" : "FALSE"}</h2>

                <p>
                    ${escapeHtml(data.message)}
                </p>

            </div>

        </div>

        <div class="breakdown-heading">
            <strong>Expression breakdown</strong>
            <span>${conditions.length} comparison(s)</span>
        </div>

        <div class="condition-list">
            ${conditionHtml}
        </div>

        <div class="tree-result">

            <span>INTERPRETED RESULT</span>

            <strong>
                ${result ? "TRUE" : "FALSE"}
            </strong>

            <small>
                Evaluated by the Java Interpreter Pattern
            </small>

        </div>

        <div class="evaluation-saved">
            <span>✓</span>
            Evaluation saved to database
            <small>Record #${data.evaluationId}</small>
        </div>
    `;

        panel.scrollIntoView({
            behavior: "smooth",
            block: "nearest"
        });
    }

    function showError(message) {

        const panel = document.getElementById("result-panel");

        panel.classList.remove(
            "result-success",
            "result-failure"
        );

        document.getElementById("result-status").textContent =
            "INVALID EXPRESSION";

        document.getElementById("result-content").innerHTML = `

        <div class="result-error">

            <div class="error-symbol">!</div>

            <div>

                <strong>
                    Unable to interpret expression
                </strong>

                <p>${escapeHtml(message)}</p>

                <small>
                    Check variable names, operators,
                    numbers and parentheses.
                </small>

            </div>

        </div>
    `;
    }

    async function loadHistory() {

        try {

            const response = await fetch(
                API_BASE + "/history"
            );

            if (response.status === 401) {
                window.location.href =
                    "<%= contextPath %>/login.jsp";
                return;
            }

            if (!response.ok) {
                throw new Error("Unable to load evaluation history.");
            }

            const data = await response.json();

            historyItems = data.map(item => ({

                expression: item.expression,
                result: item.result === "TRUE",
                time: item.evaluatedAt
                    ? new Date(item.evaluatedAt).toLocaleString()
                    : ""

            }));

            renderHistory();

        } catch (error) {

            console.error("History loading error:", error);
        }
    }

    function renderHistory() {

        const container =
            document.getElementById("evaluation-history");

        if (historyItems.length === 0) {

            container.innerHTML = `

            <div class="history-empty">

                No evaluations yet.

                <small>
                    Your recent results will appear here.
                </small>

            </div>
        `;

            return;
        }

        container.innerHTML = historyItems.map(item => `

        <div class="history-item">

            <div class="history-indicator
                ${item.result ? "history-true" : "history-false"}">
            </div>

            <div class="history-info">

                <strong>
                    ${escapeHtml(item.expression)}
                </strong>

                <small>
                    ${escapeHtml(item.time)}
                </small>

            </div>

            <span class="history-result
                ${item.result
                    ? "condition-true"
                    : "condition-false"}">

                ${item.result ? "TRUE" : "FALSE"}

            </span>

        </div>

    `).join("");
    }

    function clearHistory() {

        historyItems = [];

        renderHistory();
    }

    async function loadSummary() {

        try {

            const response = await fetch(
                API_BASE + "/summary"
            );

            if (response.status === 401) {
                window.location.href =
                    "<%= contextPath %>/login.jsp";
                return;
            }

            if (!response.ok) {
                throw new Error("Unable to load policy summary.");
            }

            const data = await response.json();

            document.getElementById(
                "metric-evaluations"
            ).textContent = data.evaluations;

            document.getElementById(
                "metric-policies"
            ).textContent = data.policies;

            document.getElementById(
                "metric-passed"
            ).textContent = data.passed;

        } catch (error) {

            console.error("Summary loading error:", error);
        }
    }

    function setPolicyConnection(connected) {
        const indicator = document.getElementById("policyConnectionIndicator");
        if (!indicator) return;
        indicator.innerHTML = connected
            ? '<span class="status-dot"></span> DATABASE CONNECTED'
            : '<span class="status-dot"></span> CONNECTION ERROR';
        indicator.classList.toggle("connection-error", !connected);
    }

    async function loadPolicies() {

        const container = document.getElementById("saved-policy-list");
        const count = document.getElementById("saved-policy-count");

        try {

            const response = await fetch(API_BASE + "/policies", {
                cache: "no-store"
            });

            if (response.status === 401) {
                window.location.href = "<%= contextPath %>/login.jsp";
                return;
            }

            if (!response.ok) {
                throw new Error("Unable to load saved policies from the database.");
            }

            const policies = await response.json();
            setPolicyConnection(true);
            const savedPolicies = Array.isArray(policies) ? policies : [];

            count.textContent = String(savedPolicies.length);
            document.getElementById("metric-policies").textContent =
                String(savedPolicies.length);

            container.replaceChildren();

            if (savedPolicies.length === 0) {
                const empty = document.createElement("div");
                empty.className = "saved-policy-empty";
                empty.textContent = "No saved policies yet. Evaluate an expression to create the first database policy.";
                container.appendChild(empty);
                return;
            }

            savedPolicies.forEach(policy => {
                const button = document.createElement("button");
                button.type = "button";
                button.className = "saved-policy-item";

                const details = document.createElement("span");
                details.className = "saved-policy-details";

                const name = document.createElement("strong");
                name.textContent = policy.policyName || "Untitled policy";

                const expression = document.createElement("small");
                expression.textContent = policy.policyExpression || "No expression stored";

                details.append(name, expression);

                const status = document.createElement("span");
                status.className = policy.active ? "saved-policy-status active" : "saved-policy-status";
                status.textContent = policy.active ? "ACTIVE" : "INACTIVE";

                button.append(details, status);
                button.addEventListener("click", () => {
                    loadSavedPolicy(
                        policy.policyExpression || "",
                        policy.policyName || "Saved city policy"
                    );
                });

                container.appendChild(button);
            });

        } catch (error) {

            setPolicyConnection(false);
            console.error("Policy loading error:", error);
            container.replaceChildren();
            const errorMessage = document.createElement("div");
            errorMessage.className = "saved-policy-empty error";
            errorMessage.textContent = error.message || "Unable to load database policies.";
            container.appendChild(errorMessage);
        }
    }

    function loadSavedPolicy(expression, policyName) {
        if (!expression) {
            showError("This saved policy does not contain an expression.");
            return;
        }

        setPolicy(expression);

        document.querySelectorAll(".policy-template").forEach(button => {
            button.classList.remove("selected");
        });

        document.getElementById("result-status").textContent = "SAVED POLICY LOADED";
        document.getElementById("result-content").innerHTML = `
            <div class="result-empty">
                <div class="empty-symbol">⌘</div>
                <h4>${escapeHtml(policyName)} loaded</h4>
                <p>The expression was retrieved from the city policy database. Evaluate it to create a new history record.</p>
            </div>
        `;
    }

    function updateMetrics() {

        document.getElementById(
            "metric-evaluations"
        ).textContent = evaluationCount;

        document.getElementById(
            "metric-passed"
        ).textContent = passedCount;
    }

    function escapeHtml(value) {

        return String(value).replace(/[&<>"']/g, character => ({

            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"

        })[character]);
    }

    function showHelp() {

        alert(
            "SMARTCITY360 — INTERPRETER PATTERN\n\n" +

            "Write a policy expression using supported variables.\n\n" +

            "Comparison operators: >, <, >=, <=, ==\n" +
            "Logical operators: AND, OR\n" +
            "Grouping: ( )\n\n" +

            "Set the city values and click Evaluate policy.\n\n" +

            "Expressions are interpreted by the Java backend " +
            "and evaluation results are saved to MySQL."
        );
    }

    updateEditorInfo();

    loadSummary();
    loadHistory();
    loadPolicies();

</script>

<jsp:include page="/includes/global-toast.jsp" />
</body>
</html>
