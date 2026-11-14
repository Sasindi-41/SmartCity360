<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    private String sidebarEscape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private String sidebarInitials(String value) {
        if (value == null || value.isBlank()) return "U";
        String[] parts = value.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1)
                + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
%>
<%
    String cp = request.getContextPath();
    String sidebarName = session.getAttribute("fullName") == null
            ? "User" : String.valueOf(session.getAttribute("fullName"));

    int sidebarRoleId = 0;
    Object roleObject = session.getAttribute("roleId");
    if (roleObject instanceof Number) {
        sidebarRoleId = ((Number) roleObject).intValue();
    }

    String sidebarRole = String.valueOf(
            request.getAttribute("currentRoleName") == null
                    ? (sidebarRoleId == 1 ? "Citizen"
                    : sidebarRoleId == 2 ? "Local Staff"
                    : sidebarRoleId == 3 ? "Supervisor"
                    : sidebarRoleId == 4 ? "City Manager"
                    : sidebarRoleId == 5 ? "Administrator" : "Member")
                    : request.getAttribute("currentRoleName")
    );

    String active = String.valueOf(
            request.getAttribute("activePage") == null
                    ? "" : request.getAttribute("activePage")
    );

    boolean canManageCity = sidebarRoleId == 4 || sidebarRoleId == 5;
%>

<div id="smartcity-csrf-token"
     data-token="<%= sidebarEscape(String.valueOf(session.getAttribute("csrfToken") == null ? "" : session.getAttribute("csrfToken"))) %>"
     hidden></div>
<script src="<%= cp %>/js/csrf.js" defer></script>

<aside class="sidebar">
    <div class="sidebar-brand-area">
        <a href="<%= cp %>/dashboard" class="brand">
            <span class="brand-logo">
                <img src="<%= cp %>/images/logo.png"
                     alt="SmartCity360"
                     onerror="this.style.display='none';this.nextElementSibling.style.display='grid';">
                <span class="brand-fallback">S</span>
            </span>
            <span class="brand-text">
                <span class="brand-name">SMARTCITY<span>360</span></span>
                <span class="brand-caption">URBAN INTELLIGENCE</span>
            </span>
        </a>
        <div class="sidebar-edition">
            <span class="sidebar-edition-dot"></span>
            CITY OPERATIONS PLATFORM
        </div>
    </div>

    <div class="sidebar-nav-scroll">
        <nav class="navigation">
            <div class="nav-group">
                <span class="nav-heading">WORKSPACE</span>

                <a href="<%= cp %>/dashboard"
                   class="nav-link <%= "dashboard".equals(active) ? "active" : "" %>">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <rect x="3" y="3" width="8" height="8" rx="1.5"/>
                            <rect x="13" y="3" width="8" height="5" rx="1.5"/>
                            <rect x="13" y="10" width="8" height="11" rx="1.5"/>
                            <rect x="3" y="13" width="8" height="8" rx="1.5"/>
                        </svg>
                    </span>
                    <span class="nav-label">Overview</span>
                    <span class="nav-current"></span>
                </a>
            </div>

            <% if (canManageCity) { %>
            <div class="nav-group">
                <span class="nav-heading">CITY INFRASTRUCTURE</span>

                <a href="<%= cp %>/hierarchy"
                   class="nav-link <%= "hierarchy".equals(active) ? "active" : "" %>">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <rect x="9" y="2" width="6" height="5" rx="1"/>
                            <rect x="2" y="17" width="6" height="5" rx="1"/>
                            <rect x="16" y="17" width="6" height="5" rx="1"/>
                            <path d="M12 7v5M5 17v-5h14v5"/>
                        </svg>
                    </span>
                    <span class="nav-label">City hierarchy</span>
                    <span class="nav-current"></span>
                </a>

                <a href="<%= cp %>/energy-systems.jsp"
                   class="nav-link <%= "energy".equals(active) ? "active" : "" %>">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M13 2 4 14h7l-1 8 10-13h-7l0-7Z"/>
                        </svg>
                    </span>
                    <span class="nav-label">Energy systems</span>
                    <span class="nav-current"></span>
                </a>

                <a href="<%= cp %>/street-lighting.jsp"
                   class="nav-link <%= "streetlights".equals(active) ? "active" : "" %>">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M9 18h6M10 22h4M8.5 14.5a7 7 0 1 1 7 0c-.9.7-1.5 1.5-1.5 2.5h-4c0-1-.6-1.8-1.5-2.5Z"/>
                            <path d="M12 2v2M4.9 4.9l1.4 1.4M19.1 4.9l-1.4 1.4"/>
                        </svg>
                    </span>
                    <span class="nav-label">Street lighting</span>
                    <span class="nav-current"></span>
                </a>

                <a href="<%= cp %>/city-policies.jsp"
                   class="nav-link <%= "policies".equals(active) ? "active" : "" %>">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8Z"/>
                            <path d="M14 2v6h6M8 13h8M8 17h8"/>
                        </svg>
                    </span>
                    <span class="nav-label">City policies</span>
                    <span class="nav-current"></span>
                </a>
            </div>
            <% } %>

            <div class="nav-group">
                <span class="nav-heading">CIVIC SERVICES</span>
                <a href="<%= cp %>/civic-requests.jsp"
                   class="nav-link <%= "complaints".equals(active) ? "active" : "" %>">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M21 11.5a8.4 8.4 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.4 8.4 0 0 1-3.8-.9L3 21l1.9-5.7a8.4 8.4 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.4 8.4 0 0 1 3.8-.9h.5a8.5 8.5 0 0 1 8 8v.5Z"/>
                            <path d="M8 10h8M8 14h5"/>
                        </svg>
                    </span>
                    <span class="nav-label">Civic requests</span>
                    <span class="nav-current"></span>
                </a>
            </div>

            <div class="nav-group">
                <span class="nav-heading">ACCOUNT</span>
                <a href="<%= cp %>/profile"
                   class="nav-link <%= "profile".equals(active) ? "active" : "" %>">
                    <span class="nav-icon">
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <circle cx="12" cy="8" r="4"/>
                            <path d="M4 21a8 8 0 0 1 16 0"/>
                        </svg>
                    </span>
                    <span class="nav-label">My profile</span>
                    <span class="nav-current"></span>
                </a>
            </div>
        </nav>
    </div>

    <div class="sidebar-bottom">
        <div class="sidebar-system-card">
            <span class="sidebar-system-icon">⌁</span>
            <div>
                <strong>Workspace status</strong>
                <span><i></i> Connected to local system</span>
            </div>
        </div>

        <div class="sidebar-profile-card">
            <a href="<%= cp %>/profile" class="sidebar-profile-main">
                <span class="profile-avatar"><%= sidebarEscape(sidebarInitials(sidebarName)) %></span>
                <span class="profile-details">
                    <strong class="profile-name"><%= sidebarEscape(sidebarName) %></strong>
                    <small class="profile-caption"><%= sidebarEscape(sidebarRole) %></small>
                </span>
                <span class="profile-open-icon">↗</span>
            </a>
            <form action="<%= cp %>/logout" method="post" class="sidebar-logout-form">
                <input type="hidden" name="_csrf"
                       value="<%= sidebarEscape(String.valueOf(session.getAttribute("csrfToken"))) %>">
                <button type="submit" class="sidebar-logout">
                    <span>↪</span> Sign out
                </button>
            </form>
        </div>
    </div>
</aside>
