package com.qrx.pentagon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    private MultidimensionalView mMultidimensionalView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mMultidimensionalView = findViewById(R.id.view_multidimensional);
        LinkedHashMap<String,Integer> data = new LinkedHashMap<>();
        data.put("结交人脉", 70);
        data.put("维护人脉", 50);
        data.put("时间投入", 30);
        data.put("忙碌程度", 40);
        data.put("逾期", 15);
        data.put("活跃", 50);
        data.put("信用", 80);
        mMultidimensionalView.bindData(data);
    }
}
