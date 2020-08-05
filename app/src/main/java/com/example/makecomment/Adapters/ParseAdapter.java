package com.example.makecomment.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makecomment.Activities.TvDetails;
import com.example.makecomment.Models.ParseItem;
import com.example.makecomment.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ViewHolder> {
    private static final String TAG = "MyActivity";


    private ArrayList<ParseItem> parseItems;
    private Context context;

    public ParseAdapter(ArrayList<ParseItem> parseItems, Context context){
        this.parseItems = parseItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ParseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parse_item, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParseAdapter.ViewHolder holder, int position) {
        ParseItem parseItem = parseItems.get(position);

        //holder.commentCount.setText(String.valueOf(parseItem.getCommentCount()));

        if (parseItem.getTitle().isEmpty()) {
            holder.textView.setText("?");
        } else{
            holder.textView.setText(parseItem.getTitle());
        }

        if (parseItem.getCommentCount() == 0) {
            holder.commentCount.setText("0");
        } else{
            holder.commentCount.setText(String.valueOf(parseItem.getCommentCount()));
        }

        if (parseItem.getImgUrl().isEmpty()) {
            holder.imageView.setImageResource(R.drawable.icon);
        } else{
            Picasso.get().load(parseItem.getImgUrl()).into(holder.imageView);
        }
        //Picasso.get().load(parseItem.getImgUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return parseItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView textView;
        TextView commentCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            commentCount = itemView.findViewById(R.id.commentCount);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Intent tvDetails = new Intent(context, TvDetails.class);
            Toast.makeText(context, position+".kanal", Toast.LENGTH_SHORT).show();
            tvDetails.putExtra("whichChannel",String.valueOf(getAdapterPosition()));
            tvDetails.putExtra("titleName",parseItems.get(position).getTitle());
            tvDetails.putExtra("imageUrl",parseItems.get(position).getImgUrl());
            tvDetails.putExtra("durationMinute",parseItems.get(position).getDurationMinute());
            tvDetails.putExtra("starttime", parseItems.get(position).getStartTime());
            context.startActivity(tvDetails);

        }
    }
}
