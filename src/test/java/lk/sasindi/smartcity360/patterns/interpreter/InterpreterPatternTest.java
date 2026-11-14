package lk.sasindi.smartcity360.patterns.interpreter;

import lk.sasindi.smartcity360.model.PolicyContext;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpreterPatternTest {

    private static PolicyContext city(double noise, double parkingHour) {
        return new PolicyContext(noise, parkingHour, 50, 50);
    }

    private static boolean eval(String expression, double noise, double hour) {
        return new PolicyParser(expression).parse().interpret(city(noise, hour));
    }

    @Test
    @DisplayName("NOISE_LEVEL > 60")
    void noiseGreaterThanSixty() {

        assertTrue(eval("NOISE_LEVEL > 60", 61, 0));
        assertFalse(eval("NOISE_LEVEL > 60", 60, 0));
        assertFalse(eval("NOISE_LEVEL > 60", 40, 0));
    }

    @Test
    @DisplayName("PARKING_HOUR < 8")
    void parkingHourLessThanEight() {

        assertTrue(eval("PARKING_HOUR < 8", 0, 7));
        assertFalse(eval("PARKING_HOUR < 8", 0, 8));
        assertFalse(eval("PARKING_HOUR < 8", 0, 20));
    }

    @Test
    @DisplayName("All comparison operators: >= <= == !=")
    void allComparisonOperators() {

        assertTrue(eval("NOISE_LEVEL >= 60", 60, 0));
        assertFalse(eval("NOISE_LEVEL >= 60", 59, 0));
        assertTrue(eval("NOISE_LEVEL <= 60", 60, 0));
        assertFalse(eval("NOISE_LEVEL <= 60", 61, 0));
        assertTrue(eval("NOISE_LEVEL == 60", 60, 0));
        assertFalse(eval("NOISE_LEVEL == 60", 61, 0));
        assertTrue(eval("NOISE_LEVEL != 60", 61, 0));
        assertFalse(eval("NOISE_LEVEL != 60", 60, 0));
    }

    @Test
    @DisplayName("Decimal and negative thresholds")
    void decimalAndNegativeNumbers() {

        assertTrue(eval("NOISE_LEVEL > 60.5", 60.6, 0));
        assertFalse(eval("NOISE_LEVEL > 60.5", 60.5, 0));
        assertTrue(eval("NOISE_LEVEL > -1", 0, 0));
    }

    @Test
    @DisplayName("Keywords and variable names are not case sensitive")
    void caseInsensitive() {

        assertTrue(eval("noise_level > 60 and parking_hour < 8", 70, 7));
    }

    @Test
    @DisplayName("AND needs both conditions")
    void andExpression() {

        String policy = "NOISE_LEVEL > 60 AND PARKING_HOUR < 8";

        assertTrue(eval(policy, 70, 7));
        assertFalse(eval(policy, 70, 9));
        assertFalse(eval(policy, 50, 7));
    }

    @Test
    @DisplayName("OR needs at least one condition")
    void orExpression() {

        String policy = "NOISE_LEVEL > 60 OR PARKING_HOUR < 8";

        assertTrue(eval(policy, 70, 20));
        assertTrue(eval(policy, 50, 7));
        assertFalse(eval(policy, 50, 20));
    }

    @Test
    @DisplayName("NOT reverses a condition")
    void notExpression() {

        assertTrue(eval("NOT NOISE_LEVEL > 60", 50, 0));
        assertFalse(eval("NOT NOISE_LEVEL > 60", 70, 0));
        assertTrue(eval("NOT NOT NOISE_LEVEL > 60", 70, 0));
    }

    @Test
    @DisplayName("NOT applies to a whole bracketed group")
    void notWithParentheses() {

        String policy = "NOT (NOISE_LEVEL > 60 AND PARKING_HOUR < 8)";

        assertFalse(eval(policy, 70, 7));
        assertTrue(eval(policy, 70, 9));
        assertTrue(eval(policy, 50, 7));
    }

    @Test
    @DisplayName("AND binds tighter than OR")
    void andBindsTighterThanOr() {

        String policy =
                "NOISE_LEVEL > 60 OR PARKING_HOUR < 8 AND NOISE_LEVEL > 100";

        assertTrue(eval(policy, 70, 20));
        assertFalse(eval(policy, 50, 7));
    }

    @Test
    @DisplayName("Parentheses override the precedence")
    void parenthesesOverridePrecedence() {

        String policy =
                "(NOISE_LEVEL > 60 OR PARKING_HOUR < 8) AND NOISE_LEVEL > 100";

        assertFalse(eval(policy, 70, 7));
        assertTrue(eval(policy, 120, 7));
    }

    @Test
    @DisplayName("NOT binds tighter than AND")
    void notBindsTighterThanAnd() {

        // Read as: (NOT NOISE_LEVEL > 60) AND PARKING_HOUR < 8
        String policy = "NOT NOISE_LEVEL > 60 AND PARKING_HOUR < 8";

        assertTrue(eval(policy, 50, 7));
        assertFalse(eval(policy, 70, 7));
    }

    private static void assertInvalid(String expression) {

        assertThrows(IllegalArgumentException.class,
                () -> new PolicyParser(expression).parse());
    }

    @Test
    @DisplayName("Empty or missing expression is rejected")
    void emptyExpression() {

        assertInvalid("");
        assertInvalid("   ");
        assertInvalid(null);
    }

    @Test
    @DisplayName("Policy variable matching is independent of the default locale")
    void variableMatchingUsesLocaleIndependentCaseConversion() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(PolicyContext.isAllowedVariable("noise_level"));
            assertTrue(new PolicyContext(70, 7, 50, 50)
                    .getValue("noise_level") == 70);
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    @DisplayName("Unknown variable is rejected")
    void unknownVariable() {

        assertInvalid("HUMIDITY > 3");
    }

    @Test
    @DisplayName("Incomplete expressions are rejected")
    void incompleteExpressions() {

        assertInvalid("NOISE_LEVEL >");
        assertInvalid("NOISE_LEVEL 60");
        assertInvalid("> 60");
        assertInvalid("NOISE_LEVEL > 60 AND");
        assertInvalid("NOT");
    }

    @Test
    @DisplayName("Unbalanced parentheses are rejected")
    void unbalancedParentheses() {

        assertInvalid("(NOISE_LEVEL > 60");
        assertInvalid("NOISE_LEVEL > 60)");
        assertInvalid("()");
    }

    @Test
    @DisplayName("Unexpected characters and extra tokens are rejected")
    void unexpectedInput() {

        assertInvalid("NOISE_LEVEL > 60 &");
        assertInvalid("NOISE_LEVEL > 60 PARKING_HOUR < 8");
        assertInvalid("NOISE_LEVEL > abc");
    }

    @Test
    @DisplayName("The context only knows the allowed variables")
    void contextVariables() {

        assertTrue(PolicyContext.isAllowedVariable("noise_level"));
        assertTrue(PolicyContext.isAllowedVariable("TRAFFIC_LEVEL"));
        assertFalse(PolicyContext.isAllowedVariable("HUMIDITY"));
        assertFalse(PolicyContext.isAllowedVariable(null));

        assertThrows(IllegalArgumentException.class,
                () -> city(1, 1).getValue("HUMIDITY"));
    }
}
