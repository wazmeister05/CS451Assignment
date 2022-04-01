import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/** @noinspection rawtypes*/
public class LCOM extends VoidVisitorAdapter {
    // Lack of Cohesion of Methods - the degree to which methods and fields within a class are related to one another

    final static String PATH = "C:\\Users\\GA\\Downloads\\CS451TestSystem\\weblog-analyzer";

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

                                Map<String, Integer> measure = new HashMap<>();
                                ArrayList<String> fields = new ArrayList<>();

                                for(FieldDeclaration fd : n.findAll(FieldDeclaration.class)){
                                    fields.add(fd.toString());
                                }

                                for(MethodDeclaration md : n.findAll(MethodDeclaration.class)){
                                    int x = 0;
                                    for(NameExpr ne : md.findAll(NameExpr.class)){
                                        if(fields.contains(ne.getNameAsString())){
                                            x++;
                                        }
                                    }
                                   measure.put(md.getNameAsString(), x);
                                }

                                System.out.println(measure);
                                super.visit(n, arg);
                            }
                        }, null);
                    }
                }
            }
        }
    }
}
