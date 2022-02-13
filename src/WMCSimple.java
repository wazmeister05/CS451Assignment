import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.ParserCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;


public class WMCSimple extends VoidVisitorAdapter{
    public static void main(String[] args) throws Exception {

//        Path pathToSource = Paths.get("C:\\Users\\GA\\IdeaProjects\\CS451Assignment");
//        ProjectRoot pr = new ParserCollectionStrategy().collect(pathToSource);
//        SourceRoot sourceRoot = new SourceRoot(pathToSource);
//        sourceRoot.tryToParse();
//        List<CompilationUnit> compilations = sourceRoot.getCompilationUnits();
//
//        for(CompilationUnit cu : compilations){
//            new MethodVisitor().visit(cu, null);
//        }

        String CS451TestSystem = "C:\\Users\\GA\\IdeaProjects\\CS451Assignment\\CS451TestSystem";
        new MethodModifier().getFiles(new File(CS451TestSystem));
    }

    private static class MethodVisitor extends VoidVisitorAdapter {
        public void visit(MethodDeclaration n, Object arg){
            System.out.println("Found method: " + n.getName());
        }
    }

    private static class MethodModifier extends ModifierVisitor{
        public void getFiles(final File folder) throws FileNotFoundException {
            for(final File entry : folder.listFiles()){
                if (entry.isDirectory()) {
                    getFiles(entry);
                } else {
                    if(entry.toString().contains(".java")) {
                        CompilationUnit compilationUnit = StaticJavaParser.parse(entry);
                        compilationUnit.accept(new ModifierVisitor<Void>(){
                            public Visitable visit(BinaryExpr n,Void arg){
                                if(n.getBegin() == "package")
                            }
                        }, null);
                        new MethodVisitor().visit(compilationUnit, null);
                    }
                }
            }
        }
    }

}
