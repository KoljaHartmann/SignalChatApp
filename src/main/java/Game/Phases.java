package Game;

public enum  Phases {

    DRAFTING,
    RESEARCH,
    ACTION;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
