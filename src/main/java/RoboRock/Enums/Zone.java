package RoboRock.Enums;

public enum Zone implements Command{

    SCHLAFZIMMER(null),
    KUECHE(-1),
    ESSZIMMER(-2),
    WOHNZIMMER(-3),
    FLUR(-4);


    private final Integer value;

    Zone(Integer value) {
        this.value = value;
    }

    public Integer getNumber() {
        return value;
    }

}
