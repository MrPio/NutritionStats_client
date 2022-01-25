package it.univpm.nutritionstats.utility.graphics.dialog;

import android.content.Context;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

public class DefaultDropDownDialog {
    private ArrayList<String>               list;
    private int width;

    public DefaultDropDownDialog(ArrayList<String> list,  int width) {
        this.list = list;
        this.width = width;
    }

    public PopupWindow popupWindows(Context context) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, list);
        ListView listViewSort = new ListView(context);
        listViewSort.setAdapter(adapter);
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(width);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listViewSort);

        return popupWindow;
    }
}
