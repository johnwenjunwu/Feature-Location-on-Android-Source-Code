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

    StringBuilder getMethodFeature() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MDirectory directory : subDirectories)
            stringBuilder.append(directory.getMethodFeature());
        for (MFile file : files)
            for (MClass mClass : file.classes)
                for (MMethod mMethod : mClass.methods)
                    stringBuilder.append(mMethod.methodFeature());

        return stringBuilder;
    }

    StringBuilder getMethodAndClassFeature() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MDirectory directory : subDirectories)
            stringBuilder.append(directory.getMethodAndClassFeature());
        for (MFile file : files)
            for (MClass mClass : file.classes) {
                stringBuilder.append(mClass.classFeature());
                for (MMethod mMethod : mClass.methods)
                    stringBuilder.append(mMethod.methodFeature());
            }
        return stringBuilder;
    }
    StringBuilder getClassFeature() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MDirectory directory : subDirectories)
            stringBuilder.append(directory.getClassFeature());
        for (MFile file : files)
            for (MClass mClass : file.classes)
                stringBuilder.append(mClass.classFeature());

        return stringBuilder;
    }
    StringBuilder getActivityFeature(Vector<MActivity> activities) {
        StringBuilder stringBuilder = new StringBuilder();
        for (MDirectory directory : subDirectories)
            stringBuilder.append(directory.getActivityFeature(activities));
        for (MFile file : files)
            if(file.packageName.contains(".activity")) {
                String prefix = file.packageName.split("\\.activity", 2)[1];
                for (MClass mClass : file.classes) {
                    for (MActivity activity : activities) {
                        if (activity.name.equals(prefix + "." + mClass.className)) {
//                            System.out.println(activity.name + " " + prefix + "." + mClass.className);
                            stringBuilder.append(mClass.classFeature());
                            break;
                        }
                    }
                }
            }

        return stringBuilder;
    }
}

class MFile {
    String packageName;
    String fileName;
    Vector<MClass> classes;

    MFile(File file) {
        try {
            fileName = file.getName();
            classes = new Vector<>();
            CompilationUnit compilationUnit = JavaParser.parse(file);
            packageName = compilationUnit.getPackageDeclaration().get().getName().toString();
            compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).forEach(c -> classes.add(new MClass(c, this)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class MClass {
    String className;
    Vector<MMethod> methods;
    Vector<String> fields;
    MFile file;
    Vector<String> ids;

    MClass(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, MFile file) {
        this.file = file;
        fields = new Vector<>();
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
            }
        });
        methods = new Vector<>();
        classOrInterfaceDeclaration.getMethods().forEach(m -> {
            methods.add(new MMethod(m, file, this));
        });
    }

    String classFeature() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(file.packageName + "_" + file.fileName + " " + className);
        stringBuilder.append(" " + file.packageName.substring("com.fsck.k9".length()).replaceAll(divide, " "));
        stringBuilder.append(" " + file.fileName.split("\\.", 2)[0].replaceAll(divide, " "));

        fields.forEach(f -> stringBuilder.append(" " + f.replaceAll(divide, " ")));

        for (MMethod mMethod : methods) {
            stringBuilder.append(' ');
            stringBuilder.append(mMethod.methodName.replaceAll(divide, " "));
            mMethod.varibles.forEach(id -> stringBuilder.append(' ' + id.replaceAll(divide, " ")));
            mMethod.ids.forEach(id -> stringBuilder.append(' ' + id.replaceAll(divide, " ")));
        }
        stringBuilder.append('\n');
        return stringBuilder.toString();
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

    StringBuilder methodFeature() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(file.packageName + "_" + file.fileName + "_" + mClass.className + " "
                + methodName);

        stringBuilder.append(" " + methodName.replaceAll(divide, " "));
        varibles.forEach(var -> stringBuilder.append(' ' + var.replaceAll(divide, " ")));
        usedFilds.forEach(f -> stringBuilder.append(' ' + f.replaceAll(divide, " ")));
        ids.forEach(id -> stringBuilder.append(' ' + id.replaceAll(divide, " ")));

        stringBuilder.append('\n');
        return stringBuilder;
    }
}

public class JsonReadWrite {

    String project, source;
    JsonReadWrite(String project, String source) throws IOException {
        this.project = Files.list(Paths.get(project)).filter(p -> p.getFileName().toString().startsWith("k-9-"))
                .findFirst().get().toString();
        this.source = source;
        generateActivityFeature();
    }
    public static void main(String[] args) throws IOException {
//        GenerateOriginClassFeature();
//
//        for (int mini = 0; mini <= 100; ++mini) {
//            StringBuilder builder = new StringBuilder();
//            for (String line : Files.readAllLines(Paths.get(Main.source + "/feature/origin"))) {
//                int length = line.split(" +").length - 2;
//                if (length > mini)
//                    builder.append(line).append('\n');
//            }
//            Files.write(Paths.get(Main.source + "/feature/wordsMoreThan" + mini), builder.toString().getBytes());
//        }
//        generateActivityFeature();
    }

    private void generateActivityFeature() throws IOException {
        MDirectory dir = new MDirectory(new File(project + "/src/com/fsck/k9"));
        //System.out.println(dir.getMethodFeature().toString().split("\n").length);
        try (FileWriter writer = new FileWriter(source + "/feature/activity")) {
            writer.write(dir.getActivityFeature(MActivity.getActivities(project + "/AndroidManifest.xml")).toString());
            //gson.toJson(dir, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void GenerateOriginClassFeature() throws IOException {
        MDirectory dir = new MDirectory(new File(project + "/src/com/fsck/k9"));
        //System.out.println(dir.getMethodFeature().toString().split("\n").length);
        try (FileWriter writer = new FileWriter(source + "/feature/origin")) {
            writer.write(dir.getMethodAndClassFeature().toString());
            //gson.toJson(dir, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
