package cc.mallet.examples;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

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
    static int noneMethodClass;
    static int hasMethodClass;
    static Map<Integer, Integer> wordSum;
    static String divide = "(?<=\\p{Ll})(?=\\p{Lu})|(?=\\p{Lu}\\p{Ll})";

    MDirectory(File dir) {
        directoryName = dir.getName();
        subDirectories = new Vector<>();
        files = new Vector<>();
        wordSum = new TreeMap<>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                subDirectories.add(new MDirectory(file));
            else if (file.isFile() && file.getName().contains(".java"))
                files.add(new MFile(file));
        }
    }

    StringBuilder jkh() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MDirectory directory : subDirectories)
            stringBuilder.append(directory.jkh());
        for (MFile file : files)
            for (MClass mClass : file.classes)
                for (MMethod mMethod : mClass.methods) {
                    stringBuilder.append(file.packageName + "." + mClass.className + "." + mMethod.methodName + '\n');
                }
        return stringBuilder;
    }

    StringBuilder getClassFeature() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MDirectory directory : subDirectories)
            stringBuilder.append(directory.getClassFeature());
        for (MFile file : files)
            for (MClass mClass : file.classes) {
//                if (mClass.methods.size() == 1) {
//                    noneMethodClass++;
//                    System.out.println(mClass.methods.get(0).methodName.split("(?=\\p{Lu})").length);
//                }
//                else
//                    hasMethodClass++;

                int word = 0;
                for (MMethod mMethod : mClass.methods)
                    word += mMethod.methodName.split(divide).length;

//                Integer in = wordSum.get(word);
//                if(in == null)
//                    wordSum.put(word, 1);
//                else
//                    wordSum.put(word, in + 1);
//                if(word <= mini) {
//                    noneMethodClass++;
//                    continue;
//                }
//                else
//                    hasMethodClass++;

                stringBuilder.append(word + " " + file.packageName + "_" + file.fileName + " " + mClass.className + " ");
                stringBuilder.append(file.packageName.substring("com.fsck.k9".length()).replace(".", " "));
                stringBuilder.append(" " + file.fileName.split("\\.", 2)[0]
                        .replaceAll(divide, " "));
//                stringBuilder.append(String.join(" ", mMethod.methodName.split("(?<=\\p{Ll})(?=\\p{Lu})|(?=\\p{Lu}\\p{Ll})")) + " ");

                for (MMethod mMethod : mClass.methods) {
                    //System.out.print(mMethod.methodName + " ");
                    stringBuilder.append(' ');
                    stringBuilder.append(mMethod.methodName.replaceAll(divide, " "));
                }
                //System.out.println();
                stringBuilder.append('\n');
            }
        return stringBuilder;
    }

    StringBuilder getPackageFeatureDepth1() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MDirectory directory : subDirectories)
            stringBuilder.append(directory.getPackageFeatureDepth1());
        for (MFile file : files)
            for (MClass mClass : file.classes) {
                stringBuilder.append(file.packageName + "_" + file.fileName + " " + mClass.className + " ");
                for (MMethod mMethod : mClass.methods) {
                    stringBuilder.append(String.join(" ", mMethod.methodName.split("(?<=\\p{Ll})(?=\\p{Lu})|(?=\\p{Lu}\\p{Ll})")) + " ");
                }
                stringBuilder.append('\n');
            }
        return stringBuilder;
    }
    StringBuilder getMethodFeature() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MDirectory directory : subDirectories)
            stringBuilder.append(directory.getMethodFeature());
        for (MFile file : files)
            for (MClass mClass : file.classes) {
                stringBuilder.append(file.packageName + "_" + file.fileName + " " + mClass.className + " ");
                for (MMethod mMethod : mClass.methods) {
                    stringBuilder.append(String.join(" ", mMethod.methodName.split("(?<=\\p{Ll})(?=\\p{Lu})|(?=\\p{Lu}\\p{Ll})")) + " ");
                }
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
            packageName = compilationUnit.getPackage().get().getPackageName();
            compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).forEach(c -> classes.add(new MClass(c)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class MClass {
    String className;
    Vector<MMethod> methods;

    MClass(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        className = classOrInterfaceDeclaration.getName().getIdentifier();
        methods = new Vector<>();
        classOrInterfaceDeclaration.getMethods().forEach(m -> {
            methods.add(new MMethod(m));
        });
    }
}

class MMethod {
    String methodName;
    Vector<String> parameters;

    MMethod(MethodDeclaration methodDeclaration) {
        methodName = methodDeclaration.getName().getIdentifier();
        parameters = new Vector<>();
        methodDeclaration.getParameters().
                forEach(p -> parameters.add(p.getName().getIdentifier()));
//        System.out.println((methodDeclaration.getJavaDoc()));
//        if(methodDeclaration.getComment() != null) {
//            System.out.println("methodName:" + methodName);
//            System.out.println("comment:" + (methodDeclaration.getComment()));
//        }


    }
}

public class JsonReadWrite {


    public static void main(String[] args) throws IOException {
        GenerateOriginClassFeature();

        for(int mini = 0; mini <= 100; ++ mini) {
            StringBuilder builder = new StringBuilder();
            for(String line: Files.readAllLines(Paths.get(Main.source + "/feature/origin"))) {
                String[] parts = line.split(" ", 2);
                if (Integer.parseInt(parts[0]) >= mini)
                    builder.append(parts[1] + '\n');
            }
            Files.write(Paths.get(Main.source + "/feature/wordsMoreThan" + mini), builder.toString().getBytes());
        }
    }

    private static void GenerateOriginClassFeature() {
        MDirectory dir = new MDirectory(new File( "/Users/wuwenjun/Documents/study/features/Implement SSL file-based session caching/k-9-43c38a047feedda4720af5bfbc188a33f8dfaced/src/com/fsck/k9"));
        try (FileWriter writer = new FileWriter(Main.source + "/feature/origin")) {
            writer.write(dir.getClassFeature().toString());
            //gson.toJson(dir, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
