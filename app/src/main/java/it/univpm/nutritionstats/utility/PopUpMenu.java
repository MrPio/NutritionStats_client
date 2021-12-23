package it.univpm.nutritionstats.utility;

import android.widget.ImageView;

import java.util.ArrayList;

public class PopUpMenu {
    private final ImageView    imageView;
    private final int          drawable;
    private final int          drawableHover;
    private final Sound.Sounds sound;

    public PopUpMenu(ImageView imageView, int drawable, int drawableHover, Sound.Sounds sound) {
        this.imageView = imageView;
        this.drawable = drawable;
        this.drawableHover = drawableHover;
        this.sound = sound;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getDrawable() {
        return drawable;
    }

    public int getDrawableHover() {
        return drawableHover;
    }

    public Sound.Sounds getSound() {
        return sound;
    }
}
