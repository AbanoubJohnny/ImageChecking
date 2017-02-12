package com.example.abanoubjohnny.imagechecking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
//import android.graphics.BitmapFactory;

public class MainActivity extends AppCompatActivity {

    Uri selectedImage1, selectedImage2;
    String picturePath1, picturePath2;
    int selected;
    int width, height, width2, height2;
    int tolerance;
    private File actualImage1, actualImage2;
    private File compressedImage1, compressedImage2, compressedResultImage;
    Bitmap bmp1, bmp2;
    Bitmap full_bmp1, full_bmp2;
    private static int RESULT_LOAD_IMAGE = 1;
    public ImageView imageView1, imageView2;
    ProgressDialog PDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView1 = (ImageView) findViewById(R.id.imgView1);
        imageView2 = (ImageView) findViewById(R.id.imgView2);

        imageView1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                selected = 1;
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                selected = 2;
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        Button submit = (Button) findViewById(R.id.check_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Match m = new Match();
                m.execute();
//                File file = new File(getFilesDir(), "image" + System.currentTimeMillis() + ".png");
                //
//                if (compareImages(bmp1, bmp2)) {
//                    Toast.makeText(MainActivity.this, "identical", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "not the same", Toast.LENGTH_LONG).show();
//                }


//                if (full_bmp1.getWidth() >= full_bmp2.getWidth() || full_bmp1.getHeight() >= full_bmp2.getHeight()){
//
//                }
////                    run(full_bmp1, full_bmp2, file, Imgproc.TM_CCOEFF_NORMED);
//                else{

//                }
//                    run(full_bmp2, full_bmp1, file, Imgproc.TM_CCOEFF_NORMED);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//
//        if(!OpenCVLoader.initDebug())
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9,MainActivity.this,mOpenCVCallBack);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            if (selected == 1) {
//                try {
//                    actualImage1 = FileUtil.from(MainActivity.this, data.getData());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                selectedImage1 = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage1,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath1 = cursor.getString(columnIndex);
                cursor.close();
                Log.d("image path", picturePath1);
                try {
//                    if (actualImage1.isFile())
//                        bmp1 = Compressor.getDefault(this).compressToBitmap(actualImage1);
//                    else
                    bmp1 = getBitmapFromUri(selectedImage1);
                    bmp1 = toGrayscale(bmp1);
//                    bmp1.setDensity(75);
                    full_bmp1 = getBitmapFromUri(selectedImage1);
                    width = full_bmp1.getWidth();
                    height = full_bmp1.getHeight();

                    tolerance = (width*height)/2;
                    if(tolerance<1000000)
                    while (tolerance>10)
                        tolerance = (tolerance)/2;
                    else
                        while (tolerance>50)
                            tolerance = (tolerance)/2;

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                imageView1.setImageBitmap(full_bmp1);

            } else if (selected == 2) {

//                try {
//                    actualImage2 = FileUtil.from(MainActivity.this, data.getData());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                selectedImage2 = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage2,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath2 = cursor.getString(columnIndex);
                cursor.close();
                try {
//                    actualImage2 = data;
//                    if (actualImage2.isFile())
//                        bmp2 = Compressor.getDefault(this).compressToBitmap(actualImage2);
//                    else
                    bmp2 = getBitmapFromUri(selectedImage2);
                    bmp2 = toGrayscale(bmp2);
//                    bmp2.setDensity(75);
                    full_bmp2 = getBitmapFromUri(selectedImage2);
                    width2 = full_bmp2.getWidth();
                    height2 = full_bmp2.getHeight();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                imageView2.setImageBitmap(full_bmp2);
            }


        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public static boolean compareImages(Bitmap imgA, Bitmap imgB) {
        // The images must be the same size.

        if (imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()) {
            int width = imgA.getWidth();
            int height = imgA.getHeight();

            // Loop over every pixel.
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Compare the pixels for equality.
                    if (imgA.getPixel(x, y) != imgB.getPixel(x, y)) {

                        return false;
                    }
                }
            }


        } else {
            return false;
        }

        return true;
    }

