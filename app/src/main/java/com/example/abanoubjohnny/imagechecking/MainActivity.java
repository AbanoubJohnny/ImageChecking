package com.example.abanoubjohnny.imagechecking;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.*;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
//import android.graphics.BitmapFactory;

public class MainActivity extends AppCompatActivity {

    Uri selectedImage1, selectedImage2;
    String picturePath1, picturePath2;
    int selected;
    int width, height, width2, height2;

    private File actualImage1, actualImage2;
    private File compressedImage1, compressedImage2, compressedResultImage;
    Bitmap bmp1, bmp2;
    Bitmap full_bmp1, full_bmp2;
    private static int RESULT_LOAD_IMAGE = 1;
    public ImageView imageView1, imageView2;

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
                File file = new File(getFilesDir(), "image" + System.currentTimeMillis() + ".png");
                //
//                if (compareImages(bmp1, bmp2)) {
//                    Toast.makeText(MainActivity.this, "identical", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "not the same", Toast.LENGTH_LONG).show();
//                }
                if (IsSubset(bmp1, bmp2)) {
                    Toast.makeText(MainActivity.this, "subset", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "not a subset", Toast.LENGTH_LONG).show();
                }
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
        if (sourceBitmap == null || serchingBitmap == null)
            return false;

        if (sourceBitmap.getConfig() != serchingBitmap.getConfig())
            return false;

        if (sourceBitmap.getWidth() < serchingBitmap.getWidth() || sourceBitmap.getHeight() < serchingBitmap.getHeight())
            return false;
        // Copy sourceBitmap to byte array
        // Serching entries
        // minimazing serching zone
        boolean found = false;

        for (int mainY = 0; mainY < sourceBitmap.getHeight() - serchingBitmap.getHeight() + 1; mainY++)
            for (int mainX = 0; mainX < sourceBitmap.getWidth() - serchingBitmap.getWidth() + 1; mainX++)
                if (sourceBitmap.getPixel(mainY, mainX) == serchingBitmap.getPixel(0, 0)) {
                    found = true;

                    inner: for (int secY = 1; secY < serchingBitmap.getHeight(); secY++)
                        for (int secX = 1; secX < serchingBitmap.getWidth(); secX++)
                            if (sourceBitmap.getPixel(mainY + secY, mainX + secX) != serchingBitmap.getPixel(secY,
                                    secX)) {
                                Log.d("sourceBitmap ",sourceBitmap.getPixel(mainY + secY, mainX + secX)+"");
                                Log.d("serchingBitmap ",serchingBitmap.getPixel(secY,secX)+"");
                                boolean b=sourceBitmap.getPixel(mainY + secY, mainX + secX) == serchingBitmap.getPixel(secY,secX);
                                Log.d("equality ",b+"");
                                found = false;
                                break inner;
                            }
                    if (found)  return true;
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

}