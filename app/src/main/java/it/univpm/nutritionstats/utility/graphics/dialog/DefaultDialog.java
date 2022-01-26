package it.univpm.nutritionstats.utility.graphics.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

public class DefaultDialog {
    private final View                            viewToAdd;
    private final String                          title;
    private final DialogInterface.OnClickListener positiveButtonFunction;
    private final DialogInterface.OnClickListener negativeButtonFunction;

    public DefaultDialog(View viewToAdd, String title, DialogInterface.OnClickListener positiveButtonPredicate,
                  DialogInterface.OnClickListener negativeButtonPredicate) {
        this.viewToAdd = viewToAdd;
        this.title = title;
        this.positiveButtonFunction = positiveButtonPredicate;
        this.negativeButtonFunction = negativeButtonPredicate;
    }

    public void spawnDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(viewToAdd);
        if (positiveButtonFunction != null)
            builder.setPositiveButton("Confirm", positiveButtonFunction);
        if (negativeButtonFunction != null)
            builder.setNegativeButton("Cancel", negativeButtonFunction);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
