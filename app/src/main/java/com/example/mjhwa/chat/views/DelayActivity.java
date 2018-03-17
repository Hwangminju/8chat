package com.example.mjhwa.chat.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mjhwa.chat.R;

import java.util.Calendar;
import java.util.Date;

public class DelayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delay);

        final EditText etText = (EditText)findViewById(R.id.etText);
        final EditText etTime = (EditText)findViewById(R.id.etTime);
        Button btnSend = (Button)findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etText.getText().toString();
                int time = Integer.parseInt(etTime.getText().toString());

                Intent intent = getIntent();
                intent.putExtra("text", String.valueOf(text));
                intent.putExtra("time", time);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
