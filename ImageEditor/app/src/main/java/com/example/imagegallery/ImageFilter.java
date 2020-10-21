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


    public void sephia(){
        withFilter = sephiaFilter(noFilter);
        onlyFilter = sephiaFilter(original);
    }

    public void gray(){
        withFilter = grayScale(noFilter);
        onlyFilter = grayScale(original);
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
                        0, 0,        0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    private Bitmap sephiaFilter(Bitmap bmp){
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
}
