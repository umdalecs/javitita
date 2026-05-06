package com.umdalecs.javitita.compiler;

import java.util.List;

public class WhileStatement extends Statement {
    Expression condition;
    List<Statement> statements;

    public WhileStatement(Expression condition, List<Statement> statements) {
        this.condition = condition;
        this.statements = statements;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
