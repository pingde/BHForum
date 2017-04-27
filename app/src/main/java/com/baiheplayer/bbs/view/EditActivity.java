package com.baiheplayer.bbs.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baiheplayer.bbs.R;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * 编辑界面
 * Created by Administrator on 2016/11/16.
 */
@ContentView(R.layout.activity_edit)
public class EditActivity extends BaseActivity {
    @ViewInject(R.id.et_input)
    private EditText mInput;
    @ViewInject(R.id.tv_edit_title)
    private TextView mTitle;
    private Intent intent;

    @Override
    public void onView(Bundle b ) {
        intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        mTitle.setText(title);
        if (content != null) {
            mInput.setText(content);
            mInput.setSelection(content.length());
        }

    }

    @Event(R.id.img_back)
    private void closeActivity(View view) {
        finish();
    }

    @Event(value = {R.id.img_edit_ensure})
    private void dealClickEvent(View view) {
        intent.putExtra("newContent", mInput.getText().toString().trim());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
