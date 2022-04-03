import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.HashMap;

/** @noinspection rawtypes*/
public class LCOM extends VoidVisitorAdapter {
    // Lack of Cohesion of Methods - the degree to which methods and fields within a class are related to one another

    final static String PATH = "files/";

    // Main, get the path of the files and call the Method Modifier
    public static void main(String[] args) throws Exception {
        new MethodModifier().analyseFiles(new File(PATH));
    }

    private static class MethodModifier {
        public void analyseFiles(final File folder) throws FileNotFoundException {
            for(final File entry : Objects.requireNonNull(folder.listFiles())){
                // if it's a directory, go into it
                if (entry.isDirectory()) {
                    analyseFiles(entry);
                } else {
                    // otherwise just read each file
                    if(entry.toString().contains(".java"))
                    {
                        CompilationUnit compilationUnit;
                        try {
                            compilationUnit = StaticJavaParser.parse(entry);
                        }catch(ParseProblemException p){
                            continue;
                        }

                        // more visitors, details within
                        compilationUnit.accept(new VoidVisitorAdapter<Void>(){
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, final Void arg){
                                System.out.print(n.getNameAsString() + ": ");

                                HashMap<String, Set<String>> measure = new HashMap<>();
                                Set<String> fields = new HashSet<>();

                                // gather the fields available in the class
                                for(FieldDeclaration fd : n.findAll(FieldDeclaration.class)){
                                    NodeList nodes = fd.getVariables();
                                    for(Object v : nodes){
                                        fields.add(v.toString());
                                    }
                                }

                                // gather non-abstract methods and the fields they reference, if any
                                for(MethodDeclaration md : n.findAll(MethodDeclaration.class)){
                                    if(!md.getModifiers().contains(Modifier.abstractModifier())) {
                                        Set<String> counted = new HashSet<>();
                                        for (NameExpr ne : md.findAll(NameExpr.class)) {
                                            if (fields.contains(ne.getNameAsString())) {
                                                counted.add(ne.toString());
                                            }
                                        }
                                        measure.put(md.getNameAsString(), counted);
                                    }
                                }

                                System.out.println(calculatePerMethod(measure));
                                super.visit(n, arg);
                            }
                        }, null);
                    }
                }
            }
        }


        public int calculatePerMethod(HashMap<String, Set<String>> measure){
            // clone measure for manipulation
            HashMap<String, Set<String>> clone = (HashMap<String, Set<String>>) measure.clone();
            int P = 0;
            int Q = 0;
            int count = 0;

            // Loops. For each entry in measure, get the name of the method and the fields it references
            for(Map.Entry entry : measure.entrySet()){
                String methodName = entry.getKey().toString();
                Set<String> fields = measure.get(methodName);
                clone.remove(methodName, fields);

                // for each of the references in clone, try and match the name of the given string.
                for(String key : clone.keySet()) {
                    boolean match = false;
                    for(String str : fields) {
                        Set<String> entries = clone.get(key);
                        if (entries.contains(str)) {
                            match = true;
                        }
                    }

                    // according to the metric online - if there is a shared variable between two methods ++Q, otherwise ++P
                    if(match){
                        Q++;
                    }
                    else{
                        P++;
                    }
                }

            }

            // if P is greater than Q, change count, otherwise return 0.
            if(P > Q){
                count = P - Q;
            }
            return count;
        }
    }
}
