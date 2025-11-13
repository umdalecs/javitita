package compiler;

import java.util.Map;

public class Lexer {
    public static final Map<String, TokenType> keywords = Map.of(
            "while", TokenType.WHILE,
            "println", TokenType.PRINTSTATEMENT,
            "class", TokenType.CLASS,
            "boolean", TokenType.BOOLEAN,
            "int", TokenType.INTEGER,
            "true", TokenType.TRUE,
            "void", TokenType.VOID,
            "false", TokenType.FALSE);

    public static TokenType lookupIdent(String identifier) {
        var kw = keywords.get(identifier);

        if (kw != null) return kw;

        return TokenType.IDENTIFIER;
    }

    private final String input;
    private int position, readPosition, line;
    private char currentChar;

    public Lexer(String input) {
        this.input = input;
        this.position = this.readPosition = 0;
        this.line = 1;

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
            if (this.currentChar == '\n') this.line++;
            this.readChar();
        }

        Token token = switch (this.currentChar) {
            case '=' -> new Token(TokenType.ASSIGN, this.currentChar);
            case '<' -> new Token(TokenType.LOWERT, this.currentChar);
            case '+' -> new Token(TokenType.PLUS, this.currentChar);
            case '-' -> new Token(TokenType.MINUS, this.currentChar);
            case '{' -> new Token(TokenType.LBRACE, this.currentChar);
            case '}' -> new Token(TokenType.RBRACE, this.currentChar);
            case '(' -> new Token(TokenType.LPAREN, this.currentChar);
            case ')' -> new Token(TokenType.RPAREN, this.currentChar);
            case ';' -> new Token(TokenType.SEMICOLON, this.currentChar);
            case 0 -> new Token(TokenType.EOF, "");
            default -> {
                if (Character.isAlphabetic(this.currentChar)) {
                    var identifier = readIdentifier();
                    yield new Token(lookupIdent(identifier), identifier);
                } else if (Character.isDigit(this.currentChar)) {
                    yield new Token(TokenType.INTEGERLITERAL, readNumber());
                }
                yield new Token(TokenType.ILLEGAL, "line " + this.line + " illegal token `" + this.currentChar + "`");
            }
        };

        this.readChar();

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
