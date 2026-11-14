package lk.sasindi.smartcity360.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lk.sasindi.smartcity360.dao.DashboardDAO;
import lk.sasindi.smartcity360.model.DashboardSummary;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !(session.getAttribute("userId") instanceof Number)
                || !(session.getAttribute("roleId") instanceof Number)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = ((Number) session.getAttribute("userId")).intValue();
        int roleId = ((Number) session.getAttribute("roleId")).intValue();
        String fullName = String.valueOf(session.getAttribute("fullName"));

        try {
            DashboardSummary summary =
                    dashboardDAO.getSummary(userId, roleId, fullName);

            request.setAttribute("dashboard", summary);
            request.setAttribute("activePage", "dashboard");
            request.setAttribute("canManageCity", roleId == 4);

            request.getRequestDispatcher("/dashboard.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            log("Unable to load dashboard data", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load dashboard information."
            );
        }
    }
}
