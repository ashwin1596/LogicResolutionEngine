package model;

import java.util.List;
import java.util.Set;

public class KnowledgeBase {
    public Set<String> predicates;
    public Set<String> variables;
    public Set<String> constants;
    public Set<String> functions;
    public List<Clause> clauses;
    public boolean isProp; // Is propositionalLogic or FOL; if there are no args, then prop; otherwise, FOL
    public FOLType folType; // if it is FOL, this will tell you whether there are constants, functions, universals or, both c+u

    public Set<String> getPredicates() {
        return predicates;
    }

    public void setPredicates(Set<String> predicates) {
        this.predicates = predicates;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }

    public Set<String> getConstants() {
        return constants;
    }

    public void setConstants(Set<String> constants) {
        this.constants = constants;
    }

    public Set<String> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<String> functions) {
        this.functions = functions;
    }

    public List<Clause> getClauses() {
        return clauses;
    }

    public void addClause(Clause newClause) {

        // Add only if, clause does not exist
        if (this.clauses.contains(newClause))
            return;

        this.clauses.add(newClause);
    }

    public void setClauses(List<Clause> clauses) {
        this.clauses = clauses;
        this.isProp = clauses.get(0).predicates.get(0).args.isEmpty();
    }

    public void setFolType() {

        if (!this.isProp) {
            if (!this.functions.isEmpty())
                this.folType = FOLType.FUNCTIONS;
            else if (!this.constants.isEmpty() && !this.variables.isEmpty())
                this.folType = FOLType.CONST_UNIV;
            else if (!this.constants.isEmpty())
                this.folType = FOLType.CONSTANTS;
            else
                this.folType = FOLType.UNIVERSALS;
        }
    }

}
