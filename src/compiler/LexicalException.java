package compiler;

public class LexicalException extends Exception {
  private Token token;

  public LexicalException(String message, Token token) {
    super("message");
    this.token = token;
  }

  public Token getToken() {
    return token;
  }
}
