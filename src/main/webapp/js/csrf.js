(function () {
    "use strict";

    function readToken() {
        const element = document.getElementById("smartcity-csrf-token");
        return element ? element.getAttribute("data-token") : "";
    }

    const originalFetch = window.fetch.bind(window);

    window.fetch = function (input, init) {
        const options = init ? { ...init } : {};
        const method = String(
            options.method || (input instanceof Request ? input.method : "GET")
        ).toUpperCase();

        if (!["POST", "PUT", "PATCH", "DELETE"].includes(method)) {
            return originalFetch(input, init);
        }

        let requestUrl;
        try {
            requestUrl = new URL(
                input instanceof Request ? input.url : input,
                window.location.href
            );
        } catch (_) {
            return originalFetch(input, init);
        }

        if (requestUrl.origin !== window.location.origin) {
            return originalFetch(input, init);
        }

        const token = readToken();
        if (!token) {
            return originalFetch(input, init);
        }

        const headers = new Headers(
            options.headers || (input instanceof Request ? input.headers : undefined)
        );
        headers.set("X-CSRF-Token", token);
        options.headers = headers;

        return originalFetch(input, options);
    };

    document.addEventListener("submit", function (event) {
        const form = event.target;
        if (!(form instanceof HTMLFormElement)
            || String(form.method).toUpperCase() !== "POST") {
            return;
        }

        const token = readToken();
        if (!token) {
            return;
        }

        let field = form.querySelector('input[name="_csrf"]');
        if (!field) {
            field = document.createElement("input");
            field.type = "hidden";
            field.name = "_csrf";
            form.appendChild(field);
        }
        field.value = token;
    }, true);
})();
