package com.example.mjhwa.chat.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.mjhwa.chat.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MakeActivity extends AppCompatActivity {

    private RelativeLayout drawArea;
    private ArrayList<TouchImageView> imageArrayList;
    private ImageView saveEmoticonButton;
    private ImageView albumButton;
    private ImageView newDraw;
    private Button initButton;
    private static final int TAKE_PHOTO_REQUEST_CODE = 201;
    private static int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make);
        imageArrayList = new ArrayList<>();
        saveEmoticonButton = (ImageView) findViewById(R.id.save);
        albumButton = (ImageView) findViewById(R.id.album);
        initButton = (Button) findViewById(R.id.init);
        drawArea = (RelativeLayout) findViewById(R.id.drawarea);
        newDraw = (ImageView) findViewById(R.id.new_draw);

        saveEmoticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //drawArea를 비트맵으로 변환
                drawArea.setDrawingCacheEnabled(true);
                drawArea.buildDrawingCache();
                Bitmap captureView = drawArea.getDrawingCache();
                TouchImageView touchImageView = imageArrayList.get(0);
                Bitmap bmp = ((BitmapDrawable)touchImageView.getDrawable()).getBitmap();
                ImageView temp = newDraw;
                for(int i=0; i<imageArrayList.size(); i++) {
                    bmp = overlay(temp, imageArrayList.get(i), drawArea);
                    temp.setImageBitmap(bmp);
                }
                touchImageView.setImageBitmap(bmp);
                //drawArea.removeAllViewsInLayout();
                //drawArea.addView(touchImageView);

                /*drawArea.setDrawingCacheEnabled(true);
                drawArea.buildDrawingCache(true);
                Bitmap captureView = Bitmap.createBitmap(drawArea.getMeasuredWidth(), drawArea.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas screenShotCanvas = new Canvas(captureView);
                drawArea.draw(screenShotCanvas);

                TouchImageView touchImageView = new TouchImageView(drawArea.getContext());
                touchImageView.setImageBitmap(captureView);

                drawArea.removeAllViewsInLayout();
                drawArea.addView(touchImageView);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                captureView.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();*/
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();

                if(imageArrayList.size() != 0) {
                    Intent _intent = getIntent();
                    _intent.putExtra("byte", bytes);
                    setResult(RESULT_OK, _intent);
                    onBackPressed();
                }
            }
        });
        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoLoadEvent(v);
            }
        });
        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawArea.removeAllViewsInLayout();
                imageArrayList.clear();
            }
        });
    }

    public void onBackPressed() { // if child acticity end
        super.onBackPressed();
    }

    public void clickStickerEvent(int sticker) {
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TouchImageView iv = new TouchImageView(drawArea.getContext());
        //iv.setImageDrawable(getResources().getDrawable(sticker));
        iv.setImageResource(sticker);
        iv.setLayoutParams(layoutParams);

        imageArrayList.add(iv);

        drawArea.addView(iv);
    }

    public void photoLoadEvent(View v) {
        // 안드로이드 갤러리 오픈
        // request 코드 201
        //TAKE_PHOTO_REQUEST_CODE

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if(data != null) {
                // 업로드 이미지를 처리합니다
                // 이미지 업로드가 완료된 경우
                // 실제 web에 업로드 된 주소를 받아서 photoUrl로 저장
                // 그다음 포토메세지 전송
                RelativeLayout.LayoutParams layoutParams =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                TouchImageView iv = new TouchImageView(drawArea.getContext());
                iv.setImageURI(data.getData());
                iv.setLayoutParams(layoutParams);

                imageArrayList.add(iv);
                drawArea.addView(iv);
            }
        }
    }
    public static Bitmap overlay(ImageView img1, ImageView img2, RelativeLayout drawArea) {

        /*Bitmap bmp2 = ((BitmapDrawable)img2.getDrawable()).getBitmap();

        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;*/

        Bitmap bmp1 = ((BitmapDrawable)img1.getDrawable()).getBitmap();
        Bitmap bmp2 = ((BitmapDrawable)img2.getDrawable()).getBitmap();

        Bitmap result = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, img2.getLeft() - img1.getLeft(), img2.getTop()-img1.getTop(), null);
        return result;
    }
}
