package com.example.makecomment.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makecomment.Activities.ProfileActivity;
import com.example.makecomment.Models.Comment;
import com.example.makecomment.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private static final String TAG = "MyActivity";
    private Context mContext;
    private List<Comment> mData;


    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.comment_row,parent,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        Picasso.get().load(mData.get(position).getUimg()).into(holder.img_user);
        holder.user_name.setText(mData.get(position).getUname());
        holder.tv_content.setText(mData.get(position).getContent());
        holder.tv_date.setText(timestampToString((Long)mData.get(position).getTimestamp()));
        //holder.show_name.setText(mData.get(position).getShowName());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_user;
        TextView user_name,tv_content,tv_date,show_name;

        public CommentViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.commentUserImgShare);
            user_name = itemView.findViewById(R.id.commentUsernameShare);
            tv_content = itemView.findViewById(R.id.commentContentShare);
            tv_date = itemView.findViewById(R.id.commentDateShare);
            show_name = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(this);//todo:without this line you cant click items

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            //Log.d(TAG, "yuaydi "+ mData.get(position).getUid());
            final Intent intent = new Intent(mContext, ProfileActivity.class);
            intent.putExtra("getUserName",mData.get(position).getUname());
            intent.putExtra("getUserImage",mData.get(position).getUimg());
            intent.putExtra("getUserInstaName",mData.get(position).getInstaUserName());

            img_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mContext.startActivity(intent);
                }
            });
        }
    }



    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();
        return date;


    }
}
