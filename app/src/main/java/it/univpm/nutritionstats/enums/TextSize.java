package it.univpm.nutritionstats.enums;

public enum TextSize {
    SMALL(14),
    MEDIUM(16),
    BIG(18),
    HUGE(22);

    private final int size;

    TextSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
