package com.bkhn.lngl.trailerapp.adapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bkhn.lngl.trailerapp.activity.DetailActivity;
import com.bkhn.lngl.trailerapp.R;
import com.bkhn.lngl.trailerapp.model.Movie;
import com.bumptech.glide.Glide;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private int resource;

    public MovieAdapter(Context context, List<Movie> movieList,int resource) {
        this.context = context;
        this.movieList = movieList;
        this.resource = resource;
    }

    @NonNull
    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(resource,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MyViewHolder holder, int position) {

        holder.title.setText(movieList.get(position).getOriginalTitle());
        holder.userrating.setText(Double.toString(movieList.get(position).getVoteAverage()));



        String poster = "https://image.tmdb.org/t/p/w500" + movieList.get(position).getPosterPath();
        Glide.with(context).load(poster).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title,userrating;
        ImageView thumbnail;
        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.title);
            userrating = (TextView)itemView.findViewById(R.id.userrating);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        Intent intent = new Intent(v.getContext(), DetailActivity.class);
                        intent.putExtra("movie",movieList.get(pos));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}

