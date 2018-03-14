package com.alexdev.puzzlegame.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexdev.puzzlegame.R;
import com.alexdev.puzzlegame.models.GamePost;
import com.alexdev.puzzlegame.utils.Fonts;

import java.util.List;

/**
 * Created by alexdev on 12/03/18.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder>{

    private List<GamePost> games;

    public ScoreAdapter(List<GamePost> g) {
        games = g;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mTextView;
        private TextView mPlaterTextView;
        private ImageView mProfileImage;
        private View view;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.info_text);
            mTextView.setTypeface(Fonts.getInstance().getPoiretOneRegular(v.getContext()));
            mTextView.setTextColor(Color.BLUE);
            mPlaterTextView = v.findViewById(R.id.playerTextView);
            mPlaterTextView.setTextColor(Color.BLUE);
            mPlaterTextView.setTypeface(Fonts.getInstance().getPoiretOneRegular(v.getContext()));
            mProfileImage = v.findViewById(R.id.playerImageView);
            view = v;
        }
    }

    public void updateList(List<GamePost> newList) {
        games.clear();
        games.addAll(newList);
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GamePost game = games.get(position);
        holder.mTextView.setText(game.getTime());
        holder.mPlaterTextView.setText(game.getUsername());
        if(game.getGender().equals("Girl"))
        {
            holder.mProfileImage.setImageDrawable(holder.view.getResources().getDrawable(R.drawable.girl));
        }else{
            holder.mProfileImage.setImageDrawable(holder.view.getResources().getDrawable(R.drawable.man));
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }



}
