<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="lk.sasindi.smartcity360.model.UserProfile" %>
<%!
    private String profileEsc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
%>
<%
    String contextPath = request.getContextPath();
    UserProfile profile = (UserProfile) request.getAttribute("profile");
    if (profile == null) {
        response.sendRedirect(contextPath + "/profile");
        return;
    }

    request.setAttribute("activePage", "profile");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartCity360 | My Profile</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/living-city.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/dashboard.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="/includes/sidebar.jsp"/>

    <main class="main-shell">
        <header class="topbar">
            <div class="topbar-heading">
                <span class="breadcrumb">SMART CITY / ACCOUNT</span>
                <h1>My profile</h1>
            </div>
            <div class="topbar-actions">
                <div class="environment-chip">
                    <span class="status-dot"></span>
                    <span>ACCOUNT SETTINGS</span>
                </div>
            </div>
        </header>

        <div class="content-scroll">
            <div class="page-content profile-page">
                <section class="profile-hero">
                    <div class="profile-hero-avatar">
                        <%= profileEsc(profile.getFullName().substring(0, 1).toUpperCase()) %>
                    </div>
                    <div class="profile-hero-copy">
                        <span class="eyebrow">YOUR SMARTCITY360 ACCOUNT</span>
                        <h2><%= profileEsc(profile.getFullName()) %></h2>
                        <p><%= profileEsc(profile.getRoleName()) %>
                            <% if (profile.getCityName() != null) { %>
                            <span>·</span> <%= profileEsc(profile.getCityName()) %>
                            <% } %>
                        </p>
                    </div>
                    <div class="profile-member-tag">
                        <span>MEMBER SINCE</span>
                        <strong><%= profile.getCreatedAt() == null
                                ? "—" : profile.getCreatedAt().toLocalDateTime().toLocalDate() %></strong>
                    </div>
                </section>

                <% if (request.getAttribute("successMessage") != null) { %>
                <div class="profile-alert success">
                    <span>✓</span>
                    <%= profileEsc(String.valueOf(request.getAttribute("successMessage"))) %>
                </div>
                <% } %>

                <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="profile-alert error">
                    <span>!</span>
                    <%= profileEsc(String.valueOf(request.getAttribute("errorMessage"))) %>
                </div>
                <% } %>

                <div class="profile-content-grid">
                    <section class="profile-card profile-details-card">
                        <div class="profile-card-heading">
                            <div>
                                <span class="eyebrow">PERSONAL INFORMATION</span>
                                <h3>Profile details</h3>
                                <p>Update the information associated with your account.</p>
                            </div>
                            <span class="profile-card-icon">⌁</span>
                        </div>

                        <form method="post" action="<%= contextPath %>/profile"
                              class="profile-form">
                            <input type="hidden" name="action" value="updateProfile">

                            <div class="profile-form-grid">
                                <label class="profile-field">
                                    <span>Full name</span>
                                    <input type="text" name="fullName"
                                           maxlength="120" required
                                           value="<%= profileEsc(profile.getFullName()) %>">
                                </label>

                                <label class="profile-field">
                                    <span>Email address</span>
                                    <input type="email" name="email"
                                           maxlength="150" required
                                           value="<%= profileEsc(profile.getEmail()) %>" disabled>
                                </label>

                                <label class="profile-field">
                                    <span>Phone number</span>
                                    <input type="tel" name="phoneNumber"
                                           maxlength="25" required
                                           value="<%= profileEsc(profile.getPhoneNumber()) %>">
                                </label>

                                <label class="profile-field">
                                    <span>Assigned city</span>
                                    <input type="text" value="<%= profile.getCityName() == null
                                            ? "Not assigned" : profileEsc(profile.getCityName()) %>"
                                           disabled>
                                    <small>City assignment is managed by the system, not changed from your profile.</small>
                                </label>
                            </div>

                            <div class="profile-form-footer">
                                <span>Changes are saved to your account record.</span>
                                <button type="submit" class="profile-save-button">
                                    Save profile <span>→</span>
                                </button>
                            </div>
                        </form>
                    </section>

                    <aside class="profile-side-column">
                        <section class="profile-card account-summary-card">
                            <span class="eyebrow">ACCOUNT OVERVIEW</span>
                            <h3>Account information</h3>
                            <div class="account-info-row">
                                <span>Account ID</span>
                                <strong>#<%= profile.getId() %></strong>
                            </div>
                            <div class="account-info-row">
                                <span>Access role</span>
                                <strong><%= profileEsc(profile.getRoleName()) %></strong>
                            </div>
                            <div class="account-info-row">
                                <span>City</span>
                                <strong><%= profile.getCityName() == null
                                        ? "Not assigned"
                                        : profileEsc(profile.getCityName()) %></strong>
                            </div>
                            <div class="account-info-row">
                                <span>Account status</span>
                                <strong class="account-active"><i></i> Active</strong>
                            </div>
                        </section>

                        <section class="profile-security-note">
                            <span class="security-note-icon">⌑</span>
                            <strong>Keep your account secure</strong>
                            <p>Use a unique password and never share your sign-in details with others.</p>
                        </section>
                    </aside>
                </div>

                <section class="profile-card password-card">
                    <div class="profile-card-heading">
                        <div>
                            <span class="eyebrow">SECURITY</span>
                            <h3>Change password</h3>
                            <p>Confirm your current password before choosing a new one.</p>
                        </div>
                        <span class="profile-card-icon">◇</span>
                    </div>

                    <form method="post" action="<%= contextPath %>/profile"
                          class="profile-form">
                        <input type="hidden" name="action" value="changePassword">

                        <div class="password-form-grid">
                            <label class="profile-field">
                                <span>Current password</span>
                                <input type="password" name="currentPassword"
                                       autocomplete="current-password" required>
                            </label>

                            <label class="profile-field">
                                <span>New password</span>
                                <input type="password" name="newPassword"
                                       minlength="8" maxlength="128"
                                       autocomplete="new-password" required>
                                <small>Use at least 8 characters.</small>
                            </label>

                            <label class="profile-field">
                                <span>Confirm new password</span>
                                <input type="password" name="confirmPassword"
                                       minlength="8" maxlength="128"
                                       autocomplete="new-password" required>
                            </label>
                        </div>

                        <div class="profile-form-footer">
                            <span>Your password is stored as a secure hash.</span>
                            <button type="submit" class="profile-save-button">
                                Update password <span>→</span>
                            </button>
                        </div>
                    </form>
                </section>

                <footer class="page-footer">
                    <span>SMARTCITY360</span>
                    <span>Account settings · Keep your information up to date</span>
                </footer>
            </div>
        </div>
    </main>
</div>
<jsp:include page="/includes/global-toast.jsp" />
</body>
</html>
