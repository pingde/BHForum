package com.baiheplayer.bbs.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.LocalInfo;
import com.baiheplayer.bbs.bean.map.Area;
import com.baiheplayer.bbs.bean.map.City;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.map.Province;
import com.baiheplayer.bbs.bean.local.TokenBack;
import com.baiheplayer.bbs.bean.local.UserBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.inter.ITokenFinish;
import com.baiheplayer.bbs.ui.SubPullToRefresh;
import com.baiheplayer.bbs.utils.FileUtils;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.InfoHelper;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人资料界面
 * Created by Administrator on 2016/11/14.
 */
@ContentView(R.layout.activity_person)
public class PersonActivity extends BaseActivity {

    @ViewInject(R.id.pull_to_refresh)
    private SubPullToRefresh pullToRefresh;
    @ViewInject(R.id.img_user_avatar)      //头像
    private ImageView userAvatar;
    @ViewInject(R.id.tv_user_name)         //昵称
    private TextView userName;
    @ViewInject(R.id.tv_user_signature)    //签名
    private TextView userSignature;
    @ViewInject(R.id.tv_user_phone)        //电话
    private TextView userPhone;
    @ViewInject(R.id.tv_user_email)        //邮箱
    private TextView userEmail;
    @ViewInject(R.id.tv_user_address)      //地址
    private TextView userAddress;
    @ViewInject(R.id.tv_user_flush)        //刷新
    private TextView mFlush;
    private DbManager db;
    private boolean isChange;
    private boolean avatarIsChange;
    private String avatarPath;
    private LocalInfo info;
    private List<Province> provinces;
    private List<City> cities;
    private List<Area> areas;
    private List<String> pNames, cNames, aNames;      //spinner的数据源
    private String provinceId, cityId, areaId;        //地区id

    @Override
    public void onView(Bundle b) {
        getUserInfoFromNet();
        isChange = false;
        avatarIsChange = false;
        provinces = new ArrayList<>();
        cities = new ArrayList<>();
        areas = new ArrayList<>();
        pNames = new ArrayList<>();
        cNames = new ArrayList<>();
        aNames = new ArrayList<>();
        info = InfoHelper.getInstance().getInfo();
        getSaveFile();
        displayMsg();
        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                getUserInfoFromNet();
            }
        });
    }

    private void getSaveFile() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                provinces = FileUtils.readData(PersonActivity.this, "map_list.txt");
                for (int i = 0; i < provinces.size(); i++) {
                    pNames.add(provinces.get(i).getName());
                }
            }
        }.start();
    }

    /**
     * 先数据库后网络调取用户信息
     */
    private void displayMsg() {
        ImageOptions options = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(userAvatar, Constant.USER_AVATAR, options);

        userName.setText(Constant.USER_NICKNAME);
        userSignature.setText(Constant.USER_SIGN);
        userPhone.setText(Constant.USER_PHONE);
        userEmail.setText(Constant.USER_EMAIL);
        userAddress.setText(Constant.USER_ADDR);
    }

    private void getUserInfoFromNet() {
        RequestParams params = new RequestParams(HttpDatas.USER_INFO);

        HttpsUtils.getInstance().getObjectFromNet(params, UserBack.class, new ISimpleNetCallBack<UserBack>() {
            @Override
            public void back(UserBack resultInfo) {
                if (resultInfo.getCode() == 0) {
                    Log.i("chen","返回信息："+(new Gson().toJson(resultInfo)));
                    info.setUserId(resultInfo.getUserId());
                    info.setNickname(resultInfo.getNickname());
                    info.setAvatar(resultInfo.getAvatar());
                    info.setSignature(resultInfo.getSignature());
                    info.setAddressFull(resultInfo.getAddressFull());
                    info.setPhone(resultInfo.getPhone());
                    info.setEmail(resultInfo.getEmail());
                    ImageOptions options = new ImageOptions.Builder().setCircular(true).build();

                    displayMsg();//再执行一遍
                } else {
                    showInfo(resultInfo.getMsg());
                }
                pullToRefresh.onRefreshComplete();
            }

            @Override
            public void fail(boolean isOnCallBack) {
                pullToRefresh.onRefreshComplete();
                showInfo("网络错误");
            }
        });
    }


    /**
     * 退出登录
     *
     * @param view
     */
    @Event(value = {R.id.btn_user_exit})
    private void sendHttpLogout(View view) {
        if (SaveUtils.isNetworkConnected(this)) {
            removeUserInfo();//没有放在成功退出之后,网络差会成功但没有重新获取AccessToken
            RequestParams params = new RequestParams(HttpDatas.USER_LOGOUT);
            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                @Override
                public void back(DataBack resultInfo) {
                    showInfo(resultInfo.getCode() == 0 ? "退出成功" : resultInfo.getMsg());
                }

                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误");
                }
            });
        } else {
            showInfo("网络异常");
        }
    }

    private void removeUserInfo() {
        userName.setText(null);
        userAddress.setText(null);
        userSignature.setText(null);
        userPhone.setText(null);
        userEmail.setText(null); //重新获取一个Token
        InfoHelper.getInstance().clearUser();
        HttpsUtils.getInstance().getToken(new ITokenFinish() {
            @Override
            public void isOk(Boolean success) {
                Log.i("chen", "重新获取Token");
                if (success) {
                    Intent intentToMain = new Intent(PersonActivity.this, MainActivity.class);
                    String loginName = getSharedPreferences("LocalMessage", MODE_PRIVATE).getString("loginName", null);
                    intentToMain.putExtra("loginName", loginName);
                    startActivity(intentToMain);
                }
            }
        });

    }


    /**
     * 修改资料
     *
     * @param view
     */
    @Event(value = {R.id.rl_user_avatar, R.id.rl_user_name, R.id.rl_user_signature,
            R.id.rl_user_phone, R.id.rl_user_email, R.id.rl_user_address,})
    private void dealClickEvent(View view) {
        switch (view.getId()) {
            case R.id.rl_user_name:
                Intent intent2Nick = new Intent(this, EditActivity.class);
                intent2Nick.putExtra("title", "设置昵称");
                intent2Nick.putExtra("content", userName.getText().toString().trim());
                startActivityForResult(intent2Nick, 1);
                break;
            case R.id.rl_user_signature:
                Intent intent2Sign = new Intent(this, EditActivity.class);
                intent2Sign.putExtra("title", "设置签名");
                intent2Sign.putExtra("content", userSignature.getText().toString().trim());
                startActivityForResult(intent2Sign, 2);
                break;
            case R.id.rl_user_phone:
                Intent intent2Phone = new Intent(this, ResetActivity.class);
                intent2Phone.putExtra("title", "手机");
                startActivityForResult(intent2Phone, 3);
                break;
            case R.id.rl_user_email:
                Intent intent2Email = new Intent(this, ResetActivity.class);
                intent2Email.putExtra("title", "邮箱");
                startActivityForResult(intent2Email, 4);
                break;
            case R.id.rl_user_address:
//                getEditData(5, userAddress.getText().toString());
                optionAboutAddress();
                break;
            case R.id.rl_user_avatar:
                Intent intent2Avatar = new Intent();
                intent2Avatar.putExtra("title", "设置头像");
                intent2Avatar.putExtra("content", Constant.USER_AVATAR);
                intent2Avatar.setClass(this, PhotoActivity.class);
                startActivityForResult(intent2Avatar, 6);
                break;
        }
    }

