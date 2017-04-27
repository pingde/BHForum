package com.baiheplayer.bbs.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.Article;
import com.baiheplayer.bbs.inter.IImageListener;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.baiheplayer.bbs.view.NetActivity;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/31.
 */

public class ArticleAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Article> articles;

    private int screenWidth;
//    private IImageListener mListener;

    public ArticleAdapter(Context context, List articles, IImageListener mListener) {
        this.context = context;
        this.articles = articles;
//        this.mListener = mListener;
        screenWidth = Constant.screenWidth;
    }

    public ArticleAdapter() {
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_news_content, null);
        return new ArticleVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Article article = articles.get(position);
        ArticleVH vh = (ArticleVH) holder;
        final List<String> picUrl = new ArrayList<>();
        for (int i = 0; i < article.getImages().size(); i++) {
            if (!TextUtils.isEmpty(article.getImages().get(i))) {
                picUrl.add(article.getImages().get(i));
            }
        }
        int picNum = picUrl.size();
        int ll_width = screenWidth == 0 ? 1080 : (int) (screenWidth * 0.92);//
        int width = 0;//不同屏幕不一样
        int height = 0;
        if (picNum == 1) {
            width = ll_width;                   //ViewGroup.LayoutParams.WRAP_CONTENT
            height = (int) (ll_width * 0.5625); //ViewGroup.LayoutParams.WRAP_CONTENT ;
        } else if (picNum == 2) {
            width = ll_width / 2;
            height = ll_width / 2;
        } else if (picNum == 3) {
            width = ll_width / 3;
            height = ll_width / 3;
        }
        vh.picContainer.removeAllViews();
        ImageOptions options = new ImageOptions.Builder().setUseMemCache(true).build();
        for (int i = 0; i < picNum; i++) {
            final String url = picUrl.get(i);
            final int _i = i;
            ImageView image = new ImageView(context);
            image.setLayoutParams(new LinearLayout.LayoutParams(width, height)); //设置图片的大小
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);  //SmoothImageView的硬性要求
            x.image().bind(image, url, options);
            vh.picContainer.addView(image);
            if (i < picNum - 1) {                //添加空隙
                ImageView space = new ImageView(context);
                space.setLayoutParams(new LinearLayout.LayoutParams(10, height));
                vh.picContainer.addView(space);
            }
        }
        vh.title.setText(article.getTitle());
        vh.date.setText(SaveUtils.formatTime(String.valueOf(article.getDate())));
        vh.author.setText(article.getAuthor());
        final String url = article.getUrl();
        vh.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, NetActivity.class);
                intent.putExtra("title", article.getTitle());
                intent.putExtra("url", article.getUrl()); // TODO: 2017/2/15
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
    private class ArticleVH extends RecyclerView.ViewHolder {

        CardView card;              //正文
        TextView title;             //标题
        TextView author;            //作者
        TextView date;              //时间戳
        LinearLayout picContainer;  //图片集

        public ArticleVH(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.card);
            title = (TextView) itemView.findViewById(R.id.tv_news_title);
            author = (TextView) itemView.findViewById(R.id.tv_news_author);
            date = (TextView) itemView.findViewById(R.id.tv_news_date);
            picContainer = (LinearLayout) itemView.findViewById(R.id.ll_pic_container);
        }
    }
}





