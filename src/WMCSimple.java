import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class WMCSimple extends VoidVisitorAdapter{

    public static void main(String[] args) throws Exception {
        String CS451TestSystem = "C:\\Users\\GA\\IdeaProjects\\CS451Assignment\\CS451TestSystem";
        String CS451working = CS451TestSystem + "\\weblog-analyzer";
        //new MethodModifier().getFiles(new File(CS451TestSystem));
        new MethodModifier().getFiles(new File(CS451working));
    }

    private static class MethodVisitor extends VoidVisitorAdapter {
        int i = 1;
        public void visit(MethodDeclaration n, Object arg){
            System.out.println("\t- " + n.getName());
            i++;
        }
        public int getMethodCount(){
            return i-1;
        }
    }

    private static class ClassNameCollector extends VoidVisitorAdapter<List<String>>{
        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<String> collector) {
            super.visit(n, collector);
            collector.add(n.getNameAsString());
        }
    }

    private static class MethodModifier {
        public void getFiles(final File folder) throws FileNotFoundException {
            for(final File entry : folder.listFiles()){
                if (entry.isDirectory()) {
                    getFiles(entry);
                } else {
                    if(entry.toString().contains(".java")) {
                        List<String> className = new ArrayList<>();
                        CompilationUnit compilationUnit = StaticJavaParser.parse(entry);
                        compilationUnit.setPackageDeclaration("blank");
                        VoidVisitor<List<String>> classNameVisitor = new ClassNameCollector();
                        classNameVisitor.visit(compilationUnit, className);
                        System.out.println(className.get(0));
                        MethodVisitor mv = new MethodVisitor();
                        mv.visit(compilationUnit, null);
                        System.out.println("Total number of methods in class: " + mv.getMethodCount() + "\n");
                    }
                }
            }
        }
    }

}
