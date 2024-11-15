package com.enescansabuncu.artbookjava;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.enescansabuncu.artbookjava.databinding.ActivityArtBinding;
import com.enescansabuncu.artbookjava.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {

    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent>activityResultLauncher;
    ActivityResultLauncher<String>permissionResultLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
        super.onCreate(savedInstanceState);
        binding =ActivityArtBinding .inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        if (info.equals("new")){
binding.nameText.setText("");
binding.artistText.setText("");
binding.yearText.setText("");
binding.button.setVisibility(View.VISIBLE);
binding.imageView.setImageResource(R.drawable.selectimage);
        }
        else{
            int artId=intent.getIntExtra("artId",0);
            binding.button.setVisibility(View.INVISIBLE);
            try {
                Cursor cursor= database.rawQuery("select*from arts where id=?",new String[] {String.valueOf(artId)});
                int artNameIx=cursor.getColumnIndex("artsname");
                int painterNameIx=cursor.getColumnIndex("paintername");
                int  yearIx=cursor.getColumnIndex("year");
                int imageIx=cursor.getColumnIndex("image");
                while (cursor.moveToNext()){
                    binding.nameText.setText(cursor.getString(artNameIx));
                    binding.artistText.setText(cursor.getString(painterNameIx));
                    binding.yearText.setText(cursor.getString(yearIx));
                    byte[] bytes=cursor.getBlob(imageIx);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);



                }
                cursor.close();



            }catch (Exception exception){
                exception.printStackTrace();
            }


        }


    }
    public void save(View view){
        String artsName=binding.nameText.getText().toString();
        String artistName=binding.artistText.getText().toString();
        String year=binding.yearText.getText().toString();
        Bitmap smalİmage=makeSmallerİmage(selectedImage,300);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smalİmage.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
        byte[] byteArray =outputStream.toByteArray();
        try {

          database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY, artsname VARCHAR ,paintername VARCHAR,year VARCHAR,image BLOB )");
          String sqlString="INSERT INTO arts(artsname,paintername,year,image) VALUES(?,?,?,?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,artsName);
            sqLiteStatement.bindString(2,artistName);
            sqLiteStatement.bindString(3,year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();



        }catch (Exception e){
            e.printStackTrace();
        }
Intent intent=new Intent(this, MainActivity.class);
intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
startActivity(intent);

    }
    public void selectİmage(View view){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed for galery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    permissionResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
            else {
                Intent intentToGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGalery);
            }

        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for galery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }
            else{
                permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else {
            Intent intentToGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGalery);
        }}

    }
    private void registerLauncher(){
        permissionResultLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
             if(result){
                 Intent intentToGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
activityResultLauncher.launch(intentToGalery);
             }
             else{
                 Toast.makeText(ArtActivity.this, "Needed permission", Toast.LENGTH_SHORT).show();
             }
            }
        });
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode()==RESULT_OK){
                  Intent intentFromResult= result.getData();
                  if (intentFromResult!=null){
                  Uri imageData= intentFromResult.getData();
                  try {
                      if (Build.VERSION.SDK_INT>=28){
                          ImageDecoder.Source source=ImageDecoder.createSource(ArtActivity.this.getContentResolver(),imageData);
                          selectedImage=  ImageDecoder.decodeBitmap(source);
                          binding.imageView.setImageBitmap(selectedImage);}
                      else{
                          selectedImage= MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(),imageData);
                          binding.imageView.setImageBitmap(selectedImage);
                      }


                  }
                  catch(Exception exception){

                      }
                  }
                  }
                }

            });
        }
    public Bitmap makeSmallerİmage(Bitmap image,int maksimumSize) {
        int width = image.getWidth();
        int hight = image.getHeight();
        float bitmapRatio = (float) width / (float) hight;
        if (bitmapRatio > 1) {
            width = maksimumSize;
            hight = (int) (width / bitmapRatio);
        } else {hight=maksimumSize;
            width=(int)(width*bitmapRatio);



        }
        return image;
    }
    }
