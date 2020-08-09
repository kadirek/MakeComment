package com.example.makecomment.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.makecomment.Models.User2;
import com.example.makecomment.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //UI
    private EditText password;
    private EditText email;
    private Button signInBttn;
    private TextView signUpBttn;
    private TextView forgotPasswordBttn;
    //private FrameLayout progressBarSignIn;
    private SignInButton loginButtonGoogle;


    //LOGIN
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount gsa;
    private static final int RC_SIGN_IN = 9001;
    private Boolean isExist;
    private Random r;;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInBttn=findViewById(R.id.signInButton);
        mAuth=FirebaseAuth.getInstance();
        signInBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        gsa=GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    protected void onStop() {
        super.onStop();
        //Facebook login
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account!=null){
                    firebaseAuthWithGoogle(account);
                }
                else{
                    Toast.makeText(this, "Bir hata oluştu", Toast.LENGTH_SHORT).show();
                }

            } catch (ApiException e) {
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        final String email1=acct.getEmail();
        final String name1=acct.getDisplayName();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //setData();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference uidRef = rootRef.child("Users").child(uid);
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()) {//TODO: Kullanıcı google hesabı ile ilk defa giriş yapıyorsa database verileri ekleniyor ve bir üye numarası veriliyor.
                                        //create new user
                                        String ad=name1;
                                        String instaUserName="Eklenmedi";
                                        String email=email1;

                                        User2 user1=new User2(
                                                ad,
                                                instaUserName,
                                                email
                                        );
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("Personal Informations").setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //progressBar.setVisibility(View.GONE);

                                                if(task.isSuccessful()){
                                                    updateUI(mAuth.getCurrentUser());
                                                }else{
                                                    mAuth.signOut();
                                                    /*Toast.makeText(LoginActivity.this, "Hata oluştu!", Toast.LENGTH_SHORT).show();*/
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        updateUI(mAuth.getCurrentUser());

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            };
                            uidRef.addListenerForSingleValueEvent(eventListener);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else{
            mAuth.signOut();
        }
    }



}
