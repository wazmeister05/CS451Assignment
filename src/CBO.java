import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.FileNotFoundException;

public class CBO extends VoidVisitorAdapter{

    final static String PATH = "C:\\Users\\GA\\IdeaProjects\\CS451Assignment\\CS451TestSystem";
    // Weighted Methods per Class: The number of methods in a class
    // Main, get the path of the files and call the Method Modifier
    public static void main(String[] args) throws Exception {
        new MethodModifier().analyseFiles(new File(PATH));
    }

    private static class MethodModifier {
        // look at the location for files
        public void analyseFiles(final File folder) throws FileNotFoundException {
            for(final File entry : folder.listFiles()){
                // if it's a directory, go into it
                if (entry.isDirectory()) {
                    analyseFiles(entry);
                } else {
                    // otherwise just read each file
                    if(entry.toString().contains(".java")) {
                        // instantiate number of method declarations (i.e. complexity)
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

                        for(FieldDeclaration fieldDeclaration : compilationUnit.findAll(FieldDeclaration.class)){
                            System.out.println(fieldDeclaration.getVariables().getParentNode());
                        }

                        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
                        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(PATH));

                        reflectionTypeSolver.setParent(reflectionTypeSolver);

                        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
                        combinedSolver.add(reflectionTypeSolver);
                        combinedSolver.add(javaParserTypeSolver);

                        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
                        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

                    }
                }
            }
        }
    }
}





