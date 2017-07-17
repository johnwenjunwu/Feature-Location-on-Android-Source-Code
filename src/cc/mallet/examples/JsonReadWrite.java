package cc.mallet.examples;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static cc.mallet.examples.MDirectory.divide;

class MDirectory {
    String directoryName;
    Vector<MFile> files;
    Vector<MDirectory> subDirectories;
    static String divide = "(?<=\\p{Ll})(?=\\p{Lu})|(?=\\p{Lu}\\p{Ll})|_|\\.";

    MDirectory(File dir) {
        directoryName = dir.getName();
        subDirectories = new Vector<>();
        files = new Vector<>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                subDirectories.add(new MDirectory(file));
            else if (file.isFile() && file.getName().contains(".java"))
                files.add(new MFile(file));
        }
    }

//    StringBuilder getMethodFeature() {
//        StringBuilder stringBuilder = new StringBuilder();
//        for (MDirectory directory : subDirectories)
//            stringBuilder.append(directory.getMethodFeature());
//        for (MFile file : files)
//            for (MClass mClass : file.classes)
//                for (MMethod mMethod : mClass.methods)
//                    stringBuilder.append(mMethod.methodFeature());
//
//        return stringBuilder;
//    }

//    StringBuilder getMethodAndClassFeature() {
//        StringBuilder stringBuilder = new StringBuilder();
//        for (MDirectory directory : subDirectories)
//            stringBuilder.append(directory.getMethodAndClassFeature());
//        for (MFile file : files)
//            for (MClass mClass : file.classes) {
//                stringBuilder.append(mClass.classFeature());
//                for (MMethod mMethod : mClass.methods)
//                    stringBuilder.append(mMethod.methodFeature());
//            }
//        return stringBuilder;
//    }
//    StringBuilder getClassFeature() {
//        StringBuilder stringBuilder = new StringBuilder();
//        for (MDirectory directory : subDirectories)
//            stringBuilder.append(directory.getClassFeature());
//        for (MFile file : files)
//            for (MClass mClass : file.classes)
//                stringBuilder.append(mClass.classFeature());
//
//        return stringBuilder;
//    }

    StringBuilder getFileFeature(boolean not, boolean R) {
        StringBuilder stringBuilder = new StringBuilder();
        for (MDirectory directory : subDirectories)
            stringBuilder.append(directory.getFileFeature(not, R));
        for (MFile file : files)
            stringBuilder.append(file.fileFeature(not, R));

        return stringBuilder;
    }

}

class MFile {
    String packageName;
    String fileName;
    String name;
    Vector<MClass> classes;

    MFile(File file) {
        try {
            fileName = file.getName();
            classes = new Vector<>();
            CompilationUnit compilationUnit = JavaParser.parse(file);
            packageName = compilationUnit.getPackageDeclaration().get().getName().toString();
            compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).forEach(c -> classes.add(new MClass(c, this)));
            name = (packageName + "." + fileName).replace("com.owncloud.android.", "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    String fileFeature(boolean not, boolean R) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(name).append(' ').append(name.replace(".java", "").replaceAll(divide, " "));

        classes.forEach(c -> {
            c.fields.forEach(f -> stringBuilder.append(" ").append(f.replaceAll(divide, " ")));
            if (not)
                c.notFields.forEach(f -> stringBuilder.append(" ").append(f.replaceAll(divide, " ")));

            c.methods.forEach(m -> {
                stringBuilder.append(' ').append(m.methodName.replaceAll(divide, " "));
                m.varibles.forEach(id -> stringBuilder.append(' ').append(id.replaceAll(divide, " ")));
                if (R)
                    m.ids.forEach(id -> stringBuilder.append(' ').append(id.replaceAll(divide, " ")));
            });
        });

        String s = stringBuilder.toString().replaceAll("\\s+", " ");
        int sum = s.split(" ").length - 1;
        return sum + " " + s + '\n';
    }

}

class MClass {
    String className;
    Vector<MMethod> methods;
    Vector<String> fields;
    Vector<String> notFields;
    MFile file;
//    Vector<String> ids;

