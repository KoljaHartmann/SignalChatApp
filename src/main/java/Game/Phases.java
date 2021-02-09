package Game;

public enum  Phases {

    DRAFTING,
    RESEARCH,
    ACTION,
    PRODUCTION;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
