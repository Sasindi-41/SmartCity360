package lk.sasindi.smartcity360.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.sasindi.smartcity360.dao.UserDAO;
import lk.sasindi.smartcity360.util.PasswordUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
                            + "[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?"
                            + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)+$"
            );

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9][0-9()\\- ]*$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(
                    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)"
                            + "(?=.*[^A-Za-z0-9\\s])\\S{8,128}$"
            );

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = trim(request.getParameter("fullName"));
        String email = trim(request.getParameter("email"));

        String mobile = trim(request.getParameter("mobile"));

        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (isBlank(fullName)
                || isBlank(email)
                || isBlank(mobile)
                || isBlank(password)
                || isBlank(confirmPassword)) {

            showError(request, response,
                    "Please complete all required fields.");
            return;
        }

        if (fullName.length() < 2 || fullName.length() > 100) {

            showError(request, response,
                    "Full name must contain between 2 and 100 characters.");
            return;
        }

        if (fullName.matches(".*[\\p{Cc}].*")) {

            showError(request, response,
                    "Full name contains invalid characters.");
            return;
        }

        email = email.toLowerCase(Locale.ROOT);

        if (email.length() > 254
                || !EMAIL_PATTERN.matcher(email).matches()) {

            showError(request, response,
                    "Please enter a valid email address.");
            return;
        }

        if (mobile.length() > 25
                || !PHONE_PATTERN.matcher(mobile).matches()) {

            showError(request, response,
                    "Please enter a valid mobile number.");
            return;
        }

        long digitCount = mobile.chars()
                .filter(Character::isDigit)
                .count();

        if (digitCount < 7 || digitCount > 15) {

            showError(request, response,
                    "Mobile number must contain between 7 and 15 digits.");
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {

            showError(request, response,
                    "Password must be 8–128 characters and include an uppercase letter, lowercase letter, number and special character. Spaces are not allowed.");
            return;
        }

        if (!password.equals(confirmPassword)) {

            showError(request, response,
                    "Password and confirm password do not match.");
            return;
        }

        try {

            if (userDAO.emailExists(email)) {

                showError(request, response,
                        "An account with this email already exists.");
                return;
            }

            String passwordHash =
                    PasswordUtil.hashPassword(password);

            boolean registered = userDAO.registerUser(
                    fullName,
                    email,
                    passwordHash,
                    mobile
            );

            if (registered) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/login.jsp?registered=1"
                );

            } else {

                showError(request, response,
                        "Registration failed. Please try again.");
            }

        } catch (SQLException e) {

            log("Registration database error", e);

            if ("23000".equals(e.getSQLState())) {

                showError(request, response,
                        "This email address may already be registered. Please use another email.");
                return;
            }

            showError(request, response,
                    "A database error occurred. Please try again.");

        } catch (GeneralSecurityException e) {

            log("Password hashing error", e);

            showError(request, response,
                    "Unable to secure your password. Please try again.");
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void showError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    ) throws ServletException, IOException {

        request.setAttribute("error", message);

        request.getRequestDispatcher("/signup.jsp")
                .forward(request, response);
    }
}