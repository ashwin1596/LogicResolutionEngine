package model;

public class Argument implements Cloneable {
    public String constant;
    public String variable;

    public Function function;
    public String argType;

    public Argument(String constant, String variable, Function function) {
        this.setConstant(constant);
        this.setVariable(variable == null ? "" : variable);
        this.setFunction(function);
    }

    public Argument() {

    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
        if (constant != null)
            this.argType = "constant";
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
        if (variable != null) {
            if (!variable.isEmpty())
                this.argType = "variable";
        }
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
        if (function != null)
            this.argType = "function";
    }

    public String getArgType() {
        return this.argType;
    }

    @Override
    public String toString() {
        if (constant != null)
            return constant;
        else if (variable != null)
            return variable;
        else if (function != null)
            return function.toString();

        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Argument otherArgument = (Argument) o;

        return this.toString().equals(otherArgument.toString());
    }

    @Override
    public Argument clone() {
        try {
            Argument clone = (Argument) super.clone();
            if (this.constant != null)
                clone.setConstant(this.constant);
            else if (this.variable != null)
                clone.setVariable(this.variable);
            else if (this.function != null)
                clone.setFunction(new Function(this.function.getName(), this.function.getArgs()));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void substituteVariableForConst(String constant) {
        this.variable = null;

        this.setConstant(constant);
    }

    public void substituteVariableForFunction(Function function) {
        this.variable = null;

        this.setFunction(function);
    }
}