//    private void getEditData(int i, String str) {
//        Intent intent = new Intent(this, EditActivity.class);
//        intent.putExtra("content", str);
//        startActivityForResult(intent, i);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String newStr = bundle.getString("newContent");
            switch (requestCode) {
                case 1:
                    userName.setText(newStr);
                    info.setNickname(newStr);
                    isChange = true;
                    break;
                case 2:
                    userSignature.setText(newStr);
                    info.setSignature(newStr);
                    isChange = true;
                    break;
                case 3:
                    userPhone.setText(newStr);
                    info.setPhone(newStr);
                    isChange = true;
                    break;
                case 4:
                    userEmail.setText(newStr);
                    info.setEmail(newStr);
                    isChange = true;
                    break;
                case 5:
                    userAddress.setText(newStr);       //修改地址栏.弹窗下不会到这儿
                    info.setAddressFull(newStr);
                    isChange = true;
                    break;
                case 6:
                    x.image().bind(userAvatar, newStr);  //头像被修改咯，返回的图片路径可能是网络或者本地
                    avatarPath = newStr;
                    info.setAvatar(newStr);         //头像地址用的是本地的。

                    isChange = true;
                    if (!avatarPath.contains("http")) {   //判断是否为网络图片
                        avatarIsChange = true;
                    }
                    Log.i("chen", "返回的头像路径是" + newStr);
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (avatarIsChange) {
            setResult(Activity.RESULT_OK);   //通知MineFragment已经改变了头像
        }
    }

    /**
     * @param view
     */
    @Event(value = {R.id.img_back, R.id.img_arrow})
    private void RefreshData(View view) {
        finish();
    }

    /**
     * 有变化则更新内容
     *
     * @param v
     */
    @Event(value = R.id.tv_user_flush)
    private void flushChangeData(View v) {
        if (isChange) {
            sendHttpRefresh();
        }
    }

    private void sendHttpRefresh() {
        if (SaveUtils.isNetworkConnected(this)) {
            Snackbar.make(userAvatar, "更新中", Snackbar.LENGTH_LONG).show();
            RequestParams params = new RequestParams(HttpDatas.UPDATE_INFO);
            params.addParameter("nickname", userName.getText().toString().trim());
            params.addParameter("signature", userSignature.getText().toString().trim());
            params.addParameter("province", provinceId);
            params.addParameter("city", cityId);
            params.addParameter("area", areaId);
            if (avatarIsChange) params.addParameter("avatar", new File(avatarPath));
            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                @Override
                public void back(DataBack resultInfo) {
                    if (resultInfo.getCode() == 0) {
                        try {
                            db.update(info);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        showInfo("更新成功");
                    } else {
                        showInfo(resultInfo.getMsg());
                        showInfo("更新失败");
                    }
                }

                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误");
                }
            });
        } else {
            Log.i("chen", "更新失败");
        }
    }


    /**
     * 弹框选择地址
     */
    private String[] currentName = new String[3];

    private void optionAboutAddress() {

        int[] num = getLocationId();
        View layout = LayoutInflater.from(this).inflate(R.layout.item_person_addr, null);
        layout.setMinimumWidth((int) (Constant.screenWidth * 0.9));
        Spinner sp1 = (Spinner) layout.findViewById(R.id.choose_province);
        Spinner sp2 = (Spinner) layout.findViewById(R.id.choose_city);
        Spinner sp3 = (Spinner) layout.findViewById(R.id.choose_Area);
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this, R.layout.widget_spinner_text, pNames);
        final ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, R.layout.widget_spinner_text, cNames);
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, R.layout.widget_spinner_text, aNames);
        sp1.setAdapter(provinceAdapter);
        sp2.setAdapter(cityAdapter);
        sp3.setAdapter(areaAdapter);
        sp1.setSelection(num[0]);
        getCityNameList(num[0]);
        cityAdapter.notifyDataSetChanged();
        sp2.setSelection(num[1]);
        getAreaNameList(num[1]);
        areaAdapter.notifyDataSetChanged();
        sp3.setSelection(num[2]);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                provinceId = provinces.get(position).getId();          //获取当前选择的id
                currentName[0] = provinces.get(position).getName();    //获取当前选择的名字
                getCityNameList(position);
                cityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId = cities.get(position).getId();
                currentName[1] = cities.get(position).getName();
                getAreaNameList(position);
                areaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaId = areas.get(position).getId();
                currentName[2] = areas.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button cancel = (Button) layout.findViewById(R.id.cancel);
        Button ensure = (Button) layout.findViewById(R.id.ensure);
        final AlertDialog builder = new AlertDialog.Builder(this).setView(layout).show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChange = true;  //地址发生改变
                builder.dismiss();
                String _addressFull = currentName[0] + "|" + currentName[1] + "|" + currentName[2];
                userAddress.setText(_addressFull);
                info.setAddressFull(_addressFull);
            }
        });
    }

    private int[] getLocationId() {   //没有就会出现越界异常
        int[] num = new int[]{0, 0, 0};
        if (info.getAddressFull() != null) {
            String address = info.getAddressFull().replace("|", ",");
            String[] parts = address.split(","); //"杭州|富阳市|浙江"
            for (int i = 0; i < provinces.size(); i++) {
                if (parts[0].equals(provinces.get(i).getName())) {  //匹配省份
                    num[0] = i;
                    List<City> temp_cities = new ArrayList<>();
                    temp_cities.addAll(provinces.get(i).getChild());
                    for (int j = 0; j < temp_cities.size(); j++) {
                        if (parts[1].equals(temp_cities.get(j).getName())) {//匹配城市
                            num[1] = j;
                            List<Area> temp_areas = new ArrayList<>();
                            temp_areas.addAll(temp_cities.get(j).getChild());
                            for (int k = 0; k < temp_areas.size(); k++) {
                                if (parts[2].equals(temp_areas.get(k).getName())) {
                                    num[2] = k;
                                    return num;
                                }
                            }

                        }
                    }
                }
            }
        }
        return num;
    }

    private void getCityNameList(int position) {
        cities.clear();
        cities.addAll(provinces.get(position).getChild());
        cNames.clear();
        for (int i = 0; i < cities.size(); i++) {
            cNames.add(cities.get(i).getName());
        }
    }

    private void getAreaNameList(int position) {
        areas.clear();
        areas.addAll(cities.get(position).getChild());
        aNames.clear();
        for (int j = 0; j < areas.size(); j++) {
            aNames.add(areas.get(j).getName());
        }
    }

    /**
     * 信息提示弹窗
     *
     * @param info
     */
    private void showInfo(String info) {
        Snackbar.make(userAvatar, info, Snackbar.LENGTH_SHORT).setAction("action", null).show();
    }
}
