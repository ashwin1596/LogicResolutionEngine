package resolvers;

import model.Clause;
import model.KnowledgeBase;
import model.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropLogicResolver extends Resolver {

    public PropLogicResolver(KnowledgeBase knowledgeBase) {
        super(knowledgeBase);
    }

    @Override
    Clause getResolvent(Clause currentClause, Clause compareToClause, Predicate currentPred, Predicate compareToPred) {

        List<Predicate> newPredicates = new ArrayList<>();
        Set<String> newPredNames = new HashSet<>();

        for (Predicate pred : currentClause.predicates) {
            if (pred != currentPred) {
                newPredicates.add(pred);
                newPredNames.add(pred.getName());
            }
        }

        for (Predicate pred : compareToClause.predicates) {
            if (pred != compareToPred) {
                newPredicates.add(pred);
                newPredNames.add(pred.getName());
            }
        }

        return new Clause(newPredicates, newPredNames);
    }

    @Override
    boolean comparePredicates(Clause clause1, Clause clause2, Predicate predicate1, Predicate predicate2) {

        return !predicate1.getSign().equals(predicate2.getSign());
    }
}
