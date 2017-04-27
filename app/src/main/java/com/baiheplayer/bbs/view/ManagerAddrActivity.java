package com.baiheplayer.bbs.view;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.map.Address;
import com.baiheplayer.bbs.bean.map.AddressBack;
import com.baiheplayer.bbs.bean.map.Area;
import com.baiheplayer.bbs.bean.map.City;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.map.Province;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.FileUtils;
import com.baiheplayer.bbs.utils.HttpsUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/5.
 */
@ContentView(R.layout.activity_manager_addr)
public class ManagerAddrActivity extends BaseActivity {
    @ViewInject(R.id.ll_address_list)
    private LinearLayout mAddressList;
    @ViewInject(R.id.tv_add_address)
    private TextView mAddAddress;

    private int screenWidth;
    private int currentPage = 1;
    private int totalpage = 1;

    private List<Address> addresses;
    private List<Province> provinces;
    private List<City> cities;
    private List<Area> areas;
    private List<String> provinceNames, cityNames, areaNames;
    private String provinceId, cityId, areaId;

    @Override
    public void onView(Bundle b) {

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        addresses = new ArrayList<>();
        cities = new ArrayList<>();
        areas = new ArrayList<>();
        cityNames = new ArrayList<>();
        areaNames = new ArrayList<>();
        mAddAddress.setOnClickListener(new ClickEvent(new Address(),"new"));
        refreshData(currentPage);

        getSavedListPlace();
    }

