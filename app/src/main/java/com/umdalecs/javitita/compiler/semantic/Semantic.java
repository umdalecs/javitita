package com.umdalecs.javitita.compiler.semantic;

import com.umdalecs.javitita.compiler.ErrorHandler;
import com.umdalecs.javitita.compiler.Symbol;
import com.umdalecs.javitita.compiler.SymbolTable;
import com.umdalecs.javitita.compiler.lexer.Token;
import com.umdalecs.javitita.compiler.lexer.TokenType;
import com.umdalecs.javitita.compiler.parser.Type;
import com.umdalecs.javitita.compiler.parser.statements.PrintStatement;
import com.umdalecs.javitita.compiler.parser.statements.VarAssignStatement;
import com.umdalecs.javitita.compiler.parser.statements.VarDeclarationStatement;
import com.umdalecs.javitita.compiler.parser.statements.WhileStatement;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Expression;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Program;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Statement;

public class Semantic {
    private final Program program;
    private final ErrorHandler errorHandler;
    private final SymbolTable symbolTable;

    public Semantic(Program program, ErrorHandler errorHandler, SymbolTable symbolTable) {
        this.program = program;
        this.errorHandler = errorHandler;
        this.symbolTable = symbolTable;
    }

    public void CheckSemantics() {
        for (var statement: program.getStatements()) {
            checkStatementSemantic(statement);
        }
    }
    private void checkStatementSemantic(Statement statement) {
        switch (statement) {
            case WhileStatement ws -> {
                var condition = ws.getCondition();

                if (checkExpressionType(condition) != Type.BOOLEAN)
                    errorHandler.addSemanticError(String.format(
                                "el tipo devuelto por la expresión de una condición debe ser BOOLEAN en linea %d",
                                condition.getLeft().line()
                            ));

                for (var st: ws.getStatements()) {
                    checkStatementSemantic(st);
                }
            }
            case PrintStatement ps -> {
                var ex = ps.getExpression();
                if (ex.getType() == null) {
                    checkExpressionType(ex);
                }
            }

            case VarAssignStatement vas -> {
                var identToken = vas.getIdentifier();
                var symbol = symbolTable.getSymbol(identToken.literal());
                var expression = vas.getExpression();

                if (symbol == null) {
                    errorHandler.addSemanticError("intentando asignar valor a un identificador que no existe");
                    return;
                }

                var type = checkExpressionType(expression);

                if (symbol.type() != type) {
                    errorHandler.addSemanticError(
                            String.format(
                                    "el tipo no coincide con el de la expresión, se esperaba `%s` se obtuvo `%s en la linea %d`",
                                    symbol.type().name(),
                                    type.name(),
                                    identToken.line()
                            ));
                }

            }
            case VarDeclarationStatement vds -> {
                var type = vds.getType();
                var identifierToken = vds.getIdentifier();

                var entry = new Symbol(identifierToken, type, identifierToken.literal());
                if (!symbolTable.addSymbol(identifierToken.literal(), entry))
                    errorHandler.addSemanticError(
                            String.format(
                                "intentando redeclarar identificador `%s` en %d:%d",
                                identifierToken.literal(),
                                identifierToken.line(),
                                identifierToken.column()
                            ));
            }
            default -> {}
        }
    }

    /**
     * checks the expression type.
     * <p>
     * this method also side effect the expression
     * rewriting its type member
     * @param expression {@link Expression}
     * @return the {@link Type} of the expression.
     */
    private Type checkExpressionType(Expression expression) {
        // In case left is TRUE, FALSE, INTEGER_LITERAL
        if (expression.getType() != null)
            return expression.getType();

        // In case left is IDENTIFIER
        if (expression.getOperation() == null) {
            var type = checkIdentifierType(expression.getLeft());
            expression.setType(type);
            return type;
        }

        // LEFT (IDENTIFIER(INTEGER) | INTEGER_LITERAL) ( + | - | * ) RIGHT (IDENTIFIER(INTEGER) | INTEGER_LITERAL) -> Type.INTEGER
        // LEFT (IDENTIFIER(INTEGER) | INTEGER_LITERAL) < RIGHT (IDENTIFIER(INTEGER) | INTEGER_LITERAL) -> Type.BOOLEAN
        if (
                !(expression.getLeft().type() == TokenType.INTEGER_LITERAL
                || checkIdentifierType(expression.getLeft()) == Type.INTEGER)
        ) errorHandler.addSemanticError(String.format("mezcla de tipos en operación en la linea %d", expression.getLeft().line()));

        if (
                !(expression.getRight().type() == TokenType.INTEGER_LITERAL
                        || checkIdentifierType(expression.getRight()) == Type.INTEGER)
        ) errorHandler.addSemanticError(String.format("mezcla de tipos en operación en la linea %d", expression.getRight().line()));

        return switch (expression.getOperation().type()) {
            case MINUS, MULTI, PLUS -> {
                expression.setType(Type.INTEGER);
                yield Type.INTEGER;
            }
            case LOWER_THAN -> {
                expression.setType(Type.BOOLEAN);
                yield Type.BOOLEAN;
            }
            default -> null;
        };
    }

    private Type checkIdentifierType(Token token) {
        var symbol = symbolTable.getSymbol(token.literal());

        if (symbol == null) {
            errorHandler.addSemanticError(String.format(
                    "intentando usar identificador inexistente en linea %d",
                    token.line()));
            return null;
        }

        return symbol.type();
    }
}
