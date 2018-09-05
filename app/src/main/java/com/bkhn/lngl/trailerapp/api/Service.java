package com.bkhn.lngl.trailerapp.api;

import com.bkhn.lngl.trailerapp.model.MovieResponse;
import com.bkhn.lngl.trailerapp.model.TVResponse;
import com.bkhn.lngl.trailerapp.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey,@Query("page") int pageIndex);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey,@Query("page") int pageIndex);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpComingMovie(@Query("api_key") String apiKey,@Query("page") int pageIndex);
    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovie(@Query("api_key") String apiKey, @Query("page") int pageIndex);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);

    @GET("tv/airing_today")
    Call<TVResponse> getTVAiringToday(@Query("api_key") String apiKey, @Query("page") int pageIndex);

    @GET("tv/on_the_air")
    Call<TVResponse> getTVOnTheAir(@Query("api_key") String apiKey,@Query("page") int pageIndex);

    @GET("tv/popular")
    Call<TVResponse> getTVPopular(@Query("api_key") String apiKey,@Query("page") int pageIndex);

    @GET("tv/top_rated")
    Call<TVResponse> getTVTopRate(@Query("api_key") String apiKey,@Query("page") int pageIndex);

    @GET("tv/{tv_id}/videos")
    Call<TrailerResponse> getTVTrailer (@Path("tv_id") int id, @Query("api_key") String apiKey);
}