package com.example.makecomment.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makecomment.Adapters.ParseAdapter;
import com.example.makecomment.Models.ParseItem;
import com.example.makecomment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private ProgressBar progressBar;
    Calendar rightNow = Calendar.getInstance();

    //UI
    private Button login;
    private Button profile;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        login = findViewById(R.id.signInButton);
        profile = findViewById(R.id.profileButton);

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

        Content content = new Content();
        content.execute();
    }

    private class Content extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
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

                    String[] arr = title.split("\\d+", 2);//parse for the name of show not time or duration
                    String parseTitle = arr[0].trim();

                    Log.d("items","img" + imgUrl + " . title: "+ parseTitle );
                    parseItems.add(new ParseItem(imgUrl, parseTitle));

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
