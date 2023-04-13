package com.matm.matmsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.matm.matmsdk.Utils.SdkConstants;

import isumatm.androidsdk.equitas.R;

public class MainActivity extends AppCompatActivity {

    Button btnTest;
    Class driverActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btnTest = findViewById(R.id.btnTest);
        getRDServiceClass();

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,driverActivity);
            }
        });

    }

    private void getRDServiceClass() {
        String accessClassName = SdkConstants.DRIVER_ACTIVITY;//getIntent().getStringExtra("activity");
        try {
            Class<? extends Activity> targetActivity = Class.forName(accessClassName).asSubclass(Activity.class);
            driverActivity = targetActivity;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}