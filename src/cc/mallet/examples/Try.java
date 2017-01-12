package cc.mallet.examples;
import java.io.File;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import cc.mallet.topics.ParallelTopicModel;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import org.atteo.evo.inflector.English;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Try {
    public static void main(String[] args){

        try {
            System.out.println(English.plural("word", 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

