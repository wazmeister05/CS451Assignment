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

                        // instantiate number of method declarations and branches
                        int methDec = 0;
                        int classBranches = 0;
                        // now read each method and pull out the switch and if statements
                        for(MethodDeclaration methodDeclaration : compilationUnit.findAll(MethodDeclaration.class)){
                            System.out.println("\t- " + methodDeclaration.getName());
                            methDec++;
                            int branches = 1;
                            for(SwitchStmt switchStmt : methodDeclaration.findAll(SwitchStmt.class)){
                                // check if the switch statement has conjunctions
                                if(switchStmt.toString().contains("&&") || switchStmt.toString().contains("||")){
                                    //System.out.println("CONDITIONAL");
                                    branches++;
                                }
                                branches++;
                            }
                            for(IfStmt ifStmt : methodDeclaration.findAll(IfStmt.class)) {
                                // check if the if statement has conjunctions
                                if(ifStmt.toString().contains("&&") || ifStmt.toString().contains("||")){
                                    //System.out.println("CONDITIONAL");
                                    branches++;
                                }
                                branches++;
                            }
                            System.out.println("\t\t- Method branch count - " + (branches));
                            classBranches = classBranches + branches;
                        }

                        // return the total method declarations
                        System.out.println("Total number of methods in class: " + methDec);
                        System.out.println("Branch count for class: " + (classBranches));
                        System.out.println("Class complexity: " + (methDec + classBranches) + "\n");
                    }
                }
            }
        }
    }
}
