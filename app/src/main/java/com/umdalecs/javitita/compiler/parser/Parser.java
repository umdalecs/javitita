package com.umdalecs.javitita.compiler.parser;

import com.umdalecs.javitita.compiler.ErrorHandler;
import com.umdalecs.javitita.compiler.lexer.Lexer;
import com.umdalecs.javitita.compiler.lexer.Token;
import com.umdalecs.javitita.compiler.lexer.TokenType;
import com.umdalecs.javitita.compiler.parser.statements.*;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Expression;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Program;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private final Lexer lexer;
    private final ErrorHandler errorHandler;

    private Token currentToken, peekToken;


    public Parser(Lexer lexer, ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.lexer = lexer;

        currentToken = lexer.nextToken();
        peekToken = lexer.nextToken();
    }

    private void nextToken() {
        System.out.println(currentToken);
        currentToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public Program parse() {
        var program = new Program();

        if (errorHandler.expectToken(currentToken, TokenType.FN))
            nextToken();

        if (errorHandler.expectToken(currentToken, TokenType.IDENTIFIER))
            nextToken();

        if (errorHandler.expectToken(currentToken, TokenType.LPAREN))
            nextToken();

        if (errorHandler.expectToken(currentToken, TokenType.RPAREN))
            nextToken();

        if (errorHandler.expectToken(currentToken, TokenType.LBRACE))
            nextToken();

        var statements = parseStatements();

        for  (var statement : statements) {
            program.addStatement(statement);
        }

        if (errorHandler.expectToken(currentToken, TokenType.RBRACE))
            nextToken();

        return program;
    }

    private List<Statement> parseStatements() {
        var statements = new ArrayList<Statement>();

        while (currentToken.type() != TokenType.RBRACE) {
            var statement = parseStatement();

            if  (statement != null) {
                statements.add(statement);
            }
        };

        return statements;
    }

    private Statement parseStatement() {
        return switch (currentToken.type()) {
            case WHILE -> parseWhile();
            case PRINT_STATEMENT -> {
                var res = parsePrint();
                if (errorHandler.expectToken(currentToken, TokenType.SEMICOLON))
                    nextToken();
                yield res;
            }
            case IDENTIFIER -> {
                var res = parseVarAssign();
                if (errorHandler.expectToken(currentToken, TokenType.SEMICOLON))
                    nextToken();
                yield res;
            }
            case BOOLEAN_TYPE, INTEGER_TYPE -> {
                var res = parseVarDeclaration(currentToken.type());
                if (errorHandler.expectToken(currentToken, TokenType.SEMICOLON))
                    nextToken();
                yield res;
            }
            default -> null;
        };
    }

    private PrintStatement parsePrint() {
        nextToken();

        if (errorHandler.expectToken(currentToken, TokenType.LPAREN))
            nextToken();

        var expression = parseExpression();

        if (errorHandler.expectToken(currentToken, TokenType.RPAREN))
            nextToken();

        return new PrintStatement(expression);
    }

    private VarAssignStatement parseVarAssign() {
        var identifier = currentToken;
        nextToken();

        if (errorHandler.expectToken(currentToken, TokenType.ASSIGN))
                nextToken();

        var expression = parseExpression();

        return new VarAssignStatement(identifier, expression);
    }

    private VarDeclarationStatement parseVarDeclaration(TokenType type) {
        nextToken();

        var tp = switch (type) {
            case BOOLEAN_TYPE -> Type.BOOLEAN;
            case INTEGER_TYPE -> Type.INTEGER;
            default -> null;
        };

        if (errorHandler.expectToken(currentToken, TokenType.IDENTIFIER)) {
            var identifier = currentToken;
            nextToken();

            return new VarDeclarationStatement(identifier, tp);
        } else return null;
    }

    private WhileStatement parseWhile() {
        nextToken();

        if (errorHandler.expectToken(currentToken, TokenType.LPAREN))
            nextToken();

        var expression = parseExpression();

        if (errorHandler.expectToken(currentToken, TokenType.RPAREN))
            nextToken();

        if (errorHandler.expectToken(currentToken, TokenType.LBRACE))
            nextToken();

        var statements =  parseStatements();

        if (errorHandler.expectToken(currentToken, TokenType.RBRACE))
            nextToken();

        return new WhileStatement(expression, statements);
    }

    private Expression parseExpression() {
        Token left;

        if (currentToken.type() == TokenType.IDENTIFIER) {
            left = currentToken;
            nextToken();

            switch (currentToken.type()) {
                case SEMICOLON, RPAREN -> {
                    return new Expression(null, left);
                }
                case PLUS, MINUS, MULTI -> {
                    Token operation = currentToken;
                    nextToken();

                    if (errorHandler.expectTokens(currentToken, Arrays.asList(
                            TokenType.INTEGER_LITERAL,
                            TokenType.IDENTIFIER
                    ))) {
                        var tmp = currentToken;
                        nextToken();
                        return new Expression(Type.BOOLEAN, operation, left, tmp);
                    }
                }
                case LOWER_THAN -> {
                    Token operation = currentToken;
                    nextToken();

                    if (errorHandler.expectTokens(currentToken, Arrays.asList(
                            TokenType.INTEGER_LITERAL,
                            TokenType.IDENTIFIER
                    ))) {
                        var tmp = currentToken;
                        nextToken();
                        return new Expression(Type.BOOLEAN, operation, left, tmp);
                    }
                }
            }
        }
        else if (currentToken.type() == TokenType.INTEGER_LITERAL) {
            left = currentToken;
            nextToken();

            switch (currentToken.type()) {
                case SEMICOLON -> {
                    return new Expression(Type.INTEGER, left);
                }
                case PLUS, MINUS, MULTI -> {
                    Token operation = currentToken;
                    nextToken();

                    if (errorHandler.expectTokens(currentToken, Arrays.asList(
                            TokenType.INTEGER_LITERAL,
                            TokenType.IDENTIFIER
                    ))) {
                        var tmp = currentToken;
                        nextToken();
                        return new Expression(Type.BOOLEAN, operation, left, tmp);
                    }
                }
                case LOWER_THAN -> {
                    Token operation = currentToken;
                    nextToken();

                    if (errorHandler.expectTokens(currentToken, Arrays.asList(
                            TokenType.INTEGER_LITERAL,
                            TokenType.IDENTIFIER
                    ))) {
                        var tmp = currentToken;
                        nextToken();
                        return new Expression(Type.BOOLEAN, operation, left, tmp);
                    }
                }
            }
        }
        else if (currentToken.type() == TokenType.TRUE_LITERAL) {
            var tmp = currentToken;
            nextToken();
            return new Expression(Type.BOOLEAN, tmp);
        }
        else if (currentToken.type() == TokenType.FALSE_LITERAL) {
            var tmp = currentToken;
            nextToken();
            return new Expression(Type.BOOLEAN, tmp);
        }

        return null;
    }
}
