package com.example.makecomment.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makecomment.Activities.TvDetails;
import com.example.makecomment.Models.ParseItem;
import com.example.makecomment.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ViewHolder> {
    private static final String TAG = "MyActivity";
    private String tvSekiz = "https://www.canlitv.vin/kanallar/tv-8-hd-izle.png";
    private String showTv = "https://www.canlitv.vin/kanallar/show-tv.png";
    private String trtBir = "https://www.canlitv.vin/kanallar/trt-1.png";
    private String foxTv = "https://www.canlitv.vin/kanallar/fox-tv-turkiye.png";
    private String starTv = "https://www.canlitv.vin/kanallar/star-tv.png";
    private String kanalD = "https://www.canlitv.vin/kanallar/kanal-d.png";
    private String kanalYedi = "https://www.canlitv.vin/kanallar/kanal-7-canli-hd-izle-1.png";
    private String haberGlobal = "https://www.canlitv.vin/kanallar/haber-global.gif";
    private String dmax = "https://www.canlitv.vin/kanallar/d-max.png";

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

        Log.d(TAG, position+" gadiek "+ parseItem.getImgUrl());
        if (parseItem.getImgUrl().isEmpty()) {
            holder.imageView.setImageResource(R.drawable.icon);
        } else if(parseItem.getImgUrl().equals(tvSekiz)){
            holder.imageView.setImageResource(R.drawable.tvsekiz);
        } else if(parseItem.getImgUrl().equals(showTv)){
            holder.imageView.setImageResource(R.drawable.showpng);
        }else if(parseItem.getImgUrl().equals(trtBir)){
            holder.imageView.setImageResource(R.drawable.trtbir);
        }else if(parseItem.getImgUrl().equals(foxTv)){
            holder.imageView.setImageResource(R.drawable.foxtv);
        }else if(parseItem.getImgUrl().equals(starTv)){
            holder.imageView.setImageResource(R.drawable.startv);
        }else if(parseItem.getImgUrl().equals(kanalD)){
            holder.imageView.setImageResource(R.drawable.kanald);
        }else if(parseItem.getImgUrl().equals(kanalYedi)){
            holder.imageView.setImageResource(R.drawable.kanalyedi);
        }else if(parseItem.getImgUrl().equals(haberGlobal)){
            holder.imageView.setImageResource(R.drawable.haberglobal);
        }else if(parseItem.getImgUrl().equals(dmax)){
            holder.imageView.setImageResource(R.drawable.dmax);
        }else {
            Picasso.get().load(parseItem.getImgUrl()).into(holder.imageView);
        }

    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
            itemView.setOnClickListener(this);//todo:without this line you cant click items
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Intent tvDetails = new Intent(context, TvDetails.class);
            //Toast.makeText(context, position+".kanal", Toast.LENGTH_SHORT).show();
            tvDetails.putExtra("whichChannel",String.valueOf(getAdapterPosition()));
            tvDetails.putExtra("titleName",parseItems.get(position).getTitle());
           // tvDetails.putExtra("imageUrl",parseItems.get(position).getImgUrl());
            if (parseItems.get(position).getImgUrl().isEmpty()) {
                tvDetails.putExtra("imageUrl",R.drawable.icon);
            } else if(parseItems.get(position).getImgUrl().equals(tvSekiz)){
                tvDetails.putExtra("imageUrl",R.drawable.tvsekiz);
            }else if(parseItems.get(position).getImgUrl().equals(showTv)){
                tvDetails.putExtra("imageUrl",R.drawable.showpng);
            } else if(parseItems.get(position).getImgUrl().equals(trtBir)){
                tvDetails.putExtra("imageUrl",R.drawable.trtbir);
            }else if(parseItems.get(position).getImgUrl().equals(foxTv)){
                tvDetails.putExtra("imageUrl",R.drawable.foxtv);
            }else if(parseItems.get(position).getImgUrl().equals(starTv)){
                tvDetails.putExtra("imageUrl",R.drawable.startv);
            }else if(parseItems.get(position).getImgUrl().equals(kanalD)){
                tvDetails.putExtra("imageUrl",R.drawable.kanald);
            }else if(parseItems.get(position).getImgUrl().equals(kanalYedi)){
                tvDetails.putExtra("imageUrl",R.drawable.kanalyedi);
            }else if(parseItems.get(position).getImgUrl().equals(haberGlobal)){
                tvDetails.putExtra("imageUrl",R.drawable.haberglobal);
            }else if(parseItems.get(position).getImgUrl().equals(dmax)){
                tvDetails.putExtra("imageUrl",R.drawable.dmax);
            }else {
                tvDetails.putExtra("imageUrl",parseItems.get(position).getImgUrl());
            }
            tvDetails.putExtra("durationMinute",parseItems.get(position).getDurationMinute());
            tvDetails.putExtra("starttime", parseItems.get(position).getStartTime());


            context.startActivity(tvDetails);

        }
    }
}
