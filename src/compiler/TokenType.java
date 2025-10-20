package compiler;

public enum TokenType {
  EOF,
  ILLEGAL,

  SEMI,

  // Identifiers, Literals
  IDENTIFIER,
  Integer,  

  // Operators
  ASSIGN,
  LOWERT,
  PLUS,
  MINUS,

  // Delimiters
  LPAREN, RPAREN, 
  LBRACE, RBRACE,

  // Keywords
  WHILE, BOOLEAN, INTEGER, TRUE, FALSE, PRINTSTATEMENT, CLASS
}

