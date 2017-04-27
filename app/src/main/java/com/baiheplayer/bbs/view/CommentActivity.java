package com.baiheplayer.bbs.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baiheplayer.bbs.R;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/11/17.
 */
@ContentView(R.layout.activity_comment)
public class CommentActivity extends BaseActivity {

    @ViewInject(R.id.et_input)
    private EditText mInput;
    private String commentId;
    private String cmtContent;
    private Intent intent;

    @Override
    public void onView(Bundle savedInstanceState) {
        intent = getIntent();
        commentId = intent.getStringExtra("commentId");

    }

    @Event(value = {R.id.tv_comment_cancel})
    private void closeActivity(View view) {
        finish();
    }

    @Event(value = {R.id.img_comment_ensure})
    private void dealClickEvent(View view) {
        cmtContent = mInput.getText().toString().trim();
        intent.putExtra("commentId",commentId);
        intent.putExtra("cmtContent", cmtContent);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
