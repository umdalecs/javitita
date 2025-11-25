package compiler;

public record Token(TokenType type, String literal, int position) {
    public Token(TokenType type, char c, int position) {
        this(type, c + "", position);
    }
}
