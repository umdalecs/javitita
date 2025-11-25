package compiler;

import java.util.*;

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
                addError("CLASS", currentToken.position());

            advance();
            if (currentToken.type() != TokenType.IDENTIFIER)
                addError("IDENTIFICADOR", currentToken.position());

            advance();
            if (currentToken.type() != TokenType.LBRACE)
                addError("{", currentToken.position());

            advance();
            if (currentToken.type() != TokenType.VOID)
                addError("VOID", currentToken.position());

            advance();
            if (currentToken.type() != TokenType.IDENTIFIER)
                addError("IDENTIFICADOR", currentToken.position());

            advance();
            if (currentToken.type() != TokenType.LPAREN)
                addError("(", currentToken.position());

            advance();
            if (currentToken.type() != TokenType.RPAREN)
                addError(")", currentToken.position());

            advance();
            if (currentToken.type() != TokenType.LBRACE)
                addError("{", currentToken.position());

            prodStatements();

            if (currentToken.type() != TokenType.RBRACE)
                addError("}", currentToken.position());

            advance();
            if (currentToken.type() != TokenType.RBRACE)
                addError("}", currentToken.position());

            advance();
            if (currentToken.type() != TokenType.EOF)
                addError("EOF", currentToken.position());
        } catch (Exception e) {
            errors.add("EOF inesperado en linea " + currentToken.position());
        }
    }

    private void advance() throws Exception {
        if (tokens.hasNext()) {
            currentToken = tokens.next();
        }
    }

    private void addError(String expected, int position) {
        String msg = String.format("Se esperaba %s en la linea %d", expected, position);
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
            addError("(", currentToken.position());

        prodExpression();

        advance();
        if (currentToken.type() != TokenType.RPAREN)
            addError(")", currentToken.position());

        advance();
        if (currentToken.type() != TokenType.LBRACE)
            addError("{", currentToken.position());

        prodStatements();

        if (currentToken.type() != TokenType.RBRACE)
            addError("}", currentToken.position());
    }

    private void prodPrintStatement() throws Exception {
        advance();
        if (currentToken.type() != TokenType.LPAREN)
            addError("(", currentToken.position());

        prodExpression();

        advance();
        if (currentToken.type() != TokenType.RPAREN)
            addError(")", currentToken.position());

        advance();
        if (currentToken.type() != TokenType.SEMICOLON)
            addError(";", currentToken.position() - 1);
    }

    private void prodVarAssignment() throws Exception {
        advance();
        if (currentToken.type() != TokenType.ASSIGN)
            addError("=", currentToken.position());

        prodExpression();

        advance();
        if (currentToken.type() != TokenType.SEMICOLON)
            addError(";", currentToken.position() - 1);
    }

    private void prodVarDeclaration() throws Exception {
        var type = currentToken.type().name();

        advance();
        if (currentToken.type() != TokenType.IDENTIFIER)
            addError("IDENTIFICADOR", currentToken.position());

        var identifier = currentToken.literal();
        var position = currentToken.position();

        advance();
        if (currentToken.type() != TokenType.SEMICOLON)
            addError(";", currentToken.position() - 1);

        symbols.put(identifier, new Symbol(identifier, type, "", position));
    }

    private void prodSimpleExpression() throws Exception {
        advance();
        if (!(currentToken.type() == TokenType.SEMICOLON ||
                currentToken.type() == TokenType.RPAREN)) {
            addError("END OF EXPRESSION", currentToken.position());
        }
        else {
            tokens.previous();
        }
    }

    private void prodComplexExpression() throws Exception {

        if (!(currentToken.type() == TokenType.INTEGERLITERAL || currentToken.type() == TokenType.IDENTIFIER))
            addError("EXPRESSION", currentToken.position());

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
