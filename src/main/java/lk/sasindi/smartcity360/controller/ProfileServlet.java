package lk.sasindi.smartcity360.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lk.sasindi.smartcity360.dao.ProfileDAO;
import lk.sasindi.smartcity360.model.UserProfile;
import lk.sasindi.smartcity360.util.PasswordUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Pattern;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final ProfileDAO profileDAO = new ProfileDAO();

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9+()\\-\\s]{7,25}$"
    );

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        int userId = getSessionUserId(request, response);
        if (userId <= 0) return;

        request.setAttribute("activePage", "profile");
        try {
            loadProfilePage(request, response, userId);
        } catch (SQLException e) {
            log("Unable to load user profile", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load profile information."
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int userId = getSessionUserId(request, response);
        if (userId <= 0) return;

        String action = request.getParameter("action");

        try {
            if ("updateProfile".equals(action)) {
                updateProfile(request, response, userId);
            } else if ("changePassword".equals(action)) {
                changePassword(request, response, userId);
            } else {
                request.setAttribute("errorMessage",
                        "Unknown profile operation.");
                loadProfilePage(request, response, userId);
            }
        } catch (SQLException e) {
            log("Profile database operation failed", e);

            if ("23000".equals(e.getSQLState())) {
                request.setAttribute("errorMessage",
                        "That email address is already being used by another account.");
            } else {
                request.setAttribute("errorMessage",
                        "Unable to save your changes. Please try again.");
            }

            try {
                loadProfilePage(request, response, userId);
            } catch (SQLException loadError) {
                log("Unable to reload profile after database error", loadError);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (GeneralSecurityException e) {
            log("Password operation failed", e);
            request.setAttribute("errorMessage",
                    "Unable to securely process the password.");

            try {
                loadProfilePage(request, response, userId);
            } catch (SQLException loadError) {
                log("Unable to reload profile after password error", loadError);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private void updateProfile(HttpServletRequest request,
                               HttpServletResponse response,
                               int userId)
            throws SQLException, ServletException, IOException {

        String fullName = trim(request.getParameter("fullName"));
        String email = trim(request.getParameter("email"))
                .toLowerCase(Locale.ROOT);
        String phone = trim(request.getParameter("phoneNumber"));

        if (fullName.isBlank() || fullName.length() > 120) {
            request.setAttribute("errorMessage",
                    "Enter your name (maximum 120 characters).");
            loadProfilePage(request, response, userId);
            return;
        }

        if (email.isBlank() || email.length() > 150
                || !EMAIL_PATTERN.matcher(email).matches()) {
            request.setAttribute("errorMessage",
                    "Enter a valid email address.");
            loadProfilePage(request, response, userId);
            return;
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            request.setAttribute("errorMessage",
                    "Enter a valid phone number (7–25 characters).");
            loadProfilePage(request, response, userId);
            return;
        }

        boolean updated = profileDAO.updateProfile(
                userId, fullName, email, phone
        );

        if (updated) {
            HttpSession session = request.getSession(false);
            session.setAttribute("fullName", fullName);
            session.setAttribute("email", email);

            request.setAttribute("successMessage",
                    "Your profile details have been updated.");
        } else {
            request.setAttribute("errorMessage",
                    "No profile changes were saved.");
        }

        loadProfilePage(request, response, userId);
    }

    private void changePassword(HttpServletRequest request,
                                HttpServletResponse response,
                                int userId)
            throws SQLException, GeneralSecurityException,
            ServletException, IOException {

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (currentPassword == null || currentPassword.isBlank()
                || newPassword == null || confirmPassword == null) {
            request.setAttribute("errorMessage",
                    "Complete all password fields.");
            loadProfilePage(request, response, userId);
            return;
        }

        if (newPassword.length() < 8 || newPassword.length() > 128) {
            request.setAttribute("errorMessage",
                    "Your new password must contain 8–128 characters.");
            loadProfilePage(request, response, userId);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage",
                    "The new password and confirmation do not match.");
            loadProfilePage(request, response, userId);
            return;
        }

        UserProfile profile = profileDAO.findById(userId);

        if (profile == null || !PasswordUtil.verifyPassword(
                currentPassword, profile.getPasswordHash())) {
            request.setAttribute("errorMessage",
                    "Your current password is incorrect.");
            loadProfilePage(request, response, userId);
            return;
        }

        String newHash = PasswordUtil.hashPassword(newPassword);

        if (profileDAO.updatePassword(userId, newHash)) {
            request.setAttribute("successMessage",
                    "Your password has been changed successfully.");
        } else {
            request.setAttribute("errorMessage",
                    "The password could not be updated.");
        }

        loadProfilePage(request, response, userId);
    }

    private void loadProfilePage(HttpServletRequest request,
                                 HttpServletResponse response,
                                 int userId)
            throws SQLException, ServletException, IOException {

        UserProfile profile = profileDAO.findById(userId);

        if (profile == null) {
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();

            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        request.setAttribute("profile", profile);
        request.setAttribute("currentUserName", profile.getFullName());
        request.setAttribute("currentRoleName", profile.getRoleName());
        request.setAttribute("canManageCity", profile.getRoleId() == 4);

        request.getRequestDispatcher("/profile.jsp")
                .forward(request, response);
    }

    private int getSessionUserId(HttpServletRequest request,
                                 HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !(session.getAttribute("userId") instanceof Number)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return -1;
        }

        return ((Number) session.getAttribute("userId")).intValue();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
