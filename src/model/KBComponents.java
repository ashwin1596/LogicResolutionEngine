package model;

public enum KBComponents {
    PREDICATES("Predicates:"),
    VARIABLES("Variables:"),
    CONSTANTS("Constants:"),
    FUNCTIONS("Functions:"),
    CLAUSES("Clauses:");

    private final String componentName;
    private final boolean isRead;

    KBComponents(String componentName){
        this.componentName = componentName;
        this.isRead = false;
    }


}
