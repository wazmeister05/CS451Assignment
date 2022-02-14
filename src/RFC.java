import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;

public class RFC extends VoidVisitorAdapter{

    public static void main(String[] args) throws Exception {
        String CS451TestSystem = "C:\\Users\\GA\\IdeaProjects\\CS451Assignment\\CS451TestSystem";
        String CS451working = CS451TestSystem + "\\weblog-analyzer";
        //new MethodModifier().getFiles(new File(CS451TestSystem));
        new MethodModifier().getFiles(new File(CS451working));
    }

    private static class MethodVisitor extends VoidVisitorAdapter {
        int methodDec = 1;
        int methodCall = 1;

        public void visit(MethodDeclaration n, Object arg){
            System.out.println("\t- " + n.getName());
            methodDec++;
            n.accept(new VoidVisitorAdapter<Void>(){
                @Override
                public void visit(final MethodCallExpr n, final Void arg){
                    System.out.println("\t\t- " + n.getName());
                    methodCall++;
                    super.visit(n, arg);
                }
            }, null);
        }
        public int getMethodDec(){
            return methodDec-1;
        }
        public int getMethodCall(){
            return methodCall-1;
        }

    }


    private static class MethodModifier {
        public void getFiles(final File folder) throws FileNotFoundException {
            for(final File entry : folder.listFiles()){
                if (entry.isDirectory()) {
                    getFiles(entry);
                } else {
                    if(entry.toString().contains(".java")) {
                        //List<String> className = new ArrayList<>();
                        CompilationUnit compilationUnit = StaticJavaParser.parse(entry);
                        compilationUnit.accept(new VoidVisitorAdapter<Void>(){
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, final Void arg) {
                                System.out.println(n.getName());
                                super.visit(n, arg);
                            }
                        }, null);
                        MethodVisitor mv = new MethodVisitor();
                        mv.visit(compilationUnit, null);
                        System.out.println("Total number of methods declarations (" + mv.getMethodDec() +
                                ") and method calls (" + mv.getMethodCall() + ") in class: " +
                                (mv.getMethodDec() + mv.getMethodCall()) + "\n");
                    }
                }
            }
        }
    }
}