    MClass(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, MFile file) {
        this.file = file;
        fields = new Vector<>();
        notFields = new Vector<>();
        className = classOrInterfaceDeclaration.getName().getIdentifier();
        classOrInterfaceDeclaration.getFields().forEach(f -> {
            boolean feature = true;
            for (Modifier m : f.getModifiers()) {
                if (m.toString().equals("STATIC") || m.toString().equals("FINAL")) {
                    feature = false;
                    break;
                }
            }
            if (feature) {
                f.getVariables().forEach(v -> {
                    fields.add(v.getName().getIdentifier());
                });
            } else
                f.getVariables().forEach(v -> notFields.add(v.getName().getIdentifier()));
        });
        methods = new Vector<>();
        classOrInterfaceDeclaration.getMethods().forEach(m -> {
            methods.add(new MMethod(m, file, this));
        });
    }
}

class MMethod {
    String methodName;
    Vector<String> parameters;
    Vector<String> varibles;
    Vector<String> usedFilds;
    Vector<String> ids;
    MFile file;
    MClass mClass;

    MMethod(MethodDeclaration methodDeclaration, MFile file, MClass mClass) {
        this.file = file;
        this.mClass = mClass;
        methodName = methodDeclaration.getName().getIdentifier();
        parameters = new Vector<>();
        varibles = new Vector<>();
        usedFilds = new Vector<>();
        ids = new Vector<>();
        methodDeclaration.getParameters().
                forEach(p -> parameters.add(p.getName().getIdentifier()));
        if (methodDeclaration.getBody().isPresent()) {
            methodDeclaration.getBody().get().getNodesByType(VariableDeclarator.class)
                    .forEach(v -> varibles.add(v.getName().getIdentifier()));
            String body = methodDeclaration.getBody().get().toString();
            mClass.fields.forEach(f -> {
                if (body.contains(f))
                    usedFilds.add(f);
            });
            Matcher matcher = Pattern.compile("R\\.id\\.([\\p{L}_]+)").matcher(body);
            while (matcher.find())
                ids.add(matcher.group(1));
        }
    }
}

public class JsonReadWrite {

    public static String[] types = {"basic", "R"};
    String project, source;

    JsonReadWrite(String project, String source) throws IOException {
        this.project = Files.list(Paths.get(project)).filter(p -> p.getFileName().toString().startsWith("android-"))
                .findFirst().get().toString();
        this.source = source;
//        generateActivityFeature();
        GenerateFileClassFeature();
    }

    public static void main(String[] args) throws IOException {

    }


    void notR(boolean not, boolean R, String src) throws IOException {
        File f = new File(project + "/src/com/owncloud/android");
        File test = null;
        if (!f.exists()) {
            f = new File(project + "/k9mail/src/main/java/com/fsck/k9");
            test = new File(project + "/k9mail/src/test/java/com/fsck/k9");
        }
        MDirectory dir = new MDirectory(f);
        //System.out.println(dir.getMethodFeature().toString().split("\n").length);
        new File(src).mkdirs();
        try (FileWriter writer = new FileWriter(src + "/origin")) {
            writer.write(dir.getFileFeature(not, R).toString());
            if (test != null)
                writer.write(new MDirectory(test).getFileFeature(not, R).toString());
            //gson.toJson(dir, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int mini = 0; mini <= 20; ++mini) {
            StringBuilder builder = new StringBuilder();
            for (String line : Files.readAllLines(Paths.get(src + "/origin"))) {
                if (Integer.parseInt(line.split(" ", 2)[0]) >= mini)
                    builder.append(line).append('\n');
            }
            Files.write(Paths.get(src + "/wordsMoreThan" + mini), builder.toString().getBytes());
        }
    }

    private void GenerateFileClassFeature() throws IOException {
        notR(true, false, source + "/basic/feature");
//        notR(false, false, source + "/not/feature");
        notR(true, true, source + "/R/feature");
//        notR(false, true, source + "/notR/feature");


    }
}
