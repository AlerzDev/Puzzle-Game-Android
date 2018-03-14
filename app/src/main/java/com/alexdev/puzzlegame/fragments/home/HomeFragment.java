package com.alexdev.puzzlegame.fragments.home;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexdev.puzzlegame.R;
import com.alexdev.puzzlegame.api.ApiUtils;
import com.alexdev.puzzlegame.api.GameApi;
import com.alexdev.puzzlegame.manager.NavigatorManager;
import com.alexdev.puzzlegame.manager.SessionManager;
import com.alexdev.puzzlegame.models.GamePost;
import com.alexdev.puzzlegame.models.SaveGameResponse;
import com.alexdev.puzzlegame.utils.Fonts;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView mUsernameText;
    private Button mGameButton;
    private View view;
    private ImageView mProfileImage;
    private GameApi gameApi;
    private List<GamePost> games;
    private TextView mLowScore;
    private TextView mHighScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        gameApi = ApiUtils.getGameAPI();
        initViews();
        initEventViews();
        loadInformation();
        return view;
    }

    private void initViews() {
        mUsernameText = view.findViewById(R.id.usernameTextView);
        mGameButton = view.findViewById(R.id.gameButton);
        mProfileImage = view.findViewById(R.id.imageView);
        mHighScore = view.findViewById(R.id.textView3);
        mLowScore  = view.findViewById(R.id.textView);
        mUsernameText.setTypeface(Fonts.getInstance().getPoiretOneRegular(getActivity().getApplicationContext()));
        mHighScore.setTypeface(Fonts.getInstance().getPoiretOneRegular(getActivity().getApplicationContext()));
        mLowScore.setTypeface(Fonts.getInstance().getPoiretOneRegular(getActivity().getApplicationContext()));
        mGameButton.setTypeface(Fonts.getInstance().getPoiretOneRegular(getActivity().getApplicationContext()));
    }

    private void initEventViews(){
        mGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigatorManager.getInstance().goGameActivity((Activity) view.getContext());
            }
        });
    }

    private void loadInformation() {
        SharedPreferences sharedpreferences = this.getActivity().getSharedPreferences(SessionManager.REFERENCE,Context.MODE_PRIVATE);
        mUsernameText.setText(sharedpreferences.getString("nameKey","username"));
        String gender = sharedpreferences.getString("genderKey","username");
        if(gender.equals("Girl"))
        {
            mProfileImage.setImageDrawable(view.getResources().getDrawable(R.drawable.girl));
        }else{
            mProfileImage.setImageDrawable(view.getResources().getDrawable(R.drawable.man));
        }
        gameApi.getSaveGames(sharedpreferences.getString("idKey","no user")).enqueue(new Callback<SaveGameResponse>() {
            @Override
            public void onResponse(Call<SaveGameResponse> call, Response<SaveGameResponse> response) {
                if(null != response.body().getGame() && response.body().getGame().size() > 0) {
                    games = response.body().getGame();
                    Collections.sort(games, new Comparator<GamePost>() {
                        @Override
                        public int compare(GamePost gamePost, GamePost t1) {
                            return gamePost.getTime().compareTo(t1.getTime());
                        }
                    });
                    mHighScore.setText("Best time:"+games.get(0).getTime());
                    mLowScore.setText("Bad Time:"+games.get(games.size()-1).getTime());
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
        loadInformation();
    }
}
