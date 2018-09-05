package com.bkhn.lngl.trailerapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bkhn.lngl.trailerapp.BuildConfig;
import com.bkhn.lngl.trailerapp.R;
import com.bkhn.lngl.trailerapp.adapter.MovieAdapter;
import com.bkhn.lngl.trailerapp.api.Client;
import com.bkhn.lngl.trailerapp.api.Service;
import com.bkhn.lngl.trailerapp.data.FavoriteDbHelper;
import com.bkhn.lngl.trailerapp.model.Movie;
import com.bkhn.lngl.trailerapp.model.MovieResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WatchAllActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private int str;
    private Button btnpage1,btnpage2,btnpage3,btnpage4;

    private FavoriteDbHelper favoriteDbHelper;
    private AppCompatActivity activity = WatchAllActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_all);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        favoriteDbHelper = new FavoriteDbHelper(activity);
        str = getIntent().getIntExtra("list",0);
        initView();

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

    private void initView() {
        btnpage1 = (Button)findViewById(R.id.page_1);
        btnpage2 = (Button)findViewById(R.id.page_2);
        btnpage3 = (Button)findViewById(R.id.page_3);
        btnpage4 = (Button)findViewById(R.id.page_4);
        btnpage1.setOnClickListener(this);
        btnpage2.setOnClickListener(this);
        btnpage3.setOnClickListener(this);
        btnpage4.setOnClickListener(this);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_watch_all);
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this,movieList,R.layout.movie_card);
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        }else {
            recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        movieAdapter.notifyDataSetChanged();

        loadListMovie(1);

    }

    private void loadListMovie(int page) {
        if(str == 1){
            LoadJSONUpcoming(page);
            recyclerView.scrollToPosition(0);

        }
        if(str == 2){
            LoadJSONPopular(page);
            recyclerView.scrollToPosition(0);
        }
        if(str == 3) {
            LoadJSONTopRate(page);
            recyclerView.scrollToPosition(0);

        }
        if(str == 4){
            initViews2();
            recyclerView.scrollToPosition(0);
        }
        if(str==5){
            LoadJSONNowPlaying(page);
            recyclerView.scrollToPosition(0);
        }
        else if(str == 0) return;
    }
    private void initViews2(){
        recyclerView = (RecyclerView) findViewById(R.id.recycle_watch_all);

        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList,R.layout.movie_card);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
        favoriteDbHelper = new FavoriteDbHelper(activity);

        getAllFavorite();
    }

    private void getAllFavorite(){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params){
                movieList.clear();
                movieList.addAll(favoriteDbHelper.getAllFavoriteMovie());
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                movieAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private void LoadJSONPopular(int page){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<MovieResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN,page);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(WatchAllActivity.this,movies,R.layout.movie_card));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(WatchAllActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(WatchAllActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    private void LoadJSONTopRate(int page){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<MovieResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN,page);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(WatchAllActivity.this,movies,R.layout.movie_card));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Toast.makeText(WatchAllActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

    private void LoadJSONUpcoming(int page){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<MovieResponse> call = apiService.getUpComingMovie(BuildConfig.THE_MOVIE_DB_API_TOKEN,page);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(WatchAllActivity.this,movies,R.layout.movie_card));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(WatchAllActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }


    private void LoadJSONNowPlaying(int page){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "NO API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getRetrofit().create(Service.class);
            Call<MovieResponse> call = apiService.getNowPlayingMovie(BuildConfig.THE_MOVIE_DB_API_TOKEN,page);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(WatchAllActivity.this,movies,R.layout.movie_card));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(WatchAllActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(WatchAllActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.page_1:
                loadListMovie(1);
                break;
            case R.id.page_2:
                loadListMovie(2);
                break;
            case R.id.page_3:
                loadListMovie(3);

                break;
            case R.id.page_4:
                loadListMovie(4);
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(movieList.isEmpty()){
            loadListMovie(1);
        }
        else {
            loadListMovie(1);
        }
    }
}
