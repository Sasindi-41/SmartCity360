<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="lk.sasindi.smartcity360.model.DashboardSummary" %>
<%@ page import="lk.sasindi.smartcity360.model.DashboardActivity" %>
<%@ page import="lk.sasindi.smartcity360.model.DashboardChartPoint" %>
<%@ page import="java.util.List" %>
<%!
    private String esc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;");
    }

    private String jsString(String value) {
        if (value == null) return "\"\"";
        String safe = value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r")
                .replace("<", "\\u003C").replace(">", "\\u003E")
                .replace("&", "\\u0026");
        return "\"" + safe + "\"";
    }

    private String chartLabels(List<DashboardChartPoint> points) {
        StringBuilder out = new StringBuilder("[");
        if (points != null) {
            for (int i = 0; i < points.size(); i++) {
                if (i > 0) out.append(',');
                out.append(jsString(points.get(i).getLabel()));
            }
        }
        return out.append(']').toString();
    }

    private String chartValues(List<DashboardChartPoint> points) {
        StringBuilder out = new StringBuilder("[");
        if (points != null) {
            for (int i = 0; i < points.size(); i++) {
                if (i > 0) out.append(',');
                out.append(points.get(i).getValue());
            }
        }
        return out.append(']').toString();
    }
%>
<%
    String contextPath = request.getContextPath();
    request.setAttribute("activePage", "dashboard");
    DashboardSummary dashboard = (DashboardSummary) request.getAttribute("dashboard");
    if (dashboard == null) {
        response.sendRedirect(contextPath + "/dashboard");
        return;
    }
    boolean canManageCity = Boolean.TRUE.equals(request.getAttribute("canManageCity"));
    List<DashboardActivity> activities = dashboard.getRecentActivities();
    String firstInitial = dashboard.getFirstName() == null || dashboard.getFirstName().isBlank()
            ? "U" : dashboard.getFirstName().substring(0, 1).toUpperCase();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartCity360 | Operations Overview</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/living-city.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/dashboard.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<div class="app-layout">
    <jsp:include page="/includes/sidebar.jsp"/>

    <main class="main-shell">
        <header class="topbar">
            <div class="topbar-heading">
                <span class="breadcrumb">SMART CITY / CONTROL CENTRE</span>
                <h1>Overview</h1>
            </div>
            <div class="topbar-actions">
                <div class="environment-chip"><span class="status-dot"></span><span>LIVE WORKSPACE</span></div>
                <a class="dashboard-profile-shortcut" href="<%= contextPath %>/profile" title="Open profile">
                    <span class="shortcut-avatar"><%= esc(firstInitial) %></span><span>My profile</span>
                </a>
            </div>
        </header>

        <div class="content-scroll">
            <div class="page-content dashboard-page dashboard-page-enhanced">
                <section class="dashboard-welcome">
                    <div>
                        <span class="eyebrow"><span class="welcome-live-dot"></span> CITY OPERATIONS / DAILY BRIEFING</span>
                        <h2>Welcome back, <%= esc(dashboard.getFirstName()) %>.</h2>
                        <p>A live overview of infrastructure, energy, civic services and city policies.</p>
                    </div>
                    <a href="<%= contextPath %>/profile" class="welcome-profile-link">Manage your account <span>↗</span></a>
                </section>

                <section class="dashboard-metrics dashboard-metrics-six">
                    <article class="dashboard-metric">
                        <div class="dashboard-metric-head"><span>BUILDINGS</span><span class="dashboard-metric-icon sage">⌂</span></div>
                        <strong><%= dashboard.getBuildingCount() %></strong>
                        <small>Registered city buildings</small>
                        <div class="metric-footnote"><span><%= dashboard.getCityCount() %> cities</span><span><%= dashboard.getDistrictCount() %> districts</span></div>
                    </article>
                    <article class="dashboard-metric">
                        <div class="dashboard-metric-head"><span>ENERGY METERS</span><span class="dashboard-metric-icon lime">ϟ</span></div>
                        <strong><%= dashboard.getEnergyMeterCount() %></strong>
                        <small>Registered monitoring devices</small>
                        <div class="metric-footnote"><span>Meter network</span><span>Database total</span></div>
                    </article>
                    <article class="dashboard-metric">
                        <div class="dashboard-metric-head"><span><%= canManageCity ? "OPEN REQUESTS" : "MY OPEN REQUESTS" %></span><span class="dashboard-metric-icon amber">≋</span></div>
                        <strong><%= dashboard.getOpenComplaintCount() %></strong>
                        <small>Requests awaiting resolution</small>
                        <div class="metric-footnote"><span>Of <%= dashboard.getTotalComplaintCount() %> total</span><a href="<%= contextPath %>/civic-requests.jsp">View requests ↗</a></div>
                    </article>
                    <article class="dashboard-metric">
                        <div class="dashboard-metric-head"><span>STREETLIGHTS</span><span class="dashboard-metric-icon blue">✳</span></div>
                        <strong><%= dashboard.getStreetlightCount() %></strong>
                        <small>Registered lighting units</small>
                        <div class="metric-footnote"><span>Smart infrastructure</span><span>Database total</span></div>
                    </article>
                    <article class="dashboard-metric">
                        <div class="dashboard-metric-head"><span>ENERGY READINGS</span><span class="dashboard-metric-icon lime">⌁</span></div>
                        <strong><%= dashboard.getEnergyReadingCount() %></strong>
                        <small>Stored meter readings</small>
                        <div class="metric-footnote"><span>All recorded readings</span><span>Energy data</span></div>
                    </article>
                    <article class="dashboard-metric">
                        <div class="dashboard-metric-head"><span>ACTIVE POLICIES</span><span class="dashboard-metric-icon sage">⌘</span></div>
                        <strong><%= dashboard.getActivePolicyCount() %></strong>
                        <small>Currently enabled city rules</small>
                        <div class="metric-footnote"><span><%= dashboard.getPolicyEvaluationCount() %> evaluations</span><span>Policy engine</span></div>
                    </article>
                </section>

                <section class="dashboard-analytics-heading">
                    <div><span class="eyebrow">OPERATIONAL INTELLIGENCE</span><h3>City performance analytics</h3></div>
                    <span class="analytics-period"><i></i> LIVE DATABASE SNAPSHOT</span>
                </section>

                <section class="dashboard-chart-grid">
                    <article class="dashboard-chart-card chart-card-wide">
                        <div class="dashboard-chart-heading">
                            <div><span class="eyebrow">ENERGY SYSTEMS</span><h4>Meter reading activity</h4><p>Number of readings stored per day · last 7 days</p></div>
                            <span class="chart-heading-icon chart-lime">ϟ</span>
                        </div>
                        <div class="dashboard-chart-canvas chart-canvas-tall"><canvas id="energyTrendChart"></canvas></div>
                    </article>
                    <article class="dashboard-chart-card">
                        <div class="dashboard-chart-heading">
                            <div><span class="eyebrow">CIVIC SERVICES</span><h4>Requests by status</h4><p><%= canManageCity ? "All city requests" : "Requests assigned to or submitted by you" %></p></div>
                            <span class="chart-heading-icon chart-amber">≋</span>
                        </div>
                        <div class="dashboard-chart-canvas"><canvas id="complaintStatusChart"></canvas></div>
                        <div class="chart-footnote">Total requests in this view: <strong><%= dashboard.getTotalComplaintCount() %></strong></div>
                    </article>
                    <article class="dashboard-chart-card">
                        <div class="dashboard-chart-heading">
                            <div><span class="eyebrow">CITY HIERARCHY</span><h4>Buildings by city</h4><p>Top 7 cities by registered buildings</p></div>
                            <span class="chart-heading-icon chart-sage">⌂</span>
                        </div>
                        <div class="dashboard-chart-canvas"><canvas id="buildingCityChart"></canvas></div>
                        <div class="chart-footnote">Across <strong><%= dashboard.getCityCount() %></strong> registered cities</div>
                    </article>
                    <article class="dashboard-chart-card">
                        <div class="dashboard-chart-heading">
                            <div><span class="eyebrow">STREET LIGHTING</span><h4>Operating modes</h4><p>Streetlights grouped by shared configuration</p></div>
                            <span class="chart-heading-icon chart-blue">✳</span>
                        </div>
                        <div class="dashboard-chart-canvas"><canvas id="streetlightModeChart"></canvas></div>
                        <div class="chart-footnote">Registered streetlights: <strong><%= dashboard.getStreetlightCount() %></strong></div>
                    </article>
                    <article class="dashboard-chart-card">
                        <div class="dashboard-chart-heading">
                            <div><span class="eyebrow">CITY POLICIES</span><h4>Evaluation outcomes</h4><p>Results produced by the policy interpreter</p></div>
                            <span class="chart-heading-icon chart-sage">⌘</span>
                        </div>
                        <div class="dashboard-chart-canvas"><canvas id="policyResultChart"></canvas></div>
                        <div class="chart-footnote">Total evaluations: <strong><%= dashboard.getPolicyEvaluationCount() %></strong></div>
                    </article>
                </section>

                <section class="dashboard-lower-grid">
                    <article class="dashboard-feature-panel">
                        <div class="dashboard-panel-heading">
                            <div><span class="eyebrow">INFRASTRUCTURE SNAPSHOT</span><h3>One connected city</h3></div>
                            <span class="dashboard-panel-index">01 / STRUCTURE</span>
                        </div>
                        <div class="dashboard-city-banner">
                            <img src="<%= contextPath %>/images/dashboard.jpg" alt="Smart city skyline" onerror="this.style.display='none';">
                            <div class="dashboard-city-overlay"><span class="city-tag">SMARTCITY360 NETWORK</span><h2>Better systems.<br>Better living.</h2><p>Infrastructure, energy and civic services in one operational workspace.</p></div>
                        </div>
                        <div class="dashboard-structure-stats">
                            <div><strong><%= dashboard.getCityCount() %></strong><span>Cities</span></div>
                            <div><strong><%= dashboard.getDistrictCount() %></strong><span>Districts</span></div>
                            <div><strong><%= dashboard.getStreetCount() %></strong><span>Streets</span></div>
                            <div><strong><%= dashboard.getBuildingCount() %></strong><span>Buildings</span></div>
                        </div>
                    </article>

                    <article class="dashboard-activity-panel">
                        <div class="dashboard-panel-heading"><div><span class="eyebrow">LATEST UPDATES</span><h3>Recent activity</h3></div><span class="activity-live"><i></i> DATABASE</span></div>
                        <div class="dashboard-activity-list">
                            <% if (activities == null || activities.isEmpty()) { %>
                            <div class="dashboard-empty-activity"><span class="empty-activity-icon">⌁</span><strong>No recent activity yet</strong><p>New system operations will appear here.</p></div>
                            <% } else { for (DashboardActivity activity : activities) {
                                String type = activity.getType() == null ? "SYSTEM" : activity.getType();
                                String iconClass = "activity-neutral"; String symbol = "•";
                                if (type.contains("CIVIC")) { iconClass = "activity-civic"; symbol = "≋"; }
                                else if (type.contains("ENERGY")) { iconClass = "activity-energy"; symbol = "ϟ"; }
                                else if (type.contains("POLICY")) { iconClass = "activity-policy"; symbol = "⌘"; }
                                else if (type.contains("POWER")) { iconClass = "activity-power"; symbol = "✳"; }
                            %>
                            <div class="dashboard-activity-row"><span class="dashboard-activity-symbol <%= iconClass %>"><%= symbol %></span><div class="dashboard-activity-copy"><div class="dashboard-activity-topline"><strong><%= esc(activity.getTitle()) %></strong><time><%= esc(activity.getTimeLabel()) %></time></div><p><%= esc(activity.getDescription()) %></p><span><%= esc(type) %></span></div></div>
                            <% } } %>
                        </div>
                        <a href="<%= contextPath %>/civic-requests.jsp" class="dashboard-activity-link">Open civic requests <span>→</span></a>
                    </article>
                </section>

                <section class="dashboard-modules-section">
                    <div class="dashboard-section-heading"><div><span class="eyebrow">SMARTCITY360 PLATFORM</span><h3>Explore your workspace</h3></div><p>Five modules. One connected city.</p></div>
                    <div class="dashboard-module-grid">
                        <% if (canManageCity) { %>
                        <a href="<%= contextPath %>/energy-systems.jsp" class="dashboard-module-card"><span class="module-card-number">01</span><span class="module-card-icon module-energy">ϟ</span><h4>Energy systems</h4><p>Secure and process incoming smart-meter data streams.</p><span class="module-card-pattern">DECORATOR</span><span class="module-card-arrow">↗</span></a>
                        <a href="<%= contextPath %>/hierarchy" class="dashboard-module-card"><span class="module-card-number">02</span><span class="module-card-icon module-hierarchy">⌂</span><h4>City hierarchy</h4><p>Explore cities, districts, streets and buildings.</p><span class="module-card-pattern">COMPOSITE</span><span class="module-card-arrow">↗</span></a>
                        <% } %>
                        <a href="<%= contextPath %>/civic-requests.jsp" class="dashboard-module-card"><span class="module-card-number">03</span><span class="module-card-icon module-civic">≋</span><h4>Civic requests</h4><p>Track complaints and follow their escalation journey.</p><span class="module-card-pattern">CHAIN OF RESPONSIBILITY</span><span class="module-card-arrow">↗</span></a>
                        <% if (canManageCity) { %>
                        <a href="<%= contextPath %>/street-lighting.jsp" class="dashboard-module-card"><span class="module-card-number">04</span><span class="module-card-icon module-light">✳</span><h4>Street lighting</h4><p>Reuse shared configurations across lighting units.</p><span class="module-card-pattern">FLYWEIGHT</span><span class="module-card-arrow">↗</span></a>
                        <a href="<%= contextPath %>/city-policies.jsp" class="dashboard-module-card"><span class="module-card-number">05</span><span class="module-card-icon module-policy">⌘</span><h4>City policies</h4><p>Interpret and evaluate operational city rules.</p><span class="module-card-pattern">INTERPRETER</span><span class="module-card-arrow">↗</span></a>
                        <% } %>
                    </div>
                </section>
                <footer class="page-footer"><span>SMARTCITY360</span><span>Urban intelligence platform · Connected operations</span></footer>
            </div>
        </div>
    </main>
