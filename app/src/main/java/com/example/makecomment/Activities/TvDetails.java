package com.example.makecomment.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.google.android.material.appbar.AppBarLayout;
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

public class TvDetails extends AppCompatActivity  {
    private static final String TAG = "MyActivity";

    //UI
    private TextView nameOfShow;
    private ImageView userImg,imgOfShow;
    private EditText commentField;
    private TextView commentFieldLogin;
    private Button sendComment;
    private LinearLayout linearLayout;
    private LinearLayout linearLayoutLogin;
    private LinearLayout linearLayoutWarning;
    private LinearLayout linearLayoutRecyclerviewContainer;
    private CollapsingToolbarLayout collapsingToolbar;
    private ProgressBar progressBar;

    private String channelNumber;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDb;
    private GoogleSignInAccount signInAccount;
    private RecyclerView commentRV;
    private List<Comment> listOfComment;
    private static String COMMENT_KEY = "CommentKey" ;

    private BottomSheetDialog dialog;
    private String imagePicasso;
    private String time;
    private String duration;
    private String remainText;
    private int imagePicasso2;
    private static String instaUserName;
    private static String title;
    private Toolbar toolbar;
    private LinearLayoutManager mLinearLayoutManager;

    private Handler handler;
    private EditText commentText;
    private View commentView;
    private Button shareCommentButton;
    private TextView remainTimeTvDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_details);

        initUI();

        getDataFromParseAdapter();

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
                    try {//Check if tv show finished or still running
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
                            if (nowh.before(enh) && nowh.after(sth) && sth.before(enh) || enh.before(sth) && !(nowh.before(enh) && nowh.after(sth))) {
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

    private void initUI(){
        userImg = findViewById(R.id.commentUserImg);
        imgOfShow = findViewById(R.id.imgOfShow);
        commentField = findViewById(R.id.commentField);
        commentFieldLogin = findViewById(R.id.commentFieldLogin);
        commentRV = findViewById(R.id.commentRV);
        linearLayout = findViewById(R.id.bottomLinearLayout);
        linearLayoutLogin = findViewById(R.id.bottomLinearLayoutLogin);
        linearLayoutWarning = findViewById(R.id.warningLinearLayout);
        linearLayoutRecyclerviewContainer = findViewById(R.id.detail_page_comment_container);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        progressBar = findViewById(R.id.progressBarTvDetails);
        shareCommentButton = findViewById(R.id.dialog_comment_bt);
        remainTimeTvDetails = findViewById(R.id.remainTimeTvDetails);

        dialog = new BottomSheetDialog(this);
        commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();

        signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        if(mUser==null){
            linearLayout.setVisibility(View.GONE);
            linearLayoutLogin.setVisibility(View.VISIBLE);
            commentFieldLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(TvDetails.this,LoginActivity.class);
                    startActivity(intent);
                }
            });
            linearLayoutWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(TvDetails.this,LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        if(signInAccount != null){
            Picasso.get().load(signInAccount.getPhotoUrl()).into(userImg);
            getInstaUserName();
            linearLayoutWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commentField.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    showCommentDialog();

                }
            });
        }
        commentField.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_UP == motionEvent.getAction()){
                    showCommentDialog();
                }
                return false;
            }
        });
        //commentField.setOnClickListener(this);

    }
    private void getDataFromParseAdapter(){
        channelNumber = getIntent().getExtras().getString("whichChannel");
        imagePicasso = getIntent().getExtras().getString("imageUrl");//default channel icon
        imagePicasso2 = getIntent().getIntExtra("imageUrl",0);//custom channel icon
        title = getIntent().getExtras().getString("titleName");//show's title
        time = getIntent().getExtras().getString("starttime");//show's start time
        duration = getIntent().getExtras().getString("durationMinute");//show's duration (minute)
        remainText = getIntent().getExtras().getString("remainTimeText");//show's duration (minute)

        collapsingToolbar.setTitle(title);//todo: setting title to collapsingToolbar
        collapsingToolbar.setCollapsedTitleTypeface(ResourcesCompat.getFont(this,R.font.baloo));//todo:font
        if(remainText.isEmpty()){
            remainTimeTvDetails.setText("?");
        } else if(Integer.valueOf(remainText) > 600){
            remainTimeTvDetails.setText("Birazdan başlayacak");
        }else{
            remainTimeTvDetails.setText(remainText+" dk. kaldı");
        }

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);//todo: State of collapsing toolbar change visibility of remaintextview
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    remainTimeTvDetails.setVisibility(View.GONE);
                    // Collapsed
                } else if (verticalOffset == 0) {
                    // Expanded
                    remainTimeTvDetails.setVisibility(View.VISIBLE);
                } else {
                    remainTimeTvDetails.setVisibility(View.VISIBLE);
                    // Somewhere in between
                }
            }
        });


        if(imagePicasso == null){
            imgOfShow.setImageResource(imagePicasso2);
        }else{
            Picasso.get().load(imagePicasso).into(imgOfShow);//head photo of tv program
        }
    }

    public void goYourProfile(View view) {
        Intent intent=new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }

    private void initCommentRV() {
        progressBar.setVisibility(View.VISIBLE);
        imgOfShow.setVisibility(View.GONE);

        handler = new Handler();
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
                handler.postDelayed(new Runnable() {
                    public void run() {
                        commentRV.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        imgOfShow.setVisibility(View.VISIBLE);
                        if(listOfComment.isEmpty()){
                            linearLayoutWarning.setVisibility(View.VISIBLE);
                            linearLayoutRecyclerviewContainer.setVisibility(View.GONE);
                        }else{
                            linearLayoutWarning.setVisibility(View.GONE);
                            linearLayoutRecyclerviewContainer.setVisibility(View.VISIBLE);
                        }
                    }
                }, 1000);



            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

/*
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.commentField){
            showCommentDialog();
        }
    }
*/

    private void showCommentDialog() {
        linearLayoutWarning.setVisibility(View.GONE);
        commentField.setImeOptions(EditorInfo.IME_ACTION_SEND);
        commentField.setRawInputType(InputType.TYPE_CLASS_TEXT);

        userImg.setVisibility(View.GONE);

        shareCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String commentContent = commentField.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContent)){
                    commentField.getText().clear();
                    commentField.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(commentField.getWindowToken(), 0);

                    DatabaseReference dRef = mDb.getReference("Comment").child(channelNumber).push();
                    String contentOfComment = commentContent;
                    String uid = mUser.getUid();
                    String uImg = mUser.getPhotoUrl().toString();
                    String uName = mUser.getDisplayName();
                    String showName = title;
                    String instagramUserName = instaUserName;


                    final Comment comment = new Comment(contentOfComment, uid,uImg,uName,showName,instaUserName);

                    dRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Toast.makeText(TvDetails.this, "Yorum eklendi", Toast.LENGTH_SHORT).show();
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
        commentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>0){
                    commentField.setTextColor(ContextCompat.getColor(TvDetails.this, R.color.gray));

                }else {
                    commentField.setTextColor(ContextCompat.getColor(TvDetails.this, R.color.hintColor));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (commentField.getLineCount() == commentField.getMaxLines()) {
                    commentField.setMaxLines((commentField.getLineCount() + 1));
                }
            }
        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {//todo:when click outside of edittext closing the keyboard
        if (getCurrentFocus() != null) {
            userImg.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
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
