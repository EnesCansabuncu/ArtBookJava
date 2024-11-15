package com.enescansabuncu.artbookjava;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.enescansabuncu.artbookjava.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Art>artArrayList;
    ArtAdapter artAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        artArrayList=new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArtAdapter artAdapter;
        artAdapter=new ArtAdapter(artArrayList);
        binding.recyclerView.setAdapter(artAdapter);
        getData();

    }
private void getData(){
        try {
            SQLiteDatabase sqLiteDatabase=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            Cursor cursor=sqLiteDatabase.rawQuery("SELECT*FROM arts",null);
            int nameIx=cursor.getColumnIndex("artsname");
            int idIx=cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                String name=cursor.getString(nameIx);
                int id=cursor.getInt(idIx);
                Art art=new Art(name,id);
                artArrayList.add(art);



            }
            artAdapter.notifyDataSetChanged();
            cursor.close();


        }catch (Exception exception){
            exception.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//burda menüyle bağlantı kuruyoruz
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.art_menu,menu);
        //burda bana ilk kısım nereyi bağlayacaz onu soruyor ikinci kısmı bana verilen üstte yazanı yazıyoruz//
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.add_art){
            Intent intent=new Intent(this,ArtActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }


}