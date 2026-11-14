package lk.sasindi.smartcity360.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lk.sasindi.smartcity360.dao.LoginAttemptDAO;
import lk.sasindi.smartcity360.dao.UserDAO;
import lk.sasindi.smartcity360.model.User;
import lk.sasindi.smartcity360.util.PasswordUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Pattern;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final LoginAttemptDAO loginAttemptDAO = new LoginAttemptDAO();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            showError(request, response,
                    "Please enter your email address and password.");
            return;
        }

        email = email.trim().toLowerCase(Locale.ROOT);

        if (email.length() > 254 || !EMAIL_PATTERN.matcher(email).matches()) {
            showError(request, response,
                    "Please enter a valid email address.");
            return;
        }

        if (password.length() > 128) {
            showError(request, response,
                    "The password you entered is too long.");
            return;
        }

        String attemptKey = LoginAttemptDAO.createAttemptKey(
                email,
                request.getRemoteAddr()
        );

        try {
            LoginAttemptDAO.LockStatus lockStatus =
                    loginAttemptDAO.getLockStatus(attemptKey);

            if (lockStatus.locked()) {
                showError(request, response,
                        "Too many failed sign-in attempts. Please try again in "
                                + lockStatus.minutesRemaining() + " minute(s).");
                return;
            }

            User user = userDAO.findActiveUserByEmail(email);
            boolean validCredentials = user != null
                    && PasswordUtil.verifyPassword(password, user.getPasswordHash());

            if (!validCredentials) {
                int failures = loginAttemptDAO.recordFailure(attemptKey);
                if (failures >= LoginAttemptDAO.MAX_FAILED_ATTEMPTS) {
                    showError(request, response,
                            "Too many failed sign-in attempts. Your account access from this address "
                                    + "is temporarily locked for 15 minutes.");
                } else {
                    showError(request, response,
                            "Invalid email or password.");
                }
                return;
            }

            loginAttemptDAO.clearFailures(attemptKey);

            HttpSession session = request.getSession(true);
            request.changeSessionId();

            session.setAttribute("userId", user.getId());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("roleId", user.getRoleId());
            session.setMaxInactiveInterval(30 * 60);

            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (SQLException ex) {
            log("Login database error", ex);
            showError(request, response,
                    "Unable to sign in right now. Please try again.");
        } catch (GeneralSecurityException ex) {
            log("Password verification error", ex);
            showError(request, response,
                    "Unable to verify your password. Please try again.");
        }
    }

    private void showError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    ) throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
