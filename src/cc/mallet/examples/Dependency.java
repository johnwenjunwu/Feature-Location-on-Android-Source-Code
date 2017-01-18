package cc.mallet.examples;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Dependency {
    String name;
    Vector<String> calls, usages;
    Dependency(String name, Vector<String> calls) {
        this.name = name;
        this.calls = calls;
        usages = new Vector<>();
    }

    @Override
    public String toString() {
        if (calls.size() > 0)
            return calls.size() + " " + name + " " + name + " " + String.join(" ", calls) + "\n";
        else
            return calls.size() + " " + name + " " + name + String.join(" ", calls) + "\n";
    }

    static String prefix = "$PROJECT_DIR$/src/com/fsck/k9/";

    public static void generateDependencyFeature(String source) throws ParserConfigurationException, IOException, SAXException {
        String s = "/Users/wuwenjun/Downloads/a.xml";

        StringBuilder builder = new StringBuilder();
        Map<String, Dependency> map = getDependencies(s);
        map.forEach((k, v)->builder.append(v));
        Files.write(Paths.get(source + "/feature/dependency"), builder.toString().getBytes());
    }



    static Map<String, Dependency> getDependencies(String s) throws ParserConfigurationException, IOException, SAXException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(s));
        doc.getDocumentElement().normalize();
        NodeList list = doc.getElementsByTagName("file");
        int sum = 0;
        Map<String, Dependency> map = new HashMap<>();
        for (int i = 0; i < list.getLength(); ++i) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)list.item(i);
                String path = element.getAttribute("path");
                if(!path.startsWith(prefix))
                    continue;
                String name = path.replace(prefix, "").replace('/', '.');

                NodeList nodeList = element.getElementsByTagName("dependency");
                Vector<String> calls = new Vector<>();
                for (int j = 0; j < nodeList.getLength(); ++j) {
                    if (nodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        String file = ((Element)nodeList.item(j)).getAttribute("path");
                        if (!file.startsWith(prefix))
                            continue;
                        calls.add(file.replace(prefix, "").replace('/', '.'));
                    }
                }
                map.put(name, new Dependency(name, calls));
            }
        }
        map.forEach((k, d) -> {
            d.calls.forEach(c -> map.get(c).usages.add(c));
        });
        return map;
    }
}
