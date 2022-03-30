import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CBO extends VoidVisitorAdapter{
    // Weighted Methods per Class: The number of methods in a class

    final static String PATH = "C:\\Users\\GA\\Downloads\\CS451TestSystem\\taxi-company-later-stage";
    //final static String PATH = "C:\\Users\\GA\\Downloads\\test";

    // Main, get the path of the files and call the Method Modifier
    public static void main(String[] args) throws Exception {
        new MethodModifier().analyseFiles(new File(PATH));
    }

    private static class MethodModifier {
        // look at the location for files
        public void analyseFiles(final File folder) throws FileNotFoundException {

            // Create map to store class names and method calls in that class
            Map<String, Set<String>> allTheClasses = new HashMap<>();

            for(final File entry : folder.listFiles()){
                // if it's a directory, go into it
                if (entry.isDirectory()) {
                    analyseFiles(entry);
                } else {
                    // otherwise just read each file
                    if(entry.toString().contains(".java"))
                    {
                        Set<String> references = new HashSet<>();
                        final String[] className = {""};
                        // instantiate number of method declarations (i.e. complexity)
                        CompilationUnit compilationUnit;
                        try {
                            compilationUnit = StaticJavaParser.parse(entry);
                        }catch(ParseProblemException p){
                            continue;
                        }
                        // first, get the class name
                        compilationUnit.accept(new VoidVisitorAdapter<Void>(){
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, final Void arg) {
                                className[0] = n.getName().toString();
                                super.visit(n, arg);
                            }
                        }, null);

                        // deal with the constructor
                        for(ConstructorDeclaration cd : compilationUnit.findAll(ConstructorDeclaration.class)){
                            BlockStmt bod = cd.getBody();
                            CallableDeclaration.Signature sig = cd.getSignature();

                        }

                        for(VariableDeclarator ci : compilationUnit.findAll(VariableDeclarator.class)){
                            if(!ci.getType().isPrimitiveType()){
                                if(ci.getTypeAsString().contains("<")){
                                    String[] ci2 = ci.getTypeAsString().replace(">", "").split("<");
                                    for(String str : ci2){
                                        if(str.contains(",")){
                                            String[] ci3 = str.split(",");
                                            references.addAll(Arrays.asList(ci3));
                                        }
                                        else{
                                            references.add(str);
                                        }
                                    }
                                }
                                else {
                                    references.add(ci.getTypeAsString().replaceAll("\\[", "").replaceAll("\\]", ""));
                                }
                            }
                        }
                        // now add the file and the references to it to the map
                        allTheClasses.put(className[0], references);
                    }
                }
            }

            List<String> classesInProject = new ArrayList<>(allTheClasses.keySet());
            for (String className: classesInProject) {
                Set<String> nested = allTheClasses.get(className);
                nested.retainAll(classesInProject);
                for (String nest: nested) {
                    allTheClasses.get(nest).add(className);
                }
            }
            handle(allTheClasses);
        }


        // TODO: need to actually deal with this...
        public void handle(Map<String, Set<String>> classReferences){
            for (String className : classReferences.keySet()) {
                Integer classReferenceCount = 0;
                for (Set<String> classReferenceSet : classReferences.values()) {
                    for (String classReference : classReferenceSet) {
                        if(classReference.contains(className)) {
                            classReferenceCount++;
                        }
                    }
                }
                System.out.println(className + " complexity: " + classReferenceCount);
            }
        }
    }
}





