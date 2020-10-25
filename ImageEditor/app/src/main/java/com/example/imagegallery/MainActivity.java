package com.example.imagegallery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ToolsRecyclerView.ItemClickListener{

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    private static final int GALLERY_REQUEST = 9;

    private ImageView imageView;

    boolean toolBarOpen = false;

    int currentTool = 0;

    LinearLayout toolbar;
    LinearLayout toolMenu;

    ImageFilter img = null;

    ImageButton open;
    Button tool;
    ImageButton save;
    ImageButton btnCamera;

    Uri image_uri;

    ToolsRecyclerView toolsAdapter;

    boolean dark_theme = true;
    boolean toggle_original = false;
    int filterApplied = 0;

    TextView toolName;
    SeekBar seekbar;

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        //recycler view setup
        ArrayList<Integer> toolsIcon =  new ArrayList<>();
        toolsIcon.add(R.drawable.ic_brightness);
        toolsIcon.add(R.drawable.ico_contrast);
        toolsIcon.add(R.drawable.ico_sepia);
        toolsIcon.add(R.drawable.ico_gray);
        toolsIcon.add(R.drawable.ico_inverted);
        toolsIcon.add(R.drawable.ico_original);
        toolsIcon.add(R.drawable.ico_delete);

        RecyclerView recyclerView = findViewById(R.id.toolsRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        toolsAdapter = new ToolsRecyclerView(this, toolsIcon);
        toolsAdapter.setClickListener(this);
        recyclerView.setAdapter(toolsAdapter);

        //GUI element initialisation
        imageView = findViewById(R.id.imageView);

        toolbar = findViewById(R.id.toolbar);
        toolMenu = findViewById(R.id.toolMenu);

        open = findViewById(R.id.btn_open);
        tool = findViewById(R.id.btn_edit);
        save = findViewById(R.id.btn_save);
        toolName = findViewById(R.id.toolName);
        btnCamera = findViewById(R.id.btn_camera);

        ImageButton theme = findViewById(R.id.btn_theme);

        Button back = findViewById(R.id.btn_back);


        //GUI listener
        btnCamera.setOnClickListener((v) -> getImageFromCamera());
        open.setOnClickListener((v) -> openButtonClicked());
        tool.setOnClickListener((v) -> toolsButtonClicked());
        save.setOnClickListener((v) -> saveButtonClicked());
        back.setOnClickListener((v) -> backButtonClicked());
        theme.setOnClickListener((v) -> themeButtonClicked());

        //seekbar Event handling
        seekbar = findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                int i = seekbar.getProgress();
                switch (currentTool){
                    case 1:
                        img.setContrast((float)(i * 0.1));
                        break;
                    case 2:
                        img.setBrightness((float)(i*5.1 - 255.0));
                        break;

                }
                toggle_original = false;

                if(filterApplied > 0) {
                    imageView.setImageBitmap(img.withFilter);
                }else {
                    imageView.setImageBitmap(img.noFilter);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(img!= null){
            if(filterApplied > 0) {
                imageView.setImageBitmap(img.withFilter);
            }else {
                imageView.setImageBitmap(img.noFilter);
            }
        }
    }

    protected void onPause() {
        super.onPause();
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

    private void noFilterButtonClicked(){
        if(img != null) {
            if (!toggle_original) {
                imageView.setImageBitmap(img.original);
            } else {
                imageView.setImageBitmap(img.withFilter);
            }
        }
        toggle_original = !toggle_original;

    }

    //Recycler view Button Click event
    @Override
    public void onItemClick( int position) {
        if( img != null) {
            if (toolbar.getVisibility() == View.VISIBLE) {
                switch (position) {
                    case 0:
                        hideToolbar();
                        toolName.setText("contrast");
                        seekbar.setVisibility(View.VISIBLE);
                        seekbar.setProgress((int) img.getContrast() * 10);
                        currentTool = 1;
                        toolMenu.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        hideToolbar();
                        toolName.setText("brightness");
                        seekbar.setVisibility(View.VISIBLE);
                        seekbar.setProgress((int) ((img.getBrightness() + 255) / 5.1));
                        currentTool = 2;
                        toolMenu.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        if (filterApplied != 1) {
                            img.sepia();
                            imageView.setImageBitmap(img.withFilter);
                            filterApplied = 1;
                            break;
                        }
                        if (filterApplied == 1) {
                            imageView.setImageBitmap(img.noFilter);
                            filterApplied = 0;
                        }
                        break;
                    case 3:
                        if (filterApplied != 2) {
                            img.gray();
                            imageView.setImageBitmap(img.withFilter);
                            filterApplied = 2;
                            break;
                        }
                        if (filterApplied == 2) {
                            imageView.setImageBitmap(img.noFilter);
                            filterApplied = 0;
                        }
                        break;

                    case 4:
                        if (filterApplied != 3) {
                            img.invert();
                            imageView.setImageBitmap(img.withFilter);
                            filterApplied = 3;
                            break;
                        }
                        if (filterApplied == 3) {
                            imageView.setImageBitmap(img.noFilter);
                            filterApplied = 0;
                        }
                        break;

                    case 5:
                        noFilterButtonClicked();
                        break;

                    case 6:
                        img = null;
                        imageView.setImageResource(R.drawable.default_img);
                        break;

                    default:
                        currentTool = 0;
                        break;
                }
            }
        }else{
            Toast.makeText(this, "First import an image", Toast.LENGTH_SHORT).show();
        }
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

    private void getImageFromCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                //permission not enabled, request it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup to request permissions
                requestPermissions(permission, PERMISSION_CODE);
            }
            else {
                //permission already granted
                openCamera();
            }
        }
        else {
            //system os < marshmallow
            openCamera();
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    private void getImageFromGallery(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void saveImageToGallery(){
        if(img != null) {
            imageView.setDrawingCacheEnabled(true);
            Bitmap b = imageView.getDrawingCache();
            MediaStore.Images.Media.insertImage(getContentResolver(), b, "image", "created with ImageEditor");
            Toast.makeText(this, "image saved", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "First import an image", Toast.LENGTH_SHORT).show();
        }
    }

    //handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user presses Allow or Deny from Permission Request Popup
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera();
                }
                else {
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //import image from gallery
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode != GALLERY_REQUEST) {
            Toast.makeText(this, "Image loaded", Toast.LENGTH_SHORT).show();

            imageView.setImageURI(image_uri);
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            img = new ImageFilter(drawable.getBitmap());
            fitViewtoScreen();
        }

        //Check if the intent was to pick image, was successful and an image was picked
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){

            //Display image on imageView
            imageView.setImageURI(data.getData());
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            img = new ImageFilter(drawable.getBitmap());

            fitViewtoScreen();
        }


    }

    private void fitViewtoScreen(){
        //scale the image to fit the screen

        //height without toolbar : 650 dp
        //height with toolbar :530dp
        //width : 400dp
        int hwt = 630;
        int hwot = 730;
        int w = 400;

        int imgHeight = img.original.getHeight();
        int imgWidth = img.original.getWidth();

        float scaleY = imgHeight/ (float) dpToPx(hwot);
        float scaleX = imgWidth/ (float)dpToPx(w);

        if(scaleX < 1 || scaleY < 1) {
            if (scaleX < scaleY) {
                scaleX = scaleY;
            } else {
                scaleY = scaleX;
            }

        imageView.getLayoutParams().height = (int) (imgHeight * 1/scaleY);
        imageView.getLayoutParams().width = (int) (imgWidth * 1/scaleX);


        }

    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}