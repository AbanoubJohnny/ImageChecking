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

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
//import android.graphics.BitmapFactory;

public class MainActivity extends AppCompatActivity {

    Uri selectedImage1, selectedImage2;
    String picturePath1, picturePath2;
    int selected;
    int width, height, width2, height2;
    int tolerance;
    private File actualImage1, actualImage2;
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
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * used to get photos from the user
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            if (selected == 1) {
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
                    try {
                        actualImage1 = FileUtil.from(MainActivity.this, data.getData());
                        bmp1 = Compressor.getDefault(this).compressToBitmap(actualImage1);
                    } catch (Exception e) {
                        bmp1 = getBitmapFromUri(selectedImage1);
                    }

//                    bmp1 = toGrayscale(bmp1);
                    full_bmp1 = getBitmapFromUri(selectedImage1);
                    width = full_bmp1.getWidth();
                    height = full_bmp1.getHeight();

                    tolerance = (width * height) / 2;
                    if (tolerance < 1000000)
                        while (tolerance > 10)
                            tolerance = (tolerance) / 2;
                    else
                        while (tolerance >= 30)
                            tolerance = (tolerance) / 2;

                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView1.setImageBitmap(bmp1);

            } else if (selected == 2) {
                selectedImage2 = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage2,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath2 = cursor.getString(columnIndex);
                cursor.close();
                try {
                    try {
                        actualImage2 = FileUtil.from(MainActivity.this, data.getData());
                        bmp2 = Compressor.getDefault(this).compressToBitmap(actualImage2);
                    } catch (Exception e) {
                        bmp2 = getBitmapFromUri(selectedImage2);
                    }
//                    bmp2 = toGrayscale(bmp2);
                    full_bmp2 = getBitmapFromUri(selectedImage2);
                    width2 = full_bmp2.getWidth();
                    height2 = full_bmp2.getHeight();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView2.setImageBitmap(bmp2);
            }


        }
    }

    /**
     * get Bitmap from a URI.
     */
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    /**
     * Ceck if an image is a subset of another.
     */
    public boolean IsSubset(Bitmap sourceBitmap, Bitmap serchingBitmap) {
        for (int mainY = 0; mainY < height - height2 + 1; mainY += tolerance)
            for (int mainX = 0; mainX < width - width2 + 1; mainX += tolerance) {
                int rgb1 = sourceBitmap.getPixel(mainX, mainY);
                int rgb2 = serchingBitmap.getPixel(0, 0);
                double result = compareARGB(rgb1, rgb2);
                if (result < 5) {

                    double result2 = CompareImages2(sourceBitmap, serchingBitmap,mainX, mainY);
//                    Bitmap created = Bitmap.createBitmap(sourceBitmap, mainX, mainY, width2, height2);
                    if (result2 < 8) {
                        return true;
                    }
                }
            }
        return false;
    }

    /**
     * Change any bitmape Colors to grayscale.
     */
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

    /**
     * Determines how different two identically sized regions are.
     */
    public double CompareImages2(Bitmap img1, Bitmap img2,int w,int h) {
        int height1 = img2.getHeight();
        int width1 = img2.getWidth();
        long diff = 0;
        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                int rgb1 = img1.getPixel(w+x, h+y);
                int rgb2 = img2.getPixel(x, y);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = (rgb1) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = (rgb2) & 0xff;
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
    public double CompareImages(Bitmap img1, Bitmap img2) {
        int height1 = img1.getHeight();
        int width1 = img1.getWidth();
        long diff = 0;
        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                int rgb1 = img1.getPixel(x, y);
                int rgb2 = img2.getPixel(x, y);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = (rgb1) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = (rgb2) & 0xff;
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
    public static double compareARGB(int rgb1, int rgb2) {
        long diff = 0;
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = (rgb1) & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = (rgb2) & 0xff;
        diff = Math.abs(r1 - r2);
        diff += Math.abs(g1 - g2);
        diff += Math.abs(b1 - b2);
        double p = diff / 255.0;
        // if there is transparency, the alpha values will make difference smaller
        return p * 100.0;
    }

    /**
     * Works in background and check the inputs so as not to inturrupt the main thread .
     */
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
            String s = "";
            if (bmp1 == null || bmp2 == null) {
                s = "Please enter the two photos";
            } else if (width < width2 &&height < height2) {
                if (IsSubset(full_bmp2, full_bmp1)) {
                    s = "subset";
                } else {
                    s = "not a subset";
                }
            } else if (width > width2 && height > height2) {
                if (IsSubset(full_bmp1, full_bmp2)) {
                    s = "subset";
                } else {
                    s = "not a subset";
                }
            } else if (width == width2 && height == height2) {
                double result2 = CompareImages(full_bmp1, full_bmp2);
                if (result2 < 5.0) {
                    s = "identical";
                } else {
                    s = "same dimentions but not identical";
                }
            } else {

                s = "not valid photos";
            }
            final String finalS = s;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, finalS, Toast.LENGTH_LONG).show();
                    Log.d("matching", finalS);
                }
            });
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (PDialog.isShowing()) {
                PDialog.dismiss();
            }
        }


        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}