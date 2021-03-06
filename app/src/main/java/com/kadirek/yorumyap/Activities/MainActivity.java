package com.kadirek.yorumyap.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kadirek.yorumyap.Adapters.ParseAdapter;
import com.kadirek.yorumyap.Models.ParseItem;
import com.kadirek.yorumyap.R;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";

    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();

    //UI
    private ImageView logo;
    private Button login;
    private Button profile;
    private Button refreshButtonNext;
    private Button refreshButtonBack;
    private TextView rightNowTV;
    private TextView afterNowTV;
    private ConstraintLayout constraintLayout;
    private ConstraintLayout constraintLayoutSecond;
    private ConstraintLayout constraintLayoutCollectOfTwo;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView commentCountTextView;

    //Firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mDb;

    public static int commentCount;
    public static boolean isRefreshClicked = false;
    private Boolean exit = false;
    private String text = "Birazdan başlayacak";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            profile.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        refreshButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//next shows
                whenGoNext();
            }
        });
        refreshButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//current shows and
                whenGoBack();
            }
        });

        loadItems();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();
            }
        });

    }

    private void initUI(){
        logo = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        login = findViewById(R.id.signInButton);
        profile = findViewById(R.id.profileButton);
        refreshButtonNext = findViewById(R.id.refreshButtonNext);
        refreshButtonBack = findViewById(R.id.refreshButtonBack);
        rightNowTV = findViewById(R.id.rightNowtextView);
        afterNowTV = findViewById(R.id.afterNowtextView);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        commentCountTextView = findViewById(R.id.commentCount);
        constraintLayout = findViewById(R.id.linearLayout);
        constraintLayoutSecond = findViewById(R.id.linearLayoutSecond);
        constraintLayoutCollectOfTwo = findViewById(R.id.linearLayoutCollectOfTwo);
        constraintLayoutCollectOfTwo.setBackgroundResource(R.drawable.gradient);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParseAdapter(parseItems, this);
        recyclerView.setAdapter(adapter);
    }

    private void whenGoBack(){
        isRefreshClicked = false;
        refreshButtonNext.setVisibility(View.VISIBLE);
        refreshButtonBack.setVisibility(View.GONE);
        afterNowTV.setVisibility(View.GONE);
        rightNowTV.setVisibility(View.VISIBLE);
        constraintLayoutCollectOfTwo.setBackgroundResource(R.drawable.gradient);
        //swipeRefreshLayout.setBackgroundResource(R.drawable.gradient);
        swipeRefreshLayout.setEnabled(true);
        loadItems();
    }

    private void whenGoNext(){
        isRefreshClicked = true;
        refreshButtonBack.setVisibility(View.VISIBLE);
        refreshButtonNext.setVisibility(View.GONE);
        afterNowTV.setVisibility(View.VISIBLE);
        rightNowTV.setVisibility(View.GONE);
        constraintLayoutCollectOfTwo.setBackgroundResource(R.drawable.gradient_reverse);
        //swipeRefreshLayout.setBackgroundResource(R.drawable.gradient_reverse);
        swipeRefreshLayout.setEnabled(false);
        loadItems();
    }

    @Override
    public void onBackPressed() {
        if(isRefreshClicked){
            whenGoBack();
        }else{
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Çıkmak için Geri tuşuna tekrar basın",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }


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
            progressBar.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
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
                Elements dataTextNext = doc.select("li.liakisgelecek").not(".liakisson");

                int size = data.size();//get all commentCount of elements
                for(int i =0 ; i<size ; i++){

                    String imgUrl = data.select("li.liakiskanal")//todo: image
                            .select("img")
                            .eq(i)
                            .attr("src");

                    String title = dataText.select("li.liakissuanda")//todo:title
                            .eq(i)
                            .text();

                    String titleNext = dataTextNext.select("li.liakisgelecek")//todo:next show's title
                            .eq(i)
                            .text();

                    String time = dataText.select("li.liakissuanda")//todo:show's start time
                            .select("span")
                            .eq(i)
                            .text();

                    String timeNextShow = dataTextNext.select("li.liakisgelecek")//todo:next show's start time
                            .select("span")
                            .eq(i)
                            .text();

                    String duration = dataText.select("li.liakissuanda")
                            .select("strong")
                            .eq(i)
                            .text();
                    duration = duration.replaceAll("\\D+","");//todo: get only digits

                    String[] arr = title.split("\\d+", 2);//todo:parse for the name of the show's not time or duration
                    final String parseTitle = arr[0].trim();

                    String[] arrNext = titleNext.split("\\d+", 2);//todo:parse for the name of the next show's not time or duration
                    final String parseTitleNext = arrNext[0].trim();

                    Calendar now = Calendar.getInstance();
                    int currentHour = now.get(Calendar.HOUR_OF_DAY);
                    int currentMİnute = now.get(Calendar.MINUTE);
                    String currentTime = (currentHour+":"+currentMİnute);//todo:find the current time
                    Log.d(TAG, i+"anlik "+ currentTime);
                    Log.d(TAG, i+"kila "+ timeNextShow);

                    String remainTime = calculateTime(currentTime,timeNextShow);//todo:find the difference between two times
                    Log.d(TAG, i+"sonucolarak "+remainTime);
                    final String imgUrl1=imgUrl;
                    final String duration1=duration;
                    final String time1=time;
                    final String remainTime1 = remainTime;
                    final int n = i;

                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Comment");
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            commentCount = (int)snapshot.child(String.valueOf(n)).getChildrenCount();
                            Log.d(TAG, n+" helin "+commentCount);

                            if(!isRefreshClicked){
                                parseItems.add(new ParseItem(imgUrl1, parseTitle,duration1,commentCount,isRefreshClicked,time1,remainTime1));
                                adapter.notifyDataSetChanged();/** this line has to be here otherwise recylerview items doesnt populate the table */

                            }
                            else{
                                parseItems.add(new ParseItem(imgUrl1, parseTitleNext,duration1,commentCount,isRefreshClicked,time1,remainTime1));
                                adapter.notifyDataSetChanged();/** this line has to be here otherwise recylerview items doesnt populate the table */

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String calculateTime(String start, String end) throws ParseException {

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");

            Date date1 = format.parse(start);

            Date date2 = format.parse(end);

            long difference = date2.getTime() - date1.getTime();

            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(difference);
            Log.d(TAG, "negadar "+minutes);
            if(minutes<0)minutes += 1440;

            return String.valueOf(minutes);


        }



    }


}