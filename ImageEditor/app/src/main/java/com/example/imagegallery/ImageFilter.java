package com.example.imagegallery;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class ImageFilter {


    private float contrast = 1;
    private float brightness = 0;
    public Bitmap original;
    public Bitmap noFilter;
    public Bitmap onlyFilter;
    public Bitmap withFilter;

    public float getContrast(){
        return contrast;
    }
    public float getBrightness(){
        return brightness;
    }


    public ImageFilter(Bitmap img){
        original = img;
        noFilter = img;
        withFilter = img;
        onlyFilter = img;
    }

    boolean imageExist(){
        if (original == null){
            return false;
        }else{
            return true;
        }
    }


    public void setBrightness(float b){
        brightness = b;
        withFilter = changeBitmapContrastBrightness(onlyFilter, contrast, brightness);
        noFilter = changeBitmapContrastBrightness(original, contrast, brightness);
    }

    public void setContrast(float c){
        contrast  = c;
        withFilter = changeBitmapContrastBrightness(onlyFilter, contrast, brightness);
        noFilter = changeBitmapContrastBrightness(original, contrast, brightness);
    }


    public void sepia(){
        withFilter = sepiaFilter(noFilter);
        onlyFilter = sepiaFilter(original);
    }

    public void gray(){
        withFilter = grayScale(noFilter);
        onlyFilter = grayScale(original);
    }

    public void invert(){
        withFilter = invertColor(noFilter);
        onlyFilter = invertColor(original);
    }

    public void delete(){
        withFilter = null;
        onlyFilter = null;
        noFilter = null;
        original = null;
    }

    private Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0,        0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    private Bitmap sepiaFilter(Bitmap bmp){
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.393f,   0.769f,   0.189f,   0,0,
                        0.349f,   0.686f,   0.168f,   0,0,
                        0.272f,   0.534f,   0.131f,   0,0,
                        0,      0,      0,         1,0,
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }

    private Bitmap grayScale(Bitmap bmp){
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                0.33f, 0.33f, 0.33f, 0, 0,
                0.33f, 0.33f, 0.33f, 0, 0,
                0.33f, 0.33f, 0.33f, 0, 0,
                0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }

    private Bitmap invertColor(Bitmap bmp){
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                -1,  0,  0,  0, 255,
                 0, -1,  0,  0, 255,
                 0,  0, -1,  0, 255,
                 0,  0,  0,  1,   0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }
}
