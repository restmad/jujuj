package com.shinado.tagme.common;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

    public static void toastUnknownError(Context context){
        Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
    }

}
