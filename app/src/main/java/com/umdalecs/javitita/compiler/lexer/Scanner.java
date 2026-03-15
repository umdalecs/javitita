package com.umdalecs.javitita.compiler.lexer;

import com.umdalecs.javitita.compiler.ErrorHandler;

import java.util.List;

public class Scanner {
    private final String input;
    private final ErrorHandler errorHandler;
    private int position, readPosition, line, column;
    private char currentChar;


    public Scanner(String input, ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.input = input;
        this.position = this.readPosition = 0;
        this.line = 1;
        this.column = 0;

        this.readChar();
    }

    private void readChar() {
        currentChar = readPosition >= input.length()
                ? 0
                : input.charAt(readPosition);

        this.position = this.readPosition++;
        this.column++;
    }

    private void peekChar() {
        currentChar = readPosition >= input.length()
                ? 0
                : input.charAt(readPosition);
    }

    private void skipWhitespaces() {
        while (" \n\t".indexOf(currentChar) != -1) {
            if (currentChar == '\n'){
                line++;
                column = 0;
            }
            readChar();
        }
    }

    public Token nextToken() {
        skipWhitespaces();
        Token token = switch (this.currentChar) {
            case '=' -> new Token(TokenType.ASSIGN, currentChar, line, column, position);
            case '<' -> new Token(TokenType.LOWER_THAN, currentChar, line, column, position);
            case '+' -> new Token(TokenType.PLUS, currentChar, line, column, position);
            case '*' -> new Token(TokenType.MULTI, currentChar, line, column, position);
            case '-' -> new Token(TokenType.MINUS, currentChar, line, column, position);
            case '{' -> new Token(TokenType.LBRACE, currentChar, line, column, position);
            case '}' -> new Token(TokenType.RBRACE, currentChar, line, column, position);
            case '(' -> new Token(TokenType.LPAREN, currentChar, line, column, position);
            case ')' -> new Token(TokenType.RPAREN, currentChar, line, column, position);
            case ';' -> new Token(TokenType.SEMICOLON, currentChar, line, column, position);
            case 0 -> new Token(TokenType.EOF, "", line, column, position);
            default -> {
                if (Character.isAlphabetic(currentChar)) {
                    var position = this.position;
                    var column = this.column;
                    var identifier = readIdentifier();
                    yield new Token(Token.lookupKw(identifier), identifier, line, column, position);
                } else if (Character.isDigit(currentChar)) {
                    var position = this.position;
                    var column = this.column;
                    yield new Token(TokenType.INTEGER_LITERAL, readNumber(), line, column, position);
                }

                var tkn = new Token(TokenType.ILLEGAL, currentChar, line, column, position);
                errorHandler.unknownToken(tkn);
                yield tkn;
            }
        };
        readChar();
        return token;
    }

    private String readIdentifier() {
        var position = this.position;

        while (true) {
            peekChar();

            if (isNumber(this.currentChar)
                    || isLetter(this.currentChar))
                readChar();
            else break;
        }

        return input.substring(position, readPosition);
    }

    private static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isLetter(char c) {
        String regex = "[a-zA-Z]";
        String s = c + "";

        return s.matches(regex);
    }

    private String readNumber() {
        var position = this.position;

        while (true) {
            peekChar();
            if (isNumber(currentChar))
                readChar();
            else break;
        }

        return input.substring(position, readPosition);
    }

}
