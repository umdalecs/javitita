package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import compiler.Lexer;
import compiler.LexicalException;
import compiler.Token;
import compiler.TokenType;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LexerWindow extends JFrame {
  private JTextArea codigoArea;
  private JTextArea erroresArea;
  private DefaultTableModel lexemModel;
  private DefaultTableModel symbolModel;

  private Set<String> identifiers;

  public LexerWindow() {
    setTitle("Análisis Léxico");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setSize(1920, 1080);
    setLayout(new BorderLayout());

    var centroPanel = new JPanel();
    centroPanel.setLayout(new GridLayout(2, 2, 5, 5));

    centroPanel.add(crearPanelCodigo());
    centroPanel.add(crearPanelErrores());
    centroPanel.add(crearPanelLexemas());
    centroPanel.add(crearPanelTablaSimbolos());

    add(centroPanel, BorderLayout.CENTER);

    var surPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    var analisisButton = new JButton("Análisis Léxico");

    analisisButton.addActionListener((_) -> {
      this.identifiers = new HashSet<>();

      var input = codigoArea.getText();

      var lexer = new Lexer(input);
      Token token;

      lexemModel.setRowCount(0);
      symbolModel.setRowCount(0);

      do {
        try {
          token = lexer.nextToken();

          switch(token.getType()) {
            case ASSIGN, LOWERT,
              PLUS, MINUS,LPAREN,
              RPAREN,LBRACE,RBRACE,
              WHILE,BOOLEAN,INTEGER, SEMI,
              TRUE,FALSE,PRINTSTATEMENT, CLASS -> {
              Object[] row = {token.getLiteral(), token.getType().name()};
              lexemModel.addRow(row);

              SwingUtilities.invokeLater(() -> {
                lexemModel.addRow(row);
              });
            }
            case IDENTIFIER -> {
              Object[] lexemRow = {token.getLiteral(), token.getType().name()};
              lexemModel.addRow(lexemRow);

              SwingUtilities.invokeLater(() -> {
                lexemModel.addRow(lexemRow);
              });

              if (identifiers.add(token.getLiteral())){
                Object[] symbolRow = {token.getLiteral()};

                SwingUtilities.invokeLater(() -> {
                  symbolModel.addRow(symbolRow);
                });
              } 

            }
            default -> {}
          }
        } catch (LexicalException e) {
          String error = String.format("""
            %s: %s `%s`
          """, e.getMessage(), e.getToken().getType().name(), e.getToken().getLiteral());
          this.erroresArea.setText(error);
          break;
        }
      } while (token.getType() != TokenType.EOF);
    });

    surPanel.add(analisisButton);

    add(surPanel, BorderLayout.SOUTH);
    setLocationRelativeTo(null);
  }

  private JPanel crearPanelCodigo() {
    var panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Editor de Código (Zona de Entrada)"));

    this.codigoArea = new JTextArea();
    this.codigoArea.setText("""
        class Test {
          int x ;
          x = 5 ;
            while ( x < 10 ) {
              println( x ) ;
              x = x + 1 ;
            }
        } 
        """);
    var scrollPane = new JScrollPane(codigoArea);
    panel.add(scrollPane, BorderLayout.CENTER);
    return panel;
  }

  private JPanel crearPanelErrores() {
    var panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Zona de Errores Léxicos"));

    this.erroresArea = new JTextArea();
    erroresArea.setEditable(false);
    var scrollPane = new JScrollPane(erroresArea);
    panel.add(scrollPane, BorderLayout.CENTER);
    return panel;
  }

  private JPanel crearPanelLexemas() {
    var panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Zona de Lexemas y Componentes Léxicos"));

    String[] columnas = {"Lexema", "Componente Léxico"};
    Object[][] datos = {};

    this.lexemModel = new DefaultTableModel(datos, columnas);
    var lexemasTable = new JTable(lexemModel);
    var scrollPane = new JScrollPane(lexemasTable);

    panel.add(scrollPane, BorderLayout.CENTER);
    return panel;
  }

  private JPanel crearPanelTablaSimbolos() {
    var panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Tabla de Símbolos"));

    String[] columnas = {"Identificador", "Tipo", "Valor", "Posición"};
    Object[][] datos = {};

    this.symbolModel = new DefaultTableModel(datos, columnas);
    var simbolosTable = new JTable(symbolModel);
    var scrollPane = new JScrollPane(simbolosTable);

    panel.add(scrollPane, BorderLayout.CENTER);
    return panel;
  }
}