import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
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

    // this is very similar to WMCSimple
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
                        // instantiate number of method declarations and branches
                        int methDec = 0;
                        int branches = 0;
                        CompilationUnit compilationUnit;
                        try {
                            compilationUnit = StaticJavaParser.parse(entry);
                        }catch(ParseProblemException p){
                            // currently just ignores any files that aren't edited to change the package import
                            continue;
                        }

                        // first, get the class name
                        for(ClassOrInterfaceDeclaration cid : compilationUnit.findAll(ClassOrInterfaceDeclaration.class)){
                            System.out.println(cid.getName());
                        }

                        // now read each method and pull out the switch and if statements
                        for(MethodDeclaration methodDeclaration : compilationUnit.findAll(MethodDeclaration.class)){
                            System.out.println("\t- " + methodDeclaration.getName());
                            methDec++;
                            for(SwitchStmt switchStmt : compilationUnit.findAll(SwitchStmt.class)){
                                branches++;
                            }
                            for(IfStmt ifStmt : compilationUnit.findAll(IfStmt.class)) {
                                branches++;
                            }
                        }

                        // return the total method declarations
                        System.out.println("Total number of methods in class: " + methDec);
                        System.out.println("Branch count for class: " + branches);
                        System.out.println("Class complexity: " + (methDec + branches) + "\n");
                    }
                }
            }
        }
    }
}
