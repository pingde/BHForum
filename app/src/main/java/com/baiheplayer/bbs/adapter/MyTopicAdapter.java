package com.baiheplayer.bbs.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.Topic;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.IRefreshListener;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.baiheplayer.bbs.view.DetailActivity;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/2/10.
 */

public class MyTopicAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Topic> lists;
    private IRefreshListener i_refresh;

    public MyTopicAdapter() {
    }

    public MyTopicAdapter(Context context, List list) {
        this.context = context;
        this.lists = list;
    }

    public void addRefreListener(IRefreshListener listener) {
        this.i_refresh = listener;
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_my_topic, null);
        return new MyTopicEntry(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Topic topic = lists.get(position);
        if (holder instanceof MyTopicEntry) {
            ((MyTopicEntry) holder).onBind(context,topic,i_refresh);

        }

    }



}





