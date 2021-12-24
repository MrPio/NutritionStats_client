package it.univpm.nutritionstats.utility;

import it.univpm.nutritionstats.R;

public enum Elements {
    CARBOHYDRATE(R.drawable.carbohydrate),
    PROTEIN(R.drawable.protein),
    LIPID(R.drawable.lipid),
    VITAMIN_A(R.drawable.vitamin_a),
    VITAMIN_C(R.drawable.vitamin_c),
    SODIUM(R.drawable.sodium),
    CALCIUM(R.drawable.calcium),
    POTASSIUM(R.drawable.potassium),
    IRON(R.drawable.iron),

    FIBER(R.drawable.fiber),
    WATER_FROM_FOOD(R.drawable.water_from_food);

    private int drawable;

    Elements(int drawable) {
        this.drawable = drawable;
    }

    public int getDrawable() {
        return drawable;
    }
}

