package com.example.sample;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import static androidx.core.content.FileProvider.getUriForFile;

public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "save";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if (call.method.equals("saveAndOpenImage")) {

                                    new Thread(){
                                        @Override
                                        public void run() {
                                            try {
                                                saveOpenImage(call.argument("data"), call.argument("name"));
                                                
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.start();



                            } 
                        }
                );
    }

    

    void saveOpenImage(byte[] data, String name) throws Exception {
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        final String IMAGES_FOLDER_NAME = "Quotation";
        OutputStream fos;
        Uri imgUri = null;
        String imagesDir = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getApplicationContext().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/" + IMAGES_FOLDER_NAME);
            imgUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imgUri);
        } else {
            imagesDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures" + File.separator + IMAGES_FOLDER_NAME;
            File file = new File(imagesDir);
            if (!file.exists()) {
                file.mkdir();
            }
            File img = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(img);
            imgUri = getUriForFile(getContext(), "com.example.fileprovider", img);

        }

        image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setDataAndType(imgUri, "image/*");
        startActivity(intent);
        fos.flush();
        fos.close();

    }


}