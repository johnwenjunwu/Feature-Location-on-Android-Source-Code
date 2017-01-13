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
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

class MDirectory {
    String directoryName;
    Vector<MFile> files;
    Vector<MDirectory> subDirectories;
    static String divide = "(?<=\\p{Ll})(?=\\p{Lu})|(?=\\p{Lu}\\p{Ll})";

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

    StringBuilder getClassFeature() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MDirectory directory : subDirectories)
            stringBuilder.append(directory.getClassFeature());
        for (MFile file : files)
            for (MClass mClass : file.classes) {
                stringBuilder.append(file.packageName + "_" + file.fileName + " " + mClass.className + " ");
                stringBuilder.append(file.packageName.substring("com.fsck.k9".length()).replace(".", " "));
                stringBuilder.append(" " + file.fileName.split("\\.", 2)[0]
                        .replaceAll(divide, " "));

                mClass.fields.forEach(f -> stringBuilder.append(" " + f));

                for (MMethod mMethod : mClass.methods) {
                    stringBuilder.append(' ');
                    stringBuilder.append(mMethod.methodName.replaceAll(divide, " "));
                    mMethod.ids.forEach(id -> stringBuilder.append(' ' + id));
                }
                //System.out.println();
                stringBuilder.append('\n');
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
            compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).forEach(c -> classes.add(new MClass(c)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class MClass {
    String className;
    Vector<MMethod> methods;
    Vector<String> fields;

    MClass(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
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
            methods.add(new MMethod(m));
        });
    }
}

class MMethod {
    String methodName;
    Vector<String> parameters;
    Vector<String> ids;

    MMethod(MethodDeclaration methodDeclaration) {
        methodName = methodDeclaration.getName().getIdentifier();
        parameters = new Vector<>();
        ids = new Vector<>();
        methodDeclaration.getParameters().
                forEach(p -> parameters.add(p.getName().getIdentifier()));
        if (methodDeclaration.getBody().isPresent())
            methodDeclaration.getBody().get().getNodesByType(VariableDeclarator.class).forEach(
                    v -> {
                        if (v.getInitializer().isPresent() && v.getInitializer().toString().contains("R.id.")) {
                            String id = v.getInitializer().toString().split("R\\.id\\.", 2)[1].split("\\)", 2)[0].replace("_", " ");
                            ids.add(id);
                            //System.out.println(id);
                        }
                    }
            );

    }
}

public class JsonReadWrite {


    public static void main(String[] args) throws IOException {
        GenerateOriginClassFeature();

        for(int mini = 0; mini <= 100; ++ mini) {
            StringBuilder builder = new StringBuilder();
            for(String line: Files.readAllLines(Paths.get(Main.source + "/feature/origin"))) {
                int length = line.split(" +").length - 2;
                if (length > mini)
                    builder.append(line).append('\n');
            }
            Files.write(Paths.get(Main.source + "/feature/wordsMoreThan" + mini), builder.toString().getBytes());
        }
    }

    private static void GenerateOriginClassFeature() throws IOException {

        MDirectory dir = new MDirectory(new File("/Users/wuwenjun/Documents/study/features/f5/k-9-cf228583bc393013941def0936d86fd636ff9257/src/com/fsck/k9"));
//        dir.getClassFeature();
        try (FileWriter writer = new FileWriter(Main.source + "/feature/origin")) {
            writer.write(dir.getClassFeature().toString());
            //gson.toJson(dir, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
