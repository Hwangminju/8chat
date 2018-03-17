package com.example.mjhwa.chat.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

import com.example.mjhwa.chat.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.UiSettings;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.View.VISIBLE;

public class MapsActivity extends FragmentActivity implements ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final int LOCATION_REQUEST_CODE = 101;
    String TAG = ".MapsActivity";

    Intent intent;

    private TextView mTapTextView;
    private Button btgo;
    private Button btmy;
    private GoogleMap mMap;
    UiSettings mapSettings;
    ListView listView;
    ItemListAdapter adapter;
    List<OneItem> items;
    int cnt = 0;

    public class OneItem {
        String time;
        String content;

        public OneItem(String time, String content) {
            this.time = time;
            this.content = content;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mTapTextView = (TextView) findViewById(R.id.tap_text);
        btgo = (Button) findViewById(R.id.go);
        btgo.setVisibility(View.INVISIBLE);
        btmy = (Button) findViewById(R.id.my);

        btmy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latlng = new LatLng(37.55806555843824,126.99823591858149);

                Bitmap size = BitmapFactory.decodeResource(getResources(), R.drawable.loc);
                size = Bitmap.createScaledBitmap(size, 50,100,true);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,17));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(size));// 마커위치설정
                mMap.addMarker(markerOptions);
                // 줌인하는 기능
            }
        });
    }

    class ItemListAdapter extends ArrayAdapter<OneItem> {
        private List<OneItem> items;
        private Context context;
        private int layoutResource;

        public void setContext(Context c) {
            this.context = c;
        }

        public ItemListAdapter(Context context, int layoutResource, List<OneItem> items) {
            super(context, layoutResource, items);
            this.context = context;
            this.items =  items;
            this.layoutResource = layoutResource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(layoutResource, null);
            }

            final OneItem oneItem = items.get(position);

            if (oneItem != null) {
                TextView content = (TextView) convertView.findViewById(R.id.content);
                TextView time = (TextView) convertView.findViewById(R.id.time);

                if (content != null){
                    content.setText(oneItem.content);
                }
                if (time != null){
                    time.setText(oneItem.time);
                }
            }
            return convertView;
        }
    }

    private void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        GPSListener gpsListener = new GPSListener();
        long minTime = 1000;
        float minDistance = 0;

        try {
            //최근에 알려진 위치 얻어오기
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

            }

            //주기적으로 GPS 정보 받도록 요청
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);
        }
        catch(SecurityException ex) {
            ex.printStackTrace();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            Timestamp cur = new Timestamp(System.currentTimeMillis());
            items.add(new OneItem(cur.toString(), cnt + "\n(" + latitude + "," + longitude+")"));
            cnt++;

            listView.setAdapter(adapter);

        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE);
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            LatLng Dongguk = new LatLng(37.558096,126.9960291);
            Marker dongguk = mMap.addMarker(new MarkerOptions().position(Dongguk)
            .title("동국대학교 신공학관")
            .snippet("서울특별시 중구 필동로 1길 30"));

            LatLng Starbucks = new LatLng(37.5553978,127.0107109);
            Marker starbucks = mMap.addMarker(new MarkerOptions().position(Starbucks)
                    .title("스타벅스 약수역점")
                    .snippet("서울특별시 중구 신당2동 다산로 129"));
            LatLng Daehan = new LatLng(37.5610636,126.9953291);
            Marker daehan = mMap.addMarker(new MarkerOptions().position(Daehan)
                    .title("대한극장")
                    .snippet("서울특별시 중구 충무로4가 125-18"));
            LatLng S1 = new LatLng(37.5599526,126.975302);
            Marker s1 = mMap.addMarker(new MarkerOptions().position(S1)
                    .title("숭례문")
                    .snippet("서울특별시 중구 남대문로4가 세종대로 40"));
            LatLng S2 = new LatLng(37.579617,126.977041);
            Marker s2 = mMap.addMarker(new MarkerOptions().position(S2)
                    .title("경복궁")
                    .snippet("서울특별시 종로구 종로1.2.3.4가동 사직로 161"));
            LatLng S3 = new LatLng(37.531537,127.066691);
            Marker s3 = mMap.addMarker(new MarkerOptions().position(S3)
                    .title("뚝섬유원지")
                    .snippet("서울특별시 광진구 자양동 능동로 10"));
            LatLng Park = new LatLng(37.5273289,126.9349602);
            Marker park = mMap.addMarker(new MarkerOptions().position(Park)
                    .title("여의도 한강공원")
                    .snippet("서울특별시 영등포구 여의도동 여의동로 330"));
        }

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        String title = marker.getTitle();
        String snippet = marker.getSnippet();
        Intent intent = getIntent();
        intent.putExtra("title", String.valueOf(title));
        intent.putExtra("info", String.valueOf(snippet));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onMapClick(LatLng point) { // 지도 상에서 한 번 클릭 했을 때
        mMap.clear();
        btgo.setVisibility(View.INVISIBLE);
        mTapTextView.setText("위치 정보 " + point);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point); // 마커위치설정
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));

        /*TextView lat = (TextView) findViewById(R.id.lat);
        TextView lon = (TextView) findViewById(R.id.lon);

        lat.setText(String.valueOf(point.latitude));
        lon.setText(String.valueOf(point.longitude));*/
    }

    @Override
    public void onMapLongClick(LatLng point) {
        mMap.clear();
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mTapTextView.setText("위치 정보 " + point);
        btgo.setVisibility(View.VISIBLE);

        final double lat_d = point.latitude;
        final double lon_d = point.longitude;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point); // 마커위치설정
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));

        btgo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                intent.putExtra("lat", String.valueOf(lat_d));
                intent.putExtra("lon", String.valueOf(lon_d));
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

    protected void requestPermission(String permissionType, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permissionType}, requestCode);
    }

    public void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {

                if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Unable to show location - permission required",
                            Toast.LENGTH_LONG).show();
                } else {
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                }
            }
        }
    }
}
