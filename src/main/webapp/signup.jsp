<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Sign Up | SmartCity360</title>

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

                <p class="hero-eyebrow">THE FUTURE IS</p>

                <h1>
                    A smarter city<br>
                    starts with you.
                </h1>

                <p class="hero-description">
                    Connect with your city in a whole new way.
                    Discover smarter services, better infrastructure,
                    and a community built for tomorrow.
                </p>

            </div>

            <div class="image-footer">
                <span class="footer-accent"></span>
                <span>SMARTER LIVING STARTS HERE</span>
            </div>

        </div>

    </section>

    <section class="auth-form-panel">

        <div class="auth-form-content signup-content">

            <div class="form-heading">

                <span class="form-eyebrow">JOIN YOUR SMART CITY</span>

                <h2>Create Account</h2>

                <p>
                    Create an account to access your smarter
                    city experience.
                </p>

            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/register"
                  method="post"
                  class="auth-form">

                <div class="form-group">

                    <label for="fullName">FULL NAME</label>

                    <input
                            type="text"
                            id="fullName"
                            name="fullName"
                            placeholder="Enter your full name"
                            autocomplete="name"
                            minlength="2"
                            maxlength="100"
                            required>

                </div>

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

                    <label for="mobile">MOBILE</label>

                    <input
                            type="tel"
                            id="mobile"
                            name="mobile"
                            placeholder="Enter your mobile number"
                            autocomplete="tel"
                            minlength="7"
                            maxlength="25"
                            pattern="\+?[0-9() -]{6,24}"
                            title="Enter a valid mobile number using 7–15 digits."
                            required>

                </div>

                <div class="form-group">

                    <label for="password">PASSWORD</label>

                    <input
                            type="password"
                            id="password"
                            name="password"
                            placeholder="Create your password"
                            autocomplete="new-password"
                            minlength="8"
                            maxlength="128"
                            required>

                    <small class="input-hint">
                        At least 8 characters, including uppercase, lowercase,
                        a number and a special character.
                    </small>

                </div>

                <div class="form-group">

                    <label for="confirmPassword">CONFIRM PASSWORD</label>

                    <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            placeholder="Enter your password again"
                            autocomplete="new-password"
                            minlength="8"
                            maxlength="128"
                            required>

                </div>

                <button type="submit" class="primary-button">
                    Create an account <span>→</span>
                </button>

            </form>

            <div class="auth-switch">

                <span>Already have an account?</span>

                <a href="${pageContext.request.contextPath}/login.jsp">
                    Sign in
                </a>

            </div>

            <div class="security-footer">

                <span class="security-dot"></span>

                <span>SECURE REGISTRATION · SMARTCITY360</span>

            </div>

        </div>

    </section>

</div>
<script>
    const signupForm = document.querySelector(".auth-form");

    const passwordInput = document.getElementById("password");
    const confirmPasswordInput =
        document.getElementById("confirmPassword");

    const mobileInput = document.getElementById("mobile");

    function validatePasswordMatch() {

        if (passwordInput.value !== confirmPasswordInput.value) {

            confirmPasswordInput.setCustomValidity(
                "Passwords do not match."
            );

        } else {

            confirmPasswordInput.setCustomValidity("");
        }
    }

    passwordInput.addEventListener("input", validatePasswordMatch);

    confirmPasswordInput.addEventListener(
        "input",
        validatePasswordMatch
    );

    mobileInput.addEventListener("input", function () {

        const digits = mobileInput.value.replace(/\D/g, "").length;

        if (digits < 7 || digits > 15) {

            mobileInput.setCustomValidity(
                "Mobile number must contain between 7 and 15 digits."
            );

        } else {

            mobileInput.setCustomValidity("");
        }
    });

    signupForm.addEventListener("submit", function (event) {

        validatePasswordMatch();

        mobileInput.dispatchEvent(new Event("input"));

        if (!signupForm.checkValidity()) {

            event.preventDefault();
            signupForm.reportValidity();
        }
    });
</script>
</body>
</html>