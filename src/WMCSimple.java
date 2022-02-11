import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;

public class WMCSimple {
    public static void main(String[] args) throws Exception {
        String fileName = "C:\\Users\\GA\\IdeaProjects\\CS451Assignment\\weblog-analyzer\\LogAnalyzer.java";
        FileInputStream in = new FileInputStream(fileName);
        CompilationUnit cu = null;
        try{
            cu = StaticJavaParser.parse(in);
        } catch(ParseProblemException e){
            System.out.println("There was an error reading " + fileName);
            System.out.println(e);
            System.exit(1);
        } finally {
            in.close();
        }
        new MethodVisitor().visit(cu, null);
    }

    private static class MethodVisitor extends VoidVisitorAdapter {
        public void visit(MethodDeclaration n, Object arg){
            System.out.println("Found method: " + n.getName());
        }

        private int dummyMethod(){
            return 0;
        }
    }

}
