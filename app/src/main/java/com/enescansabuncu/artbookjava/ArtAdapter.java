package com.enescansabuncu.artbookjava;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enescansabuncu.artbookjava.databinding.RecryclerRowBinding;

import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {
    ArrayList<Art>artArrayList;


    public ArtAdapter(ArrayList<Art>artArrayList){
        this.artArrayList=artArrayList;
    }
    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecryclerRowBinding recryclerRowBinding=RecryclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

 return  new ArtHolder(recryclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtAdapter.ArtHolder holder, int position) {
holder.binding.recrylerViewTextView.setText(artArrayList.get(position).name);
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
    Intent intent=new Intent(holder.itemView.getContext(),ArtActivity.class);
    intent.putExtra("info","old");
   intent.putExtra("artId",artArrayList.get(position).id);
  holder.itemView.getContext().startActivity(intent);

    }
});
    }

    @Override
    public int getItemCount() {
        return artArrayList.size();
    }

    public class ArtHolder extends RecyclerView.ViewHolder{
private RecryclerRowBinding binding;
        public ArtHolder( RecryclerRowBinding binding) {

            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
