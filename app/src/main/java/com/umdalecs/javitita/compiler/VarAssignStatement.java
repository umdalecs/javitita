package com.umdalecs.javitita.compiler;

public class VarAssignStatement extends Statement {
    private Token identifier;
    private Expression expression;

    public VarAssignStatement(Token identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public Token getIdentifier() {
        return identifier;
    }
}
