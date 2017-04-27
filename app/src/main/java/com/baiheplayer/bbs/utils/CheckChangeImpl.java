package com.baiheplayer.bbs.utils;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by Administrator on 2017/2/22.
 */

public class CheckChangeImpl implements CompoundButton.OnCheckedChangeListener {
    private EditText mEdit;

    public CheckChangeImpl() {
    }

    public CheckChangeImpl(EditText edit) {
        this.mEdit = edit;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            mEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}
