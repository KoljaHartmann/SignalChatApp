package RoboRock.Enums;

public enum Zone implements Command{

    SCHLAFZIMMER(null),
    KUECHE(-2),
    ESSZIMMER(-3),
    WOHNZIMMER(-4),
    FLUR(-5);


    private final Integer value;

    Zone(Integer value) {
        this.value = value;
    }

    public Integer getNumber() {
        return value;
    }

}
