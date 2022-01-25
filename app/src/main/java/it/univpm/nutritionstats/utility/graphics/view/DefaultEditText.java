package it.univpm.nutritionstats.utility.graphics.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.EditText;

import it.univpm.nutritionstats.enums.TextColor;
import it.univpm.nutritionstats.enums.TextSize;

public class DefaultEditText extends DefaultView {
    private final Context   context;
    private final String    hint;
    private final TextSize  textSize;
    private final TextColor textColor;

    public DefaultEditText(Context context, String hint, TextSize textSize, TextColor textColor) {
        this.context = context;
        this.hint = hint;
        this.textSize = textSize;
        this.textColor = textColor;

        super.view=generateView();
    }

    private EditText generateView() {
        EditText editText = new EditText(context);
        switch (textColor) {
            case DARK:
                editText.setTextColor(Color.DKGRAY);
                break;
            case LIGHT:
                editText.setTextColor(Color.LTGRAY);
                break;
        }
        editText.setTextSize(textSize.getSize());
        editText.setHint(hint);
        editText.setTypeface(Typeface.DEFAULT_BOLD);
        return editText;
    }

    @Override
    public EditText getView() {
        return (EditText) view;
    }
}
