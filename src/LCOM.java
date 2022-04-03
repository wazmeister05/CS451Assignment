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

    final static String PATH = "C:\\Users\\GA\\Downloads\\CS451TestSystem";

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

                        compilationUnit.accept(new VoidVisitorAdapter<Void>(){
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, final Void arg){
                                System.out.print(n.getNameAsString() + ": ");

                                HashMap<String, Set<String>> measure = new HashMap<>();
                                Set<String> fields = new HashSet<>();

                                for(FieldDeclaration fd : n.findAll(FieldDeclaration.class)){
                                    NodeList nodes = fd.getVariables();
                                    for(Object v : nodes){
                                        fields.add(v.toString());
                                    }
                                }

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

            HashMap<String, Set<String>> clone = (HashMap<String, Set<String>>) measure.clone();

            int P = 0;
            int Q = 0;
            int count = 0;

            for(Map.Entry entry : measure.entrySet()){
                String methodName = entry.getKey().toString();
                Set<String> fields = measure.get(methodName);
                clone.remove(methodName, fields);

                for(String key : clone.keySet()) {
                    boolean match = false;
                    for(String str : fields) {
                        Set<String> entries = clone.get(key);
                        if (entries.contains(str)) {
                            match = true;
                        }
                    }
                    if(match){
                        Q++;
                    }
                    else{
                        P++;
                    }
                }

            }
            if(P > Q){
                count = P - Q;
            }
            return count;
        }
    }
}
