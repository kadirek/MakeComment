package com.example.makecomment.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    private String remainText = "Birazdan başlayacak";
    private int remain = 600;

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

        Log.d(TAG, "suankidurumu "+ parseItem.getClicked());


        if(position % 2==0){
            holder.constraintLayoutMain.setBackgroundResource(R.drawable.gradient_odd);
            }else if(position % 2==1){
            holder.constraintLayoutMain.setBackgroundResource(R.drawable.gradient_even);
        }

        if(parseItem.getClicked()){
            holder.imageView.setClickable(true);
            holder.imageView.setEnabled(true);
            holder.imageView.setFocusable(true);

            holder.textView.setClickable(true);
            holder.textView.setEnabled(true);
            holder.textView.setFocusable(true);

            holder.commentCount.setClickable(true);
            holder.commentCount.setEnabled(true);
            holder.commentCount.setFocusable(true);

            holder.constraintLayout.setClickable(true);
            holder.constraintLayout.setEnabled(true);
            holder.constraintLayout.setFocusable(true);

            holder.imageViewBackground.setClickable(true);
            holder.imageViewBackground.setEnabled(true);
            holder.imageViewBackground.setFocusable(true);

            holder.peopleIcon.setClickable(true);
            holder.peopleIcon.setEnabled(true);
            holder.peopleIcon.setFocusable(true);

            holder.yorumTextView.setClickable(true);
            holder.yorumTextView.setEnabled(true);
            holder.yorumTextView.setFocusable(true);

            holder.remainTimeTextView.setClickable(true);
            holder.remainTimeTextView.setEnabled(true);
            holder.remainTimeTextView.setFocusable(true);

            holder.peopleIcon.setVisibility(View.GONE);
            holder.yorumTextView.setVisibility(View.GONE);
            holder.commentCount.setVisibility(View.GONE);
            holder.remainTimeTextView.setVisibility(View.GONE);
        }else{
            holder.imageView.setClickable(false);
            holder.imageView.setEnabled(false);
            holder.imageView.setFocusable(false);

            holder.textView.setClickable(false);
            holder.textView.setEnabled(false);
            holder.textView.setFocusable(false);

            holder.commentCount.setClickable(false);
            holder.commentCount.setEnabled(false);
            holder.commentCount.setFocusable(false);

            holder.constraintLayout.setClickable(false);
            holder.constraintLayout.setEnabled(false);
            holder.constraintLayout.setFocusable(false);

            holder.imageViewBackground.setClickable(false);
            holder.imageViewBackground.setEnabled(false);
            holder.imageViewBackground.setFocusable(false);

            holder.peopleIcon.setClickable(false);
            holder.peopleIcon.setEnabled(false);
            holder.peopleIcon.setFocusable(false);

            holder.yorumTextView.setClickable(false);
            holder.yorumTextView.setEnabled(false);
            holder.yorumTextView.setFocusable(false);

            holder.remainTimeTextView.setClickable(false);
            holder.remainTimeTextView.setEnabled(false);
            holder.remainTimeTextView.setFocusable(false);

            holder.peopleIcon.setVisibility(View.VISIBLE);
            holder.yorumTextView.setVisibility(View.VISIBLE);
            holder.commentCount.setVisibility(View.VISIBLE);
            holder.remainTimeTextView.setVisibility(View.VISIBLE);

        }


        if (parseItem.getRemainTime().isEmpty()) {
            holder.remainTimeTextView.setText("?");
        } else if(Integer.valueOf(parseItem.getRemainTime())>remain){
            holder.remainTimeTextView.setText(remainText);
            holder.remainTimeTextView.setTextColor(Color.parseColor("#F47676"));
        } else{
            holder.remainTimeTextView.setText(parseItem.getRemainTime()+" dk. kaldı");
            holder.remainTimeTextView.setTextColor(Color.parseColor("#A2A1A1"));
        }

        if (parseItem.getTitle().isEmpty() ) {
            holder.textView.setText("");
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
        ImageView imageViewBackground;
        ImageView peopleIcon;
        TextView yorumTextView;
        TextView textView;
        TextView commentCount;
        TextView remainTimeTextView;
        ConstraintLayout constraintLayout;
        ConstraintLayout constraintLayoutMain;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);

            imageViewBackground = itemView.findViewById(R.id.backgroundImage);
            peopleIcon = itemView.findViewById(R.id.peopleIcon);
            yorumTextView = itemView.findViewById(R.id.commentCountText);
            remainTimeTextView = itemView.findViewById(R.id.textViewRemainTime);
            textView = itemView.findViewById(R.id.textView);
            commentCount = itemView.findViewById(R.id.commentCount);
            constraintLayout = itemView.findViewById(R.id.constraintLayoutMain);
            constraintLayoutMain = itemView.findViewById(R.id.constraintLayoutMainindeMaini);


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
