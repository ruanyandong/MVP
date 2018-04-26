package com.example.ai.phonenumberquery;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ai.phonenumberquery.model.Phone;
import com.example.ai.phonenumberquery.presenter.MainPresenter;
import com.example.ai.phonenumberquery.view.MvpMainView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,MvpMainView{

    EditText input_phone;
    Button btn_search;
    TextView result_phone;
    TextView result_province;
    TextView result_type;
    TextView result_carrier;

    MainPresenter mainPresenter;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input_phone=findViewById(R.id.input_phone);
        btn_search=findViewById(R.id.btn_search);

        result_phone=findViewById(R.id.result_phone);
        result_province=findViewById(R.id.result_province);
        result_type=findViewById(R.id.result_type);
        result_carrier=findViewById(R.id.result_carrier);

        btn_search.setOnClickListener(this);

        mainPresenter=new MainPresenter(this);

        mainPresenter.attach(this);


    }

    @Override
    public void onClick(View v) {

        mainPresenter.searchPhoneInfo(input_phone.getText().toString());
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateView() {
        Phone phone=mainPresenter.getPhoneInfo();
        result_phone.setText("手机号码:"+phone.getTelString());
        result_province.setText("省份:"+phone.getProvince());
        result_type.setText("运营商:"+phone.getCatName());
        result_carrier.setText("归属运营商:"+phone.getCarrier());

    }

    @Override
    public void showLoading() {

        if (progressDialog==null){
            progressDialog=ProgressDialog.show(MainActivity.this,"提示","正在加载...",true,false);
        }else if(progressDialog.isShowing()){
            progressDialog.setTitle("提示");
            progressDialog.setMessage("正在加载...");
        }
        progressDialog.show();

    }

    @Override
    public void hideLoading() {

        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
