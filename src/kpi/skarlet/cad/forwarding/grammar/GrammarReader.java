package kpi.skarlet.cad.forwarding.grammar;

import kpi.skarlet.cad.forwarding.constants.GrammarConstants;
import kpi.skarlet.cad.forwarding.constants.GrammarConstantsXML;
import kpi.skarlet.cad.forwarding.constants.WordType;
import kpi.skarlet.cad.forwarding.exceptions.AttributeNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class GrammarReader {
    private String path = "res/grammar.xml";
    private GrammarConstants GC;
    private GrammarConstantsXML GCX;
//    private Map<String, Rule> rules;
    private GrammarMap rules;

    public static void main(String[] args) {
        GrammarReader gr = new GrammarReader("res/grammar.xml");
        System.out.println(gr.rules);
    }

    public GrammarReader(String path) {
        this.path = path;
        this.rules = this.ruleMap();
    }

    public GrammarMap getRules() {
        return rules;
    }

    private GrammarMap ruleMap() {
        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse(path);

            // Получаем корневой элемент
            Node root = document.getDocumentElement();

            rules = new GrammarMap();

            NodeList ruleList = root.getChildNodes();
            // -> rules
            for (int i = 0; i < ruleList.getLength(); i++) {
                Node rule = ruleList.item(i);
                // -> state
                if (nodeCheck(rule, GCX.RULE.str())) {
                    handleRule(rule);
                }
            }

            return rules;

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

        return null;
    }

    private void handleRule(Node rule) throws IOException {
        String ruleName = checkForAttrSafe(rule, GCX.NAME.str());
//        rules.put(ruleName, new Rule(ruleName));
        rules.add(new Rule(ruleName));

        NodeList rightSides = rule.getChildNodes();
        // -> подтеги rule
        for (int j = 0; j < rightSides.getLength(); j++) {
            Node rightSide = rightSides.item(j);
            // -> right side
            if (nodeCheck(rightSide, GCX.VALUES.str())) {
                RightSide rs = handleRightSide(rightSide);
                rules.get(ruleName).addRightSide(rs);
            } // end right side
        }
    }

    private RightSide handleRightSide(Node rightSide) throws AttributeNotFoundException {
        RightSide rs = new RightSide();
        NodeList words = rightSide.getChildNodes();
        for (int k = 0; k < words.getLength(); k++) {
            Node word = words.item(k);
            // -> word
            if (nodeCheck(word, GCX.VAL.str())) {
                Word w = handleWord(word);
                rs.addWord(w);
            } // end word
        }
        return rs;
    }

    private Word handleWord(Node word) throws AttributeNotFoundException {
        WordType wordType = WordType.getWordType(checkForAttrSafe(word, GCX.TYPE.str()));
        String wordStr = getNodeContext(word);
        Word w = new Word(wordStr, wordType);
        return w;
    }

    private String checkForAttrSafe(Node tagNode, String attr) throws AttributeNotFoundException {
        Node attribute = tagNode.getAttributes().getNamedItem(attr);
        if (attribute != null) {
            return attribute.getTextContent();
        } else {
            throw new AttributeNotFoundException(tagNode.getNodeName(), attr);
        }
    }

    private String checkForAttribute(Node tagNode, String attr) {
        Node attribute = tagNode.getAttributes().getNamedItem(attr);
        if (attribute != null) {
            return attribute.getTextContent();
        } else {
            return null;
        }
    }

    private boolean nodeCheck(Node node, String field) {
        return node.getNodeName().equals(field);
    }

    private String getNodeContext(Node node) {
        Node text = node.getChildNodes().item(0);
        return (text != null) ? text.getTextContent() : null;
    }
}