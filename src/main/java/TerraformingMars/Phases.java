package TerraformingMars;

public enum  Phases {

    DRAFTING,
    RESEARCH,
    ACTION,
    END,
    PRODUCTION,
    PRELUDES;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