</div>

<script>
    (() => {
        const palette = ['#BDEB62', '#6F8F7A', '#D6A45C', '#7396A9', '#9DAE8C', '#596A60', '#D1D9CE'];
        const gridColor = 'rgba(93, 108, 96, 0.12)';
        const tickColor = '#879188';
        const fontFamily = 'Inter, ui-sans-serif, system-ui, -apple-system, Segoe UI, sans-serif';

        const common = {
            responsive: true,
            maintainAspectRatio: false,
            animation: { duration: 850, easing: 'easeOutQuart' },
            interaction: { intersect: false, mode: 'index' },
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#68756B', usePointStyle: true, pointStyle: 'circle',
                        boxWidth: 7, boxHeight: 7, padding: 17,
                        font: { family: fontFamily, size: 10, weight: '600' }
                    }
                },
                tooltip: {
                    backgroundColor: '#17211B', titleColor: '#DCE7D8', bodyColor: '#FFFFFF',
                    padding: 12, cornerRadius: 9, displayColors: true,
                    titleFont: { family: fontFamily, weight: '700', size: 11 },
                    bodyFont: { family: fontFamily, size: 11 },
                    boxPadding: 4
                }
            },
            scales: {
                x: {
                    ticks: { color: tickColor, font: { family: fontFamily, size: 10 }, maxRotation: 0 },
                    grid: { display: false }, border: { display: false }
                },
                y: {
                    beginAtZero: true,
                    ticks: { color: tickColor, precision: 0, padding: 9, font: { family: fontFamily, size: 10 } },
                    grid: { color: gridColor, drawTicks: false }, border: { display: false }
                }
            }
        };

        function emptyChart(id, message) {
            const canvas = document.getElementById(id);
            if (!canvas) return;
            const parent = canvas.parentElement;
            parent.classList.add('chart-empty-state');
            parent.innerHTML = '<div class="chart-no-data"><span>⌁</span><strong>No records to visualise</strong><p>' + message + '</p></div>';
        }

        function renderChart(id, labels, values, type, config = {}) {
            const canvas = document.getElementById(id);
            if (!canvas) return;
            if (!Array.isArray(labels) || labels.length === 0) {
                emptyChart(id, config.emptyMessage || 'Records will appear here when data becomes available.');
                return;
            }
            if (typeof Chart === 'undefined') {
                emptyChart(id, 'The chart library did not load. Check your internet connection and refresh.');
                return;
            }

            const circular = type === 'doughnut' || type === 'pie';
            const horizontal = config.indexAxis === 'y';
            let background = palette;
            let border = '#FFFFFF';
            let borderWidth = 3;
            let pointRadius = 0;
            let tension = 0;
            let fill = false;

            if (type === 'line') {
                const context = canvas.getContext('2d');
                const gradient = context.createLinearGradient(0, 0, 0, canvas.clientHeight || 260);
                gradient.addColorStop(0, 'rgba(189, 235, 98, 0.38)');
                gradient.addColorStop(1, 'rgba(189, 235, 98, 0.015)');
                background = gradient;
                border = '#91BD42';
                borderWidth = 2.5;
                pointRadius = 3.5;
                tension = 0.38;
                fill = true;
            } else if (type === 'bar') {
                background = horizontal
                    ? labels.map((_, i) => i === 0 ? '#BDEB62' : 'rgba(111, 143, 122, 0.72)')
                    : 'rgba(189, 235, 98, 0.78)';
                border = 'transparent';
                borderWidth = 0;
            }

            new Chart(canvas, {
                type,
                data: {
                    labels,
                    datasets: [{
                        label: config.datasetLabel || 'Records', data: values,
                        backgroundColor: background, borderColor: border,
                        borderWidth, borderRadius: type === 'bar' ? 7 : 0,
                        borderSkipped: false, tension, fill,
                        pointRadius, pointHoverRadius: 6,
                        pointBackgroundColor: '#BDEB62', pointBorderColor: '#FFFFFF',
                        pointBorderWidth: 2, hoverOffset: circular ? 7 : 0,
                        maxBarThickness: horizontal ? 18 : 34,
                        categoryPercentage: 0.72, barPercentage: 0.78
                    }]
                },
                options: {
                    ...common,
                    indexAxis: config.indexAxis || 'x',
                    cutout: type === 'doughnut' ? '72%' : undefined,
                    plugins: {
                        ...common.plugins,
                        legend: { ...common.plugins.legend, display: circular, position: 'bottom' }
                    },
                    scales: circular ? {} : (horizontal ? {
                        x: { beginAtZero: true, ticks: { color: tickColor, precision: 0, font: { family: fontFamily, size: 10 } }, grid: { color: gridColor }, border: { display: false } },
                        y: { ticks: { color: '#647168', font: { family: fontFamily, size: 10, weight: '600' } }, grid: { display: false }, border: { display: false } }
                    } : common.scales)
                }
            });
        }

        renderChart('energyTrendChart', <%= chartLabels(dashboard.getEnergyTrend()) %>, <%= chartValues(dashboard.getEnergyTrend()) %>, 'line', { datasetLabel: 'Readings' });
        renderChart('complaintStatusChart', <%= chartLabels(dashboard.getComplaintStatusBreakdown()) %>, <%= chartValues(dashboard.getComplaintStatusBreakdown()) %>, 'doughnut', { emptyMessage: 'No complaint records match this account.' });
        renderChart('buildingCityChart', <%= chartLabels(dashboard.getBuildingDistribution()) %>, <%= chartValues(dashboard.getBuildingDistribution()) %>, 'bar', { indexAxis: 'y', datasetLabel: 'Buildings' });
        renderChart('streetlightModeChart', <%= chartLabels(dashboard.getStreetlightModeBreakdown()) %>, <%= chartValues(dashboard.getStreetlightModeBreakdown()) %>, 'doughnut', { emptyMessage: 'Streetlight configurations have not been assigned yet.' });
        renderChart('policyResultChart', <%= chartLabels(dashboard.getPolicyResultBreakdown()) %>, <%= chartValues(dashboard.getPolicyResultBreakdown()) %>, 'bar', { datasetLabel: 'Evaluations' });
    })();
</script>
<jsp:include page="/includes/global-toast.jsp" />
</body>
</html>
