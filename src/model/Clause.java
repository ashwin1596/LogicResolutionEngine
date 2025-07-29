package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Clause {
    public Set<String> predNames;
    public List<Predicate> predicates;
    public List<Substitution> substitutions;

    public Clause(List<Predicate> predicates, Set<String> predNames) {
        this.predicates = predicates;
        this.predNames = predNames;
        this.substitutions = new ArrayList<>();
    }

    public Predicate getPredicateByName(String predName) {
        for (Predicate pred : this.predicates) {
            if (pred.getName().equals(predName))
                return pred;
        }

        return null;
    }

    public int getPredCount() {
        return this.predicates.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Clause otherClause = (Clause) o;
        if (this.predicates.size() != otherClause.predicates.size())
            return false;

        for (Predicate thisPred : this.predicates) {
            boolean isMatched = false;
            for (Predicate otherPred : otherClause.predicates) {
                if (thisPred.equals(otherPred)) {
                    isMatched = true;
                    break;
                }
            }

            if (!isMatched)
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(predNames, predicates);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        for (int i = 0; i < this.predicates.size() - 1; i++) {
            sb.append(this.predicates.get(i));
            sb.append(" ");
        }
        sb.append(this.predicates.get(this.predicates.size() - 1));
        sb.append("]");

        return sb.toString();
    }

    public List<Substitution> getSubstitutions() {
        return this.substitutions;
    }

    public void addSubstitution(Substitution substitution) {
        this.substitutions.add(substitution);
    }
}
