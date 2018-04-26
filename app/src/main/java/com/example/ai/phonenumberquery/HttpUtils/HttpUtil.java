package com.example.ai.phonenumberquery.HttpUtils;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    private String mUrl;
    //url参数
    private Map<String,String> mParam;

    private HttpResponse mHttpResponse;

    private final OkHttpClient client=new OkHttpClient();
    //主线程
    private Handler mHandler=new Handler(Looper.getMainLooper());

    public HttpUtil(HttpResponse response){

        mHttpResponse=response;

    }

    public interface HttpResponse{

        void onSuccess(Object object);
        void onFail(String error);

    }

    public void sendPostHttp(String url,
                             Map<String,String> param){

        sendHttp(url,param,true);
    }

    public void sendGetHttp(String url,
                            Map<String,String> param){
       sendHttp(url,param,false);

    }

    private void sendHttp(String url,
                          Map<String,String> param,
                          boolean isPost){
        mUrl=url;
        mParam=param;
        run(isPost);
    }

    //编写网络请求
    private void run(boolean isPost){

        Request request=createRequest(isPost);

        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (mHttpResponse!=null){

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //处于主线程
                            mHttpResponse.onFail("请求错误");
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if(mHttpResponse==null)return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                       if(!response.isSuccessful()){
                           mHttpResponse.onFail("请求失败");
                       }else{
                           try {
                               mHttpResponse.onSuccess(response.body().string());
                           }catch (Exception e){
                               e.printStackTrace();
                               mHttpResponse.onFail("结果转换失败");
                           }
                       }
                    }
                });
            }
        });

    }

    private Request createRequest(boolean isPost){

        Request request=null;
        if(isPost){
            /**
             * post请求
             */
            MultipartBody.Builder requestBodyBuilder=new MultipartBody.Builder();
            requestBodyBuilder.setType(MultipartBody.FORM);
            //遍历map请求参数
            Iterator<Map.Entry<String,String>> iterator=mParam.entrySet().iterator();

            while (iterator.hasNext()){
                Map.Entry<String,String> entry=iterator.next();
                //把请求参数添加到requestBodyBuilder中
                requestBodyBuilder.addFormDataPart(entry.getKey(),entry.getValue());

            }

            RequestBody requestBody=requestBodyBuilder.build();

            request=new Request.Builder()
                    .url(mUrl)
                    .post(requestBody).build();


        }else{
            /**
             * get请求
             */
            String urlGet=mUrl+"?"+MapParamToString(mParam);
            Request.Builder requestBuilder=new Request.Builder();
            request=requestBuilder.url(urlGet).build();
        }
        return request;
    }


    private String MapParamToString(Map<String,String> param){

        StringBuilder stringBuilder=new StringBuilder();

        Iterator<Map.Entry<String,String>> iterator=mParam.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String,String> entry=iterator.next();
            stringBuilder.append(entry.getKey()+"="+entry.getValue()+"&");
        }
        String str=stringBuilder.toString().substring(0,stringBuilder.length()-1);
        return str;
    }

}
