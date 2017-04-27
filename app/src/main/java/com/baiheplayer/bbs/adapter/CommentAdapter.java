package com.baiheplayer.bbs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.Comment;
import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */

public class CommentAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Comment> comments;
    private String topicId;


    private static int TYPE_HAVE_COMMENT = 1;
    private static int TYPE_NONE_COMMENT = 0;

    public CommentAdapter() {
    }

    public CommentAdapter(Context context,String topicId, List comments) {
        this.context = context;
        this.topicId = topicId;
        this.comments = comments;

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(comments.size() == 0){
            return TYPE_NONE_COMMENT;
        }
        return TYPE_HAVE_COMMENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_NONE_COMMENT){

        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum_comment, null);
        return new CommentEntry(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        final CommentEntry entry = (CommentEntry) holder;

        entry.onBind(context,topicId,comment);
    }

}
