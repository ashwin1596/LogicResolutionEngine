package model;

public enum Sign {
    POS,
    NEG;

    @Override
    public String toString() {
        return switch (this.ordinal()) {
            case 0 -> "";
            case 1 -> "!";
            default -> null;
        };
    }
}
