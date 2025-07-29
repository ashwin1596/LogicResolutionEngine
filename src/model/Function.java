package model;

public class Function {
    public String name;
    public Argument args; // assuming that no function will have more than one args

    public Function() {

    }

    public Function(String name, Argument args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Argument getArgs() {
        return args;
    }

    public void setArgs(Argument args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", name, args);
    }
}
