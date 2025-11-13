package compiler;

public class Token {
  private TokenType type;
  private String literal;

  public Token() {}

  public Token(TokenType type, char c) {
    this.type = type;
    this.literal = c + "";
  }

  public Token(TokenType type, String literal) {
    this.type = type;
    this.literal = literal;
  }

  public TokenType getType() {
    return type;
  }

  public void setType(TokenType type) {
    this.type = type;
  }

  public String getLiteral() {
    return literal;
  }

  public boolean equals(Token obj) {
    return this.type.equals(obj.type) && this.literal.equals(obj.literal);
  }
}
