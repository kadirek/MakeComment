package com.example.makecomment.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makecomment.Adapters.CommentAdapter;
import com.example.makecomment.Models.Comment;
import com.example.makecomment.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TvDetails extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyActivity";

    //UI
    private TextView nameOfShow;
    private ImageView userImg,imgOfShow;
    private TextView commentField;
    private Button sendComment;
    private LinearLayout linearLayout;

    String channelNumber;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mDb;

    RecyclerView commentRV;
    //CommentAdapter commentAdapter;
    List<Comment> listOfComment;
    static String COMMENT_KEY = "CommentKey" ;

    private BottomSheetDialog dialog;
    static String titleForTry ;
    static String instaUserName;
    private Toolbar toolbar;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_details);

        //For full screen and hide actionbar
       /* Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();*/

        //nameOfShow = findViewById(R.id.titleOfShow);
        userImg = findViewById(R.id.commentUserImg);
        imgOfShow = findViewById(R.id.imgOfShow);
        commentField = findViewById(R.id.commentField);
        //sendComment = findViewById(R.id.commentSendButton);
        commentRV = findViewById(R.id.commentRV);
        linearLayout = findViewById(R.id.bottomLinearLayout);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();

        if(mUser==null){
            linearLayout.setVisibility(View.GONE);
        }

        channelNumber = getIntent().getExtras().getString("whichChannel");
        //GET DATA from ParseAdapter class
        String imagePicasso = getIntent().getExtras().getString("imageUrl");
        int imagePicasso2 = getIntent().getIntExtra("imageUrl",0);
        Log.d(TAG, "simge"+ imagePicasso);//show's image
        final String title = getIntent().getExtras().getString("titleName");//show's title
        titleForTry = title;
        final String time = getIntent().getExtras().getString("starttime");//show's start time
        String duration = getIntent().getExtras().getString("durationMinute");//show's duration (minute)

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(titleForTry);
        collapsingToolbar.setCollapsedTitleTypeface(ResourcesCompat.getFont(this,R.font.baloo));//todo:font

        commentField.setOnClickListener(this);


        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if(imagePicasso == null){
            imgOfShow.setImageResource(imagePicasso2);
        }else{
            Picasso.get().load(imagePicasso).into(imgOfShow);//head photo of tv program
        }

        if(signInAccount != null){
            Picasso.get().load(signInAccount.getPhotoUrl()).into(userImg);
            getInstaUserName();
        }

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//adding duration to start time and finding the finish hour
        Date d = null;
        try {
            d = df.parse(time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, Integer.valueOf(duration));
        final String newTime = df.format(cal.getTime());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Comment");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(channelNumber)) {
                    //Toast.makeText(TvDetails.this, "Burda yorum var", Toast.LENGTH_SHORT).show();///todo:önce yorum var mı diye kontrol ediyo
                    try {//Check if tv show finished or still live
                        Calendar now = Calendar.getInstance();
                        String current_hour = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
                        String st_hour = time;
                        String en_hour = newTime;
                        @SuppressLint("SimpleDateFormat") final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                        Date sth = null;
                        sth = format.parse(st_hour);
                        Date enh = format.parse(en_hour);
                        Date nowh = format.parse(current_hour );
                        if (nowh != null) {
                            if (nowh.before(enh) && nowh.after(sth) && sth.before(enh)
                                    || enh.before(sth) && !(nowh.before(enh) && nowh.after(sth))) {
                                Log.d(TAG, "gecti");
                            } else {
                                Toast.makeText(TvDetails.this, "Yeni Program Başladı", Toast.LENGTH_SHORT).show();//saati geçti ve yorumlar silindi
                                FirebaseDatabase.getInstance().getReference("Comment").child(channelNumber).removeValue();
                            }
                        }
                    } catch (ParseException ignored) {
                    }

                    FirebaseDatabase.getInstance().getReference().child("Comment").child(channelNumber).orderByChild("showName").equalTo(title)
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        //Toast.makeText(TvDetails.this, "selam", Toast.LENGTH_SHORT).show();todo:yorum var ancak program hala bitmedi
                                        Log.d(TAG, "kackadir "+ dataSnapshot.getRef());
                                        //bus number exists in Database
                                    } else {
                                        Toast.makeText(TvDetails.this, "Yeni Program Başladı", Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase.getInstance().getReference("Comment").child(channelNumber).removeValue();//Programa özel yapılan yorumlar silindi

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        initCommentRV();
    }
    public void goYourProfile(View view) {
        Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
        startActivity(intent);
    }

    private void initCommentRV() {

        listOfComment = new ArrayList<>();
        final CommentAdapter adapter = new CommentAdapter(TvDetails.this,listOfComment);

        DatabaseReference commentRef = mDb.getReference("Comment").child(channelNumber);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfComment.clear();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    listOfComment.add(comment) ;
                    adapter.notifyDataSetChanged();
                }

                mLinearLayoutManager = new LinearLayoutManager(TvDetails.this);
                mLinearLayoutManager.setReverseLayout(true);
                commentRV.setLayoutManager(mLinearLayoutManager);
                commentRV.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.commentField){

            showCommentDialog();
        }
    }

    private void showCommentDialog() {

        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button commentField = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);

        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());

        commentField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContent)){

                    //commentOnWork(commentContent);
                    dialog.dismiss();

                    //sendComment.setVisibility(View.INVISIBLE);

                    DatabaseReference dRef = mDb.getReference("Comment").child(channelNumber).push();
                    String contentOfComment = commentContent;
                    String uid = mUser.getUid();
                    String uImg = mUser.getPhotoUrl().toString();
                    String uName = mUser.getDisplayName();
                    String showName = titleForTry;
                    String instagramUserName = instaUserName;


                    Comment comment = new Comment(contentOfComment, uid,uImg,uName,showName,instaUserName);

                    dRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Toast.makeText(TvDetails.this, "Yorum eklendi", Toast.LENGTH_SHORT).show();
                            commentField.setText("");
                            //sendComment.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TvDetails.this, "Hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //Toast.makeText(TvDetails.this,"Yorum başarılı\n",Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(TvDetails.this,"Yorum içeriği boş olamaz\n",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    commentField.setBackgroundColor(Color.parseColor("#F47676"));
                }else {
                    commentField.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    private void getInstaUserName(){
        final String userKey = mUser.getUid();
        mDb.getReference().child("Users").child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userInstagram = String.valueOf(dataSnapshot.child("Personal Informations").child("instaUserName").getValue());
                instaUserName = userInstagram;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


}
