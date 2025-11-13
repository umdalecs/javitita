package compiler;

public enum TokenType {
  EOF,
  ILLEGAL,

  SEMICOLON,

  // Identifiers, Literals
  IDENTIFIER,
  INTEGERLITERAL,

  // Operators
  ASSIGN,
  LOWERT,
  PLUS,
  MINUS,

  // Delimiters
  LPAREN, RPAREN,
  LBRACE, RBRACE,

  // Keywords
  WHILE, BOOLEAN, INTEGER, VOID, TRUE, FALSE, PRINTSTATEMENT, CLASS
}

