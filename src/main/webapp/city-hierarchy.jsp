
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    request.setAttribute("activePage", "hierarchy");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>SmartCity360 | Infrastructure Management</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/living-city.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/dashboard.css">


</head>

<body>

<div class="app-layout">

    <jsp:include page="/includes/sidebar.jsp" />

    <main class="main-shell">

        <header class="topbar">

            <div class="topbar-heading">

                <span class="breadcrumb">
                    SMARTCITY360 / INFRASTRUCTURE / COMPOSITE
                </span>

                <h1>Infrastructure management</h1>

            </div>

            <div class="topbar-actions">

                <div class="environment-chip">
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

            <div class="page-content hierarchy-page">

                <section class="hh-hero">

                    <div class="hh-copy">

                        <span class="eyebrow">
                            <span class="intro-indicator"></span>
                            PART B · COMPOSITE DESIGN PATTERN
                        </span>

                        <h2>City infrastructure, <span>one structure.</span></h2>

                        <p>
                            Explore a city as a single tree of districts,
                            streets and buildings. Send one command, such as
                            shutting down power or downloading a usage report,
                            to any level and let every component below it respond.
                        </p>

                        <div class="hh-tags">
                            <span><i class="hh-tag-dot"></i> Uniform operations</span>
                            <span><i class="hh-tag-dot"></i> Power control</span>
                            <span><i class="hh-tag-dot"></i> Usage reports</span>
                        </div>

                        <div class="hh-actions">

                            <button class="infra-button secondary"
                                    type="button"
                                    onclick="expandAll()">

                                <span class="button-icon">
                                    <svg viewBox="0 0 24 24">
                                        <path d="M8 3H5a2 2 0 0 0-2 2v3"/>
                                        <path d="M16 3h3a2 2 0 0 1 2 2v3"/>
                                        <path d="M8 21H5a2 2 0 0 1-2-2v-3"/>
                                        <path d="M16 21h3a2 2 0 0 0 2-2v-3"/>
                                        <path d="M8 12h8"/>
                                        <path d="M12 8v8"/>
                                    </svg>
                                </span>

                                Expand all
                            </button>

                            <button class="infra-button primary"
                                    type="button"
                                    onclick="focusAddComponent()">

                                <span class="button-icon">
                                    <svg viewBox="0 0 24 24">
                                        <path d="M12 5v14"/>
                                        <path d="M5 12h14"/>
                                    </svg>
                                </span>

                                Add component
                            </button>

                        </div>

                    </div>

                    <div class="hh-visual" aria-hidden="true">

                        <div class="hh-orbit hh-orbit-one"></div>
                        <div class="hh-orbit hh-orbit-two"></div>

                        <div class="hh-core">
                            <svg viewBox="0 0 24 24">
                                <rect x="9" y="2" width="6" height="5" rx="1"/>
                                <rect x="2" y="17" width="6" height="5" rx="1"/>
                                <rect x="16" y="17" width="6" height="5" rx="1"/>
                                <path d="M12 7v6M5 17v-2a1 1 0 0 1 1-1h12a1 1 0 0 1 1 1v2"/>
                            </svg>
                            <span>CITY<br>TREE</span>
                        </div>

                        <div class="hh-node hh-node-city">
                            <b>01</b>
                            <small>CITY</small>
                        </div>

                        <div class="hh-node hh-node-district">
                            <b>02</b>
                            <small>DISTRICT</small>
                        </div>

                        <div class="hh-node hh-node-street">
                            <b>03</b>
                            <small>STREET</small>
                        </div>

                        <div class="hh-node hh-node-building">
                            <b>04</b>
                            <small>BUILDING</small>
                        </div>

                    </div>

                </section>

                <section class="infrastructure-metrics">

                    <article class="infra-metric-card">

                        <div class="infra-metric-top">

                            <span class="infra-metric-label">
                                Total cities
                            </span>

                            <span class="infra-metric-icon">
                                <svg viewBox="0 0 24 24">
                                    <path d="M3 21h18"/>
                                    <path d="M5 21V7l8-4v18"/>
                                    <path d="M19 21V11l-6-4"/>
                                    <path d="M8 9v.01M8 12v.01M8 15v.01"/>
                                    <path d="M16 13v.01M16 16v.01M16 19v.01"/>
                                </svg>
                            </span>

                        </div>

                        <div class="infra-metric-value" id="cityCount">
                            01
                        </div>

                        <div class="infra-metric-foot">
                            <span class="metric-status-dot"></span>
                            Root infrastructure
                        </div>

                    </article>


                    <article class="infra-metric-card">

                        <div class="infra-metric-top">

                            <span class="infra-metric-label">
                                Zones
                            </span>

                            <span class="infra-metric-icon">
                                <svg viewBox="0 0 24 24">
                                    <rect x="3" y="3" width="8" height="8" rx="1"/>
                                    <rect x="13" y="3" width="8" height="8" rx="1"/>
                                    <rect x="3" y="13" width="8" height="8" rx="1"/>
                                    <rect x="13" y="13" width="8" height="8" rx="1"/>
                                </svg>
                            </span>

                        </div>

                        <div class="infra-metric-value" id="districtCount">
                            02
                        </div>

                        <div class="infra-metric-foot">
                            Registered city zones
                        </div>

                    </article>


                    <article class="infra-metric-card">

                        <div class="infra-metric-top">

                            <span class="infra-metric-label">
                                Streets
                            </span>

                            <span class="infra-metric-icon">
                                <svg viewBox="0 0 24 24">
                                    <path d="M4 21 9 3"/>
                                    <path d="M20 21 15 3"/>
                                    <path d="M12 4v3M12 10v3M12 16v3"/>
                                </svg>
                            </span>

                        </div>

                        <div class="infra-metric-value" id="streetCount">
                            03
                        </div>

                        <div class="infra-metric-foot">
                            Registered road networks
                        </div>

                    </article>


                    <article class="infra-metric-card">

                        <div class="infra-metric-top">

                            <span class="infra-metric-label">
                                Buildings
                            </span>

                            <span class="infra-metric-icon">
                                <svg viewBox="0 0 24 24">
                                    <rect x="4" y="3" width="16" height="18" rx="1"/>
                                    <path d="M9 7h2M14 7h2M9 11h2M14 11h2"/>
                                    <path d="M10 21v-5h4v5"/>
                                </svg>
                            </span>

                        </div>

                        <div class="infra-metric-value" id="buildingCount">
                            03
                        </div>

                        <div class="infra-metric-foot">
                            Managed infrastructure assets
                        </div>

                    </article>

                </section>

                <section class="infrastructure-workspace">

                    <section class="infra-panel hierarchy-explorer">

                        <div class="infra-panel-header">

                            <div>

                                <span class="eyebrow">
                                    INFRASTRUCTURE DIRECTORY
                                </span>

                                <h2>Hierarchy explorer</h2>

                                <p>
                                    Navigate through the city's infrastructure.
                                </p>

                            </div>

                            <span class="panel-counter"
                                  id="componentCount">
                                09 COMPONENTS
                            </span>

                        </div>

                        <div class="hierarchy-search-area">

                            <div class="hierarchy-search">

                                <svg viewBox="0 0 24 24">
                                    <circle cx="11" cy="11" r="7"/>
                                    <path d="m20 20-4-4"/>
                                </svg>

                                <input type="search"
                                       id="hierarchySearch"
                                       placeholder="Search infrastructure..."
                                       autocomplete="off">

                                <kbd>SEARCH</kbd>

                            </div>

                        </div>

                        <div class="hierarchy-filter-bar">

                            <button class="hierarchy-filter active"
                                    type="button"
                                    data-filter="all"
                                    onclick="setFilter('all', this)">
                                All components
                            </button>

                            <button class="hierarchy-filter"
                                    type="button"
                                    data-filter="active"
                                    onclick="setFilter('active', this)">
                                Active
                            </button>

                            <button class="hierarchy-filter"
                                    type="button"
                                    data-filter="off"
                                    onclick="setFilter('off', this)">
                                Power off
                            </button>

                        </div>

                        <div class="hierarchy-tree-toolbar">

                            <div class="hierarchy-legend">

                                <span>
                                    <i class="legend-symbol city-symbol"></i>
                                    City
                                </span>

                                <span>
                                    <i class="legend-symbol district-symbol"></i>
                                    Zone
                                </span>

                                <span>
                                    <i class="legend-symbol street-symbol"></i>
                                    Street
                                </span>

                                <span>
                                    <i class="legend-symbol building-symbol"></i>
                                    Building
                                </span>

                            </div>

                            <button class="tree-action-button"
                                    type="button"
                                    onclick="collapseAll()">
                                Collapse all
                            </button>

                        </div>

                        <div class="hierarchy-tree-container">

                            <div id="hierarchyTree"
                                 class="advanced-hierarchy-tree">
                            </div>

                            <div id="emptyTreeMessage"
                                 class="empty-tree-message"
                                 hidden>

                                <span class="empty-tree-icon">
                                    <svg viewBox="0 0 24 24">
                                        <circle cx="11" cy="11" r="7"/>
                                        <path d="m20 20-4-4"/>
                                    </svg>
                                </span>

                                <strong>No components found</strong>

                                <span>
                                    Try another search term or filter.
                                </span>

                            </div>

                        </div>


                        <div class="hierarchy-panel-footer">

                            <span>
                                <span class="footer-live-dot"></span>
                                Structure preview
                            </span>

                            <span>
                                Select a node to inspect
                            </span>

                        </div>

                    </section>

                    <section class="infra-panel component-inspector">

                        <div class="infra-panel-header">

                            <div>

                                <span class="eyebrow">
                                    ASSET INFORMATION
                                </span>

                                <h2>Component inspector</h2>

                                <p>
                                    Details and operations for the selected node.
                                </p>

                            </div>

                            <span class="inspector-status"
                                  id="inspectorStatus">
                                ACTIVE
                            </span>

                        </div>

                        <div class="component-identity">

                            <div class="component-identity-icon"
                                 id="selectedIcon">

                                <svg viewBox="0 0 24 24">
                                    <path d="M3 21h18"/>
                                    <path d="M5 21V7l8-4v18"/>
                                    <path d="M19 21V11l-6-4"/>
                                </svg>

                            </div>

                            <div class="component-identity-text">

                                <span class="component-type-label"
                                      id="selectedType">
                                    CITY ROOT
                                </span>

                                <h3 id="selectedName">
                                    SmartCity360
                                </h3>

                                <span class="component-id"
                                      id="selectedId">
                                    COMPONENT ID: CTY-001
                                </span>

                            </div>

                        </div>

                        <div class="inspector-section">

                            <div class="inspector-section-heading">
                                Component properties
                            </div>

                            <div class="component-property-grid">

                                <div class="component-property">

                                    <span>Component type</span>

                                    <strong id="propertyType">
                                        City
                                    </strong>

                                </div>

                                <div class="component-property">

                                    <span>Direct children</span>

                                    <strong id="propertyChildren">
                                        02
                                    </strong>

                                </div>

                                <div class="component-property">

                                    <span>Power status</span>

                                    <strong id="propertyPower"
                                            class="property-active">
                                        Operational
                                    </strong>

                                </div>

                                <div class="component-property">

                                    <span>Total buildings</span>

                                    <strong id="propertyBuildings">
                                        03
                                    </strong>

                                </div>

                            </div>

                        </div>

                        <div class="usage-summary">

                            <div class="usage-summary-top">

                                <div>
                                    <span class="usage-label">
                                        AGGREGATED ENERGY USAGE
                                    </span>

                                    <div class="usage-value">
                                        <strong id="usageValue">—</strong>
                                        <span>kWh</span>
                                    </div>
                                </div>

                                <span class="usage-icon">
                                    <svg viewBox="0 0 24 24">
                                        <path d="M13 2 4 14h7l-1 8 10-13h-7l0-7Z"/>
                                    </svg>
                                </span>

                            </div>

                            <p id="usageDescription">
                                Combined energy usage across the selected
                                component and its descendants.
                            </p>

                        </div>

                        <div class="inspector-section management-section">

                            <div class="inspector-section-heading">
                                Infrastructure operations
                            </div>

                            <button class="operation-action report-action"
                                    type="button"
                                    onclick="downloadUsageReport()">

                                <span class="operation-action-icon">
                                    <svg viewBox="0 0 24 24">
                                        <path d="M12 3v12"/>
                                        <path d="m7 11 5 5 5-5"/>
                                        <path d="M5 21h14"/>
                                    </svg>
                                </span>

                                <span class="operation-action-text">
                                    <strong>Download usage report</strong>
                                    <small>Save this scope as a PDF file</small>
                                </span>

                                <span class="operation-arrow">→</span>

                            </button>


                            <button class="operation-action shutdown-action"
                                    type="button"
                                    onclick="shutdownSelected()">

                                <span class="operation-action-icon">
                                    <svg viewBox="0 0 24 24">
                                        <path d="M12 2v10"/>
                                        <path d="M5.6 5.6a9 9 0 1 0 12.8 0"/>
                                    </svg>
                                </span>

                                <span class="operation-action-text">
                                    <strong>Shutdown power</strong>
                                    <small>Turn off this component and descendants</small>
                                </span>

                                <span class="operation-arrow">→</span>

                            </button>


                            <button class="operation-action restore-action"
                                    type="button"
                                    onclick="restoreSelected()">

                                <span class="operation-action-icon">
                                    <svg viewBox="0 0 24 24">
                                        <path d="M20 7v5h-5"/>
                                        <path d="M4 17v-5h5"/>
                                        <path d="M5.6 9a8 8 0 0 1 13.2-2L20 12"/>
                                        <path d="M4 12l1.2 5a8 8 0 0 0 13.2-2"/>
                                    </svg>
                                </span>

                                <span class="operation-action-text">
                                    <strong>Restore power</strong>
                                    <small>Restore this component and descendants</small>
                                </span>

                                <span class="operation-arrow">→</span>

                            </button>

                        </div>

                        <div class="inspector-section add-component-section"
                             id="addComponentSection">

                            <div class="inspector-section-heading">
                                Add child component
                            </div>

                            <p class="add-component-description">
                                Select an existing parent component and add its next
                                valid infrastructure level.
                            </p>

                            <div class="infra-form-field">

                                <label for="parentComponent">
                                    Parent component <span class="required-mark">*</span>
                                </label>

                                <select id="parentComponent" required>
                                    <option value="">Loading components...</option>
                                </select>

                                <small class="field-helper">
                                    Select the City, Zone or Street that will contain this component.
                                </small>

                            </div>

                            <div class="infra-form-field">

                                <label for="newComponentType">
                                    Child component type
                                </label>

                                <input type="text"
                                       id="newComponentType"
                                       value=""
                                       placeholder="Automatically determined"
                                       readonly>

                                <small class="field-helper" id="componentTypeHelp">
                                    The available child type depends on the selected parent.
                                </small>

                            </div>

                            <div class="infra-form-field">

                                <label for="newComponentName">
                                    Component name <span class="required-mark">*</span>
                                </label>

                                <input type="text"
                                       id="newComponentName"
                                       maxlength="150"
                                       placeholder="Enter component name"
                                       autocomplete="off"
                                       required>

                                <small class="field-helper">
                                    Maximum 150 characters.
                                </small>

                            </div>

                            <div class="infra-form-field" id="descriptionField">

                                <label for="newComponentDescription">
                                    Description
                                </label>

                                <textarea id="newComponentDescription"
                                          rows="3"
                                          maxlength="500"
                                          placeholder="Enter a short description"
                                          autocomplete="off"></textarea>

                                <small class="field-helper">
                                    Optional. Maximum 500 characters.
                                </small>

                            </div>

                            <div id="buildingFields" hidden>

                                <div class="form-group">
                                    <label for="newBuildingType">Building Type</label>
                                    <select id="newBuildingType" name="buildingType">
                                        <option value="">Loading building types...</option>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label for="newBuildingAddressLine1">Address Line 1 (Optional)</label>
                                    <input type="text"
                                           id="newBuildingAddressLine1"
                                           name="addressLine1"
                                           maxlength="255"
                                           placeholder="Enter address line 1">
                                </div>

                                <div class="form-group">
                                    <label for="newBuildingAddressLine2">Address Line 2 (Optional)</label>
                                    <input type="text"
                                           id="newBuildingAddressLine2"
                                           name="addressLine2"
                                           maxlength="255"
                                           placeholder="Enter address line 2">
                                </div>

                            </div>

                            <button class="infra-button primary add-component-button"
                                    id="createComponentButton"
                                    type="button"
                                    onclick="addComponent()"
                                    disabled>

        <span class="button-icon">
            <svg viewBox="0 0 24 24">
                <path d="M12 5v14"/>
                <path d="M5 12h14"/>
            </svg>
        </span>

                                Create component

                            </button>

                            <div class="form-helper-text">
                                A parent component is required. Only valid Composite
                                hierarchy relationships are permitted.
                            </div>

                        </div>

                        <div id="operationResult"
                             class="infra-operation-result"
                             role="status"
                             aria-live="polite"
                             hidden>
                        </div>

                    </section>

                </section>

                <section class="infrastructure-lower-grid">

                    <article class="infra-information-panel">

                        <div class="information-panel-heading">

                            <div>
                                <span class="eyebrow">
                                    DESIGN ARCHITECTURE
                                </span>

                                <h2>Composite structure</h2>
                            </div>

                            <span class="architecture-icon">
                                <svg viewBox="0 0 24 24">
                                    <path d="M12 3v5"/>
                                    <path d="M5 16v-3h14v3"/>
                                    <path d="M5 16v5M12 8v5M19 16v5"/>
                                    <circle cx="12" cy="3" r="1.5"/>
                                    <circle cx="5" cy="20" r="1.5"/>
                                    <circle cx="12" cy="20" r="1.5"/>
                                    <circle cx="19" cy="20" r="1.5"/>
                                </svg>
                            </span>

                        </div>

                        <p class="architecture-description">
                            The Composite pattern allows individual objects
                            and groups of objects to be treated through
                            a common component interface.
                        </p>

                        <div class="architecture-flow">

                            <div class="architecture-node composite-node">
                                <span>01</span>
                                <strong>City</strong>
                                <small>Composite</small>
                            </div>

                            <span class="architecture-connector">→</span>

                            <div class="architecture-node composite-node">
                                <span>02</span>
                                <strong>Zone</strong>
                                <small>Composite</small>
                            </div>

                            <span class="architecture-connector">→</span>

                            <div class="architecture-node composite-node">
                                <span>03</span>
                                <strong>Street</strong>
                                <small>Composite</small>
                            </div>

                            <span class="architecture-connector">→</span>

                            <div class="architecture-node leaf-node">
                                <span>04</span>
                                <strong>Building</strong>
                                <small>Leaf</small>
                            </div>

                        </div>

                        <div class="architecture-footer">
                            <span>
                                <i class="architecture-dot composite-dot"></i>
                                Composite objects contain children
                            </span>

                            <span>
                                <i class="architecture-dot leaf-dot"></i>
                                Leaf objects have no children
                            </span>
                        </div>

                    </article>

                    <article class="infra-information-panel activity-panel">

                        <div class="information-panel-heading">

                            <div>
                                <span class="eyebrow">
                                    SESSION MONITOR
                                </span>

                                <h2>Recent activity</h2>
                            </div>

                            <span class="activity-count"
                                  id="activityCount">
                                01
                            </span>

                        </div>

                        <div class="activity-list"
                             id="activityList">

                            <div class="activity-item">

                                <span class="activity-indicator"></span>

                                <div>
                                    <strong>Infrastructure workspace initialized</strong>
                                    <small>System demonstration started</small>
                                </div>

                                <span class="activity-time">NOW</span>

                            </div>

                        </div>

                    </article>

                    <article class="infra-information-panel activity-panel">

                        <div class="information-panel-heading">

                            <div>
                                <span class="eyebrow">
                                    DATABASE ACTIVITY
                                </span>

                                <h2>Power operation history</h2>
                            </div>

                            <span class="activity-count"
                                  id="powerHistoryCount">
                                00
                            </span>

                        </div>

                        <div class="activity-list"
                             id="powerHistoryList">

                            <div class="activity-item">

                                <span class="activity-indicator"></span>

                                <div>
                                    <strong>Loading history...</strong>
                                    <small>Reading saved power operations</small>
                                </div>

                            </div>

                        </div>

                    </article>

                </section>

                <div class="hierarchy-disclaimer">

                    <span>
                        <svg viewBox="0 0 24 24">
                            <circle cx="12" cy="12" r="9"/>
                            <path d="M12 11v5M12 8h.01"/>
                        </svg>

                        Demonstration environment
                    </span>

                    <p>
                        Hierarchy changes and power operations are saved in
                        the database. Power and usage commands are applied
                        through the Java Composite classes.
                    </p>

                </div>


                <footer class="page-footer">

                    <span>SMARTCITY360</span>

                    <span>
                        Infrastructure management · Composite pattern
                    </span>

                </footer>

            </div>

        </div>

    </main>

