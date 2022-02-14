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

    // Main, get the path of the files and call the Method Modifier
    public static void main(String[] args) throws Exception {
        String PATH = "C:\\Users\\GA\\IdeaProjects\\CS451Assignment\\CS451TestSystem";
        new MethodModifier().getFiles(new File(PATH));
    }

    // this is a separate class because of the getMethodCall and getMethodDec methods
    private static class MethodVisitor extends VoidVisitorAdapter {
        // the number of methods in a class
        int methodDec = 1;

        // return number of methods in a class
        public int getMethodDec(){
            return methodDec-1;
        }

        // the number of method calls in a class
        int methodCall = 1;

        // return number of method calls in a class
        public int getMethodCall(){
            return methodCall-1;
        }

        // visit each of the nodes in the tree
        public void visit(MethodDeclaration n, Object arg){
            System.out.println("\t- " + n.getName());
            // found a method so count it
            methodDec++;
            // each method may have calls in it, so deal with it
            n.accept(new VoidVisitorAdapter<Void>(){
                @Override
                public void visit(final MethodCallExpr n, final Void arg){
                    System.out.println("\t\t- " + n.getName());
                    // found a method call so count it
                    methodCall++;
                    super.visit(n, arg);
                }
            }, null);
        }
    }

    private static class MethodModifier {
        public void getFiles(final File folder) throws FileNotFoundException {
            for(final File entry : folder.listFiles()){
                if (entry.isDirectory()) {
                    // check subdirectories for files
                    getFiles(entry);
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
                        // now get the method name and method calls
                        MethodVisitor mv = new MethodVisitor();
                        mv.visit(compilationUnit, null);

                        // return the total method declarations and calls
                        System.out.println("Total number of methods declarations (" + mv.getMethodDec() +
                                ") and method calls (" + mv.getMethodCall() + ") in class: " +
                                (mv.getMethodDec() + mv.getMethodCall()) + "\n");
                    }
                }
            }
        }
    }
}
