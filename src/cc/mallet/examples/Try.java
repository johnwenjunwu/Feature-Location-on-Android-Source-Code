package cc.mallet.examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
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
    public static void main(String[] args) throws IOException {

//        Matcher matcher = Pattern.compile("R\\.id\\.([\\p{L}_]+)").matcher("super.onCreate(savedInstanceState);\n" +
//                "        setContentView(R.layout.account_setup_basics);\n" +
//                "        mEmailView = (EditText)findViewById(R.id.account_email);\n" +
//                "        mPasswordView = (EditText)findViewById(R.id.account_password);\n" +
//                "        mNextButton = (Button)findViewById(R.id.next);\n" +
//                "        mManualSetupButton = (Button)findViewById(R.id.manual_setup);\n" +
//                "        CheckBox showPassword = (CheckBox) findViewById(R.id.show_password);");
//        while (matcher.find())
//            System.out.println(matcher.group(1));

        System.out.println(Arrays.toString("jk.jkdAB_jkB".split("(?<=\\p{Ll})(?=\\p{Lu})|(?=\\p{Lu}\\p{Ll})|_|\\.")));
//        Arrays.stream(new File("try").listFiles()).forEach(f -> f.delete());
//        Vector<String> s = new Vector<>();
//        Vector<Path> paths = new Vector<>();
//        String[] sarry;
//        for (int i = 11; i > 7; --i)
//            for (int j = 8; j < 11; ++j)
//                for (int k = 8; k < 11; ++k) {
//                    Files.createFile(Paths.get("try/" + i + '_' + j + '_' + k));
//                    s.add(i + "_" + j + '_' + k);
//                }
//
//        Files.list(Paths.get("try")).sorted(new AlphanumComparator())
//                .forEach(System.out::println);
        //directoryStream.forEach(paths::add);
//        Collections.sort(paths, new AlphanumComparator());
        //new File()).listFiles()

//        Systemem.out.println(directoryStream);
//        for (File f: files)
//            System.out.println(f.getName());

    }
}

