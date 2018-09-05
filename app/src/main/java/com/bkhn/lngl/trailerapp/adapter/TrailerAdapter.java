package com.bkhn.lngl.trailerapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bkhn.lngl.trailerapp.R;
import com.bkhn.lngl.trailerapp.model.Trailer;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder>{
    private Context context;
    private List<Trailer> trailerList;

    public TrailerAdapter(Context context, List<Trailer> trailerList) {
        this.context = context;
        this.trailerList = trailerList;
    }

    @NonNull
    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trailer,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.MyViewHolder holder, int position) {
        String thumbnail= trailerList.get(position).getKey();
        Glide.with(context).load("http://img.youtube.com/vi/"+thumbnail+"/0.jpg").into(holder.thumbnailYoutube);
      //  Picasso.get().load("http://img.youtube.com/vi/ID/0.jpg"+thumbnail).placeholder(R.drawable.placeholder).into(holder.thumbnailYoutube);
        holder.txtTrailerTitle.setText(trailerList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnailYoutube;
        TextView txtTrailerTitle;
        public MyViewHolder(final View itemView) {
            super(itemView);

            thumbnailYoutube = (ImageView)itemView.findViewById(R.id.thumbnail_youtube);
            txtTrailerTitle =  (TextView)itemView.findViewById(R.id.trailer_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Trailer trailer = trailerList.get(pos);
                        String videoId = trailer.getKey();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+videoId));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("VIDEOID",videoId);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
