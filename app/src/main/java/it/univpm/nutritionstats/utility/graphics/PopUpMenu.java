package it.univpm.nutritionstats.utility.graphics;

import android.widget.ImageView;

import it.univpm.nutritionstats.utility.sound.Sound;

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
