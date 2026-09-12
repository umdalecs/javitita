# Javitita - compiler and GUI

Proyecto de Lenguajes y Autómatas II

## Gramática

```
Program ::= fn main() { Statements }
Statements ::= Statement*
Statement ::= while (Expression) { Statements }
    | println(Expression);
    | Identifier = Expression;
    | VarDeclaration;
Type ::= boolean | int
VarDeclaration ::= Type Identifier
Expression ::= (Identifier | Integer) (< | + | - | *) (Identifier | Integer)
    | true | false | Identifier | Integer
Identifier ::= Letter [Letter | Digit]
Integer ::= Digit+
Letter ::= [a-Z][A-Z]
Digit ::= [0-9]
Gramática BNF
G = {SI,P,NT,T}
SI = {PROGRAM}
P={10}
NT={PROGRAM,VARDECLARATION,TYPE,STATEMENT,EXPRESSION,IDENTIFIER,INTEGER,LETTER,DIGIT}
T={class, {, }, ;, boolean, int, void, while, (, ), println, =, <, +, -, _, true, false, a, b, …, z, A,B, …,Z, 0, 1, …, 9}
```

## Instrucciones intel utilizadas

```
### MOV

1000 10dw oorrmmm
- mov reg reg ->
- mov mem reg ->
+ disp
- mov reg mem ->
1100 011w oo000mmm disp data
- mov mem imm -> 
1011 wrrr data
- mov reg imm -> 

### CMP
- cmp reg reg -> 0011 10dw oorrrmmm
- cmp reg imm -> 1000 00sw oo111mmm data
- xor reg reg -> 0001 10dw oorrrmmm

### JUMPS
0000 1111 1000 cccc disp
- jl tag -> 1100
- jne tag -> 0101
- je tag -> 0100


- jmp tag -> 1110 1011 disp
- loop tag -> 1110 0010 disp

### ARITHMETIC
- div reg -> 1111 011w oo110mmm
- inc reg -> 1111 111w oo000mmm
- add reg imm -> 1000 00sw oo000mmm data
- add reg reg -> 0000 00dw oorrrmmm
- SUB reg reg -> 0001 01dw oorrrmmm
- MUL reg reg -> 1111 011w oo100mmm

### STACK
- push reg -> 0101 0rrr
- pop reg -> 0101 1rrr

### INTERRUPTS
- int imm -> 1100 1101 type
```
