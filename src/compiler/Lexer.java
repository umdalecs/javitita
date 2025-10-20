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
      "false", TokenType.FALSE);

  public static TokenType lookupIdent(String indentifier) {
    var kw = keywords.get(indentifier);
    if (kw != null) {
      return kw;
    }

    return TokenType.Identifier;
  }

  private String input;
  private int position, readPosition;
  private char currentChar;

  public Lexer(String input) {
    this.input = input;
    this.position = this.readPosition = 0;

    this.readChar();
  }

  private void readChar() {
    if (this.readPosition >= input.length())
      this.currentChar = 0;
    else
      this.currentChar = this.input.charAt(this.readPosition);

    this.position = this.readPosition++;
  }

  public Token nextToken() throws LexicalException {
    Token token = switch (this.currentChar) {
      case '=' -> new Token(TokenType.ASSIGN, this.currentChar);
      case '<' -> new Token(TokenType.LOWERT, this.currentChar);
      case '+' -> new Token(TokenType.PLUS, this.currentChar);
      case '-' -> new Token(TokenType.MINUS, this.currentChar);
      case '{' -> new Token(TokenType.LBrace, this.currentChar);
      case '}' -> new Token(TokenType.RBrace, this.currentChar);
      case '(' -> new Token(TokenType.LParen, this.currentChar);
      case ')' -> new Token(TokenType.RParen, this.currentChar);
      case 0 -> new Token(TokenType.EOF, "");
      default -> {
        if (Character.isAlphabetic(this.currentChar)) {
          var identifier = readIdentifier();
          yield new Token(lookupIdent(identifier), identifier);
        } else if (Character.isDigit(this.currentChar)) {
          yield new Token(TokenType.INTEGER, readNumber());
        } 
        throw new LexicalException(
          "Illegal token", 
          new Token(TokenType.ILLEGAL, this.currentChar)
          );
      }
    }
    ;

    this.readChar();

    return token;
  }

  private String readIdentifier() {
    var position = this.position;

    while (Character.isAlphabetic(this.currentChar) ||
        Character.isDigit(this.currentChar)) {
      this.readChar();
    }

    return input.substring(position, this.position);
  }

  private String readNumber() {
    var position = this.position;

    while (Character.isDigit(this.currentChar)) {
      this.readChar();
    }

    return input.substring(position, this.position);
  }

}
