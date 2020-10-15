package com.example.imagegallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 9;
    private ImageView imageView;

    boolean toolBarOpen = false;

    int currentTool = 0;
    Bitmap originalImg;

    LinearLayout toolbar;
    LinearLayout toolMenu;

    Button open;
    Button tool;
    Button save;

    boolean dark_theme = false;

    TextView toolName;

    SeekBar seekbar;

    float contrast = 1;
    float brightness = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        setContentView(R.layout.activity_main);

        //set fullScreen
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        //GUI element initialisation
        imageView = findViewById(R.id.imageView);

        toolbar = findViewById(R.id.toolbar);
        toolMenu = findViewById(R.id.toolMenu);

        open = findViewById(R.id.btn_open);
        tool = findViewById(R.id.btn_edit);
        save = findViewById(R.id.btn_save);

        toolName = findViewById(R.id.toolName);

        ImageButton tool1 = findViewById(R.id.btn_tool1);
        ImageButton tool2 = findViewById(R.id.btn_tool2);
        ImageButton tool3 = findViewById(R.id.btn_tool3);
        ImageButton tool4 = findViewById(R.id.btn_tool4);

        ImageButton theme = findViewById(R.id.btn_theme);


        Button back = findViewById(R.id.btn_back);
        Button reset = findViewById(R.id.btn_reset);

        //GUI listener
        open.setOnClickListener((v) -> openButtonClicked());
        tool.setOnClickListener((v) -> toolsButtonClicked());
        save.setOnClickListener((v) -> saveButtonClicked());

        back.setOnClickListener((v) -> backButtonClicked());
        reset.setOnClickListener((v) -> resetButtonClicked());

        tool1.setOnClickListener((v) -> tool1ButtonClicked());
        tool2.setOnClickListener((v) -> tool2ButtonClicked());
        tool3.setOnClickListener((v) -> tool3ButtonClicked());
        tool4.setOnClickListener((v) -> tool4ButtonClicked());

        theme.setOnClickListener((v) -> themeButtonClicked());

        //seekbar Event handling
        seekbar = findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                int i = seekbar.getProgress();
                switch (currentTool){
                    case 1:
                        contrast = (float)(i * 0.1);
                        imageView.setImageBitmap(changeBitmapContrastBrightness(originalImg, contrast, brightness));
                        break;
                    case 2:
                        brightness = (float)(i*5.1 - 255.0);
                        imageView.setImageBitmap(changeBitmapContrastBrightness(originalImg, contrast, brightness));
                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                }
            }
        });


    }

    /**
     *
     * @param bmp input bitmap
     * @param contrast 0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    private Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    private void saveButtonClicked(){
        saveImageToGallery();
    }

    private void openButtonClicked(){
        getImageFromGallery();
    }


    private void toolsButtonClicked(){
        //Show and Hide tool buttons
        if(!toolBarOpen){
            toolbar.setVisibility(View.VISIBLE);
            TranslateAnimation animate = new TranslateAnimation(0, 0, toolbar.getHeight(), 0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            toolbar.startAnimation(animate);
        } else {
            toolbar.setVisibility(View.INVISIBLE);
            TranslateAnimation animate = new TranslateAnimation(0, 0, 0, toolbar.getHeight());
            animate.setDuration(500);
            animate.setFillAfter(true);
            toolbar.startAnimation(animate);
        }
        toolBarOpen = !toolBarOpen;
    }

    private void backButtonClicked(){
        toolMenu.setVisibility(View.INVISIBLE);
        showToolbar();
        currentTool = 0;
    }

    private void resetButtonClicked(){
        imageView.setImageBitmap(originalImg);
        switch (currentTool){
            case 1:
                    contrast = 1;
                    seekbar.setProgress((int)contrast*10);
                break;
            case 2:
                    brightness = 0;
                    seekbar.setProgress((int)((brightness+255)/5.1));
                break;
            case 3:

                break;
            case 4:
                break;
        }
    }

    private void tool1ButtonClicked(){
        hideToolbar();
        toolName.setText("contrast");
        seekbar.setProgress((int)contrast*10);
        toolMenu.setVisibility(View.VISIBLE);
        currentTool = 1;
    }

    private void tool2ButtonClicked(){
        hideToolbar();
        toolName.setText("brightness");
        seekbar.setProgress((int)((brightness+255)/5.1));
        toolMenu.setVisibility(View.VISIBLE);
        currentTool = 2;
    }

    private void tool3ButtonClicked(){
        hideToolbar();
        toolName.setText("tool3");
        toolMenu.setVisibility(View.VISIBLE);
        currentTool = 3;
    }

    private void tool4ButtonClicked(){
        hideToolbar();
        toolName.setText("tool4");
        toolMenu.setVisibility(View.VISIBLE);
        currentTool = 4;
    }

    private void themeButtonClicked(){
        if(dark_theme) {
            dark_theme = false;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            dark_theme = true;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void showToolbar(){
        toolbar.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, toolbar.getHeight(), 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        toolbar.startAnimation(animate);
        toolBarOpen = true;
    }

    private void hideToolbar(){
        toolbar.setVisibility(View.INVISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, toolbar.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        toolbar.startAnimation(animate);
        toolBarOpen = false;
    }

    private void getImageFromGallery(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void saveImageToGallery(){
        imageView.setDrawingCacheEnabled(true);
        Bitmap b = imageView.getDrawingCache();
        MediaStore.Images.Media.insertImage(getContentResolver(), b,"hello", "description");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //import image from gallery
        super.onActivityResult(requestCode, resultCode, data);

        //Check if the intent was to pick image, was successful and an image was picked
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){

            //Display image on imageView
            imageView.setImageURI(data.getData());
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            originalImg = drawable.getBitmap();
        }
    }
}