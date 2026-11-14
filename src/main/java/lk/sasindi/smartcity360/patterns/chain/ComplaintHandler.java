package lk.sasindi.smartcity360.patterns.chain;

import java.sql.SQLException;

public abstract class ComplaintHandler {

    private ComplaintHandler next;

    public ComplaintHandler setNext(ComplaintHandler next) {
        this.next = next;
        return next;
    }

    public ComplaintHandler getNext() {
        return next;
    }

    public abstract void handle(RouteContext context) throws SQLException;

    protected void passToNext(RouteContext context) throws SQLException {

        if (next != null
                && context.getCompletedLevel() < context.getRequiredLevel()) {

            next.handle(context);
        }
    }
}
