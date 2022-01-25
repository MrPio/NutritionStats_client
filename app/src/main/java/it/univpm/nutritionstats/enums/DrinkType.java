package it.univpm.nutritionstats.enums;

public enum DrinkType {
    GLASS(200f),
    CUP(260f),
    MUG(350f),
    ML_500(500f),
    ML_750(750f),
    ML_1000(1000f);

    private float value;

    DrinkType(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
