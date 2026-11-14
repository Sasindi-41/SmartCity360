
package lk.sasindi.smartcity360.patterns.interpreter;

import lk.sasindi.smartcity360.model.PolicyContext;

public class ComparisonExpression implements Expression {

    private final String variable;
    private final String operator;
    private final double expectedValue;

    public ComparisonExpression(
            String variable,
            String operator,
            double expectedValue
    ) {
        this.variable = variable;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean interpret(PolicyContext context) {

        double actualValue = context.getValue(variable);

        return switch (operator) {

            case ">" -> actualValue > expectedValue;

            case "<" -> actualValue < expectedValue;

            case ">=" -> actualValue >= expectedValue;

            case "<=" -> actualValue <= expectedValue;

            case "==" -> Double.compare(
                    actualValue,
                    expectedValue
            ) == 0;

            case "!=" -> Double.compare(
                    actualValue,
                    expectedValue
            ) != 0;

            default -> throw new IllegalArgumentException(
                    "Unsupported comparison operator: " + operator
            );
        };
    }

    public String getVariable() {
        return variable;
    }

    public String getOperator() {
        return operator;
    }

    public double getExpectedValue() {
        return expectedValue;
    }
}
