package TerraformingMars;

public enum  Phases {

    DRAFTING,
    RESEARCH,
    ACTION,
    END,
    PRODUCTION;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