</div>

<div id="hierarchyOverviewModal"
     class="hierarchy-overview-modal"
     aria-hidden="true">

    <div class="hierarchy-overview-backdrop"
         onclick="closeHierarchyOverview()">
    </div>

    <div class="hierarchy-overview-dialog">

        <div class="hierarchy-overview-header">

            <div>
                <div class="overview-eyebrow">
                    SMARTCITY360 / INFRASTRUCTURE
                </div>

                <h2>City Hierarchy</h2>

                <p>
                    Explore the complete structure of your city.
                </p>
            </div>

            <button type="button"
                    class="hierarchy-overview-close"
                    onclick="closeHierarchyOverview()"
                    aria-label="Close hierarchy overview">
                &times;
            </button>

        </div>

        <div class="hierarchy-overview-toolbar">

            <div class="overview-status">
                <span class="overview-live-indicator"></span>
                <span>Live hierarchy</span>
            </div>

            <div class="overview-legend">

                <span>
                    <i class="legend-city"></i>
                    City
                </span>

                <span>
                    <i class="legend-district"></i>
                    District
                </span>

                <span>
                    <i class="legend-street"></i>
                    Street
                </span>

                <span>
                    <i class="legend-building"></i>
                    Building
                </span>

            </div>

        </div>

        <div id="hierarchyOverviewContent"
             class="hierarchy-overview-content">

            <div id="hierarchyCanvasStage"
                 class="hierarchy-canvas-stage">

                <div id="hierarchyOverviewTree"
                     class="hierarchy-overview-tree">
                </div>

            </div>

            <div class="hierarchy-zoom-controls">

                <button type="button"
                        onclick="changeHierarchyZoom(0.1)"
                        title="Zoom in"
                        aria-label="Zoom in">
                    +
                </button>

                <div id="hierarchyZoomLevel"
                     class="hierarchy-zoom-level">
                    100%
                </div>

                <button type="button"
                        onclick="changeHierarchyZoom(-0.1)"
                        title="Zoom out"
                        aria-label="Zoom out">
                    −
                </button>

                <div class="zoom-control-divider"></div>

                <button type="button"
                        onclick="resetHierarchyZoom()"
                        title="Reset zoom"
                        aria-label="Reset zoom">
                    <svg viewBox="0 0 24 24"
                         width="17"
                         height="17"
                         fill="none"
                         stroke="currentColor"
                         stroke-width="1.8"
                         stroke-linecap="round"
                         stroke-linejoin="round">
                        <path d="M3 11a9 9 0 0 1 15.5-6L21 8"/>
                        <path d="M21 3v5h-5"/>
                        <path d="M21 13a9 9 0 0 1-15.5 6L3 16"/>
                        <path d="M3 21v-5h5"/>
                    </svg>
                </button>

            </div>

        </div>

        <div class="hierarchy-overview-footer">

            <span>SMARTCITY360</span>

            <span>
                CITY → DISTRICT → STREET → BUILDING
            </span>

        </div>

    </div>

