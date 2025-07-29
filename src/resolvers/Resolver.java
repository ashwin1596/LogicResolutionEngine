package resolvers;

import model.Clause;
import model.KnowledgeBase;
import model.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Resolver {
    KnowledgeBase knowledgeBase;
    int previousClauseCount;

    Resolver(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
        this.previousClauseCount = 0;
    }

    abstract boolean comparePredicates(Clause clause1, Clause clause2, Predicate predicate1, Predicate predicate2); // tells you whether two predicates resolve

    abstract Clause getResolvent(Clause currentClause, Clause compareToClause, Predicate currentPred, Predicate compareToPred);

    private boolean compareClauses(Clause clause1, Clause clause2) {

        for (int i = 0; i < clause1.getPredCount(); i++) {
            Predicate currentPredicate = clause1.predicates.get(i);

            if (!clause2.predNames.contains(currentPredicate.getName()))
                continue;

            Predicate compareToPred = clause2.getPredicateByName(currentPredicate.getName());

            if (currentPredicate.getSign() == compareToPred.getSign())
                continue;

            if (!comparePredicates(clause1, clause2, currentPredicate, compareToPred))
                continue;

            Clause resolvent = this.getResolvent(clause1, clause2, currentPredicate, compareToPred);

            // check if we got an empty clause; return false for empty clause; otherwise add resolvent to kb and return true
            if (resolvent.getPredCount() != 0) {
                this.knowledgeBase.addClause(resolvent);
                return true;
            } else
                return false;
        }

        // return true, in case clauses don't resolve, so that we keep moving forward with other comparisons
        return true;
    }

    public boolean resolve() {
        List<Clause> clauses = this.knowledgeBase.getClauses();

        while (this.knowledgeBase.getClauses().size() > this.previousClauseCount) {
            this.previousClauseCount = this.knowledgeBase.getClauses().size();

            // Take first clause to compare against
            for (int i = 0; i < clauses.size(); i++) {
                Clause currentClause = clauses.get(i);

                for (int k = i + 1; k < clauses.size(); k++) {
                    Clause compareToClause = clauses.get(k);

                    if (!this.compareClauses(currentClause, compareToClause))
                        return false;
                }

            }

            clauses = this.knowledgeBase.getClauses();
        }

        // return true if we could not find an empty clause and aren't able to add any more clauses to kb
        return true;
    }

}
