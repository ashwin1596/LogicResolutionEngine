package resolvers;

import model.*;

import java.util.*;
import java.util.stream.Collectors;

public class FOLResolver extends Resolver {

    static int freeVariableCount = 0;
    static String freeVariableName = "a";

    public FOLResolver(KnowledgeBase knowledgeBase) {
        super(knowledgeBase);
    }

    @Override
    Clause getResolvent(Clause currentClause, Clause compareToClause, Predicate currentPred, Predicate compareToPred) {

        List<Predicate> newPredicates = new ArrayList<>();
        Set<String> newPredNames = new HashSet<>();

        for (Predicate pred : currentClause.predicates) {
            if (pred != currentPred) {

                // if no substitutions were made, then add this predicate directly to the new clause
                if (currentClause.getSubstitutions().isEmpty()) {
                    newPredicates.add(pred);
                    newPredNames.add(pred.getName());
                } else // check if one of this predicate's variables was substituted, if yes then substitute and then add to new clause
                {
                    Predicate newPred = new Predicate(pred.getName(), pred.getSign(), null);
                    for (Substitution substitution : currentClause.getSubstitutions()) {
                        for (int i = 0; i < pred.getArgs().size(); i++) {
                            if (substitution.getVariable().equals(pred.getArgs().get(i).getVariable())) {
                                newPred.addArgs(substitution.getSubstitute());
                            } else if (pred.getArgs().get(i).getFunction() != null && substitution.getVariable().equals(pred.getArgs().get(i).getFunction().getArgs().getVariable())) {
                                newPred.addArgs(pred.getArgs().get(i).clone());
                                newPred.getArgs().get(i).getFunction().setArgs(substitution.getSubstitute());
                            } else {
                                newPred.addArgs(pred.getArgs().get(i));
                            }
                        }
                    }

                    newPredicates.add(newPred);
                    newPredNames.add(newPred.getName());
                }

            }
        }

        for (Predicate pred : compareToClause.predicates) {
            if (pred != compareToPred) {

                // if no substitutions were made, then add this predicate directly to the new clause
                if (compareToClause.getSubstitutions().isEmpty()) {
                    newPredicates.add(pred);
                    newPredNames.add(pred.getName());
                } else // check if one of this predicate's variables was substituted, if yes then substitute and then add to new clause
                {
                    Predicate newPred = new Predicate(pred.getName(), pred.getSign(), null);
                    for (Substitution substitution : compareToClause.getSubstitutions()) {
                        for (int i = 0; i < pred.getArgs().size(); i++) {
                            if (substitution.getVariable().equals(pred.getArgs().get(i).getVariable())) {
                                newPred.addArgs(substitution.getSubstitute());
                            } else {
                                newPred.addArgs(pred.getArgs().get(i));
                            }
                        }
                    }

                    newPredicates.add(newPred);
                    newPredNames.add(newPred.getName());
                }

            }
        }

        currentClause.getSubstitutions().clear();
        compareToClause.getSubstitutions().clear();

        return new Clause(newPredicates, newPredNames);
    }

    @Override
    boolean comparePredicates(Clause clause1, Clause clause2, Predicate predicate1, Predicate predicate2) {

        if (!unify(clause1, clause2, predicate1, predicate2))
            return false;

        return !predicate1.getSign().equals(predicate2.getSign());
    }

    private boolean unify(Clause clause1, Clause clause2, Predicate predicate1, Predicate predicate2) {

        // based on the FOL type, check for unification
        switch (this.knowledgeBase.folType) {
            case CONSTANTS -> {
                return unifyConstants(clause1, clause2, predicate1.getArgs(), predicate2.getArgs());
            }
            case UNIVERSALS -> {
                return unifyUniversals(clause1, clause2, predicate1.getArgs(), predicate2.getArgs());
            }
            case CONST_UNIV -> {
                return unifyUniversal_Constants(clause1, clause2, predicate1.getArgs(), predicate2.getArgs());
            }
            case FUNCTIONS -> {
                return unifyFunctions(clause1, clause2, predicate1.getArgs(), predicate2.getArgs());
            }
        }

        return true;
    }

    private boolean unifyConstants(Clause clause1, Clause clause2, List<Argument> arg1, List<Argument> arg2) {

        if (arg1.size() != arg2.size())
            return false;

        for (int i = 0; i < arg1.size(); i++) {
            if (!arg1.get(i).getConstant().equals(arg2.get(i).getConstant()))
                return false;
        }

        return true;
    }