</div>
<script>

    let selectedId = null;

    let currentFilter = "all";

    let collapsedNodes = new Set();

    let cityData = [];

    const contextPath = "<%= contextPath %>";

    const allowedChildren = {
        City: "District",
        District: "Street",
        Street: "Building",
        Building: null
    };

    const icons = {

        City: `
            <svg viewBox="0 0 24 24">
                <path d="M3 21h18"/>
                <path d="M5 21V7l8-4v18"/>
                <path d="M19 21V11l-6-4"/>
                <path d="M8 9v.01M8 12v.01M8 15v.01"/>
            </svg>
        `,

        District: `
            <svg viewBox="0 0 24 24">
                <rect x="3" y="3" width="8" height="8" rx="1"/>
                <rect x="13" y="3" width="8" height="8" rx="1"/>
                <rect x="3" y="13" width="8" height="8" rx="1"/>
                <rect x="13" y="13" width="8" height="8" rx="1"/>
            </svg>
        `,

        Street: `
            <svg viewBox="0 0 24 24">
                <path d="M4 21 9 3"/>
                <path d="M20 21 15 3"/>
                <path d="M12 4v3M12 10v3M12 16v3"/>
            </svg>
        `,

        Building: `
            <svg viewBox="0 0 24 24">
                <rect x="4" y="3" width="16" height="18" rx="1"/>
                <path d="M9 7h2M14 7h2M9 11h2M14 11h2"/>
                <path d="M10 21v-5h4v5"/>
            </svg>
        `
    };

    function getNodeKey(node) {
        return node.type + "-" + node.id;
    }


    function getChildren(node) {
        return Array.isArray(node.children) ? node.children : [];
    }


    function findNode(nodes, id) {

        if (!Array.isArray(nodes)) {
            nodes = [nodes];
        }

        for (const node of nodes) {

            if (getNodeKey(node) === id) {
                return node;
            }

            const result = findNode(getChildren(node), id);

            if (result) {
                return result;
            }
        }

        return null;
    }


    function findNodeAndParent(nodes, id, parent = null) {

        if (!Array.isArray(nodes)) {
            nodes = [nodes];
        }

        for (const node of nodes) {

            if (getNodeKey(node) === id) {
                return {
                    node: node,
                    parent: parent
                };
            }

            const result = findNodeAndParent(
                getChildren(node),
                id,
                node
            );

            if (result) {
                return result;
            }
        }

        return null;
    }


    function getNodePath(id, nodes = cityData, path = []) {

        for (const node of nodes) {

            const currentPath = [...path, node.name];

            if (getNodeKey(node) === id) {
                return currentPath;
            }

            const result = getNodePath(
                id,
                getChildren(node),
                currentPath
            );

            if (result.length > 0) {
                return result;
            }
        }

        return [];
    }

    function countByType(nodes, type) {

        if (!Array.isArray(nodes)) {
            nodes = [nodes];
        }

        let count = 0;

        for (const node of nodes) {

            if (node.type === type) {
                count++;
            }

            count += countByType(getChildren(node), type);
        }

        return count;
    }


    function countDescendantBuildings(node) {

        return countByType(getChildren(node), "Building");
    }


    function countAllComponents(nodes) {

        if (!Array.isArray(nodes)) {
            nodes = [nodes];
        }

        let count = 0;

        for (const node of nodes) {

            count++;

            count += countAllComponents(getChildren(node));
        }

        return count;
    }


    function calculateUsage(node) {

        if (node.type === "Building") {
            return Number(node.usage) || 0;
        }

        return getChildren(node).reduce(
            (total, child) => total + calculateUsage(child),
            0
        );
    }

    function isFullyPowered(node) {

        if (!node.power) {
            return false;
        }

        return getChildren(node).every(
            child => isFullyPowered(child)
        );
    }


    function getPowerStatus(node) {

        if (node.powerStatus) {
            return node.powerStatus;
        }

        const children = getChildren(node);

        if (isFullyPowered(node)) {
            return "ACTIVE";
        }

        if (
            !node.power &&
            children.every(child => !child.power)
        ) {
            return "OFF";
        }

        return "PARTIAL";
    }

    function matchesSearch(node, search) {

        if (!search) {
            return true;
        }

        const name = String(node.name || "").toLowerCase();
        const type = String(node.type || "").toLowerCase();
        const code = String(node.code || "").toLowerCase();

        const ownMatch =
            name.includes(search) ||
            type.includes(search) ||
            code.includes(search);

        if (ownMatch) {
            return true;
        }

        return getChildren(node).some(
            child => matchesSearch(child, search)
        );
    }


    function matchesFilter(node) {

        if (currentFilter === "all") {
            return true;
        }

        const status = getPowerStatus(node);

        if (currentFilter === "active") {
            return status === "ACTIVE";
        }

        if (currentFilter === "off") {
            return status === "OFF";
        }

        return true;
    }


    function nodeOrDescendantMatches(node, search) {

        if (!matchesSearch(node, search)) {
            return false;
        }

        if (matchesFilter(node)) {
            return true;
        }

        return getChildren(node).some(
            child => nodeOrDescendantMatches(child, search)
        );
    }

    function renderNode(node) {

        const search = document.getElementById("hierarchySearch")
            .value.trim().toLowerCase();

        if (!nodeOrDescendantMatches(node, search)) {
            return null;
        }

        const wrapper = document.createElement("div");

        wrapper.className = "advanced-tree-node";
        wrapper.dataset.id = getNodeKey(node);

        const row = document.createElement("div");

        row.className = "advanced-tree-row";

        if (getNodeKey(node) === selectedId) {
            row.classList.add("selected");
        }

        if (!node.power) {
            row.classList.add("power-disabled");
        }

        const childrenList = getChildren(node);
        const hasChildren = childrenList.length > 0;

        const nodeKey = getNodeKey(node);
        const isCollapsed = collapsedNodes.has(nodeKey);

        const toggle = document.createElement("button");

        toggle.type = "button";
        toggle.className = "tree-expand-button";

        toggle.setAttribute(
            "aria-label",
            isCollapsed ? "Expand node" : "Collapse node"
        );

        toggle.innerHTML = hasChildren
            ? (isCollapsed ? "＋" : "−")
            : "·";

        toggle.disabled = !hasChildren;

        toggle.addEventListener("click", function(event) {

            event.stopPropagation();

            if (collapsedNodes.has(nodeKey)) {
                collapsedNodes.delete(nodeKey);
            } else {
                collapsedNodes.add(nodeKey);
            }

            renderTree();
        });


        const select = document.createElement("button");

        select.type = "button";
        select.className = "tree-component-select";

        select.setAttribute(
            "aria-label",
            "Select " + node.name
        );


        const icon = document.createElement("span");

        icon.className =
            "tree-component-icon " + node.type.toLowerCase();

        icon.innerHTML = icons[node.type] || "";


        const details = document.createElement("span");

        details.className = "tree-component-details";

        const name = document.createElement("strong");

        name.className = "tree-component-name";
        name.textContent = node.name;

        const meta = document.createElement("span");

        meta.className = "tree-component-meta";
        meta.textContent = node.code + " · " + node.type;

        details.append(name, meta);


        const status = document.createElement("span");

        status.className = "tree-component-status";

        const powerStatus = getPowerStatus(node);

        status.classList.add(powerStatus.toLowerCase());
        status.textContent = powerStatus;


        select.append(icon, details, status);


        select.addEventListener("click", function() {

            selectedId = nodeKey;

            renderTree();
            updateInspector();

        });


        row.append(toggle, select);

        wrapper.appendChild(row);


        if (hasChildren && !isCollapsed) {

            const children = document.createElement("div");

            children.className = "advanced-tree-children";

            childrenList.forEach(child => {

                const childElement = renderNode(child);

                if (childElement) {
                    children.appendChild(childElement);
                }
            });

            wrapper.appendChild(children);
        }

        return wrapper;
    }

    function renderTree() {

        const tree = document.getElementById("hierarchyTree");

        const search = document.getElementById("hierarchySearch")
            .value.trim().toLowerCase();

        tree.replaceChildren();

        let visibleCount = 0;

        cityData.forEach(city => {

            const cityElement = renderNode(city);

            if (cityElement) {
                tree.appendChild(cityElement);
                visibleCount++;
            }
        });

        const emptyMessage =
            document.getElementById("emptyTreeMessage");

        emptyMessage.hidden = visibleCount > 0;

        document.getElementById("componentCount").textContent =
            countAllComponents(cityData)
                .toString().padStart(2, "0") +
            " COMPONENTS";
    }

    function updateMetrics() {

        document.getElementById("cityCount").textContent =
            countByType(cityData, "City").toString().padStart(2, "0");

        document.getElementById("districtCount").textContent =
            countByType(cityData, "District").toString().padStart(2, "0");

        document.getElementById("streetCount").textContent =
            countByType(cityData, "Street").toString().padStart(2, "0");

        document.getElementById("buildingCount").textContent =
            countByType(cityData, "Building").toString().padStart(2, "0");
    }

    function populateParentDropdown(preferredId = null) {

        const select = document.getElementById("parentComponent");

        select.replaceChildren();

        const placeholder = document.createElement("option");

        placeholder.value = "";
        placeholder.textContent = "Select a parent component";

        select.appendChild(placeholder);


        function addOptions(nodes, path = []) {

            nodes.forEach(node => {

                const currentPath = [...path, node.name];

                if (allowedChildren[node.type]) {

                    const option = document.createElement("option");

                    option.value = getNodeKey(node);

                    option.textContent =
                        currentPath.join("  /  ") +
                        "  (" + node.type + ")";

                    option.dataset.id = node.id;
                    option.dataset.type = node.type;

                    select.appendChild(option);
                }

                addOptions(getChildren(node), currentPath);
            });
        }


        addOptions(cityData);


        const preferred = preferredId || selectedId;

        if (preferred) {
            select.value = preferred;
        }

        if (!select.value) {
            select.value = "";
        }

        updateAddOptions();
    }

    function updateAddOptions() {

        const parentSelect =
            document.getElementById("parentComponent");

        const typeInput =
            document.getElementById("newComponentType");

        const nameInput =
            document.getElementById("newComponentName");

        const createButton =
            document.getElementById("createComponentButton");

        const buildingFields =
            document.getElementById("buildingFields");

        const descriptionField =
            document.getElementById("descriptionField");

        const descriptionInput =
            document.getElementById("newComponentDescription");

        const typeHelp =
            document.getElementById("componentTypeHelp");


        const selectedOption =
            parentSelect.options[parentSelect.selectedIndex];

        const parent = selectedOption && selectedOption.value
            ? findNode(cityData, selectedOption.value)
            : null;


        typeInput.value = "";

        buildingFields.hidden = true;

        descriptionField.hidden = false;

        createButton.disabled = true;


        if (!parent) {

            typeHelp.textContent =
                "Select a parent to determine the available child type.";

            nameInput.disabled = true;

            return;
        }


        const childType = allowedChildren[parent.type];


        if (!childType) {

            typeHelp.textContent =
                "This component cannot contain children.";

            nameInput.disabled = true;

            return;
        }


        typeInput.value = childType;

        typeHelp.textContent =
            parent.type + " components can contain " +
            childType + " components.";

        nameInput.disabled = false;

        createButton.disabled = false;

        nameInput.placeholder =
            "Enter " + childType.toLowerCase() + " name";

        buildingFields.hidden = childType !== "Building";

        descriptionField.hidden = childType === "Building";

        if (childType !== "Building") {

            document.getElementById("newBuildingType").value = "";

            document.getElementById("newBuildingAddressLine1").value = "";

            document.getElementById("newBuildingAddressLine2").value = "";
        }

        if (childType === "Building") {

            descriptionInput.value = "";
        }
    }

    document.getElementById("parentComponent")
        .addEventListener("change", function() {

            const parentKey = this.value;

            if (!parentKey) {

                updateAddOptions();

                return;
            }

            const parent = findNode(cityData, parentKey);

            if (!parent) {

                updateAddOptions();

                return;
            }

            selectedId = parentKey;

            renderTree();

            updateInspector();

            updateAddOptions();

        });

    function updateInspector() {

        const node = findNode(cityData, selectedId);

        if (!node) {
            return;
        }

        const children = getChildren(node);
        const status = getPowerStatus(node);


        document.getElementById("selectedName").textContent =
            node.name;

        document.getElementById("selectedType").textContent =
            node.type.toUpperCase();

        document.getElementById("selectedId").textContent =
            "COMPONENT ID: " + node.code;

        document.getElementById("selectedIcon").innerHTML =
            icons[node.type] || "";


        document.getElementById("propertyType").textContent =
            node.type;

        document.getElementById("propertyChildren").textContent =
            children.length.toString().padStart(2, "0");

        document.getElementById("propertyBuildings").textContent =
            countDescendantBuildings(node)
                .toString().padStart(2, "0");


        const powerProperty =
            document.getElementById("propertyPower");

        powerProperty.textContent =
            status === "ACTIVE"
                ? "Operational"
                : status === "OFF"
                    ? "Power off"
                    : "Partially active";

        powerProperty.className =
            status === "ACTIVE"
                ? "property-active"
                : status === "OFF"
                    ? "property-off"
                    : "property-partial";


        const inspectorStatus =
            document.getElementById("inspectorStatus");

        inspectorStatus.textContent = status;

        inspectorStatus.className =
            "inspector-status " + status.toLowerCase();

        document.getElementById("usageValue").textContent =
            formatKwh(calculateUsage(node));

        document.getElementById("usageDescription").textContent =
            "Combined energy usage across the selected component and " +
            "its descendants, from successful meter readings (kWh).";


        const path = getNodePath(selectedId);

        document.getElementById("selectedId").title =
            path.join(" / ");


        populateParentDropdown(selectedId);
    }

    async function addComponent() {

        const parentSelect =
            document.getElementById("parentComponent");

        const parentKey = parentSelect.value;

        const parent = findNode(cityData, parentKey);


        if (!parent) {

            showResult(
                "Please select a valid parent component.",
                "error"
            );

            return;
        }

        const name =
            document.getElementById("newComponentName")
                .value.trim();

        const type =
            document.getElementById("newComponentType")
                .value;

        const description = type === "Building"
            ? ""
            : document.getElementById("newComponentDescription")
                .value.trim();


        const buildingType =
            document.getElementById("newBuildingType")
                .value.trim();


        const addressLine1 =
            document.getElementById("newBuildingAddressLine1")
                .value.trim();

        const addressLine2 =
            document.getElementById("newBuildingAddressLine2")
                .value.trim();


        if (!name) {

            showResult(
                "Component name is required.",
                "error"
            );

            document.getElementById("newComponentName").focus();

            return;
        }

        if (name.length > 150) {

            showResult(
                "Component name cannot exceed 150 characters.",
                "error"
            );

            return;
        }

        if (type !== "Building" && description.length > 500) {

            showResult(
                "Description cannot exceed 500 characters.",
                "error"
            );

            document.getElementById("newComponentDescription").focus();

            return;
        }


        if (allowedChildren[parent.type] !== type) {

            showResult(
                "Invalid hierarchy relationship.",
                "error"
            );

            return;
        }

        const duplicate = getChildren(parent).some(child =>
            child.name.trim().toLowerCase() === name.toLowerCase()
        );

        if (duplicate) {

            showResult(
                "A component with this name already exists under this parent.",
                "error"
            );

            return;
        }

        if (type === "Building") {

            if (!buildingType) {

                showResult(
                    "Please select a building type.",
                    "error"
                );

                document.getElementById("newBuildingType").focus();

                return;
            }

            if (buildingType.length > 80) {

                showResult(
                    "Building type cannot exceed 80 characters.",
                    "error"
                );

                document.getElementById("newBuildingType").focus();

                return;
            }

            if (addressLine1.length > 255) {

                showResult(
                    "Address line 1 cannot exceed 255 characters.",
                    "error"
                );

                document.getElementById("newBuildingAddressLine1").focus();

                return;
            }

            if (addressLine2.length > 255) {

                showResult(
                    "Address line 2 cannot exceed 255 characters.",
                    "error"
                );

                document.getElementById("newBuildingAddressLine2").focus();

                return;
            }
        }

        const button =
            document.getElementById("createComponentButton");

        button.disabled = true;

        button.textContent = "Creating...";

        try {

            const formData = new URLSearchParams();

            formData.append("action", "add");

            formData.append("parentId", String(parent.id));

            formData.append("parentType", parent.type);

            formData.append("name", name);

            formData.append("type", type);

            formData.append("description", description);

            formData.append("buildingType", buildingType);

            formData.append("addressLine1", addressLine1);

            formData.append("addressLine2", addressLine2);


            const response = await fetch(
                contextPath + "/hierarchy",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded;charset=UTF-8",

                        "Accept": "application/json"
                    },

                    body: formData.toString()
                }
            );

            let result;

            try {

                result = await response.json();

            } catch {

                throw new Error(
                    "The server returned an invalid response."
                );
            }

            if (!response.ok || !result.success) {

                throw new Error(
                    result.message ||
                    "Unable to create the component."
                );
            }

            document.getElementById("newComponentName").value = "";

            document.getElementById("newComponentDescription").value = "";

            document.getElementById("newBuildingType").value = "";

            document.getElementById("newBuildingAddressLine1").value = "";

            document.getElementById("newBuildingAddressLine2").value = "";

            const prefixes = {
                District: "DST",
                Street: "STR",
                Building: "BLD"
            };

            const newCode =
                prefixes[type] + "-" +
                String(result.id).padStart(3, "0");

            await initializeHierarchy(type, newCode);

            addActivity(
                type + " created",
                name + " was saved under " + parent.name + ".",
                "success"
            );

            showResult(
                name + " was successfully saved to the database.",
                "success"
            );

        } catch (error) {

            console.error("Add component error:", error);

            showResult(
                error.message ||
                "Unable to save the component.",
                "error"
            );

        } finally {

            button.disabled = false;

            button.textContent = "Create Component";

            updateAddOptions();
        }
    }

    function removeSelectedComponent() {

        showResult(
            "Database deletion has not been implemented yet.",
            "error"
        );
    }

    function formatKwh(value) {

        return Number(value || 0).toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
    }


    async function sendPowerOperation(action) {

        const node = findNode(cityData, selectedId);

        if (!node) {
            return;
        }

        const isShutdown = action === "shutdown";

        if (isShutdown && !window.confirm(
            "Shut down power for " + node.name +
            " and all components below it?"
        )) {
            return;
        }

        try {

            const formData = new URLSearchParams();

            formData.append("action", action);
            formData.append("componentType", node.type);
            formData.append("componentId", node.id);

            const response = await fetch(
                contextPath + "/hierarchy",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded;charset=UTF-8",

                        "Accept": "application/json"
                    },

                    body: formData.toString()
                }
            );

            let result;

            try {

                result = await response.json();

            } catch {

                throw new Error(
                    "The server returned an invalid response."
                );
            }

            if (!response.ok || !result.success) {

                throw new Error(
                    result.message ||
                    "Unable to change the power state."
                );
            }

            await initializeHierarchy(node.type, node.code);

            addActivity(
                isShutdown ? "Power shutdown" : "Power restored",
                result.message,
                isShutdown ? "warning" : "success"
            );

            showResult(result.message, "success");

        } catch (error) {

            showResult(
                error.message || "Unable to change the power state.",
                "error"
            );
        }
    }


    function shutdownSelected() {

        sendPowerOperation("shutdown");
    }


    function restoreSelected() {

        sendPowerOperation("restore");
    }

    async function downloadUsageReport() {

        const node = findNode(cityData, selectedId);

        if (!node) {
            return;
        }

        try {

            const response = await fetch(
                contextPath + "/hierarchy/report/download?type=" +
                encodeURIComponent(node.type) +
                "&id=" + encodeURIComponent(node.id)
            );

            if (!response.ok) {

                let message = "Unable to download the usage report.";

                try {
                    const error = await response.json();
                    message = error.message || message;
                } catch {

                }

                throw new Error(message);
            }

            const blob = await response.blob();

            const fileName =
                "usage-report-" +
                String(node.code).replace(/[^A-Za-z0-9_-]/g, "") +
                ".pdf";

            const link = document.createElement("a");

            link.href = URL.createObjectURL(blob);
            link.download = fileName;

            document.body.appendChild(link);
            link.click();
            link.remove();

            URL.revokeObjectURL(link.href);

            showResult(
                "Usage report downloaded as " + fileName + ".",
                "success"
            );

            addActivity(
                "Usage report downloaded",
                node.name + " saved as " + fileName + ".",
                "success"
            );

        } catch (error) {

            showResult(
                error.message || "Unable to download the usage report.",
                "error"
            );
        }
    }

    async function loadPowerHistory() {

        const list = document.getElementById("powerHistoryList");

        if (!list) {
            return;
        }

        try {

            const response = await fetch(
                contextPath + "/hierarchy/history?limit=10",
                {
                    method: "GET",

                    headers: {
                        "Accept": "application/json"
                    }
                }
            );

            const data = await response.json();

            if (!response.ok || data.success === false) {

                throw new Error(
                    data.message ||
                    "Unable to load the power operation history."
                );
            }

            renderPowerHistory(
                Array.isArray(data.history) ? data.history : []
            );

        } catch (error) {

            renderPowerHistoryMessage(
                "History unavailable",
                error.message ||
                "Unable to load the power operation history.",
                "error"
            );

            document.getElementById("powerHistoryCount").textContent = "00";
        }
    }


    function renderPowerHistoryMessage(title, description, type) {

        const list = document.getElementById("powerHistoryList");

        list.replaceChildren(
            buildHistoryItem(title, description, "", type)
        );
    }


    function buildHistoryItem(title, description, time, type) {

        const item = document.createElement("div");

        item.className = "activity-item";


        const indicator = document.createElement("span");

        indicator.className = "activity-indicator " + type;


        const details = document.createElement("div");

        const heading = document.createElement("strong");

        heading.textContent = title;

        const message = document.createElement("small");

        message.textContent = description;

        details.append(heading, message);


        const timeLabel = document.createElement("span");

        timeLabel.className = "activity-time";

        timeLabel.textContent = time;


        item.append(indicator, details, timeLabel);

        return item;
    }


    function renderPowerHistory(history) {

        const list = document.getElementById("powerHistoryList");

        document.getElementById("powerHistoryCount").textContent =
            String(history.length).padStart(2, "0");

        if (history.length === 0) {

            renderPowerHistoryMessage(
                "No power operations yet",
                "Shutdown and restore actions will appear here.",
                ""
            );

            return;
        }

        list.replaceChildren(...history.map(record => {

            const isShutdown = record.operation === "SHUTDOWN";

            return buildHistoryItem(
                (isShutdown ? "Shutdown" : "Restore") + " · " +
                record.componentType + " · " + record.componentName,

                record.affectedBuildings + " building(s) changed · by " +
                record.performedBy,

                record.performedAt,

                isShutdown ? "warning" : ""
            );
        }));
    }

    document.getElementById("hierarchySearch")
        .addEventListener("input", function() {

            renderTree();
        });

    function setFilter(filter, button) {

        currentFilter = filter;

        document.querySelectorAll(".hierarchy-filter")
            .forEach(item => {
                item.classList.remove("active");
            });

        button.classList.add("active");

        renderTree();
    }

    let hierarchyZoom = 1;

    const MIN_HIERARCHY_ZOOM = 0.5;
    const MAX_HIERARCHY_ZOOM = 1.6;
    const HIERARCHY_ZOOM_STEP = 0.1;


    function expandAll() {

        const modal = document.getElementById(
            "hierarchyOverviewModal"
        );

        const tree = document.getElementById(
            "hierarchyOverviewTree"
        );

        const content = document.getElementById(
            "hierarchyOverviewContent"
        );

        if (!modal || !tree || !content) {
            console.error("Hierarchy overview elements not found.");
            return;
        }

        tree.replaceChildren();

        hierarchyZoom = 1;

        if (!Array.isArray(cityData) || cityData.length === 0) {

            const emptyMessage = document.createElement("div");

            emptyMessage.className = "overview-empty";
            emptyMessage.textContent =
                "No hierarchy components are currently available.";

            tree.appendChild(emptyMessage);

            openHierarchyOverview();
            return;
        }

        const rootList = document.createElement("ul");

        cityData.forEach(node => {

            rootList.appendChild(
                createOverviewNode(node)
            );

        });

        tree.appendChild(rootList);

        openHierarchyOverview();
    }

    function createOverviewNode(node) {

        const listItem = document.createElement("li");

        const card = document.createElement("div");

        const type = String(node.type || "Component");
        const name = String(node.name || "Unnamed component");

        const typeClass = type
            .toLowerCase()
            .replace(/[^a-z0-9_-]/g, "");

        card.className =
            "overview-node-card overview-node-" + typeClass;


        const typeLabel = document.createElement("span");

        typeLabel.className = "overview-node-type";
        typeLabel.textContent = type;

        const nameLabel = document.createElement("span");

        nameLabel.className = "overview-node-name";
        nameLabel.textContent = name;

        card.appendChild(typeLabel);
        card.appendChild(nameLabel);

        if (node.id !== undefined && node.id !== null) {

            const idLabel = document.createElement("span");

            idLabel.className = "overview-node-id";
            idLabel.textContent = "ID: " + node.id;

            card.appendChild(idLabel);
        }

        listItem.appendChild(card);

        const children = Array.isArray(node.children)
            ? node.children
            : [];

        if (children.length > 0) {

            const childList = document.createElement("ul");

            children.forEach(child => {

                childList.appendChild(
                    createOverviewNode(child)
                );

            });

            listItem.appendChild(childList);
        }

        return listItem;
    }

    function openHierarchyOverview() {

        const modal = document.getElementById(
            "hierarchyOverviewModal"
        );

        const content = document.getElementById(
            "hierarchyOverviewContent"
        );

        if (!modal || !content) return;

        modal.classList.add("is-open");

        modal.setAttribute("aria-hidden", "false");

        document.body.style.overflow = "hidden";

        requestAnimationFrame(() => {

            updateHierarchyZoom();

            content.scrollTop = 0;

            content.scrollLeft = Math.max(
                0,
                (content.scrollWidth - content.clientWidth) / 2
            );

        });
    }

    function closeHierarchyOverview() {

        const modal = document.getElementById(
            "hierarchyOverviewModal"
        );

        if (!modal) return;

        modal.classList.remove("is-open");

        modal.setAttribute("aria-hidden", "true");

        document.body.style.overflow = "";
    }

    function changeHierarchyZoom(amount) {

        hierarchyZoom += amount;

        hierarchyZoom = Math.max(
            MIN_HIERARCHY_ZOOM,
            Math.min(MAX_HIERARCHY_ZOOM, hierarchyZoom)
        );

        hierarchyZoom = Math.round(hierarchyZoom * 10) / 10;

        updateHierarchyZoom();
    }

    function resetHierarchyZoom() {

        hierarchyZoom = 1;

        updateHierarchyZoom();
    }

    function updateHierarchyZoom() {

        const tree = document.getElementById(
            "hierarchyOverviewTree"
        );

        const stage = document.getElementById(
            "hierarchyCanvasStage"
        );

        const content = document.getElementById(
            "hierarchyOverviewContent"
        );

        const zoomLabel = document.getElementById(
            "hierarchyZoomLevel"
        );

        if (!tree || !stage || !content) return;

        if (zoomLabel) {

            zoomLabel.textContent =
                Math.round(hierarchyZoom * 100) + "%";

        }

        tree.style.transform =
            `translateX(-50%) scale(${hierarchyZoom})`;

        const originalWidth = tree.offsetWidth;
        const originalHeight = tree.offsetHeight;

        const scaledWidth = originalWidth * hierarchyZoom;
        const scaledHeight = originalHeight * hierarchyZoom;

        stage.style.width = Math.max(
            content.clientWidth,
            scaledWidth
        ) + "px";

        stage.style.height =
            Math.max(content.clientHeight, scaledHeight + 20) + "px";
    }

    document.addEventListener("keydown", function(event) {

        const modal = document.getElementById(
            "hierarchyOverviewModal"
        );

        if (!modal || !modal.classList.contains("is-open")) {
            return;
        }

        if (event.key === "Escape") {

            closeHierarchyOverview();

        }

    });

    window.addEventListener("resize", function() {

        const modal = document.getElementById(
            "hierarchyOverviewModal"
        );

        if (modal && modal.classList.contains("is-open")) {

            updateHierarchyZoom();

        }

    });



    function collapseAll() {

        function collapse(node) {

            const children = getChildren(node);

            if (children.length > 0) {

                collapsedNodes.add(getNodeKey(node));

                children.forEach(collapse);
            }
        }

        cityData.forEach(collapse);

        renderTree();

        addActivity(
            "Hierarchy collapsed",
            "All infrastructure branches are collapsed.",
            "success"
        );
    }

    function focusAddComponent() {

        const section =
            document.getElementById("addComponentSection");

        section.scrollIntoView({
            behavior: "smooth",
            block: "center"
        });

        document.getElementById("parentComponent").focus();
    }

    function showResult(message, type) {

        const result =
            document.getElementById("operationResult");

        result.hidden = false;

        result.className =
            "infra-operation-result " + type;

        result.textContent = message;
    }

    function addActivity(title, description, type) {

        const list =
            document.getElementById("activityList");

        const item = document.createElement("div");

        item.className = "activity-item";


        const indicator = document.createElement("span");

        indicator.className =
            "activity-indicator " + type;


        const details = document.createElement("div");


        const heading = document.createElement("strong");

        heading.textContent = title;


        const message = document.createElement("small");

        message.textContent = description;


        details.append(heading, message);


        const time = document.createElement("span");

        time.className = "activity-time";

        time.textContent = "NOW";


        item.append(indicator, details, time);

        list.prepend(item);


        while (list.children.length > 6) {
            list.removeChild(list.lastElementChild);
        }


        document.getElementById("activityCount").textContent =
            list.children.length.toString().padStart(2, "0");
    }

    function showHelp() {

        alert(
            "CITY INFRASTRUCTURE MANAGEMENT\n\n" +

            "This page demonstrates the Composite design pattern.\n\n" +

            "City contains Districts.\n" +
            "Districts contain Streets.\n" +
            "Streets contain Buildings.\n\n" +

            "Select a component to inspect it.\n\n" +

            "To add a component, select a parent from the " +
            "Parent Component dropdown. The child type is " +
            "determined automatically.\n\n" +

            "Building types are loaded from the database.\n" +
            "Building addresses are optional.\n\n" +

            "Buildings are leaf components and cannot contain children."
        );
    }

    function normalizeNode(node) {

        return {

            id: node.id,

            code: node.code || (node.type + "-" + node.id),

            name: node.name || "Unnamed component",

            type: node.type,

            description: node.description || "",

            buildingType: node.buildingType || "",

            addressLine1: node.addressLine1 || "",

            addressLine2: node.addressLine2 || "",

            power: node.power !== false,

            powerStatus: node.powerStatus || "",

            usage: Number(node.usage) || 0,

            totalBuildings: Number(node.totalBuildings) || 0,

            poweredBuildings: Number(node.poweredBuildings) || 0,

            children: Array.isArray(node.children)
                ? node.children.map(normalizeNode)
                : []
        };
    }

    async function initializeHierarchy(
        preferredType = null,
        preferredCode = null
    ) {

        const tree =
            document.getElementById("hierarchyTree");

        tree.textContent = "Loading infrastructure data...";


        try {

            const response = await fetch(
                contextPath + "/hierarchy/data",
                {
                    method: "GET",

                    headers: {
                        "Accept": "application/json"
                    }
                }
            );


            if (!response.ok) {

                throw new Error(
                    "The server returned HTTP " + response.status
                );
            }


            const cities = await response.json();


            if (!Array.isArray(cities)) {

                throw new Error(
                    "Invalid hierarchy data received from the server."
                );
            }


            cityData = cities.map(normalizeNode);


            if (cityData.length === 0) {

                tree.replaceChildren();

                selectedId = null;

                updateMetrics();

                document.getElementById("componentCount").textContent =
                    "00 COMPONENTS";

                document.getElementById("emptyTreeMessage").hidden = false;

                document.getElementById("emptyTreeMessage")
                    .querySelector("strong").textContent =
                    "No cities registered";

                populateParentDropdown();

                showResult(
                    "No City records exist. Add a City root component to begin.",
                    "error"
                );

                return;
            }

            let preferredNode = null;

            if (preferredType && preferredCode) {

                function findPreferred(nodes) {

                    for (const node of nodes) {

                        if (
                            node.type === preferredType &&
                            node.code === preferredCode
                        ) {
                            return node;
                        }

                        const result =
                            findPreferred(getChildren(node));

                        if (result) {
                            return result;
                        }
                    }

                    return null;
                }

                preferredNode = findPreferred(cityData);
            }


            if (preferredNode) {

                selectedId = getNodeKey(preferredNode);

            } else if (!findNode(cityData, selectedId)) {

                selectedId = getNodeKey(cityData[0]);
            }


            renderTree();

            updateMetrics();

            updateInspector();

            loadPowerHistory();


            addActivity(
                "Infrastructure data loaded",
                countAllComponents(cityData) +
                " components retrieved from the database.",
                "success"
            );


            const environmentChip =
                document.querySelector(".environment-chip");

            if (environmentChip) {

                environmentChip.innerHTML =
                    '<span class="status-dot"></span> DATABASE CONNECTED';
            }


        } catch (error) {

            console.error(
                "Failed to load infrastructure:",
                error
            );


            tree.replaceChildren();

            const errorMessage =
                document.createElement("p");

            errorMessage.textContent =
                "Unable to load infrastructure data. Check the server " +
                "and database connection, then reload the page.";

            tree.appendChild(errorMessage);


            showResult(
                error.message ||
                "Could not load hierarchy data from the server.",
                "error"
            );


            const environmentChip =
                document.querySelector(".environment-chip");

            if (environmentChip) {

                environmentChip.innerHTML =
                    '<span class="status-dot"></span> CONNECTION ERROR';
            }
        }
    }

    async function loadBuildingTypes() {

        const select =
            document.getElementById("newBuildingType");

        if (!select) {
            console.error("Building type dropdown was not found.");
            return;
        }

        select.replaceChildren();

        const loadingOption = document.createElement("option");

        loadingOption.value = "";
        loadingOption.textContent = "Loading building types...";

        select.appendChild(loadingOption);

        select.disabled = true;


        try {

            const response = await fetch(
                contextPath + "/hierarchy/building-types",
                {
                    method: "GET",

                    headers: {
                        "Accept": "application/json"
                    }
                }
            );


            if (!response.ok) {

                throw new Error(
                    "Unable to load building types. HTTP " +
                    response.status
                );
            }


            const types = await response.json();


            if (!Array.isArray(types)) {

                throw new Error(
                    "Invalid building type data received from the server."
                );
            }


            select.replaceChildren();

            const placeholder = document.createElement("option");

            placeholder.value = "";
            placeholder.textContent = "Select building type";

            select.appendChild(placeholder);


            if (types.length === 0) {

                const emptyOption = document.createElement("option");

                emptyOption.value = "";
                emptyOption.textContent =
                    "No building types found in database";

                emptyOption.disabled = true;

                select.appendChild(emptyOption);

                select.disabled = true;

                console.warn(
                    "No building types are currently available in the database."
                );

                return;
            }


            types.forEach(type => {

                const option = document.createElement("option");

                option.value = type;
                option.textContent = type;

                select.appendChild(option);
            });


            select.disabled = false;


        } catch (error) {

            console.error(
                "Building type loading error:",
                error
            );


            select.replaceChildren();

            const errorOption = document.createElement("option");

            errorOption.value = "";
            errorOption.textContent = "Unable to load building types";

            select.appendChild(errorOption);

            select.disabled = true;
        }
    }

    document.addEventListener("DOMContentLoaded", async function() {

        await Promise.all([
            loadBuildingTypes(),
            initializeHierarchy()
        ]);

    });

</script>

</body>
</html>
