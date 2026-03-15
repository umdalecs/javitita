package com.umdalecs.javitita.compiler.lexer;

public enum TokenType {
  EOF,
  ILLEGAL,

  SEMICOLON,

  // Identifiers, Literals
  IDENTIFIER,
  INTEGER_LITERAL,

  // Operators
  ASSIGN, MULTI,
  LOWER_THAN,
  PLUS,
  MINUS,

  // Delimiters
  LPAREN, RPAREN,
  LBRACE, RBRACE,

  // Keywords
  WHILE, BOOLEAN_TYPE, INTEGER_TYPE, TRUE_LITERAL, FALSE_LITERAL, PRINT_STATEMENT, FN;

  public String tokenName() {
    return switch (this) {
      case WHILE, BOOLEAN_TYPE, INTEGER_TYPE,
           TRUE_LITERAL, FALSE_LITERAL,
           PRINT_STATEMENT ->
        "KEYWORD";
      default -> name();
    };
  }
}
