import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/** @noinspection rawtypes*/
public class LCOM extends VoidVisitorAdapter {
    // Lack of Cohesion of Methods - the degree to which methods and fields within a class are related to one another

    final static String PATH = "C:\\Users\\GA\\Downloads\\CS451TestSystem\\taxi-company-later-stage";

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
                        int x = 1;
                        CompilationUnit compilationUnit;
                        try {
                            compilationUnit = StaticJavaParser.parse(entry);
                        }catch(ParseProblemException p){
                            continue;
                        }

                        int[] fieldCount = {0};
                        int[] useCount = {0};

                        compilationUnit.accept(new VoidVisitorAdapter<Void>(){
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, final Void arg) {
                                System.out.println(n.getNameAsString().toUpperCase());
                                super.visit(n, arg);
                            }

                            public void visit(NameExpr n, final Void arg){
                                System.out.println("NameExpr " + n.getName().toString());
                                useCount[0]++;
                                super.visit(n, arg);
                            }

                            public void visit(FieldDeclaration n, final Void arg) {
                                System.out.println("FieldDec " + n.getVariables());
                                fieldCount[0]++;
                                super.visit(n, arg);
                            }

                        }, null);

                        int methodAndFieldSum = fieldCount[0] + useCount[0];

                        //System.out.println(" complexity: " + (1 - (x / methodAndFieldSum)));
                        System.out.println(" complexity: " + (methodAndFieldSum));
                        System.out.println();
                    }
                }
                    /*
                    For each field in the class, you count the methods that reference it,
                    and then you add all of those up across all fields.
                    You then divide that by the count of methods times the count of fields, and you subtract the result from one.

                     */
            }
        }
    }
}
