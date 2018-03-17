package com.example.mjhwa.chat.views;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mjhwa.chat.R;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;

public class GeoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);

        final TextView tv = (TextView) findViewById(R.id.tvGeo); // 결과창
        Button bt1 = (Button)findViewById(R.id.bt1);
        Button bt2 = (Button)findViewById(R.id.bt2);
        final EditText etLat = (EditText)findViewById(R.id.etLat);
        final EditText etLon = (EditText)findViewById(R.id.etLon);

        Intent intent = getIntent();
        etLat.setText(intent.getStringExtra("latitude"));
        etLon.setText(intent.getStringExtra("lontitude"));

        final Geocoder geocoder = new Geocoder(this);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 위도,경도 입력 후 변환 버튼 클릭
                List<Address> list = null;
                try {
                    double d1 = Double.parseDouble(etLat.getText().toString());
                    double d2 = Double.parseDouble(etLon.getText().toString());

                    list = geocoder.getFromLocation(
                            d1, // 위도
                            d2, // 경도
                            10); // 얻어올 값의 개수
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (list != null) {
                    if (list.size()==0) {
                        tv.setText("해당 주소의 정보가 존재하지 않습니다");
                    } else {
                        tv.setText(list.get(0).getAddressLine(0).toString() + ", " + list.get(0).getAddressLine(1).toString());
                        // 시군구, 나라 까지만 표현 되도록 (구글 맵이라서 그런지 한글이 출력 X)
                    }
                }
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 위도,경도 입력 후 지도 버튼 클릭 => 지도화면으로 인텐트 날리기
                double d1 = Double.parseDouble(etLat.getText().toString());
                double d2 = Double.parseDouble(etLon.getText().toString());
                String label = "Your Location";

                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?q="+ d1  +"," + d2 +"("+ label + ")&iwloc=A&hi=es"));
                startActivity(intent);
            }
        });
    }
}
