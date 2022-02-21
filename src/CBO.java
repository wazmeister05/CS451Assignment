import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.javaparsermodel.declarators.VariableSymbolDeclarator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CBO extends VoidVisitorAdapter{
    // Weighted Methods per Class: The number of methods in a class

    //final static String PATH = "C:\\Users\\GA\\Downloads\\CS451TestSystem";
    final static String PATH = "C:\\Users\\GA\\Downloads\\CS451TestSystem\\test";

    // Main, get the path of the files and call the Method Modifier
    public static void main(String[] args) throws Exception {
        new MethodModifier().analyseFiles(new File(PATH));
    }

    private static class MethodModifier {
        // look at the location for files
        public void analyseFiles(final File folder) throws FileNotFoundException {

            // Create map to store file names and references it makes to other files
            Map<String, Set<String>> classReferences = new HashMap<>();

            for(final File entry : folder.listFiles()){
                // if it's a directory, go into it
                if (entry.isDirectory()) {
                    analyseFiles(entry);
                } else {
                    // otherwise just read each file
                    if(entry.toString().contains(".java")) {
                        Set<String> references = new HashSet<>();
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
                                System.out.println(n.getName().toString().toUpperCase());
                                super.visit(n, arg);
                            }
                        }, null);

//                        for(FieldDeclaration fd : compilationUnit.findAll(FieldDeclaration.class)){
//                            fd.removeComment();
//                            System.out.println(fd);
//                        }

                        for(MethodDeclaration md : compilationUnit.findAll(MethodDeclaration.class)){
                            System.out.println(md);
                            System.out.println(md.findAll(VariableDeclarationExpr.class));
                        }




                        // now add the file and the references to it to the map
                        classReferences.put(entry.getName(), references);

                    }
                }
            }
        }
    }
}





