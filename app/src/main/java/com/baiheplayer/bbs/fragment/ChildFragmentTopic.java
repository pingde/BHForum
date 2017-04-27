package com.baiheplayer.bbs.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.adapter.TopicAdapter;
import com.baiheplayer.bbs.bean.Topic;
import com.baiheplayer.bbs.bean.TopicsBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.common.ImageClickListenerImpl;
import com.baiheplayer.bbs.inter.FlushCallback;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;

import org.xutils.DbManager;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/31.
 */
@ContentView(R.layout.fragment_child)
public class ChildFragmentTopic extends BaseFragment {
    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView;

    private Context context;
    private List<Topic> topics;
    private TopicAdapter adapter;
    private int currentPage = 1;
    private int totalPage = 1;

    @Override
    public void getData() {
        context = getContext();
        topics = new ArrayList<>();
        adapter = new TopicAdapter(context, topics);
    }

    @Override
    public void onView() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(adapter);
        getCurrentPage(currentPage, new FlushCallback() {
            @Override
            public void onSuccess() {

            }
        });
    }



    private void getCurrentPage(int page, final FlushCallback callback) {
        if (page > totalPage ||page<0) {
            callback.onSuccess();
            return;
        }
        RequestParams params = new RequestParams(HttpDatas.HOT_TOPIC);
        params.addParameter("pcurrent", page);
        HttpsUtils.getInstance().requestSavely(params, TopicsBack.class, new INetCallBack() {
            @Override
            public <T> void back(String type,T resultInfo) {
                TopicsBack data = (TopicsBack)resultInfo;
                if((data).getCode() == 0){
                    totalPage = Integer.valueOf(data.getTotal());  //totalPage
                    currentPage = Integer.valueOf(data.getCurrent());  //currentPage
                    topics.addAll(data.getTopicList());
                }
                adapter.notifyDataSetChanged();
                callback.onSuccess();
            }
            @Override
            public void fail(boolean isOnCallBack) {
                callback.onSuccess();
            }
        });
    }

    /**
     * 下拉刷新
     */
    public void refresh(FlushCallback callback) {
        topics.clear();
        currentPage = 1;
        getCurrentPage(currentPage,callback);
    }

    /**
     * 上拉加载
     */
    public void loadMore(FlushCallback callback) {
        currentPage++;
        getCurrentPage(currentPage,callback);
    }

//    private class ImageClickListenerImpl implements IImageListener {
//
//        @Override
//        public void onImageClick(View view, List<String> urls, int position) {
//
//            int[] location = new int[2];
//            view.getLocationOnScreen(location);
//            int height = view.getHeight();
//            int width = view.getWidth();
//            Log.i("chen", "点击在屏幕上的位置" + location[0] + " " + location[1] + " " + width + " " + height);
//            Intent intent = new Intent(context, PictureActivity.class);
//            intent.putExtra("images", (ArrayList) urls);
//            intent.putExtra("position", position);
//            if (Build.VERSION.SDK_INT < 21) {
//                startActivity(intent);
//                getActivity().overridePendingTransition(0, 0);
//            } else {
//                ActivityOptionsCompat options = ActivityOptionsCompat
//                        .makeScaleUpAnimation(view, location[0] + width / 2, location[1] + height / 2, width, height);
//                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
//            }
//
//        }
//    }
}
