package model;

import java.util.ArrayList;
import java.util.List;

public class Predicate {
    public String name;
    public Sign sign;
    public List<Argument> args;

    public Predicate() {
    }

    public Predicate(String name, Sign sign, List<Argument> arguments) {
        this.name = name;
        this.sign = sign;
        this.args = arguments == null ? new ArrayList<>() : arguments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public List<Argument> getArgs() {
        return args;
    }

    public void setArgs(List<Argument> args) {
        this.args = args;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sign);
        sb.append(name);

        if (!this.args.isEmpty()) {
            sb.append("(");
            for (int i = 0; i < this.args.size() - 1; i++) {
                sb.append(this.args.get(i));
                sb.append(",");
            }
            sb.append(this.args.get(this.args.size() - 1));
            sb.append(")");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Predicate otherPred = (Predicate) o;

        if (!this.name.equals(otherPred.getName()))
            return false;

        if (this.sign != otherPred.getSign())
            return false;

        if (this.args.size() != otherPred.getArgs().size())
            return false;

        for (int i = 0; i < this.args.size(); i++) {
            Argument arg1 = this.args.get(i);
            Argument arg2 = otherPred.getArgs().get(i);

            if (!arg1.getArgType().equals(arg2.getArgType()))
                return false;
        }

        return true;
    }

    public void addArgs(Argument arg) {
        this.args.add(arg);
    }
}
