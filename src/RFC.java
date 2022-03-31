import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** @noinspection rawtypes*/
public class RFC extends VoidVisitorAdapter{
    // Response For a Class: number of methods and method calls in a class

    final static String PATH = "C:\\Users\\GA\\Downloads\\CS451TestSystem";

    // Main, get the path of the files and call the Method Modifier
    public static void main(String[] args) throws Exception {
        new MethodModifier().analyseFiles(new File(PATH));
    }

    private static class MethodModifier {
        public void analyseFiles(final File folder) throws FileNotFoundException {
            for(final File entry : Objects.requireNonNull(folder.listFiles())){
                if (entry.isDirectory()) {
                    // check subdirectories for files
                    analyseFiles(entry);
                } else {
                    // found a java file, parse it.
                    if(entry.toString().contains(".java")) {
                        final int[] methodDec = {0};
                        final int[] methodCall = {0};
                        CompilationUnit compilationUnit;
                        try{
                            // try parsing the file
                            compilationUnit = StaticJavaParser.parse(entry);
                        }catch(ParseProblemException p){
                            continue;
                        }
                        // first return the class name
                        compilationUnit.accept(new VoidVisitorAdapter<Void>(){
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, final Void arg) {
                                // get the name of the class
                                System.out.print(n.getName());
                                super.visit(n, arg);
                            }

                            public void visit(ConstructorDeclaration n, final Void arg) {
                                Set<String> methodsCallExp = new HashSet<>();
                                //System.out.println("\t- " + methodDeclaration.getName());
                                methodDec[0]++;
                                for(MethodCallExpr mce : n.findAll(MethodCallExpr.class)){
                                    methodsCallExp.add(mce.toString());
                                }
                                methodCall[0] += methodsCallExp.size();
                                super.visit(n, arg);
                            }

                            public void visit(MethodDeclaration n, final Void arg) {
                                Set<String> methodsCallExp = new HashSet<>();
                                //System.out.println("\t- " + methodDeclaration.getName());
                                methodDec[0]++;
                                for(MethodCallExpr mce : n.findAll(MethodCallExpr.class)){
                                    methodsCallExp.add(mce.toString());
                                }
                                methodCall[0] += methodsCallExp.size();
                                super.visit(n, arg);
                            }

                        }, null);

                        // return the total method declarations and calls
                        System.out.println(" complexity: " + (methodDec[0] + methodCall[0]));
                    }
                }
            }
        }
    }
}
