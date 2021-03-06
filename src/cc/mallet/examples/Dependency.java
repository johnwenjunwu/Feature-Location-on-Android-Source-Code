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
        String ret = (calls.size() + usages.size()) + " " + name + " " + name;
        if (calls.size() > 0)
            ret += " " + String.join(" ", calls);
        if (usages.size() > 0)
            ret += " " + String.join(" ", usages);
        return ret + "\n";
    }

    static String prefix = "$PROJECT_DIR$/src/com/owncloud/android/";
    static String p2 = "$PROJECT_DIR$/k9mail/src/main/java/com/fsck/k9/";

    public static void generateDependencyFeature(String source, String xml) throws ParserConfigurationException, IOException, SAXException {

        System.out.println(xml);
        StringBuilder builder = new StringBuilder();
        Map<String, Dependency> map = getDependencies(xml);
        map.forEach((k, v)->builder.append(v));
        new File(source + "/feature").mkdirs();
        Files.write(Paths.get(source + "/feature/origin"), builder.toString().getBytes());
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

                String name;
                if(path.startsWith(prefix)) {
                    name = path.replace(prefix, "").replace('/', '.');
                    System.out.println(name);
                }
                else if(path.startsWith(p2))
                    name = path.replace(p2, "").replace('/', '.');
                else
                    continue;

                NodeList nodeList = element.getElementsByTagName("dependency");
                Vector<String> calls = new Vector<>();
                for (int j = 0; j < nodeList.getLength(); ++j) {
                    if (nodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        String file = ((Element)nodeList.item(j)).getAttribute("path");
                        if (file.startsWith(prefix))
                            calls.add(file.replace(prefix, "").replace('/', '.'));
                        else if (file.startsWith(p2))
                            calls.add(file.replace(p2, "").replace('/', '.'));
                    }
                }
                map.put(name, new Dependency(name, calls));
            }
        }
        map.forEach((k, d) -> {
            d.calls.forEach(c -> map.get(c).usages.add(d.name));
        });
        return map;
    }
}
