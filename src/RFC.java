import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;

public class RFC extends VoidVisitorAdapter{
    // Response For a Class: number of methods and method calls in a class

    final static String PATH = "C:\\Users\\GA\\Downloads\\CS451TestSystem";

    // Main, get the path of the files and call the Method Modifier
    public static void main(String[] args) throws Exception {
        new MethodModifier().analyseFiles(new File(PATH));
    }

    private static class MethodModifier {
        public void analyseFiles(final File folder) throws FileNotFoundException {
            for(final File entry : folder.listFiles()){
                if (entry.isDirectory()) {
                    // check subdirectories for files
                    analyseFiles(entry);
                } else {
                    // found a java file, parse it.
                    if(entry.toString().contains(".java")) {
                        CompilationUnit compilationUnit;
                        try{
                            // try parsing the file
                            compilationUnit = StaticJavaParser.parse(entry);
                        }catch(ParseProblemException p){
                            // currently just ignores any files that aren't edited to change the package import
                            continue;
                        }
                        // first return the class name, inline because it doesn't need anything more than visit.
                        compilationUnit.accept(new VoidVisitorAdapter<Void>(){
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, final Void arg) {
                                // get the name of the class
                                System.out.println(n.getName());
                                super.visit(n, arg);
                            }
                        }, null);

                        int methodDec = 0;
                        int methodCall = 0;

                        // for each found method, print name and increment methodDec
                        for(MethodDeclaration methodDeclaration : compilationUnit.findAll(MethodDeclaration.class)){
                            System.out.println("\t- " + methodDeclaration.getName());
                            methodDec++;
                            for(MethodCallExpr mce : methodDeclaration.findAll(MethodCallExpr.class)){
                                System.out.println("\t\t- " + mce.getName());
                                // found a method call so count it
                                methodCall++;
                            }
                        }

                        // return the total method declarations and calls
                        System.out.println("RFC Class complexity (method declarations + method calls): " +
                                (methodDec + methodCall) + "\n");
                    }
                }
            }
        }
    }
}
