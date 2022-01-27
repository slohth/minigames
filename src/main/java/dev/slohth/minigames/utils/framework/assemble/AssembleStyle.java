package dev.slohth.minigames.utils.framework.assemble;

public enum AssembleStyle {

    KOHI(true, 15),
    VIPER(true, -1),
    MODERN(false, 1);

    private final boolean descending;
    private final int startNumber;

    AssembleStyle(boolean descending, int startNumber) {
        this.descending = descending;
        this.startNumber = startNumber;
    }

    public boolean isDescending() {
        return descending;
    }

    public int getStartNumber() {
        return startNumber;
    }
}
