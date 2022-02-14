import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;

public class WMCComplex extends VoidVisitorAdapter{
    // Weighted Methods per Class using Cyclometric Complexity: The number of decisions in a method + 1

    // Main, get the path of the files and call the Method Modifier
    public static void main(String[] args) throws Exception {
        String PATH = "C:\\Users\\GA\\IdeaProjects\\CS451Assignment\\CS451TestSystem";
        new MethodModifier().getFiles(new File(PATH));
    }

    // separate class because of the getMethodCount method to return number of methods in class
    private static class MethodVisitor extends VoidVisitorAdapter {
        // number of methods in class
        int methodCount = 1;

        // return it
        public int getMethodCount(){
            return methodCount -1;
        }

        // visit the node
        public void visit(MethodDeclaration n, Object arg){
            System.out.println("\t- " + n.getName());
            methodCount++;
        }

    }

    private static class MethodModifier {
        // look at the location for files
        public void getFiles(final File folder) throws FileNotFoundException {
            for(final File entry : folder.listFiles()){
                // if it's a directory, go into it
                if (entry.isDirectory()) {
                    getFiles(entry);
                } else {
                    // otherwise just read each file
                    if(entry.toString().contains(".java")) {
                        CompilationUnit compilationUnit;
                        try {
                            compilationUnit = StaticJavaParser.parse(entry);
                        }catch(ParseProblemException p){
                            // currently just ignores any files that aren't edited to change the package import
                            continue;
                        }
                        // first, get the class name
                        compilationUnit.accept(new VoidVisitorAdapter<Void>(){
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, final Void arg) {
                                System.out.println(n.getName());
                                super.visit(n, arg);
                            }
                        }, null);

                        // now read each method
                        MethodVisitor mv = new MethodVisitor();
                        mv.visit(compilationUnit, null);

                        // return the total method declarations
                        System.out.println("Total number of methods in class: " + mv.getMethodCount() + "\n");
                    }
                }
            }
        }
    }
}
