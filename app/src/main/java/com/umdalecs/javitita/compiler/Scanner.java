package com.umdalecs.javitita.compiler;

import java.util.Map;

public class Scanner {
    private static final Map<String, TokenType> keywords = Map.of(
            "while", TokenType.WHILE,
            "println", TokenType.PRINTSTATEMENT,
            "class", TokenType.CLASS,
            "int", TokenType.INTEGER,
            "bool", TokenType.BOOLEAN,
            "true", TokenType.TRUE,
            "void", TokenType.VOID,
            "false", TokenType.FALSE);

    private static TokenType lookupIdent(String identifier) {
        var kw = keywords.get(identifier);

        if (kw != null)
            return kw;

        return TokenType.IDENTIFIER;
    }

    private final String input;
    private int position, readPosition, line, column;
    private char currentChar;

    public Scanner(String input) {
        this.input = input;
        this.position = this.readPosition = 0;
        this.line = 1;
        this.column = 1;

        this.readChar();
    }

    private void readChar() {
        this.currentChar = this.readPosition >= input.length()
                ? 0
                : this.input.charAt(this.readPosition);

        this.position = this.readPosition++;
    }

    public Token nextToken() {
        while (" \n\t".indexOf(this.currentChar) != -1) {
            if (this.currentChar == '\n'){
                this.line++;
                this.column = 1;
            }
            this.readChar();
        }
        Token token = switch (this.currentChar) {
            case '=' -> new Token(TokenType.ASSIGN, currentChar, line, column, position);
            case '<' -> new Token(TokenType.LOWERT, currentChar, line, column, position);
            case '+' -> new Token(TokenType.PLUS, currentChar, line, column, position);
            case '*' -> new Token(TokenType.MULT, currentChar, line, column, position);
            case '-' -> new Token(TokenType.MINUS, currentChar, line, column, position);
            case '{' -> new Token(TokenType.LBRACE, currentChar, line, column, position);
            case '}' -> new Token(TokenType.RBRACE, currentChar, line, column, position);
            case '(' -> new Token(TokenType.LPAREN, currentChar, line, column, position);
            case ')' -> new Token(TokenType.RPAREN, currentChar, line, column, position);
            case ';' -> new Token(TokenType.SEMICOLON, currentChar, line, column, position);
            case 0 -> new Token(TokenType.EOF, "", line, column, position);
            default -> {
                if (Character.isAlphabetic(currentChar)) {
                    var identifier = readIdentifier();
                    yield new Token(lookupIdent(identifier), identifier, line, column, position);
                } else if (Character.isDigit(currentChar)) {
                    yield new Token(TokenType.INTEGERLITERAL, readNumber(), line, column, position);
                }
                yield new Token(TokenType.ILLEGAL, currentChar, line, column, position);
            }
        };
        readChar();
        return token;
    }

    private String readIdentifier() {
        var position = this.position;

        while (Character.isAlphabetic(this.currentChar) ||
                Character.isDigit(this.currentChar)) {
            this.readChar();
        }

        // this solves the bug where
        // cant detect tokens after identifiers
        this.position = --this.readPosition;

        return input.substring(position, this.position);
    }

    private String readNumber() {
        var position = this.position;

        while (Character.isDigit(this.currentChar)) {
            this.readChar();
        }

        // this solves the bug where
        // cant detect tokens after numbers
        this.position = --this.readPosition;

        return input.substring(position, this.position);
    }

}
