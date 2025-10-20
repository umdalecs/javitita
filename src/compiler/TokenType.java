package compiler;

public enum TokenType {
  EOF,
  ILLEGAL,

  // Identifiers, Literals
  Identifier,
  Integer,

  // Operators
  ASSIGN,
  LOWERT,
  PLUS,
  MINUS,

  // Delimiters
  LParen, RParen, 
  LBrace, RBrace,

  // Keywords
  WHILE, BOOLEAN, INTEGER, TRUE, FALSE, PRINTSTATEMENT, CLASS
}

