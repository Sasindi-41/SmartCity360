package lk.sasindi.smartcity360.patterns.interpreter;

import lk.sasindi.smartcity360.model.PolicyContext;

public interface Expression {

    boolean interpret(PolicyContext context);
}
