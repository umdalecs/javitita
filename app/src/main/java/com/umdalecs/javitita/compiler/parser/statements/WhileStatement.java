package com.umdalecs.javitita.compiler.parser.statements;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Expression;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Statement;

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
