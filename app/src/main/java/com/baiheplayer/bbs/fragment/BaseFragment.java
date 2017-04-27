package com.baiheplayer.bbs.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.x;

/**
 * Created by Administrator on 2016/11/7.
 */

public abstract class BaseFragment extends Fragment implements View.OnTouchListener{
    private boolean injected = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        App app = (App) (getActivity().getApplication());
//        DbManager db = x.getDb(app.getDaoConfig());
//        getData(db);
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        injected = true;
        return x.view().inject(this,inflater,container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!injected){
            x.view().inject(this,this.getView());
        }
        onView();
    }
//    public abstract void getData(DbManager db);
    public abstract void getData();
    public abstract void onView();


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //拦截事件
        return true;
    }

}
