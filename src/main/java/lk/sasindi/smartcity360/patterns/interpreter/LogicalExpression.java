
package lk.sasindi.smartcity360.patterns.interpreter;

import lk.sasindi.smartcity360.model.PolicyContext;

public class LogicalExpression implements Expression {

    public enum Operator {
        AND,
        OR
    }

    private final Expression leftExpression;
    private final Expression rightExpression;
    private final Operator operator;

    public LogicalExpression(
            Expression leftExpression,
            Expression rightExpression,
            Operator operator
    ) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.operator = operator;
    }

    @Override
    public boolean interpret(PolicyContext context) {

        return switch (operator) {

            case AND ->
                    leftExpression.interpret(context)
                            && rightExpression.interpret(context);

            case OR ->
                    leftExpression.interpret(context)
                            || rightExpression.interpret(context);
        };
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    public Operator getOperator() {
        return operator;
    }
}
