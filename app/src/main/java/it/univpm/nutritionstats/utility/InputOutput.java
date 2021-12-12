package it.univpm.nutritionstats.utility;

import android.content.Context;

public interface InputOutput {
    String readFile();

    boolean writeFile(String sBody);

    boolean existFile();
}
