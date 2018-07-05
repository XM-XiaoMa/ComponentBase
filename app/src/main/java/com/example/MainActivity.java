package com.example;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mabaochang.component_base.R;
import com.example.manager.AppManager;
import com.example.manager.HttpManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_list_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_list_btn:
                HttpManager.getInstance(AppManager.getInstance().getApp()).getMainList(0, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String a = "";
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        String a = "";
                    }
                });
                break;
        }
    }
}