    /**
     * 异步提取存储的地名列表
     */
    private void getSavedListPlace(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                provinces = FileUtils.readData(ManagerAddrActivity.this, "map_list.txt"); // TODO: 2016/12/9  转异步子线程
                provinceNames = new ArrayList<>();
                for (int i = 0; i < provinces.size(); i++) {
                    provinceNames.add(provinces.get(i).getName());
                }
            }
        }.start();

    }


    /**
     * 从服务器获得我的地址
     */
    private void refreshData(int page) {
        RequestParams params = new RequestParams(HttpDatas.MY_ADDRESS);
        params.addParameter("pcurrent", page);
        params.addParameter("psize", 10);
        HttpsUtils.getInstance().getObjectFromNet(params, AddressBack.class, new ISimpleNetCallBack<AddressBack>() {
            @Override
            public void back( AddressBack resultInfo) {
                if (resultInfo.getCode() == 0) {
                    addresses.clear();
                    addresses.addAll(resultInfo.getList());
                    displayAddress();
                } else {
                    showInfo(resultInfo.getMsg());
                }
            }

            @Override
            public void fail(boolean isOnCallBack) {
                showInfo("网络错误");
            }

        });
    }

    private void displayAddress() {
        mAddressList.removeAllViews();
        for (int i = 0; i < addresses.size(); i++) {
            final Address address = addresses.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.item_address_blog, null);
            view.setMinimumWidth(screenWidth);
            TextView truename = (TextView) view.findViewById(R.id.tv_book_name);
            TextView phone = (TextView) view.findViewById(R.id.tv_book_phone);
            TextView zipcode = (TextView) view.findViewById(R.id.tv_book_zipcode);
            TextView province = (TextView) view.findViewById(R.id.tv_book_province);
            TextView city = (TextView) view.findViewById(R.id.tv_book_city);
            TextView area = (TextView) view.findViewById(R.id.tv_book_area);
            TextView street = (TextView) view.findViewById(R.id.tv_book_street);
            TextView tag = (TextView) view.findViewById(R.id.tv_book_ps);
            Button edit = (Button) view.findViewById(R.id.btn_location_edit);
            truename.setText(address.getTruename());
            phone.setText(address.getPhone());
            zipcode.setText(address.getZipcode());
            province.setText(address.getProvinceName());
            city.setText(address.getCityName());
            area.setText(address.getAreaName());
            street.setText(address.getAddress());
            tag.setText(address.getTag());
            edit.setOnClickListener(new ClickEvent(address, "change"));
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String[] array = {"删除", "设为默认"};
                    new AlertDialog.Builder(ManagerAddrActivity.this).setItems(array, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestParams params = null;
                            if (which == 0) {
                                params = new RequestParams(HttpDatas.REMOVE_ADDRESS);  //删除
                            } else {
                                params = new RequestParams(HttpDatas.DEFAULT_ADDRESS); //设为默认
                            }
                            params.addParameter("id", address.getId());
                            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                                @Override
                                public void back( DataBack resultInfo) {
                                    if (resultInfo.getCode() == 0) {
                                        refreshData(currentPage);
                                    } else {
                                        showInfo(resultInfo.getMsg());
                                    }
                                }

                                @Override
                                public void fail(boolean isOnCallBack) {
                                    showInfo("网络错误");
                                }

                            });
                        }
                    }).show();
                    return false;
                }
            });
            view.setTag(address.getId());
            mAddressList.addView(view);
        }
    }


    private class ClickEvent implements View.OnClickListener {

        private Address _address;
        private String type;

        public ClickEvent(Address address, String type) {
            this._address = address;
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            int[] num = getPositionOfAddress(_address);
            View layout = LayoutInflater.from(ManagerAddrActivity.this).inflate(R.layout.item_pop_address, null);
            layout.setMinimumWidth((int) (screenWidth * 0.9));
            final EditText adsName = (EditText) layout.findViewById(R.id.et_ads_name);
            final EditText adsPhone = (EditText) layout.findViewById(R.id.et_ads_phone);
            final EditText adsStreet = (EditText) layout.findViewById(R.id.et_ads_Street);
            final EditText adsZipcode = (EditText) layout.findViewById(R.id.et_ads_zipcode);
            final EditText adsPs = (EditText) layout.findViewById(R.id.et_ads_ps);
            Spinner spnProvince = (Spinner) layout.findViewById(R.id.sp_province);
            Spinner spnCity = (Spinner) layout.findViewById(R.id.sp_city);
            Spinner spnArea = (Spinner) layout.findViewById(R.id.sp_area);
            adsName.setText(_address.getTruename());
            adsPhone.setText(_address.getPhone());
            adsStreet.setText(_address.getAddress());
            adsZipcode.setText(_address.getZipcode());
            adsPs.setText(_address.getTag());
            spinnerGetDatas(spnProvince, spnCity, spnArea, num);
            provinceId = _address.getProvince();
            cityId = _address.getCity();
            areaId = _address.getArea();

            final AlertDialog builder = new AlertDialog.Builder(ManagerAddrActivity.this).setView(layout).show();
            Button adsCancel = (Button) layout.findViewById(R.id.btn_ads_cancel);
            adsCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cityNames.clear();
                    areaNames.clear();
                    areaId = null;
                    provinceId = null;
                    cityId = null;
                    builder.dismiss();
                }
            });
            Button adsEnsure = (Button) layout.findViewById(R.id.btn_ads_ensure);
            adsEnsure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = adsName.getText().toString().trim();
                    String phone = adsPhone.getText().toString().trim();
                    String street = adsStreet.getText().toString().trim();
                    String zipcode = adsZipcode.getText().toString().trim();
                    String tag = adsPs.getText().toString().trim();
                    Log.i("chen", "采集数据为：" + name + " " + phone + " " + street + " " + zipcode
                            + " " + tag + " " + provinceId + " " + cityId + " " + areaId);

                    RequestParams params = null;
                    if (type == "change") {
                        params = new RequestParams(HttpDatas.UPDATE_ADDRESS);
                        params.addParameter("id", _address.getId());
                    } else {
                        params = new RequestParams(HttpDatas.ADD_ADDRESS);
                    }
                    params.addParameter("truename", name);
                    params.addParameter("phone", phone);
                    params.addParameter("province", provinceId);
                    params.addParameter("city", cityId);
                    params.addParameter("area", areaId);
                    params.addParameter("address", street);
                    params.addParameter("zipcode", zipcode);
                    params.addParameter("tag", tag);
                    HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                        @Override
                        public void back( DataBack resultInfo) {
                            if (resultInfo.getCode() == 0) {
                                refreshData(currentPage);
                                cityNames.clear();
                                areaNames.clear();
                                areaId = null;
                                provinceId = null;
                                cityId = null;
                                builder.dismiss();
                            }
                            showInfo(resultInfo.getMsg());
                        }

                        @Override
                        public void fail(boolean isOnCallBack) {
                            showInfo("网络错误");
                        }

                    });
                }
            });

        }
    }

    /**
     * 下拉框处理
     */
    private void spinnerGetDatas(Spinner sp1, final Spinner sp2, final Spinner sp3, int[] num) {

        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this, R.layout.widget_spinner_text, provinceNames);
        final ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, R.layout.widget_spinner_text, cityNames);
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, R.layout.widget_spinner_text, areaNames);
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
                provinceId = provinces.get(position).getId();
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private int[] getPositionOfAddress(Address address) {
        int[] num = new int[]{0, 0, 0};
        if (address.getProvince() != null) {
            for (int i = 0; i < provinces.size(); i++) {
                if (address.getProvince().equals(provinces.get(i).getId())) {  //匹配省份
                    num[0] = i;
                    List<City> temp_cities = new ArrayList<>();
                    temp_cities.addAll(provinces.get(i).getChild());
                    for (int j = 0; j < temp_cities.size(); j++) {
                        if (address.getCity().equals(temp_cities.get(j).getId())) {//匹配城市
                            num[1] = j;
                            List<Area> temp_areas = new ArrayList<>();
                            temp_areas.addAll(temp_cities.get(j).getChild());
                            for (int k = 0; k < temp_areas.size(); k++) {
                                if (address.getArea().equals(temp_areas.get(k).getId())) {
                                    num[2] = k;
                                    temp_areas = null;
                                    temp_cities = null;
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
        cityNames.clear();
        for (int i = 0; i < cities.size(); i++) {
            cityNames.add(cities.get(i).getName());
        }

    }

    private void getAreaNameList(int position) {
        areas.clear();
        areas.addAll(cities.get(position).getChild());
        areaNames.clear();
        for (int j = 0; j < areas.size(); j++) {
            areaNames.add(areas.get(j).getName());
        }
    }

    /**
     * 返回
     *
     * @param view
     */
    @Event(value = {R.id.img_arrow,R.id.img_back})
    private void goBack(View view) {
        finish();
    }

    private void showInfo(String str) {
        Snackbar.make(mAddAddress, str, Snackbar.LENGTH_SHORT).setAction("action", null).show();
    }
}
