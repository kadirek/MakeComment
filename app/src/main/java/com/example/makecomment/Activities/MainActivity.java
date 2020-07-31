package com.example.makecomment.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.makecomment.Adapters.ParseAdapter;
import com.example.makecomment.Models.ParseItem;
import com.example.makecomment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";


    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ArrayList<String> parseItemsForComment = new ArrayList<String>();
    private ProgressBar progressBar;
    Calendar rightNow = Calendar.getInstance();

    //UI
    private ImageView logo;
    private Button login;
    private Button profile;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView commentCountTextView;
    private String number;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mDb;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        login = findViewById(R.id.signInButton);
        profile = findViewById(R.id.profileButton);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        commentCountTextView = findViewById(R.id.commentCount);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            profile.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParseAdapter(parseItems, this);
        recyclerView.setAdapter(adapter);

        loadItems();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();
            }
        });

    }

    private void loadItems() {
        Content content = new Content();
        content.execute();
    }

    private class Content extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,android.R.anim.fade_in));
            parseItems.clear();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                String url ="https://www.canlitv.vin/yayin-akisi";
                Document doc = Jsoup.connect(url).get();
                Elements data = doc.select("li.liakiskanal");
                Elements dataText = doc.select("li.liakissuanda");

                int size = data.size();//get all count of elements
                for(int i =0 ; i<size ; i++){

                    String imgUrl = data.select("li.liakiskanal")//todo: image
                            .select("img")
                            .eq(i)
                            .attr("src");

                    String title = dataText.select("li.liakissuanda")//todo:title
                            .eq(i)
                            .text();

                    String time = dataText.select("li.liakissuanda")//todo:title
                            .select("span")
                            .eq(i)
                            .text();

                    String duration = dataText.select("li.liakissuanda")
                            .select("strong")
                            .eq(i)
                            .text();
                    duration = duration.replaceAll("\\D+","");//todo: get only digits

                    String[] arr = title.split("\\d+", 2);//parse for the name of show not time or duration
                    final String parseTitle = arr[0].trim();
                    final int n = i;

                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Comment");
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            number = String.valueOf(snapshot.child(String.valueOf(n)).getChildrenCount());
                    /*        databaseReference = mDb.getReference("Kadir").child(String.valueOf(n)).push();

                            databaseReference.setValue(number).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "Sayı eklendi", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Hata var", Toast.LENGTH_SHORT).show();
                                }
                            });*/
                            parseItemsForComment.add(n,number);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if(parseItemsForComment!= null && parseItemsForComment.size() !=0) {
                        Log.d(TAG, n+" gadir "+parseItemsForComment.get(n));
                        number = parseItemsForComment.get(n);
                    }

                    parseItems.add(new ParseItem(imgUrl, parseTitle,duration,number,time));

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }


}