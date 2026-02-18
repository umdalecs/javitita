package com.umdalecs.javitita.compiler;

import java.util.Map;
import java.util.ListIterator;
import java.util.List;
import java.util.LinkedList;

public class Parser {
    private final ListIterator<Token> tokens;
    private final Map<String, Symbol> symbols;
    private Token currentToken;
    private List<String> errors;

    public Parser(List<Token> tokens, Map<String, Symbol> symbols) {
        this.tokens = tokens.listIterator();
        this.symbols = symbols;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void parse() {
        errors = new LinkedList<>();

        try {
            advance();
            if (currentToken.type() != TokenType.CLASS)
                addError("CLASS", currentToken.line(), currentToken.column());

            advance();
            if (currentToken.type() != TokenType.IDENTIFIER)
                addError("IDENTIFICADOR", currentToken.line(), currentToken.column());

            advance();
            if (currentToken.type() != TokenType.LBRACE)
                addError("{", currentToken.line(), currentToken.column());

            advance();
            if (currentToken.type() != TokenType.VOID)
                addError("VOID", currentToken.line(), currentToken.column());

            advance();
            if (currentToken.type() != TokenType.IDENTIFIER)
                addError("IDENTIFICADOR", currentToken.line(), currentToken.column());

            advance();
            if (currentToken.type() != TokenType.LPAREN)
                addError("(", currentToken.line(), currentToken.column());

            advance();
            if (currentToken.type() != TokenType.RPAREN)
                addError(")", currentToken.line(), currentToken.column());

            advance();
            if (currentToken.type() != TokenType.LBRACE)
                addError("{", currentToken.line(), currentToken.column());

            prodStatements();

            if (currentToken.type() != TokenType.RBRACE)
                addError("}", currentToken.line(), currentToken.column());

            advance();
            if (currentToken.type() != TokenType.RBRACE)
                addError("}", currentToken.line(), currentToken.column());

            advance();
            if (currentToken.type() != TokenType.EOF)
                addError("EOF", currentToken.line(), currentToken.column());
        } catch (Exception e) {
            errors.add("EOF inesperado en linea " + currentToken.line());
        }
    }

    private void advance() throws Exception {
        if (tokens.hasNext()) {
            currentToken = tokens.next();
        }
    }

    private void addError(String expected, int line, int column) {
        String msg = String.format("Se esperaba %s en la linea %d:%d", expected, line, column);
        errors.add(msg);

        tokens.previous();
    }

    private void prodStatements() throws Exception {
        advance();
        while ((currentToken.type() == TokenType.WHILE ||
                currentToken.type() == TokenType.PRINTSTATEMENT ||
                currentToken.type() == TokenType.IDENTIFIER ||
                currentToken.type() == TokenType.BOOLEAN ||
                currentToken.type() == TokenType.INTEGER)) {
            prodStatement();
            advance();
        }
    }

    private void prodStatement() throws Exception {
        switch (currentToken.type()) {
            case WHILE -> prodWhile();
            case PRINTSTATEMENT -> prodPrintStatement();
            case INTEGER, BOOLEAN -> prodVarDeclaration();
            case IDENTIFIER -> prodVarAssignment();
            default -> {
            }
        }
    }

    private void prodWhile() throws Exception {
        advance();
        if (currentToken.type() != TokenType.LPAREN)
            addError("(", currentToken.line(), currentToken.column());

        prodExpression();

        advance();
        if (currentToken.type() != TokenType.RPAREN)
            addError(")", currentToken.line(), currentToken.column());

        advance();
        if (currentToken.type() != TokenType.LBRACE)
            addError("{", currentToken.line(), currentToken.column());

        prodStatements();

        if (currentToken.type() != TokenType.RBRACE)
            addError("}", currentToken.line(), currentToken.column());
    }

    private void prodPrintStatement() throws Exception {
        advance();
        if (currentToken.type() != TokenType.LPAREN)
            addError("(", currentToken.line(), currentToken.column());

        prodExpression();

        advance();
        if (currentToken.type() != TokenType.RPAREN)
            addError(")", currentToken.line(), currentToken.column());

        advance();
        if (currentToken.type() != TokenType.SEMICOLON)
            addError(";", currentToken.line(), currentToken.column() - 1);
    }

    private void prodVarAssignment() throws Exception {
        advance();
        if (currentToken.type() != TokenType.ASSIGN)
            addError("=", currentToken.line(), currentToken.column());

        prodExpression();

        advance();
        if (currentToken.type() != TokenType.SEMICOLON)
            addError(";", currentToken.line(), currentToken.column() - 1);
    }

    private void prodVarDeclaration() throws Exception {
        var type = currentToken.type().name();

        advance();
        if (currentToken.type() != TokenType.IDENTIFIER)
            addError("IDENTIFICADOR", currentToken.line(), currentToken.column());

        var identifier = currentToken.literal();
        var line = currentToken.line();
        var column = currentToken.column();

        advance();
        if (currentToken.type() != TokenType.SEMICOLON)
            addError(";", currentToken.line(), currentToken.column() - 1);

        symbols.put(identifier, new Symbol(identifier, type, "", line, column));
    }

    private void prodSimpleExpression() throws Exception {
        advance();
        if (!(currentToken.type() == TokenType.SEMICOLON ||
                currentToken.type() == TokenType.RPAREN)) {
            addError("END OF EXPRESSION", currentToken.line(), currentToken.column());
        }
        else {
            tokens.previous();
        }
    }

    private void prodComplexExpression() throws Exception {

        if (!(currentToken.type() == TokenType.INTEGERLITERAL || currentToken.type() == TokenType.IDENTIFIER))
            addError("EXPRESSION", currentToken.line(), currentToken.column());

        advance();
        if (currentToken.type() == TokenType.LOWERT ||
                currentToken.type() == TokenType.PLUS ||
                currentToken.type() == TokenType.MINUS ||
                currentToken.type() == TokenType.MULT )
        {
            advance();
            prodComplexExpression();
        }

        else if (!(currentToken.type() == TokenType.INTEGERLITERAL ||
                currentToken.type() == TokenType.IDENTIFIER)) {
            tokens.previous();
        }
    }

    private void prodExpression() throws Exception {
        advance();
        if (currentToken.type() == TokenType.TRUE || currentToken.type() == TokenType.FALSE) {
            prodSimpleExpression();
        } else if (currentToken.type() == TokenType.IDENTIFIER
                || currentToken.type() == TokenType.INTEGERLITERAL) {
            prodComplexExpression();
        }
    }
}
