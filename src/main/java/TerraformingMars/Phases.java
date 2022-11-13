package TerraformingMars;

public enum  Phases {
    //TODO Game.Phases.INITIAL_DRAFTING

    DRAFTING,
    RESEARCH,
    ACTION,
    PRODUCTION;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