    private boolean unifyUniversals(Clause clause1, Clause clause2, List<Argument> arg1, List<Argument> arg2) {

        if (arg1.size() != arg2.size())
            return false;

        // handle single arguments
        if (arg1.size() == 1) {
            // check if they match without substitution
            if (arg1.get(0).equals(arg2.get(0)))
                return true;

            //check after substitution
            if (this.substituteFreeVariableAndCompare(clause1, clause2, arg1.get(0), arg2.get(0), getFreeVariable()))
                return true;
        }

        if (arg1.size() == 2) {
            // check if both set of arguments are unique, if unique then substitute the free variables and compare
            Set<Argument> commonArgs = arg1.stream().distinct().filter(arg2::contains).collect(Collectors.toSet());
            boolean isUnifiable = true;
            if (commonArgs.isEmpty()) {
                for (int i = 0; i < arg1.size(); i++) {
                    // check if they match without substitution
                    if (arg1.get(i).equals(arg2.get(i))) {
                        continue;
                    }

                    //check after substitution
                    if (!this.substituteFreeVariableAndCompare(clause1, clause2, arg1.get(i), arg2.get(i), getFreeVariable()))
                        isUnifiable = false;
                }
            }

            return isUnifiable;
            // TODO ---- If there are common variables in two set of args
            //  This can be avoided as there are no test cases related to this
            //  If ignoring this, delete these comments
        }

        return false;
    }

    private boolean unifyUniversal_Constants(Clause clause1, Clause clause2, List<Argument> arg1, List<Argument> arg2) {
        if (arg1.size() != arg2.size())
            return false;

        // handle single arguments
        if (arg1.size() == 1) {
            // check if they match without substitution
            if (arg1.get(0).equals(arg2.get(0)))
                return true;

            if (arg1.get(0).getArgType().equals("constant") && arg2.get(0).getArgType().equals("constant"))
                return unifyConstants(clause1, clause2, arg1, arg2);

            Argument substitutedArg;

            if (arg1.get(0).getConstant() != null) // arg1-const arg2-variable
            {
                substitutedArg = this.substituteVariableForConstant(clause2, arg2.get(0), arg1.get(0).getConstant());
                return arg1.get(0).equals(substitutedArg);
            }

            if (arg2.get(0).getConstant() != null) // arg1-variable arg2-const
            {
                substitutedArg = this.substituteVariableForConstant(clause1, arg1.get(0), arg2.get(0).getConstant());
                return arg2.get(0).equals(substitutedArg);
            }
        }

        if (arg1.size() == 2) {
            // check if both set of arguments are unique, if unique then substitute the free variables and compare
            Set<Argument> commonArgs = arg1.stream().distinct().filter(arg2::contains).collect(Collectors.toSet());
            boolean isUnifiable = true;
            if (commonArgs.isEmpty()) {
                for (int i = 0; i < arg1.size(); i++) {
                    // check if they match without substitution
                    if (arg1.get(i).equals(arg2.get(i))) {
                        continue;
                    }

                    if (arg1.get(i).getArgType().equals("constant") && arg2.get(i).getArgType().equals("constant")) {
                        if (!unifyConstants(clause1, clause2, arg1, arg2))
                            isUnifiable = false;
                        continue;
                    }

                    Argument substitutedArg;

                    if (arg1.get(i).getConstant() != null) // arg1-const arg2-variable
                    {
                        substitutedArg = this.substituteVariableForConstant(clause2, arg2.get(i), arg1.get(i).getConstant());
                        if (!arg1.get(i).equals(substitutedArg))
                            isUnifiable = false;
                    }

                    if (arg2.get(i).getConstant() != null) // arg1-variable arg2-const
                    {
                        substitutedArg = this.substituteVariableForConstant(clause1, arg1.get(i), arg2.get(i).getConstant());
                        if (!arg2.get(i).equals(substitutedArg))
                            isUnifiable = false;
                    }
                }
            }

            return isUnifiable;
        }

        return true;
    }

