package cc.mallet.examples;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class MActivity {
    String name;

    MActivity(String n) {
        name = n;
    }
    public String toString() {
        return name;
    }
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String s = "/Users/wuwenjun/Documents/study/features/f1/k-9-a495627d72990f0ce6cb795d5e2d9dae3df523fe/AndroidManifest.xml";
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(s));
        doc.getDocumentElement().normalize();
        NodeList list = doc.getElementsByTagName("activity");
        Vector<MActivity> activities = new Vector<>();
        for (int i = 0; i < list.getLength(); ++i) {
            String name = list.item(i).getAttributes().getNamedItem("android:name").getTextContent().split("\\.activity\\.", 2)[1];
            activities.add(new MActivity(name));
        }
        System.out.println(activities);
    }

    public static Vector<MActivity> getActivities(String s) {
        Vector<MActivity> activities = new Vector<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(s));
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName("activity");
            for (int i = 0; i < list.getLength(); ++i) {
                String name = list.item(i).getAttributes().getNamedItem("android:name").getTextContent().split("\\.activity", 2)[1];
                activities.add(new MActivity(name));
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return activities;
    }
}
