package com.kdr.novel_reader;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.novel_reader.R;

public class Property_Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_settings);


        Button saveBtn = findViewById(R.id.saveBtn);
        TextView nV = findViewById(R.id.novelView);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nV.setText("LOL!!!!!!!!!!!");
                finish();


            }
        });



    }




}
