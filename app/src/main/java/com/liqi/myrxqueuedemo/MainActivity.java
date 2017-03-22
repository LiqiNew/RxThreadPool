package com.liqi.myrxqueuedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.liqi.myrxqueuedemo.rx_threadpool.RxMessageSource;
import com.liqi.myrxqueuedemo.rx_threadpool.interchange.RxThreadInterchange;
import com.liqi.myrxqueuedemo.rx_threadpool.interfa.OnIsRequestListener;
import com.liqi.myrxqueuedemo.rx_threadpool.model.RxRequestModel;

import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity implements OnIsRequestListener<String> ,View.OnClickListener{
    private Button button01,button02,button03;
    private TextView test_text_add,test_text;
    //测试的写法
    private Object SIGN[]={new Object(),new Object(),new Object(),new Object(),new Object(),new Object(),new Object(),new Object(),new Object(),new Object()};

    private String mContent="";
    private int runIndxe=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button01= (Button) findViewById(R.id.button01);
        button01.setOnClickListener(this);
        button02= (Button) findViewById(R.id.button02);
        button02.setOnClickListener(this);
        button03= (Button) findViewById(R.id.button03);
        button03.setOnClickListener(this);
        test_text= (TextView) findViewById(R.id.test_text);
        test_text_add= (TextView) findViewById(R.id.test_text_add);
    }

    @Override
    public void getT(String s) {
        mContent+="运行时间："+getTime()+"\n获取内容："+s+"\n";
        test_text.setText(mContent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button01:
                for (int i = 0; i < SIGN.length; i++) {
                    RxRequestModel<String> rxRequestModel=new RxRequestModel<>();
                    rxRequestModel.setContent("线程编号："+i);
                    rxRequestModel.setOnIsRequestListener(this);
                    RxMessageSource.getRxMessageSource().add(rxRequestModel, SIGN[i]);
                }
                break;
            case R.id.button02:
                RxMessageSource.getRxMessageSource().cancel(SIGN[1]).cancel(SIGN[4]).cancel(SIGN[8]);
                break;
            case R.id.button03:
                RxRequestModel<String> rxRequestModel=new RxRequestModel<>();
                rxRequestModel.setContent("线程编号："+(runIndxe));
                rxRequestModel.setOnIsRequestListener(this);
                RxMessageSource.getRxMessageSource().add(rxRequestModel);
                test_text_add.setText("添加线程编号：\n"+(++runIndxe));
                break;
        }
        RxThreadInterchange.getRxThreadInterchange().start(RxMessageSource.getRxMessageSource());
    }
    private String getTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return  sDateFormat.format(new java.util.Date());
    }
}
