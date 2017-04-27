package com.baiheplayer.bbs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.Topic;
import com.baiheplayer.bbs.inter.IImageListener;


import java.util.HashMap;
import java.util.List;

/**
 * 论坛帖子的适配
 * Created by Administrator on 2016/11/14.
 */

public class TopicAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Topic> topics;


    private RecyclerView.LayoutParams mLayoutParams;
    private HashMap<String, Boolean> storeMem;


    public List<Topic> getList() {
        return topics;
    }


    public TopicAdapter() {
    }


    public TopicAdapter(Context context, List topics) {
        this.context = context;
        this.topics = topics;

        storeMem = new HashMap<>();
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_forum_content, null);
        return new TopicEntry(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final TopicEntry vh = (TopicEntry) holder;
        final Topic topic = topics.get(position);
        vh.onBind(context,topic,storeMem);

    }

}


