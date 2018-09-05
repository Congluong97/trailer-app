package com.bkhn.lngl.trailerapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bkhn.lngl.trailerapp.BuildConfig;
import com.bkhn.lngl.trailerapp.R;
import com.bkhn.lngl.trailerapp.adapter.TrailerAdapter;
import com.bkhn.lngl.trailerapp.api.Client;
import com.bkhn.lngl.trailerapp.api.Service;
import com.bkhn.lngl.trailerapp.data.FavoriteContract;
import com.bkhn.lngl.trailerapp.data.FavoriteDbHelper;
import com.bkhn.lngl.trailerapp.model.Movie;
import com.bkhn.lngl.trailerapp.model.TV;
import com.bkhn.lngl.trailerapp.model.Trailer;
import com.bkhn.lngl.trailerapp.model.TrailerResponse;
import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity{

    private ImageView thumbnail,backdrop;
    private TextView nameOfMovie,userrating,releaseDate,overview;
    private RecyclerView recyclerViewTrailer;
    private String strNameOfMovie,strUserrating,strReleaseDate,strOverView,strThumbnail,strBackdropPath;
    private Movie movie;
    private TV tv;
    private int movie_id;

    private FavoriteDbHelper favoriteDbHelper;
    private Movie favoriteMovie;
    private TV favoriteTV;
    private final AppCompatActivity activity = DetailActivity.this;
    private SQLiteDatabase mDb;

    private TrailerAdapter adapter;
    private List<Trailer> trailerList;
    private String tyle;

    private MaterialFavoriteButton materialFavoriteButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detail);

            Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_detail);
            setSupportActionBar(toolbar);
            FavoriteDbHelper dbHelper = new FavoriteDbHelper(this);
            mDb = dbHelper.getWritableDatabase();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            initCollapsing();
            initView();
            getDataMovie();

            //checkFavorite();
            initRecycleTrailer();
        }

        private void checkFavorite(){
            if (Exists(movie.getOriginalTitle(),FavoriteContract.FavoriteEntry.TABLE_NAME_MOVIE)|| Exists(tv.getOriginalTitle(), FavoriteContract.FavoriteEntry.TABLE_NAME_TV) ){
                materialFavoriteButton.setFavorite(true);
                materialFavoriteButton.setOnFavoriteChangeListener(
                        new MaterialFavoriteButton.OnFavoriteChangeListener() {
                            @Override
                            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                                if (favorite == true) {
                                    saveFavorite();
                                    Snackbar.make(buttonView, "Added to Favorite",
                                            Snackbar.LENGTH_SHORT).show();
                                } else {
                                    favoriteDbHelper = new FavoriteDbHelper(DetailActivity.this);
                                    if(tyle.equals("movie")) {
                                        favoriteDbHelper.deleteFavoriteMovie(movie_id);
                                    }
                                    else {
                                        favoriteDbHelper.deleteFavoriteTV(movie_id);
                                    }
                                    Snackbar.make(buttonView, "Removed from Favorite",
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });


            }else {
                materialFavoriteButton.setOnFavoriteChangeListener(
                        new MaterialFavoriteButton.OnFavoriteChangeListener() {
                            @Override
                            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                                if (favorite == true) {
                                    saveFavorite();
                                    Snackbar.make(buttonView, "Added to Favorite",
                                            Snackbar.LENGTH_SHORT).show();
                                } else {
                                    int movie_id = getIntent().getExtras().getInt("id");
                                    favoriteDbHelper = new FavoriteDbHelper(DetailActivity.this);
                                    if(tyle.equals("movie")) {
                                        favoriteDbHelper.deleteFavoriteMovie(movie_id);
                                    }
                                    else {
                                        favoriteDbHelper.deleteFavoriteTV(movie_id);
                                    }
                                    Snackbar.make(buttonView, "Removed from Favorite",
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });


            }

        }
        public void saveFavorite(){

        favoriteDbHelper = new FavoriteDbHelper(activity);

            if(tyle.equals("movie")) {
                favoriteMovie = new Movie();
                Double rate = movie.getVoteAverage();
                favoriteMovie.setId(movie_id);
                favoriteMovie.setOriginalTitle(movie.getOriginalTitle());
                favoriteMovie.setPosterPath(movie.getPosterPath());
                favoriteMovie.setVoteAverage(rate);
                favoriteMovie.setOverview(movie.getOverview());
                favoriteDbHelper.addFavoriteMovie(favoriteMovie);
            }
            else if(tyle.equals("tv")){
                    favoriteTV = new TV();
                    Double rate = tv.getVoteAverage();
                    favoriteTV.setId(movie_id);
                    favoriteTV.setOriginalTitle(tv.getOriginalTitle());
                    favoriteTV.setPosterPath(tv.getPosterPath());
                    favoriteTV.setVoteAverage(rate);
                    favoriteTV.setOverview(tv.getOverview());
                    favoriteDbHelper.addFavoriteTV(favoriteTV);
            }
        }




        private void initView() {
            thumbnail = (ImageView)findViewById(R.id.thumbnail);
            backdrop = (ImageView)findViewById(R.id.backdrop_path);

            nameOfMovie = (TextView)findViewById(R.id.movie_title_detail);
            userrating = (TextView)findViewById(R.id.userrating_detail);
            releaseDate = (TextView)findViewById(R.id.releasedate_detail);
            overview = (TextView)findViewById(R.id.overview_detail);

             materialFavoriteButton = (MaterialFavoriteButton)findViewById(R.id.material_favorite);

        }
        public boolean Exists(String searchItem,String tableName) {

        String[] projection = {
                FavoriteContract.FavoriteEntry._ID,
                FavoriteContract.FavoriteEntry.COLUMN_MOVIEID,
                FavoriteContract.FavoriteEntry.COLUMN_TITLE,
                FavoriteContract.FavoriteEntry.COLUMN_USERRATING,
                FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH,
                FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS

        };
        String selection = FavoriteContract.FavoriteEntry.COLUMN_TITLE + " =?";
        String[] selectionArgs = { searchItem };
        String limit = "1";

        Cursor cursor = mDb.query( tableName, projection, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

        private void initRecycleTrailer(){
            trailerList = new ArrayList<>();
            adapter = new TrailerAdapter(this, trailerList);

            recyclerViewTrailer = (RecyclerView) findViewById(R.id.recycleview_trailer);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerViewTrailer.setLayoutManager(mLayoutManager);
            recyclerViewTrailer.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            LoadJSONTrailer();
        }

        private void getDataMovie(){
            Intent intent = getIntent();
            if(intent.hasExtra("movie")){
                movie = (Movie) getIntent().getSerializableExtra("movie");
                tyle = "movie";

                strNameOfMovie = movie.getOriginalTitle();
                strUserrating = Double.toString(movie.getVoteAverage());
                strOverView = movie.getOverview();
                strReleaseDate = movie.getReleaseDate();
                strThumbnail = "https://image.tmdb.org/t/p/w500"+movie.getPosterPath();
                strBackdropPath = "https://image.tmdb.org/t/p/w500"+movie.getBackdropPath();
                movie_id = movie.getId();

//                Picasso.get().load(strBackdropPath).placeholder(R.drawable.placeholder).into(backdrop);
//                Picasso.get().load(strThumbnail).placeholder(R.drawable.placeholder).into(thumbnail);
                Glide.with(getApplicationContext()).load(strBackdropPath).into(backdrop);
                Glide.with(this).load(strThumbnail).into(thumbnail);
                nameOfMovie.setText(strNameOfMovie);
                userrating.setText(strUserrating);
                releaseDate.setText("Release Date :"+strReleaseDate);
                overview.setText("Overview:"+strOverView);

            }
            if(intent.hasExtra("tv")){
                tv = (TV) getIntent().getSerializableExtra("tv");
                tyle ="tv";
                strNameOfMovie = tv.getOriginalTitle();
                strUserrating = Double.toString(tv.getVoteAverage());
                strOverView = tv.getOverview();
                strReleaseDate = tv.getFirstAirDate();
                strThumbnail = "https://image.tmdb.org/t/p/w500"+tv.getPosterPath();
                strBackdropPath = "https://image.tmdb.org/t/p/w500"+tv.getBackdropPath();
                movie_id = tv.getId();

//                Picasso.get().load(strBackdropPath).placeholder(R.drawable.placeholder).into(backdrop);
//                Picasso.get().load(strThumbnail).placeholder(R.drawable.placeholder).into(thumbnail);
                Glide.with(getApplicationContext()).load(strBackdropPath).into(backdrop);
                Glide.with(this).load(strThumbnail).into(thumbnail);
                nameOfMovie.setText(strNameOfMovie);
                userrating.setText(strUserrating);
                releaseDate.setText(" Firt Air Day :"+strReleaseDate);
                overview.setText("Overview:"+strOverView);

            }
        }

        private void initCollapsing(){
            final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapSing_detail);
            collapsingToolbarLayout.setTitle(" ");
            AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar_detail);
            appBarLayout.setExpanded(true);


            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scroll = -1;
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(scroll == -1){
                        scroll = appBarLayout.getTotalScrollRange();
                    }
                    if(scroll+verticalOffset ==0){
                        collapsingToolbarLayout.setTitle(strNameOfMovie);
                        isShow=false;

                    }else if(isShow){
                        collapsingToolbarLayout.setTitle("");
                        isShow=false;
                    }
                }
            });
        }

        private void LoadJSONTrailer(){
            try{
                if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please obtain your API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                    return;
                }
                Client Client = new Client();
                Service apiService = Client.getRetrofit().create(Service.class);
                Call<TrailerResponse> call;
                if(tyle.equals("movie")) {
                    call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
                }
                else {
                    call = apiService.getTVTrailer(movie_id,BuildConfig.THE_MOVIE_DB_API_TOKEN);
                }
                call.enqueue(new Callback<TrailerResponse>() {
                    @Override
                    public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                        List<Trailer> trailer = response.body().getResults();
                        recyclerViewTrailer.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                        recyclerViewTrailer.smoothScrollToPosition(0);
                    }

                    @Override
                    public void onFailure(Call<TrailerResponse> call, Throwable t) {

                    }
                });

            }catch (Exception e){
                Log.d("Error", e.getMessage());
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }


}