    public boolean IsSubset(Bitmap sourceBitmap, Bitmap serchingBitmap) {
//
//


//        int[] pixels = new int[sourceBitmap.getHeight() * sourceBitmap.getWidth()];
//        sourceBitmap.getPixels(pixels, 0, sourceBitmap.getWidth(), 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
//        int[] pixels2 = new int[serchingBitmap.getHeight() * serchingBitmap.getWidth()];
//        sourceBitmap.getPixels(pixels2, 0, serchingBitmap.getWidth(), 0, 0, serchingBitmap.getWidth(), serchingBitmap.getHeight());
//        for (int i = 0; i < sourceBitmap.getHeight() * sourceBitmap.getWidth(); i++) {
//            String hexColor = String.format("#%06X", (0xFFFFFF & pixels[i]));
//            Log.d("", "pixel" + i + "" + Color.parseColor(hexColor));
//        }
//        for (int j = 0; j < serchingBitmap.getHeight() * serchingBitmap.getWidth(); j++) {
//            String hexColor = String.format("#%06X", (0xFFFFFF & pixels2[j]));
//            Log.d("", "pixel2" + j + "" + Color.parseColor(hexColor));
//        }
//
//        boolean found = false;
//        for (int i = 0; i < sourceBitmap.getHeight() * sourceBitmap.getWidth(); i++){
//            if(pixels[i]==pixels2[0])
//                found = true;
//
//            if(found)
//                for (int j = 0; j < serchingBitmap.getHeight() * serchingBitmap.getWidth(); j++) {
//                    if(pixels[i+j]==pixels2[0])
//                }
//
//        }
        // minimazing serching zone
//        double lowestDiff = Double.POSITIVE_INFINITY;
        for (int mainX = 0; mainX < width - width2 + 1; mainX+=tolerance)
            for (int mainY = 0; mainY <height - height2 + 1; mainY+=tolerance) {
                int rgb1 = sourceBitmap.getPixel(mainX, mainY);
                int rgb2 = serchingBitmap.getPixel(0, 0);
                double result = compareARGB(rgb1, rgb2);
                if (result < 10.0) {
                    Bitmap created = Bitmap.createBitmap(sourceBitmap, mainX, mainY, width2, height2);

                    double result2 = compareImages3(created, serchingBitmap);
                    if (result2 < 5.0) {
                        return true;
                    }
                }
            }
        return false;
    }


    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
public static int[] findSubimage2(Bitmap im1, Bitmap im2){
    int w1 = im1.getWidth(); int h1 = im1.getHeight();
    int w2 = im2.getWidth(); int h2 = im2.getHeight();
    assert(w2 <= w1 && h2 <= h1);
    // will keep track of best position found
    int bestX = 0; int bestY = 0; double lowestDiff = Double.POSITIVE_INFINITY;
    // brute-force search through whole image (slow...)
    for(int x = 0;x < w1-w2;x++){
        for(int y = 0;y < h1-h2;y++){
            double comp = compareImages2(Bitmap.createBitmap(im1,x,y,w2,h2),im2);
            if(comp < lowestDiff){
                bestX = x; bestY = y; lowestDiff = comp;
            }
        }
    }
    // output similarity measure from 0 to 1, with 0 being identical
    System.out.println(lowestDiff);
    // return best location
    return new int[]{bestX,bestY};
}

    /**
     * Determines how different two identically sized regions are.
     */
    public static double compareImages2(Bitmap im1, Bitmap im2){
        assert(im1.getHeight() == im2.getHeight() && im1.getWidth() == im2.getWidth());
        double variation = 0.0;
        for(int x = 0;x < im1.getWidth();x++){
            for(int y = 0;y < im1.getHeight();y++){
                variation += compareARGB(im1.getPixel(x,y),im2.getPixel(x,y))/Math.sqrt(3);
            }
        }
        return variation/(im1.getWidth()*im1.getHeight());
    }

    public double compareImages3(Bitmap img1, Bitmap img2){
        int height1 = img1.getHeight();
        int width1 = img1.getWidth();
        long diff = 0;
        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                int rgb1 = img1.getPixel(x, y);
                int rgb2 = img2.getPixel(x, y);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >>  8) & 0xff;
                int b1 = (rgb1      ) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >>  8) & 0xff;
                int b2 = (rgb2      ) & 0xff;
                diff += Math.abs(r1 - r2);
                diff += Math.abs(g1 - g2);
                diff += Math.abs(b1 - b2);
            }
        }
        double n = width1 * height1 * 3;
        double p = diff / n / 255.0;
        System.out.println("diff percent: " + (p * 100.0));

        return p * 100.0;
    }
    /**
     * Calculates the difference between two ARGB colours (BufferedImage.TYPE_INT_ARGB).
     */
    public static double compareARGB(int rgb1, int rgb2){
        long diff = 0;
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >>  8) & 0xff;
        int b1 = (rgb1      ) & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >>  8) & 0xff;
        int b2 = (rgb2      ) & 0xff;
        diff = Math.abs(r1 - r2);
        diff += Math.abs(g1 - g2);
        diff += Math.abs(b1 - b2);
        double p = diff /255.0;
        // if there is transparency, the alpha values will make difference smaller
        return p * 100.0;
    }
    private class Match extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            PDialog = new ProgressDialog(MainActivity.this);
            PDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            PDialog.setMessage("Matching Please Wait");
            PDialog.setCancelable(false);
            PDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            if (bmp1 == null || bmp2 == null) {
                Toast.makeText(MainActivity.this, "Please enter the two photos", Toast.LENGTH_LONG).show();
                PDialog.dismiss();
            }

            else if (bmp1.getWidth() < bmp2.getWidth() && bmp1.getHeight() < bmp2.getHeight())
            {
                if (IsSubset(bmp2, bmp1)) {
                    Toast.makeText(MainActivity.this, "subset", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "not a subset", Toast.LENGTH_LONG).show();
                }
                PDialog.dismiss();
            }
            else if (bmp1.getWidth() > bmp2.getWidth() && bmp1.getHeight() > bmp2.getHeight())
            {
                if (IsSubset(bmp1, bmp2)) {
                    Toast.makeText(MainActivity.this, "subset", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "not a subset", Toast.LENGTH_LONG).show();
                }
                PDialog.dismiss();
            }
            else if (bmp1.getWidth()== bmp2.getWidth() && bmp1.getHeight()== bmp2.getHeight())
            {
                double result2 = compareImages3(bmp1, bmp2);
                if (result2 < 5.0) {
                    Toast.makeText(MainActivity.this, "identical", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "same dimentions but not identical", Toast.LENGTH_LONG).show();
                }
                PDialog.dismiss();
            }
            else {

                Toast.makeText(MainActivity.this, "not valid photos", Toast.LENGTH_LONG).show();
                PDialog.dismiss();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if(PDialog.isShowing()){
                PDialog.dismiss();
            }
        }



        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}