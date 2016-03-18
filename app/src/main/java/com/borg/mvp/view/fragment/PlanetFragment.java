package com.borg.mvp.view.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;

import java.util.Locale;

/**
 * Created by gulliver on 16/1/4.
 */
public class PlanetFragment extends Fragment {
    public static final String ARG_PLANET_NUMBER = "planet_number";
    private static final String TAG = PlanetFragment.class.getSimpleName();
    public PlanetFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogHelper.i(TAG,"onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
        int i;
        if (getArguments()==null){
            i = 0;
        }else {
            i = getArguments().getInt(ARG_PLANET_NUMBER);
        }

        String planet = getResources().getStringArray(R.array.planets_array)[i];

        int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                "drawable", getActivity().getPackageName());
        ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
        getActivity().setTitle(planet);

        return rootView;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogHelper.i(TAG,"onViewCreated");
    }
}
