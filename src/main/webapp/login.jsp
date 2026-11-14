<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Sign In | SmartCity360</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/auth.css">
</head>

<body class="auth-page">

<div class="auth-container">

    <section class="auth-image-panel">

        <div class="image-overlay"></div>

        <div class="image-content">

            <a href="${pageContext.request.contextPath}/"
               class="brand">

                <div class="brand-icon">
                    <img src="${pageContext.request.contextPath}/images/logo.png"
                         alt="SmartCity360 logo">
                </div>

                <div class="brand-text">
                    <span class="brand-name">SmartCity360</span>
                    <span class="brand-caption">
                        INTELLIGENT CITY MANAGEMENT
                    </span>
                </div>

            </a>

            <div class="hero-content">

                <p class="hero-eyebrow">WELCOME BACK</p>

                <h1>
                    Your city.<br>
                    Your connection.
                </h1>

                <p class="hero-description">
                    Sign in to stay connected with your city.
                    Access smarter services, explore city updates,
                    and manage your everyday urban experience.
                </p>

            </div>

            <div class="image-footer">
                <span class="footer-accent"></span>
                <span>SMARTER LIVING STARTS HERE</span>
            </div>

        </div>

    </section>

    <section class="auth-form-panel">

        <div class="auth-form-content">

            <div class="form-heading">

                <span class="form-eyebrow">YOUR SMART CITY ACCOUNT</span>

                <h2>Sign In</h2>

                <p>
                    Welcome back! Enter your details to access
                    your SmartCity360 account.
                </p>

            </div>


            <% if (request.getParameter("registered") != null) { %>
            <div class="alert alert-success">
                Your account has been created successfully. You can now sign in.
            </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/login"
                  method="post"
                  class="auth-form">

                <div class="form-group">

                    <label for="email">EMAIL ADDRESS</label>

                    <input
                            type="email"
                            id="email"
                            name="email"
                            placeholder="Enter your email address"
                            autocomplete="email"
                            maxlength="254"
                            required>

                </div>

                <div class="form-group">

                    <label for="password">PASSWORD</label>

                    <input
                            type="password"
                            id="password"
                            name="password"
                            placeholder="Enter your password"
                            autocomplete="current-password"
                            maxlength="128"
                            required>

                </div>

                <button type="submit" class="primary-button">
                    Sign In <span>→</span>
                </button>

            </form>

            <div class="auth-switch">

                <span>Don't have an account?</span>

                <a href="${pageContext.request.contextPath}/signup.jsp">
                    Create account
                </a>

            </div>

            <div class="security-footer">

                <span class="security-dot"></span>

                <span>SECURE ACCESS · SMARTCITY360</span>

            </div>

        </div>

    </section>

</div>
<script>
    const loginForm = document.querySelector(".auth-form");

    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    loginForm.addEventListener("submit", function (event) {

        if (emailInput.value.trim() === "") {

            emailInput.setCustomValidity(
                "Please enter your email address."
            );

        } else {

            emailInput.setCustomValidity("");
        }

        if (passwordInput.value === "") {

            passwordInput.setCustomValidity(
                "Please enter your password."
            );

        } else {

            passwordInput.setCustomValidity("");
        }

        if (!loginForm.checkValidity()) {

            event.preventDefault();
            loginForm.reportValidity();
        }
    });

    emailInput.addEventListener("input", function () {
        emailInput.setCustomValidity("");
    });

    passwordInput.addEventListener("input", function () {
        passwordInput.setCustomValidity("");
    });
</script>
</body>
</html>