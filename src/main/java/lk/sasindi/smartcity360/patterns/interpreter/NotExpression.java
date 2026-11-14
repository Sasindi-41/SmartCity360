package lk.sasindi.smartcity360.patterns.interpreter;

import lk.sasindi.smartcity360.model.PolicyContext;

public class NotExpression implements Expression {

    private final Expression expression;

    public NotExpression(Expression expression) {

        if (expression == null) {
            throw new IllegalArgumentException(
                    "NOT requires an expression."
            );
        }

        this.expression = expression;
    }

    @Override
    public boolean interpret(PolicyContext context) {
        return !expression.interpret(context);
    }

    public Expression getExpression() {
        return expression;
    }
}
