
package lk.sasindi.smartcity360.patterns.interpreter;

import lk.sasindi.smartcity360.model.PolicyContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PolicyParser {

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "\\G\\s*(>=|<=|==|!=|>|<|\\(|\\)|" +
                    "-?(?:\\d+(?:\\.\\d*)?|\\.\\d+)|" +
                    "[A-Za-z_][A-Za-z0-9_]*)"
    );

    private final List<Token> tokens;

    private int position;

    public PolicyParser(String expression) {

        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException(
                    "Policy expression cannot be empty."
            );
        }

        this.tokens = tokenize(expression);
        this.position = 0;
    }

    public Expression parse() {

        Expression expression = parseOrExpression();

        if (position < tokens.size()) {
            throw error(
                    "Unexpected token '" +
                            peek().value() + "'."
            );
        }

        return expression;
    }

    private Expression parseOrExpression() {

        Expression expression = parseAndExpression();

        while (match(TokenType.OR)) {

            Expression right = parseAndExpression();

            expression = new LogicalExpression(
                    expression,
                    right,
                    LogicalExpression.Operator.OR
            );
        }

        return expression;
    }

    private Expression parseAndExpression() {

        Expression expression = parseUnary();

        while (match(TokenType.AND)) {

            Expression right = parseUnary();

            expression = new LogicalExpression(
                    expression,
                    right,
                    LogicalExpression.Operator.AND
            );
        }

        return expression;
    }

    private Expression parseUnary() {

        if (match(TokenType.NOT)) {
            return new NotExpression(parseUnary());
        }

        return parsePrimary();
    }

    private Expression parsePrimary() {

        if (match(TokenType.LEFT_PAREN)) {

            Expression expression = parseOrExpression();

            consume(
                    TokenType.RIGHT_PAREN,
                    "Missing closing parenthesis ')'."
            );

            return expression;
        }

        return parseComparison();
    }

    private Expression parseComparison() {

        Token variableToken = consume(
                TokenType.IDENTIFIER,
                "Expected a city context variable."
        );

        String variable = variableToken.value()
                .toUpperCase(Locale.ROOT);

        if (!PolicyContext.isAllowedVariable(variable)) {

            throw error(
                    "Unknown city variable: " + variable
            );
        }

        Token operatorToken = consume(
                TokenType.COMPARISON,
                "Expected a comparison operator."
        );

        Token numberToken = consume(
                TokenType.NUMBER,
                "Expected a numeric value after the operator."
        );

        double expectedValue;

        try {

            expectedValue = Double.parseDouble(
                    numberToken.value()
            );

        } catch (NumberFormatException ex) {

            throw error("Invalid numeric value.");
        }

        if (!Double.isFinite(expectedValue)) {

            throw error(
                    "Policy threshold must be a finite number."
            );
        }

        return new ComparisonExpression(
                variable,
                operatorToken.value(),
                expectedValue
        );
    }

    private List<Token> tokenize(String expression) {

        List<Token> result = new ArrayList<>();

        Matcher matcher = TOKEN_PATTERN.matcher(expression);

        int index = 0;

        while (index < expression.length()) {

            matcher.region(index, expression.length());

            if (!matcher.find()) {

                if (expression.substring(index).isBlank()) {
                    break;
                }

                throw new IllegalArgumentException(
                        "Invalid character at position " +
                                (index + 1) + "."
                );
            }

            String value = matcher.group(1);

            int start = matcher.start(1);

            TokenType type;

            if ("AND".equalsIgnoreCase(value)) {

                type = TokenType.AND;

            } else if ("OR".equalsIgnoreCase(value)) {

                type = TokenType.OR;

            } else if ("NOT".equalsIgnoreCase(value)) {

                type = TokenType.NOT;

            } else if ("(".equals(value)) {

                type = TokenType.LEFT_PAREN;

            } else if (")".equals(value)) {

                type = TokenType.RIGHT_PAREN;

            } else if (Set.of(
                    ">", "<", ">=", "<=", "==", "!="
            ).contains(value)) {

                type = TokenType.COMPARISON;

            } else if (value.matches(
                    "-?(?:\\d+(?:\\.\\d*)?|\\.\\d+)"
            )) {

                type = TokenType.NUMBER;

            } else {

                type = TokenType.IDENTIFIER;
            }

            result.add(new Token(type, value, start));

            index = matcher.end();
        }

        if (result.isEmpty()) {

            throw new IllegalArgumentException(
                    "Policy expression contains no valid tokens."
            );
        }

        return result;
    }

    private Token consume(
            TokenType expected,
            String message
    ) {

        if (position >= tokens.size()
                || peek().type() != expected) {

            throw error(message);
        }

        return tokens.get(position++);
    }

    private boolean match(TokenType expected) {

        if (position >= tokens.size()) {
            return false;
        }

        if (peek().type() != expected) {
            return false;
        }

        position++;

        return true;
    }

    private Token peek() {
        return tokens.get(position);
    }

    private IllegalArgumentException error(String message) {

        int errorPosition = position < tokens.size()
                ? tokens.get(position).position() + 1
                : -1;

        if (errorPosition > 0) {

            message += " Position: " + errorPosition + ".";
        }

        return new IllegalArgumentException(message);
    }

    private enum TokenType {
        IDENTIFIER,
        NUMBER,
        COMPARISON,
        AND,
        OR,
        NOT,
        LEFT_PAREN,
        RIGHT_PAREN
    }

    private record Token(
            TokenType type,
            String value,
            int position
    ) {
    }
}