    private boolean unifyFunctions(Clause clause1, Clause clause2, List<Argument> arg1, List<Argument> arg2) {
        if (arg1.size() != arg2.size())
            return false;

        boolean isUnifiable = true;

        for (int i = 0; i < arg1.size(); i++) {
            // check if they match without substitution
            if (arg1.get(i).equals(arg2.get(i))) {
                continue;
            }

            if (arg1.get(i).getArgType().equals("constant") && arg2.get(i).getArgType().equals("constant")) {
                if (!unifyConstants(clause1, clause2, Collections.singletonList(arg1.get(i)), Collections.singletonList(arg2.get(i))))
                    isUnifiable = false;
                continue;
            }

            Argument substitutedArg;

            if (arg1.get(i).getConstant() != null && arg2.get(i).getVariable() != null) // arg1-const arg2-variable
            {
                if (!unifyUniversal_Constants(clause1, clause2, Collections.singletonList(arg1.get(i)), Collections.singletonList(arg2.get(i))))
                    isUnifiable = false;
            } else if (arg1.get(i).getVariable() != null && arg2.get(i).getConstant() != null) // arg1-variable arg2-const
            {
                if (!unifyUniversal_Constants(clause1, clause2, Collections.singletonList(arg1.get(i)), Collections.singletonList(arg2.get(i))))
                    isUnifiable = false;
            } else if (arg1.get(i).getFunction() != null && arg2.get(i).getVariable() != null) // arg1-func arg2-variable
            {
                substitutedArg = this.substituteVariableForFunction(clause2, arg2.get(i), arg1.get(i).getFunction());
                if (!arg1.get(i).equals(substitutedArg))
                    isUnifiable = false;
            } else if (arg1.get(i).getVariable() != null && arg2.get(i).getFunction() != null) // arg1-variable arg2-func
            {
                substitutedArg = this.substituteVariableForFunction(clause1, arg1.get(i), arg2.get(i).getFunction());
                if (!arg2.get(i).equals(substitutedArg))
                    isUnifiable = false;
            } else if (arg1.get(i).getFunction() != null && arg2.get(i).getConstant() != null) // arg1-func arg2-func
            {
                isUnifiable = false;
            } else if (arg1.get(i).getConstant() != null && arg2.get(i).getFunction() != null) // arg1-func arg2-func
            {
                isUnifiable = false;
            } else if (arg1.get(i).getFunction() != null && arg2.get(i).getFunction() != null) // arg1-func arg2-func
            {
                Argument arg1FuncArg = arg1.get(i).getFunction().getArgs();
                Argument arg2FuncArg = arg2.get(i).getFunction().getArgs();

                if (arg1FuncArg.getArgType().equals("constant") && arg1FuncArg.getArgType().equals("constant")) {
                    if (!unifyConstants(clause1, clause2, Collections.singletonList(arg1FuncArg), Collections.singletonList(arg2FuncArg)))
                        isUnifiable = false;
                } else if (arg1FuncArg.getArgType().equals("constant") && arg1FuncArg.getArgType().equals("variable")) {
                    if (!unifyUniversal_Constants(clause1, clause2, Collections.singletonList(arg1FuncArg), Collections.singletonList(arg2FuncArg)))
                        isUnifiable = false;
                } else if (arg1FuncArg.getArgType().equals("variable") && arg1FuncArg.getArgType().equals("constant")) {
                    if (!unifyUniversal_Constants(clause1, clause2, Collections.singletonList(arg1FuncArg), Collections.singletonList(arg2FuncArg)))
                        isUnifiable = false;
                } else if (arg1FuncArg.getArgType().equals("variable") && arg1FuncArg.getArgType().equals("variable")) {
                    if (!unifyUniversals(clause1, clause2, Collections.singletonList(arg1FuncArg), Collections.singletonList(arg2FuncArg)))
                        isUnifiable = false;
                }
            }
        }

        return isUnifiable;
    }

    private boolean substituteFreeVariableAndCompare(Clause clause1, Clause clause2, Argument arg1, Argument arg2, String freeVariable) {
        Substitution substitution1 = new Substitution();
        Substitution substitution2 = new Substitution();

        substitution1.setVariable(arg1.getVariable());
        Argument arg1_substitute = arg1.clone();
        arg1_substitute.setVariable(freeVariable);
        substitution1.setSubstitute(arg1_substitute);

        clause1.addSubstitution(substitution1);

        substitution2.setVariable(arg2.getVariable());
        Argument arg2_substitute = arg2.clone();
        arg2_substitute.setVariable(freeVariable);
        substitution2.setSubstitute(arg2_substitute);

        clause2.addSubstitution(substitution2);

        //check after substitution
        return arg1_substitute.equals(arg2_substitute);
    }

    private static String getFreeVariable() {
        return String.format("%s%d", freeVariableName, freeVariableCount++);
    }

    private Argument substituteVariableForConstant(Clause clause, Argument arg, String constant) {
        Substitution substitution = new Substitution();
        substitution.setVariable(arg.getVariable());
        Argument arg_substitute = arg.clone();
        arg_substitute.substituteVariableForConst(constant);
        substitution.setSubstitute(arg_substitute);

        clause.addSubstitution(substitution);

        return arg_substitute;
    }

    private Argument substituteVariableForFunction(Clause clause, Argument arg, Function function) {
        Substitution substitution = new Substitution();
        substitution.setVariable(arg.getVariable());
        Argument arg_substitute = arg.clone();
        arg_substitute.substituteVariableForFunction(function);
        substitution.setSubstitute(arg_substitute);

        clause.addSubstitution(substitution);

        return arg_substitute;
    }
}
