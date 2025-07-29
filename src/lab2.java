import model.KnowledgeBase;
import resolvers.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class lab2 {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.print("Missing one argument: KnowledgeBase");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])))) {
            List<String> inputKB = new ArrayList<>();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                inputKB.add(inputLine);
            }

            Parser parser = new Parser();
            parser.parseKnowledgeBase(inputKB);

            KnowledgeBase knowledgeBase = parser.getKnowledgeBase();

            boolean resolutionRes = false;
            // resolution if it is prop logic
            if (knowledgeBase.isProp) {
                resolutionRes = new PropLogicResolver(knowledgeBase).resolve();
            }
            // resolution if it is FOL
            else {
                resolutionRes = new FOLResolver(knowledgeBase).resolve();
            }

            //print final result
            System.out.println(resolutionRes ? "yes" : "no");

        } catch (FileNotFoundException e) {
            System.err.printf("Caught FileNotFoundException while reading knowledge base:%s", e.getMessage());
        } catch (IOException e) {
            System.err.printf("Caught IOException while reading knowledge base:%s", e.getMessage());
        }
    }
}
