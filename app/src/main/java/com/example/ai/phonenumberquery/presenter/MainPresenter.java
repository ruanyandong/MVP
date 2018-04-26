package com.example.ai.phonenumberquery.presenter;

import com.example.ai.phonenumberquery.HttpUtils.HttpUtil;
import com.example.ai.phonenumberquery.model.Phone;
import com.example.ai.phonenumberquery.view.MvpMainView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainPresenter extends BasePresenter{

    MvpMainView mvpMainView;
    Phone mPhone;

    //请求网址
    String mUrl="https://tcc.taobao.com/cc/json/mobile_tel_segment.htm";

    public MainPresenter(MvpMainView mainView){
        mvpMainView=mainView;

    }

    public Phone getPhoneInfo(){
        return mPhone;
    }
    public void searchPhoneInfo(String phone){

        if(phone.length()!=11){
           mvpMainView.showToast("请输入正确的手机号");
           return;
        }
        mvpMainView.showLoading();
        //写上http请求的处理逻辑
        sendHttp(phone);
    }

    /**
     * 主流Json解析框架
     * JSONObject:自带Json解析框架，采用对象和数组两种方式解析
     * Gson：java对象和Json可以相互直接转换
     * FastJson：阿里提供的，和Gson类似
     * @param phone
     */
    private void sendHttp(String phone){
        final Map<String,String> map=new HashMap<>();
        map.put("tel",phone);
        HttpUtil httpUtil=new HttpUtil(new HttpUtil.HttpResponse() {
            @Override
            public void onSuccess(Object object) {
                String json=object.toString();
                int index=json.indexOf("{");
                json=json.substring(index,json.length());

                //使用JSONObject
                mPhone=parseModelByOrgJson(json);

                //使用Gson
                mPhone=parseModelByGson(json);
                //使用FastJson
                mPhone=parseModeByFastjson(json);


                mvpMainView.hideLoading();

                mvpMainView.updateView();
            }
            @Override
            public void onFail(String error) {

                mvpMainView.showToast(error);
                mvpMainView.hideLoading();
            }
        });

        httpUtil.sendGetHttp(mUrl,map);
    }

    /**
     * org.Json解析
     * @param json
     * @return
     */
    private Phone parseModelByOrgJson(String json){

        Phone phone=new Phone();
        try {
            JSONObject jsonObject=new JSONObject(json);

            String value=jsonObject.getString("telString");
            phone.setTelString(value);

            value=jsonObject.getString("province");
            phone.setProvince(value);

            value=jsonObject.getString("catName");
            phone.setCatName(value);

            value=jsonObject.getString("carrier");
            phone.setCarrier(value);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return phone;
    }

    private Phone parseModelByGson(String json){

        Gson gson=new Gson();
        Phone phone=gson.fromJson(json,Phone.class);
        return phone;
    }

    private Phone parseModeByFastjson(String json){
        Phone phone= com.alibaba.fastjson.JSONObject.parseObject(json,Phone.class);

        return phone;
    }

}
