import model.*;

import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private final String splitChar = "\\s+";
    private final KnowledgeBase knowledgeBase;

    Parser() {
        this.knowledgeBase = new KnowledgeBase();
    }

    public void parseKnowledgeBase(List<String> inputKB) {
        this.knowledgeBase.setPredicates(Arrays.stream(inputKB.get(0).split("\\s+")).skip(1).collect(Collectors.toSet()));
        this.knowledgeBase.setVariables(Arrays.stream(inputKB.get(1).split("\\s+")).skip(1).collect(Collectors.toSet()));
        this.knowledgeBase.setConstants(Arrays.stream(inputKB.get(2).split("\\s+")).skip(1).collect(Collectors.toSet()));
        this.knowledgeBase.setFunctions(Arrays.stream(inputKB.get(3).split("\\s+")).skip(1).collect(Collectors.toSet()));

        List<Clause> clauses = new ArrayList<>();
        for (int i = 5; i < inputKB.size(); i++) {
            List<Predicate> clause = new ArrayList<>();
            Set<String> predNames = new HashSet<>();
            for (String predicate : inputKB.get(i).split("\\s+")) {
                Predicate parsedPred = parsePredicate(predicate);
                predNames.add(parsedPred.getName());
                clause.add(parsedPred);
            }
            clauses.add(new Clause(clause, predNames));
        }
        this.knowledgeBase.setClauses(clauses);
        this.knowledgeBase.setFolType();
    }

    private Predicate parsePredicate(String predicate) {
        String[] splitPred = predicate.split("[(,)]"); // splitting predicates into name and args
        Predicate parsedPred = new Predicate();

        // handle args
        parsedPred.setArgs(parseArgs(Arrays.asList(splitPred).subList(1, splitPred.length).toArray(String[]::new)));

        // handle name and sign
        if (this.knowledgeBase.predicates.contains(splitPred[0])) {
            parsedPred.setSign(Sign.POS);
            parsedPred.setName(splitPred[0]);
        } else {
            parsedPred.setSign(Sign.NEG);
            parsedPred.setName(splitPred[0].split("!")[1]);
        }

        return parsedPred;
    }

    private List<Argument> parseArgs(String[] argSplit) {
        List<Argument> arguments = new ArrayList<>();
        int ind = 0;
        while (ind < argSplit.length) {
            Argument arg = new Argument();
            if (this.knowledgeBase.variables.contains(argSplit[ind])) {
                arg.setVariable(argSplit[ind++]);
                arguments.add(arg);
            } else if (this.knowledgeBase.constants.contains(argSplit[ind])) {
                arg.setConstant(argSplit[ind++]);
                arguments.add(arg);
            } else if (this.knowledgeBase.functions.contains(argSplit[ind]) && this.knowledgeBase.variables.contains(argSplit[ind + 1])) {
                Function func = new Function();
                func.setName(argSplit[ind++]);

                if (this.knowledgeBase.constants.contains(argSplit[ind]))
                    func.setArgs(new Argument(argSplit[ind++], null, null));
                else {
                    func.setArgs(new Argument(null, argSplit[ind++], null));
                }
                arg.setFunction(func);
                arguments.add(arg);
            } else {
                ind++;
            }
        }

        return arguments;
    }

    public KnowledgeBase getKnowledgeBase() {
        return this.knowledgeBase;
    }
}
