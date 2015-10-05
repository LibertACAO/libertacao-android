package com.libertacao.libertacao.util;

import android.text.TextUtils;
import android.widget.EditText;

import com.libertacao.libertacao.MyApp;
import com.libertacao.libertacao.R;

public class Validator {
    private Validator(){

    }

    public static boolean validate(EditText editText, boolean ret) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError(MyApp.getAppContext().getString(R.string.obrigatorio));
            if (ret) {
                editText.requestFocus();
            }
            return false;
        }
        return ret;
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
