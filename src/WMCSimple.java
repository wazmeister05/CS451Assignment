import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

/** @noinspection rawtypes*/
public class WMCSimple extends VoidVisitorAdapter{
    // Weighted Methods per Class: The number of methods in a class

    final static String PATH = "files/";

    // Main, get the path of the files and call the Method Modifier
    public static void main(String[] args) throws Exception {

        new MethodModifier().analyseFiles(new File(PATH));
    }

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
                        // instantiate number of method declarations (i.e. complexity)
                        final int[] complexity = {0};
                        CompilationUnit compilationUnit;
                        try {
                            compilationUnit = StaticJavaParser.parse(entry);
                        }catch(ParseProblemException p){
                            continue;
                        }
                        // get the class name and the number of methods
                        compilationUnit.accept(new VoidVisitorAdapter<Void>(){
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, final Void arg) {
                                System.out.print(n.getName());
                                super.visit(n, arg);
                            }

                            public void visit(MethodDeclaration n, final Void arg) {
                                complexity[0]++;
                                super.visit(n, arg);
                            }
                        }, null);

                        // return the total method declarations
                        System.out.println(" complexity: " + complexity[0]);
                    }
                }
            }
        }
    }
}
