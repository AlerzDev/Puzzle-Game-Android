package com.alexdev.puzzlegame.fragments.home;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.alexdev.puzzlegame.R;
import com.alexdev.puzzlegame.manager.NavigatorManager;
import com.alexdev.puzzlegame.manager.SessionManager;
import com.alexdev.puzzlegame.utils.Fonts;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private View view;
    private Button mLogoutButton;
    private ImageView mProfileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews();
        initEventsViews();
        loadInformation();
        return view;
    }
    private void initViews(){
        mLogoutButton = view.findViewById(R.id.logoutButton);
        mLogoutButton.setTypeface(Fonts.getInstance().getPoiretOneRegular(getActivity().getApplicationContext()));
        mProfileImage = view.findViewById(R.id.imageView3);
    }
    private void initEventsViews(){
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }
    private void logout()
    {
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(SessionManager.REFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        NavigatorManager.getInstance().goSplashScreenActivity(getActivity());
    }
    private void loadInformation(){
        SharedPreferences sharedpreferences = this.getActivity().getSharedPreferences(SessionManager.REFERENCE,Context.MODE_PRIVATE);
        String gender = sharedpreferences.getString("genderKey","username");
        if(gender.equals("Girl"))
        {
            mProfileImage.setImageDrawable(view.getResources().getDrawable(R.drawable.girl));
        }else{
            mProfileImage.setImageDrawable(view.getResources().getDrawable(R.drawable.man));
        }
    }
}
