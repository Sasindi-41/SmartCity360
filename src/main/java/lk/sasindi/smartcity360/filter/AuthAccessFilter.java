package lk.sasindi.smartcity360.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@WebFilter(
        filterName = "AuthAccessFilter",
        urlPatterns = "/*"
)
public class AuthAccessFilter implements Filter {

    private static final int CITIZEN_ROLE = 1;
    private static final int LOCAL_STAFF_ROLE = 2;
    private static final int SUPERVISOR_ROLE = 3;
    private static final int CITY_MANAGER_ROLE = 4;
    private static final int ADMIN_ROLE = 5;
    private static final String CSRF_SESSION_ATTRIBUTE = "csrfToken";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest request =
                (HttpServletRequest) servletRequest;

        HttpServletResponse response =
                (HttpServletResponse) servletResponse;

        String contextPath = request.getContextPath();

        String requestURI = request.getRequestURI();

        String path = requestURI.substring(contextPath.length());

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null
                || !(session.getAttribute("userId") instanceof Number)
                || !(session.getAttribute("roleId") instanceof Number)) {

            denyUnauthenticated(request, response, path);
            return;
        }

        int userId = ((Number) session.getAttribute("userId")).intValue();

        int roleId = ((Number) session.getAttribute("roleId")).intValue();

        if (userId <= 0 || !isKnownRole(roleId)) {

            session.invalidate();

            denyUnauthenticated(request, response, path);
            return;
        }

        if (isCityManagementPath(path)
                && !canManageCity(roleId)) {

            denyForbidden(response, path);
            return;
        }

        String csrfToken = getOrCreateCsrfToken(session);

        if (requiresCsrfProtection(request.getMethod())
                && !isPublicMutationPath(path)
                && !isValidCsrfToken(request, csrfToken)) {
            denyCsrf(request, response, path);
            return;
        }

        request.setAttribute("csrfToken", csrfToken);

        String fullName = String.valueOf(
                session.getAttribute("fullName") == null
                        ? "User"
                        : session.getAttribute("fullName")
        );

        request.setAttribute(
                "currentUserName",
                escapeHtml(fullName)
        );

        request.setAttribute(
                "currentRoleName",
                getRoleName(roleId)
        );

        request.setAttribute(
                "currentUserInitials",
                getInitials(fullName)
        );

        request.setAttribute(
                "canManageCity",
                canManageCity(roleId)
        );

        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate, max-age=0"
        );

        response.setHeader("Pragma", "no-cache");

        response.setDateHeader("Expires", 0);

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {

        return path.equals("/")
                || path.equals("/index.jsp")
                || path.equals("/login.jsp")
                || path.equals("/signup.jsp")
                || path.equals("/login")
                || path.equals("/register")
                || path.equals("/favicon.ico")
                || path.startsWith("/css/")
                || path.startsWith("/images/")
                || path.startsWith("/js/")
                || path.startsWith("/fonts/");
    }

    private boolean isKnownRole(int roleId) {

        return roleId == CITIZEN_ROLE
                || roleId == LOCAL_STAFF_ROLE
                || roleId == SUPERVISOR_ROLE
                || roleId == CITY_MANAGER_ROLE
                || roleId == ADMIN_ROLE;
    }

    private String getRoleName(int roleId) {

        return switch (roleId) {

            case CITIZEN_ROLE -> "Citizen";

            case LOCAL_STAFF_ROLE -> "Local Staff";

            case SUPERVISOR_ROLE -> "Supervisor";

            case CITY_MANAGER_ROLE -> "City Manager";

            case ADMIN_ROLE -> "Administrator";

            default -> "Unknown Role";
        };
    }

    private boolean isCityManagementPath(String path) {

        return path.equals("/city-hierarchy.jsp")
                || path.equals("/energy-systems.jsp")
                || path.equals("/street-lighting.jsp")
                || path.equals("/city-policies.jsp")

                || path.equals("/hierarchy")
                || path.startsWith("/hierarchy/")

                || path.startsWith("/energy/")

                || path.startsWith("/city-policies/")

                || path.equals("/api/streetlights");
    }

    private boolean canManageCity(int roleId) {
        return roleId == CITY_MANAGER_ROLE || roleId == ADMIN_ROLE;
    }

    private String getOrCreateCsrfToken(HttpSession session) {
        Object existing = session.getAttribute(CSRF_SESSION_ATTRIBUTE);
        if (existing instanceof String token && !token.isBlank()) {
            return token;
        }

        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(CSRF_SESSION_ATTRIBUTE, token);
        return token;
    }

    private boolean requiresCsrfProtection(String method) {
        return "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method);
    }

    private boolean isPublicMutationPath(String path) {
        return "/login".equals(path) || "/register".equals(path);
    }

    private boolean isValidCsrfToken(
            HttpServletRequest request,
            String expectedToken
    ) {
        String supplied = request.getHeader("X-CSRF-Token");
        if (supplied == null || supplied.isBlank()) {
            supplied = request.getParameter("_csrf");
        }
        if (supplied == null || supplied.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
                expectedToken.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8)
        );
    }

    private void denyCsrf(
            HttpServletRequest request,
            HttpServletResponse response,
            String path
    ) throws IOException {
        if (isApiPath(path)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"Your security token is missing or expired. Refresh the page and try again.\"}"
            );
            return;
        }

        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Your security token is missing or expired. Refresh the page and try again."
        );
    }

    private void denyUnauthenticated(
            HttpServletRequest request,
            HttpServletResponse response,
            String path
    ) throws IOException {

        if (isApiPath(path)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType("application/json;charset=UTF-8");

            response.getWriter().write(
                    "{\"success\":false,"
                            + "\"message\":\"Please sign in to continue.\"}"
            );

            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/login.jsp"
        );
    }

    private void denyForbidden(
            HttpServletResponse response,
            String path
    ) throws IOException {

        if (isApiPath(path)) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            response.setContentType("application/json;charset=UTF-8");

            response.getWriter().write(
                    "{\"success\":false,"
                            + "\"message\":\"You do not have permission "
                            + "to access this operation.\"}"
            );

            return;
        }

        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "You do not have permission to access this page."
        );
    }

    private boolean isApiPath(String path) {

        return path.startsWith("/api/")
                || path.startsWith("/energy/")
                || path.startsWith("/hierarchy/")
                || path.startsWith("/city-policies/");
    }

    private String escapeHtml(String value) {

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private String getInitials(String fullName) {

        if (fullName == null || fullName.isBlank()) {
            return "U";
        }

        String[] parts = fullName.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }

        return (
                parts[0].substring(0, 1)
                        + parts[parts.length - 1].substring(0, 1)
        ).toUpperCase();
    }
}