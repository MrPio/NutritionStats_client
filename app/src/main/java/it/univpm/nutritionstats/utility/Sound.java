package it.univpm.nutritionstats.utility;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;

import java.io.IOException;

import it.univpm.nutritionstats.R;

public class Sound {
    public enum Sounds{
        SLIDE_IN(R.raw.fx_menu_laterale_in),
        SLIDE_OUT_2(R.raw.fx_menu_laterale_out_2),
        BIP_1(R.raw.other_blip_select),
        BIP_2(R.raw.other_blip_select2),
        BIP_3(R.raw.other_blip_select3),
        BIP_4(R.raw.other_blip_select4),
        BIP_5(R.raw.other_blip_select5),
        BIP_6(R.raw.other_blip_select6),
        BIP_7(R.raw.other_blip_select7),
        BIP_8(R.raw.other_blip_select8),
        BIP_9(R.raw.other_blip_select9),
        BIP_10(R.raw.other_blip_select10),
        BIP_11(R.raw.other_blip_select11),
        BIP_12(R.raw.other_blip_select12),
        BIP_13(R.raw.other_blip_select13),
        PICKUP_COIN(R.raw.other_pickup_coin),
        WATER_SPLASH(R.raw.water_splash),
        FOOD_1(R.raw.food1),
        FOOD_2(R.raw.food2),
        BADGE(R.raw.fx_badge),
        NO_BUY(R.raw.fx_no_buy);

        public int res;

        Sounds(int res) {
            this.res = res;
        }
    }
    public static void makeSound(Context context,Sound.Sounds sound) {
        new Thread(() -> {
            final MediaPlayer[] mPlayer =
                    {MediaPlayer.create(context, sound.res)};
            mPlayer[0].setOnCompletionListener(mpd -> {
                mPlayer[0].release();
                mPlayer[0] = null;
            });
            mPlayer[0].start();
        }).start();
    }
}
