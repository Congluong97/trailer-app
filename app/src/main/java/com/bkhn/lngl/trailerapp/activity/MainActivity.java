package com.bkhn.lngl.trailerapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bkhn.lngl.trailerapp.BuildConfig;
import com.bkhn.lngl.trailerapp.R;
import com.bkhn.lngl.trailerapp.adapter.MovieAdapter;
import com.bkhn.lngl.trailerapp.adapter.TVAdapter;
import com.bkhn.lngl.trailerapp.api.Client;
import com.bkhn.lngl.trailerapp.api.Service;
import com.bkhn.lngl.trailerapp.model.Movie;
import com.bkhn.lngl.trailerapp.model.MovieResponse;
import com.bkhn.lngl.trailerapp.model.TV;
import com.bkhn.lngl.trailerapp.model.TVResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MovieAdapter adapterPopular, adapterTopRating,adapterUpcoming,adapterNowPlaying;
    private TVAdapter adapterAiringToday,adapterTVOnTheAir,adapterTVPopular,adapterTVTopRate;
    private List<Movie> popularList,topRatingList,upcomingList,nowPlayingList;
    private List<TV> airingToday,tvOnTheAirList,tvPopularList,tvTopRateList;
    private RecyclerView recyclerPopular,recyclerTopRatingList,recyclerUpcoming,recyclerViewNowPlaying;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txtUpcoming,txtPopular,txtTopRate,txtNowPlaying;
    private Intent intentWatchAll;
    private int menu=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);




        initView();
        initListerner();
    }

    private void initView() {

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigationView);
        recyclerPopular = (RecyclerView)findViewById(R.id.recycler_popular);
        recyclerTopRatingList = (RecyclerView)findViewById(R.id.recycler_top_rate);
        recyclerUpcoming = (RecyclerView)findViewById(R.id.recycleview_upcoming);
        recyclerViewNowPlaying = (RecyclerView)findViewById(R.id.recycler_now_playing);
        txtUpcoming = (TextView)findViewById(R.id.txt_watch_all_upcoming);
        txtPopular = (TextView)findViewById(R.id.txt_watch_all_popular);
        txtTopRate = (TextView)findViewById(R.id.txt_watch_all_toprate);
        txtNowPlaying = (TextView)findViewById(R.id.txt_watch_all_now_playing);



        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_content);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                initView();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });
        initPopular();
        initTopRating();
        initUpcoming();
        initNowPlaying();



        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void initNowPlaying(){
        if(menu == 1) {
            nowPlayingList = new ArrayList<>();
            adapterNowPlaying = new MovieAdapter(this, nowPlayingList, R.layout.item_movie);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerViewNowPlaying.setLayoutManager(linearLayoutManager);
            recyclerViewNowPlaying.setAdapter(adapterNowPlaying);
            loadJSONNowPlaying();
        }
        if(menu ==2){
            tvOnTheAirList = new ArrayList<>();
            adapterTVOnTheAir = new TVAdapter(this,tvOnTheAirList,R.layout.item_movie);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerViewNowPlaying.setLayoutManager(linearLayoutManager);
            recyclerViewNowPlaying.setAdapter(adapterTVOnTheAir);
            loadJSONTVOnTheAir();
        }
    }

    private void initPopular(){
        if(menu==1) {
            popularList = new ArrayList<>();
            adapterPopular = new MovieAdapter(this, popularList, R.layout.item_movie);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerPopular.setLayoutManager(linearLayoutManager);
            recyclerPopular.setAdapter(adapterPopular);
            loadJSONPopular();
        }
        if(menu ==2){
            tvPopularList = new ArrayList<>();
            adapterTVPopular = new TVAdapter(this,tvPopularList,R.layout.item_movie);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerPopular.setLayoutManager(linearLayoutManager);
            recyclerPopular.setAdapter(adapterTVPopular);
            loadJSONTVPopular();
        }
    }

    private void initTopRating(){
        if(menu == 1) {
            topRatingList = new ArrayList<>();
            adapterTopRating = new MovieAdapter(this, topRatingList, R.layout.item_movie);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerTopRatingList.setLayoutManager(linearLayoutManager);
            recyclerTopRatingList.setAdapter(adapterTopRating);
            loadJSONTopRating();
        }
        if(menu == 2){
            tvTopRateList = new ArrayList<>();
            adapterTVTopRate = new TVAdapter(this,tvTopRateList,R.layout.item_movie);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerTopRatingList.setLayoutManager(linearLayoutManager);
            recyclerTopRatingList.setAdapter(adapterTVTopRate);
            loadJSONTVTopRate();
        }
    }

    private void initUpcoming(){

        if(menu == 1) {
            upcomingList = new ArrayList<>();
            adapterUpcoming = new MovieAdapter(this, upcomingList, R.layout.item_movie);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerUpcoming.setLayoutManager(linearLayoutManager);
            recyclerUpcoming.setAdapter(adapterUpcoming);
            loadJSONUpComing();
        }
        if(menu == 2){
            airingToday = new ArrayList<>();
            adapterAiringToday = new TVAdapter(this, airingToday, R.layout.item_movie);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerUpcoming.setLayoutManager(linearLayoutManager);
            recyclerUpcoming.setAdapter(adapterAiringToday);
            loadJSONTVAiringToday();
        }
    }

    public Activity getActivity(){
        Context context = this;
        while (context instanceof ContextWrapper){
            if(context instanceof Activity){
                return (Activity)context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return  null;
    }

    private void initListerner() {
        navigationView.setNavigationItemSelectedListener(this);
        txtUpcoming.setOnClickListener(this);
        txtTopRate.setOnClickListener(this);
        txtPopular.setOnClickListener(this);
        txtNowPlaying.setOnClickListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.favorite:
                intentWatchAll = new Intent(MainActivity.this,WatchAllActivity.class);
                intentWatchAll.putExtra("list",4);
                intentWatchAll.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentWatchAll);
                break;
            case R.id.nav__tv:
                menu=2;
                initView();
                break;
            case R.id.nav_movie:
                menu=1;
                initView();
                break;

        }
        drawerLayout.closeDrawers();
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return  true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void loadJSONPopular(){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<MovieResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN,1);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerPopular.setAdapter(new MovieAdapter(getApplicationContext(),movies,R.layout.item_movie));
                    recyclerPopular.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    private void loadJSONTopRating(){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<MovieResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN,1);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerTopRatingList.setAdapter(new MovieAdapter(getApplicationContext(),movies,R.layout.item_movie));
                    recyclerTopRatingList.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

    private void loadJSONUpComing(){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<MovieResponse> call = apiService.getUpComingMovie(BuildConfig.THE_MOVIE_DB_API_TOKEN,1);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerUpcoming.setAdapter(new MovieAdapter(getApplicationContext(),movies,R.layout.item_movie));
                    recyclerUpcoming.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

    private void loadJSONNowPlaying(){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<MovieResponse> call = apiService.getNowPlayingMovie(BuildConfig.THE_MOVIE_DB_API_TOKEN,1);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerViewNowPlaying.setAdapter(new MovieAdapter(getApplicationContext(),movies,R.layout.item_movie));
                    recyclerViewNowPlaying.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

    private void loadJSONTVAiringToday(){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<TVResponse> call = apiService.getTVAiringToday(BuildConfig.THE_MOVIE_DB_API_TOKEN,1);
            call.enqueue(new Callback<TVResponse>() {
                @Override
                public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {
                    List<TV> tvs = response.body().getResults();
                    recyclerUpcoming.setAdapter(new TVAdapter(getApplicationContext(),tvs,R.layout.item_movie));
                    recyclerUpcoming.smoothScrollToPosition(0);

                }

                @Override
                public void onFailure(Call<TVResponse> call, Throwable t) {

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }


    }

    private void loadJSONTVOnTheAir() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<TVResponse> call = apiService.getTVOnTheAir(BuildConfig.THE_MOVIE_DB_API_TOKEN, 1);
            call.enqueue(new Callback<TVResponse>() {
                @Override
                public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {
                    List<TV> tvs = response.body().getResults();
                    recyclerViewNowPlaying.setAdapter(new TVAdapter(getApplicationContext(), tvs, R.layout.item_movie));
                    recyclerViewNowPlaying.smoothScrollToPosition(0);

                }

                @Override
                public void onFailure(Call<TVResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

    private void loadJSONTVPopular() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<TVResponse> call = apiService.getTVPopular(BuildConfig.THE_MOVIE_DB_API_TOKEN, 1);
            call.enqueue(new Callback<TVResponse>() {
                @Override
                public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {
                    List<TV> tvs = response.body().getResults();
                    recyclerPopular.setAdapter(new TVAdapter(getApplicationContext(), tvs, R.layout.item_movie));
                    recyclerPopular.smoothScrollToPosition(0);

                }

                @Override
                public void onFailure(Call<TVResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    private void loadJSONTVTopRate() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<TVResponse> call = apiService.getTVTopRate(BuildConfig.THE_MOVIE_DB_API_TOKEN, 1);
            call.enqueue(new Callback<TVResponse>() {
                @Override
                public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {
                    List<TV> tvs = response.body().getResults();
                    recyclerTopRatingList.setAdapter(new TVAdapter(getApplicationContext(), tvs, R.layout.item_movie));
                    recyclerTopRatingList.smoothScrollToPosition(0);

                }

                @Override
                public void onFailure(Call<TVResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

        @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.txt_watch_all_upcoming:
                intentWatchAll = new Intent(MainActivity.this,WatchAllActivity.class);
                intentWatchAll.putExtra("list",1);
                intentWatchAll.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentWatchAll);
                break;
            case R.id.txt_watch_all_popular:
                intentWatchAll = new Intent(MainActivity.this,WatchAllActivity.class);
                intentWatchAll.putExtra("list",2);
                intentWatchAll.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentWatchAll);
                break;
            case R.id.txt_watch_all_toprate:
                 intentWatchAll = new Intent(MainActivity.this,WatchAllActivity.class);
                intentWatchAll.putExtra("list",3);
                intentWatchAll.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentWatchAll);
                break;
            case R.id.txt_watch_all_now_playing:
                intentWatchAll = new Intent(MainActivity.this,WatchAllActivity.class);
                intentWatchAll.putExtra("list",5);
                intentWatchAll.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentWatchAll);
                break;
        }


    }


}
