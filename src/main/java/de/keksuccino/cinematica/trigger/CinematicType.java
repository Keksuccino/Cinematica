package de.keksuccino.cinematica.trigger;

public enum CinematicType {

    CUTSCENE("cutscene"),
    AUDIO("audio");

    protected final String name;

    CinematicType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static CinematicType getByName(String name) {
        for (CinematicType t : CinematicType.values()) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

}
