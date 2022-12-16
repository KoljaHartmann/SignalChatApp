package RoboRock.Enums;

public enum Zone implements Command{

    SCHLAFZIMMER(null),
    MULTIZIMMER(-1),
    WOHNZIMMER(-2),
    KUECHE(-3),
    FLUR_BAD(-4);

    private final Integer value;

    Zone(Integer value) {
        this.value = value;
    }

    public Integer getNumber() {
        return value;
    }

}
