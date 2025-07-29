package model;

public class Substitution {
    public String variable;
    public Argument substitute;

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public Argument getSubstitute() {
        return substitute;
    }

    public void setSubstitute(Argument substitute) {
        this.substitute = substitute;
    }
}
