package com.kadirek.yorumyap.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kadirek.yorumyap.Models.OpenDialogForInsta;
import com.kadirek.yorumyap.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements OpenDialogForInsta.ExampleDialogListener{
    private static final String TAG = "MyActivity";
    private TextView nameTextView, emailTextView, instagramTextView;
    private Button logout;
    private ImageView profilePic;
    private Button instagram;
    private Button instagramEditButton;
    private ConstraintLayout constraintLayout;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;
    private FirebaseUser currentUser;
    private GoogleSignInClient mGoogleSignInClient;

    private String commentUserUid;
    private String commentUserName;
    private String commentUserImage;
    private String commentInstaUserName;
    private String userUidGoggle;

   /* private SharedPreferences myPrefs;
    private SharedPreferences.Editor editor;
    private String userImageUrlShared;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = findViewById(R.id.profilePhoto);
        logout = findViewById(R.id.logout);
        nameTextView = findViewById(R.id.nameText);
        emailTextView = findViewById(R.id.emailText);
        instagram = findViewById(R.id.instagramButton);
        instagramTextView = findViewById(R.id.instagramText);
        instagramEditButton = findViewById(R.id.editInstagramButton);
        constraintLayout = findViewById(R.id.emailConstraintLayout);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

       /* myPrefs = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = myPrefs.edit();
        userImageUrlShared = myPrefs.getString("key_name_imageurl",null);*/
        try {
            commentUserUid = getIntent().getExtras().getString("getUserUid");
            commentUserName = getIntent().getExtras().getString("getUserName");
            commentUserImage = getIntent().getExtras().getString("getUserImage");
            commentInstaUserName = getIntent().getExtras().getString("getUserInstaName");
            userUidGoggle = currentUser.getUid();

        }catch (Exception e){

        }

        if (!(commentUserUid == null || commentUserUid.equals("") || commentUserUid.equals("null") || commentUserUid.isEmpty()) && (commentUserUid == userUidGoggle||commentUserUid.equals(userUidGoggle))){
            Picasso.get().load(signInAccount.getPhotoUrl()).into(profilePic);
            getDataFromGoogleWithFirebase();
        } else if (!(commentUserName == null || commentUserName.equals("") || commentUserName.equals("null") || commentUserName.isEmpty())) {
            Picasso.get().load(commentUserImage).into(profilePic);
            nameTextView.setText(commentUserName);
            instagramTextView.setText(commentInstaUserName);
            instagramEditButton.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);
            constraintLayout.setVisibility(View.GONE);
            whenInstaUserExist(commentInstaUserName);
        }else if(signInAccount != null || !signInAccount.equals("")){
            Picasso.get().load(signInAccount.getPhotoUrl()).into(profilePic);
            getDataFromGoogleWithFirebase();

        }



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutG();
            }
        });

        instagramEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogForAddInsta();
            }
        });



    }
    private void getDataFromGoogleWithFirebase(){
        final String userKey = currentUser.getUid();
        mDb.getReference().child("Users").child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = String.valueOf(dataSnapshot.child("Personal Informations").child("ad").getValue());
                String userEmail = String.valueOf(dataSnapshot.child("Personal Informations").child("email").getValue());
                String userInstagram = String.valueOf(dataSnapshot.child("Personal Informations").child("instaUserName").getValue());

              /*  String userNameShared = myPrefs.getString("key_name_username",null);
                String userEmailShared = myPrefs.getString("key_name_email",null);*/

                nameTextView.setText(userName);
                emailTextView.setText(userEmail);
                if(userInstagram.equals("Eklenmedi")){
                    instagramTextView.setText("Eklenmedi! Hemen ekle...");
                whenInstaUserNotExist();

                }else{
                    instagramTextView.setText(userInstagram);
                whenInstaUserExist(userInstagram);
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void whenInstaUserExist(final String userName){


            instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(userName == "Eklenmedi" || userName.equals("Eklenmedi"))){
                Toast.makeText(ProfileActivity.this, "Açılıyor", Toast.LENGTH_SHORT).show();
                            Uri uri = Uri.parse("https://www.instagram.com/_u/"+userName);
                            Intent instagram = new Intent(Intent.ACTION_VIEW,uri);
                            instagram.setPackage("com.instagram.android");
                            try {
                                startActivity(instagram);
                            }catch (ActivityNotFoundException e){
                                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.instagram.com/_u/"+userName)));
                            }
            }
                else{
                    Toast.makeText(ProfileActivity.this, "Instagram kullanıcı adı bulunamadı", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void whenInstaUserNotExist(){
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogForAddInsta();
            }
        });
    }

    public void openDialogForAddInsta()
    {
        OpenDialogForInsta openDialogForInsta = new OpenDialogForInsta();
        openDialogForInsta.show(getSupportFragmentManager(),"example dialog");
    }

    @Override
    public void applyTexts(String username) {//update instagram user name on database
        if(username.length() <= 2){
            Toast.makeText(this, "En az 3 karakter girilmeli", Toast.LENGTH_SHORT).show();
        } else {
            final String userKey1 = currentUser.getUid();

            DatabaseReference updateData = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userKey1)
                    .child("Personal Informations")
                    .child("instaUserName");

            updateData.setValue(username);
            instagramTextView.setText(username);
        }



    }

    private void signOutG() {//Choose everytime different account
        // Firebase sign out
      /*  editor.clear();
        editor.commit();*/
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
    }
}
