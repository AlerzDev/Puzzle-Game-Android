package com.alexdev.puzzlegame.fragments.home;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alexdev.puzzlegame.R;
import com.alexdev.puzzlegame.adapters.ScoreAdapter;
import com.alexdev.puzzlegame.api.ApiUtils;
import com.alexdev.puzzlegame.api.GameApi;
import com.alexdev.puzzlegame.helpers.GameDbHelper;
import com.alexdev.puzzlegame.manager.SessionManager;
import com.alexdev.puzzlegame.models.GamePost;
import com.alexdev.puzzlegame.models.SaveGameResponse;
import com.alexdev.puzzlegame.utils.Fonts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoresFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private View view;
    private RecyclerView.LayoutManager mLayoutManager;
    private ScoreAdapter mAdapter;
    private GameApi gameApi;
    private List<GamePost> games;
    private Button mGlobalScoreButton;
    private boolean isGlobalScore;
    private GameDbHelper gameDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_scores, container, false);
        gameApi = ApiUtils.getGameAPI();
        gameDbHelper = new GameDbHelper(getContext());
        initViews();
        return  view;
    }

    private void initViews(){
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mGlobalScoreButton = view.findViewById(R.id.globalScoreButton);
        mGlobalScoreButton.setTypeface(Fonts.getInstance().getPoiretOneRegular(getActivity().getApplicationContext()));
        mGlobalScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isGlobalScore)
                {
                    isGlobalScore = true;
                    mGlobalScoreButton.setText("Personal Scores");
                    populatingGlobalScores();
                }else{
                    isGlobalScore = false;
                    mGlobalScoreButton.setText("Global scores");
                    populatingScores();
                }
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);
        if(games == null) games = new ArrayList<>();
        mAdapter = new ScoreAdapter(games);
        mRecyclerView.setAdapter(mAdapter);
        populatingScores();
    }

    private void populatingScores() {
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(SessionManager.REFERENCE, Context.MODE_PRIVATE);
        gameApi.getSaveGames(sharedpreferences.getString("idKey","no user")).enqueue(new Callback<SaveGameResponse>() {
            @Override
            public void onResponse(Call<SaveGameResponse> call, Response<SaveGameResponse> response) {
                if(null != response.body().getGame()) {
                    games = response.body().getGame();
                    Collections.sort(games, new Comparator<GamePost>() {
                        @Override
                        public int compare(GamePost gamePost, GamePost t1) {
                            return gamePost.getTime().compareTo(t1.getTime());
                        }
                    });
                    mAdapter.updateList(games);
                    gameDbHelper.saveGames(games);
                }
            }

            @Override
            public void onFailure(Call<SaveGameResponse> call, Throwable t) {
                Log.d("error list",t.getMessage());
            }
        });
    }

    private void populatingGlobalScores()
    {
        gameApi.getAllGames().enqueue(new Callback<SaveGameResponse>() {
            @Override
            public void onResponse(Call<SaveGameResponse> call, Response<SaveGameResponse> response) {
                if(null != response.body().getGame()) {
                    games = response.body().getGame();
                    Collections.sort(games, new Comparator<GamePost>() {
                        @Override
                        public int compare(GamePost gamePost, GamePost t1) {
                            return gamePost.getTime().compareTo(t1.getTime());
                        }
                    });
                    mAdapter.updateList(games);
                }
            }

            @Override
            public void onFailure(Call<SaveGameResponse> call, Throwable t) {
                Log.d("error list",t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isGlobalScore = false;
        populatingScores();
    }
}
