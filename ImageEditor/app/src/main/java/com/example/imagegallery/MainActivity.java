package com.example.imagegallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 9;
    private ImageView imageView;
    boolean opened = false;
    LinearLayout toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();

        //set fullScreen
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_main);

        //GUI element initialisation
        imageView = findViewById(R.id.imageView);
        Button open = findViewById(R.id.btn_open);
        Button tool = findViewById(R.id.btn_edit);
        toolbar = findViewById(R.id.toolbar);

        //GUI listener
        open.setOnClickListener((v) -> getImageFromGallery());
        tool.setOnClickListener((v) -> toolsButtonClicked());
    }

    private void getImageFromGallery(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void toolsButtonClicked(){
        if(!opened){
            toolbar.setVisibility(View.VISIBLE);
            TranslateAnimation animate = new TranslateAnimation(
                    0,
                    0,
                    toolbar.getHeight(),
                    0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            toolbar.startAnimation(animate);
        } else {
            toolbar.setVisibility(View.INVISIBLE);
            TranslateAnimation animate = new TranslateAnimation(
                    0,
                    0,
                    0,
                    toolbar.getHeight());
            animate.setDuration(500);
            animate.setFillAfter(true);
            toolbar.startAnimation(animate);
        }
        opened = !opened;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //import image from gallery
        super.onActivityResult(requestCode, resultCode, data);

        //Check if the intent was to pick image, was successful and an image was picked
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){

            //Display image on imageView
            imageView.setImageURI(data.getData());
        }
    }
}