package kpi.skarlet.cad.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import kpi.skarlet.cad.forwarding.FTCreator;
import kpi.skarlet.cad.forwarding.analyser.FTAnalyser;
import kpi.skarlet.cad.forwarding.analyser.TableElem;
import kpi.skarlet.cad.forwarding.exceptions.TranslatorException;
import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.VariableType;
import kpi.skarlet.cad.lexer.lexemes.Constant;
import kpi.skarlet.cad.lexer.lexemes.Identifier;
import kpi.skarlet.cad.lexer.lexemes.Label;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;
import kpi.skarlet.cad.poliz.PolizCalculator;
import kpi.skarlet.cad.poliz.entity.PolizTableElem;
import kpi.skarlet.cad.poliz.performer.Performer;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Controller {

    private LexicalAnalyser lexer;
    private ObservableList<TranslatorException> errors;
    private boolean lexerPass;
    private boolean synzerPass;
    private FTCreator creator;
    private FTAnalyser analyser;
    private Performer codePerformer;

    @FXML
    public MenuItem menuShowGrammar;

    @FXML
    private TextArea textArea;

    @FXML
    private TableView<Lexeme> Lexemes;

    @FXML
    private TableColumn<Lexeme, Integer> lexemesId;

    @FXML
    private TableColumn<Lexeme, Integer> lexemesLine;

    @FXML
    private TableColumn<Lexeme, String> lexemesLexeme;

    @FXML
    private TableColumn<Lexeme, Integer> lexemesCodeLex;

    @FXML
    private TableColumn<Lexeme, Integer> lexemesCodeIdn;

    @FXML
    private TableColumn<Lexeme, Integer> lexemesCodeCon;

    @FXML
    private TableColumn<Lexeme, Integer> lexemesCodeLbl;

    @FXML
    private TableView<Identifier> Identifiers;

    @FXML
    private TableColumn<Identifier, Integer> idntId;

    @FXML
    private TableColumn<Identifier, String> idntName;

    @FXML
    private TableColumn<Identifier, VariableType> idntType;

    @FXML
    private TableView<Constant> Constants;

    @FXML
    private TableColumn<Constant, Integer> contId;

    @FXML
    private TableColumn<Constant, VariableType> contType;

    @FXML
    private TableColumn<Constant, String> contValue;

    @FXML
    private TableView<Label> Labels;

    @FXML
    private TableColumn<Label, Integer> lbltId;

    @FXML
    private TableColumn<Label, String> lbltName;

    @FXML
    private TableView<TranslatorException> Errors;

    @FXML
    private TableColumn<TranslatorException, Integer> errorId;

    @FXML
    private TableColumn<TranslatorException, String> errorMsg;

    @FXML
    private TableView<TableElem> parseTable;

    @FXML
    private TableColumn<TableElem, Integer> ptId;

    @FXML
    private TableColumn<TableElem, String> ptStack;

    @FXML
    private TableColumn<TableElem, String> ptRatio;

    @FXML
    private TableColumn<TableElem, String> ptInput;

    @FXML
    private TableColumn<TableElem, String> ptBasis;

    @FXML
    private TableColumn<TableElem, String> ptPoliz;

    @FXML
    private TableView<TableElem> polizParsed;

    @FXML
    private TableColumn<TableElem, Integer> pptId;

    @FXML
    private TableColumn<TableElem, String> pptPoliz;

    @FXML
    private TableView<PolizTableElem> polizCalculation;

    @FXML
    private TableColumn<PolizTableElem, Integer> pctId;

    @FXML
    private TableColumn<PolizTableElem, String> pctStack;

    @FXML
    private TableColumn<PolizTableElem, String> pctPoliz;

    @FXML
    private TextArea rtInputArea;

    @FXML
    private TextArea rtOutputArea;

    @FXML
    private TextArea rtbInputArea;

    @FXML
    private TextArea rtbOutputArea;

    @FXML
    void initialize() {
        initFields();
        initLexerTables();
        initParseTable();
        initParsedTable();
        initPolizTable();
    }

    private void initFields() {
        lexer = new LexicalAnalyser();
        creator = new FTCreator();
        analyser = new FTAnalyser(lexer, creator);
        codePerformer = new Performer();
    }

    private void initLexerTables() {
        lexemesId.setCellValueFactory(new PropertyValueFactory<>("id"));
        lexemesLine.setCellValueFactory(new PropertyValueFactory<>("line"));
        lexemesLexeme.setCellValueFactory(new PropertyValueFactory<>("name"));
        lexemesCodeLex.setCellValueFactory(new PropertyValueFactory<>("code"));
        lexemesCodeIdn.setCellValueFactory(new PropertyValueFactory<>("spCodeIdn"));
        lexemesCodeCon.setCellValueFactory(new PropertyValueFactory<>("spCodeCon"));
        lexemesCodeLbl.setCellValueFactory(new PropertyValueFactory<>("spCodeLbl"));

        idntId.setCellValueFactory(new PropertyValueFactory<>("id"));
        idntName.setCellValueFactory(new PropertyValueFactory<>("name"));
        idntType.setCellValueFactory(new PropertyValueFactory<>("type"));

        contId.setCellValueFactory(new PropertyValueFactory<>("id"));
        contType.setCellValueFactory(new PropertyValueFactory<>("type"));
        contValue.setCellValueFactory(new PropertyValueFactory<>("name"));

        lbltId.setCellValueFactory(new PropertyValueFactory<>("id"));
        lbltName.setCellValueFactory(new PropertyValueFactory<>("name"));

        errorId.setCellValueFactory(new PropertyValueFactory<>("id"));
        errorMsg.setCellValueFactory(new PropertyValueFactory<>("message"));
    }

    private void initParseTable() {
        ptId.setCellValueFactory(new PropertyValueFactory<>("id"));
        ptStack.setCellValueFactory(new PropertyValueFactory<>("stack"));
        ptRatio.setCellValueFactory(new PropertyValueFactory<>("ratio"));
        ptInput.setCellValueFactory(new PropertyValueFactory<>("input"));
        ptBasis.setCellValueFactory(new PropertyValueFactory<>("basis"));
        ptPoliz.setCellValueFactory(new PropertyValueFactory<>("poliz"));
    }

    private void initParsedTable() {
        pptId.setCellValueFactory(new PropertyValueFactory<>("id"));
        pptPoliz.setCellValueFactory(new PropertyValueFactory<>("poliz"));
    }

    private void initPolizTable() {
        pctId.setCellValueFactory(new PropertyValueFactory<>("id"));
        pctStack.setCellValueFactory(new PropertyValueFactory<>("strStack"));
        pctPoliz.setCellValueFactory(new PropertyValueFactory<>("strPoliz"));
    }

    public void onOpenFileClick() {
        JFileChooser choosedFile = new JFileChooser("./res");
//                int ret = choosedFile.showDialog(null, "Открыть файл");
        int ret = choosedFile.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = choosedFile.getSelectedFile();
            textArea.setText(file.getName());
            textArea.setText(readFile(file, Charset.defaultCharset()));
        }
    }

    public void onSaveFileClick() {
        JFileChooser choosedFile = new JFileChooser("./");
//                int ret = choosedFile.showDialog(null, "Открыть файл");
        int ret = choosedFile.showSaveDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = choosedFile.getSelectedFile();
            try {
                FileUtils.writeStringToFile(file, textArea.getText());
                JOptionPane.showMessageDialog(null, "Successfully saved");
            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Saving file error");
            }
        }
    }

    public void onCloseFileClick() {
        textArea.clear();
    }

    public void onGrammarShowClick() {
        File file = new File(FTCreator.PATH_RESULT_GRAMMAR);
        textArea.setText(readFile(file, Charset.defaultCharset()));
    }

    public void onPolizGrammarShowClick() {
        JOptionPane.showMessageDialog(null, "This is deprecated feature and will be delete soon");
    }

    public void onParseTableShowClick() {
        ObservableList<TableElem> parseTable = FXCollections.observableArrayList(TableElem.getParseTable());
        this.parseTable.setItems(parseTable);
    }

    private void fillParsedTable() {
        ObservableList<TableElem> parsedTable = FXCollections.observableArrayList(TableElem.getParseTable());
        this.polizParsed.setItems(parsedTable);
    }

    public void onMatrixRelationShowClick() {
        FTCreator.writeSimpleGrammar(creator.getRules().toString(), null);
        creator.showHtml();
    }

    public void onBuildLexer() {
        clearTables();
        lexer.clear();
        lexer.run(textArea.getText());
        if (lexer.getExceptions().isEmpty()) {
            ObservableList<Lexeme> lexemes = FXCollections.observableArrayList(lexer.getLexemes());
            this.Lexemes.setItems(lexemes);
            ObservableList<Identifier> idns = FXCollections.observableArrayList(lexer.getIdentifiers());
            this.Identifiers.setItems(idns);
            ObservableList<Constant> cons = FXCollections.observableArrayList(lexer.getConstants());
            this.Constants.setItems(cons);
            ObservableList<Label> lbls = FXCollections.observableArrayList(lexer.getLabels());
            this.Labels.setItems(lbls);
            lexerPass = true;
            analyser.countLabels();
        } else {
            this.errors = FXCollections.observableArrayList(lexer.getExceptions());
            this.Errors.setItems(errors);
            lexerPass = false;
        }
    }

    public void onBuildSynzer() {
        this.onBuildLexer();
        if (lexer.getExceptions().isEmpty()) {
            analyser.clear();
            analyser.analyse();
            onParseTableShowClick();
            fillParsedTable();
            codePerformer.perform(this, analyser.getBuilder());
            synzerPass = true;
        } else {
            synzerPass = false;
            JOptionPane.showMessageDialog(null, "Lexical error!");
        }
    }

    public void onBuildPolizCalculate() {
        if (synzerPass) {
            PolizCalculator polizCalculator = new PolizCalculator(analyser.getPoliz());
            polizCalculator.calculate();
            ObservableList<PolizTableElem> parseTable = FXCollections.observableArrayList(polizCalculator.getTable());
            this.polizCalculation.setItems(parseTable);
        } else {
            JOptionPane.showMessageDialog(null, "Please run syntax analyser before calculating POLIZ");
        }
    }

    public void onBuildExecute() {
        // empty body now
        JOptionPane.showMessageDialog(null, "here will be result of program");
    }

    private void clearTables() {
        Lexemes.setItems(FXCollections.observableArrayList());
        Identifiers.setItems(FXCollections.observableArrayList());
        Constants.setItems(FXCollections.observableArrayList());
        Labels.setItems(FXCollections.observableArrayList());
        Errors.setItems(FXCollections.observableArrayList());
        parseTable.setItems(FXCollections.observableArrayList());
        TableElem.getParseTable().clear();
    }

    private static String readFile(File file, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
            return new String(encoded, encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "error";
    }

    public void appendOutput(String string) {
        StringBuilder builder = new StringBuilder(rtOutputArea.getText());
        builder.append(string);
        rtOutputArea.setText(builder.toString());
        rtbOutputArea.setText(builder.toString());
    }

}