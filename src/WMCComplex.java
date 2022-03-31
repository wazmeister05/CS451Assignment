import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

/** @noinspection rawtypes*/
public class WMCComplex extends VoidVisitorAdapter{
    // Weighted Methods per Class using Cyclometric Complexity: The number of decisions in a method + 1

    final static String PATH = "C:\\Users\\GA\\Downloads\\CS451TestSystem";

    // Main, get the path of the files and call the Method Modifier
    public static void main(String[] args) throws Exception {
        new MethodModifier().analyseFiles(new File(PATH));
    }

    // this is very similar to WMCSimple
    private static class MethodModifier {

        // look at the location for files
        public void analyseFiles(final File folder) throws FileNotFoundException {
            for(final File entry : Objects.requireNonNull(folder.listFiles())){
                // if it's a directory, go into it
                if (entry.isDirectory()) {
                    analyseFiles(entry);
                } else {
                    // otherwise just read each file
                    if(entry.toString().contains(".java")) {
                        final int[] classBranches = {0};
                        CompilationUnit compilationUnit;
                        try {
                            compilationUnit = StaticJavaParser.parse(entry);
                        }catch(ParseProblemException p){
                            // currently just ignores any files that aren't edited to change the package import
                            continue;
                        }

                        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, final Void arg){
                                System.out.print(n.getName());
                                super.visit(n, arg);
                            }

                            public void visit(ConstructorDeclaration n, final Void arg){
                                int branches = 1;
                                for(WhileStmt whileStmt : n.findAll(WhileStmt.class)){
                                    // check if the while statement has conjunctions
                                    if(whileStmt.toString().contains("&&") || whileStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }
                                for(DoStmt doStmt : n.findAll(DoStmt.class)){
                                    // check if the do while statement has conjunctions
                                    if(doStmt.toString().contains("&&") || doStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }
                                for(ForEachStmt forEachStmt : n.findAll(ForEachStmt.class)){
                                    // check if the for-each statement has conjunctions
                                    if(forEachStmt.toString().contains("&&") || forEachStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }
                                for(ForStmt forStmt : n.findAll(ForStmt.class)){
                                    // check if the for statement has conjunctions
                                    if(forStmt.toString().contains("&&") || forStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }
                                for(SwitchStmt switchStmt : n.findAll(SwitchStmt.class)){
                                    // check if the switch statement has conjunctions
                                    if(switchStmt.toString().contains("&&") || switchStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }
                                for(IfStmt ifStmt : n.findAll(IfStmt.class)) {
                                    // check if the if statement has conjunctions
                                    if(ifStmt.toString().contains("&&") || ifStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }

                                classBranches[0] = classBranches[0] + branches;
                            }

                            public void visit(MethodDeclaration n, final Void arg){
                                int branches = 1;
                                for(WhileStmt whileStmt : n.findAll(WhileStmt.class)){
                                    // check if the while statement has conjunctions
                                    if(whileStmt.toString().contains("&&") || whileStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }
                                for(DoStmt doStmt : n.findAll(DoStmt.class)){
                                    // check if the do while statement has conjunctions
                                    if(doStmt.toString().contains("&&") || doStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }
                                for(ForEachStmt forEachStmt : n.findAll(ForEachStmt.class)){
                                    // check if the for-each statement has conjunctions
                                    if(forEachStmt.toString().contains("&&") || forEachStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }
                                for(ForStmt forStmt : n.findAll(ForStmt.class)){
                                    // check if the for statement has conjunctions
                                    if(forStmt.toString().contains("&&") || forStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }
                                for(SwitchStmt switchStmt : n.findAll(SwitchStmt.class)){
                                    // check if the switch statement has conjunctions
                                    if(switchStmt.toString().contains("&&") || switchStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }
                                for(IfStmt ifStmt : n.findAll(IfStmt.class)) {
                                    // check if the if statement has conjunctions
                                    if(ifStmt.toString().contains("&&") || ifStmt.toString().contains("||")){
                                        branches++;
                                    }
                                    branches++;
                                }

                                //System.out.println("\t\t- Method branch count - " + (branches));
                                classBranches[0] = classBranches[0] + branches;
                            }
                        }, null);

                        // return the total method declarations (i.e. complexity)
                        System.out.println(" complexity: " + classBranches[0]);
                    }
                }
            }
        }
    }
}
