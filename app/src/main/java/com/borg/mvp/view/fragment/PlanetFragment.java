package com.borg.mvp.view.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.borg.androidemo.R;

import java.util.Locale;

/**
 * Created by gulliver on 16/1/4.
 */
public class PlanetFragment extends Fragment {
    public static final String ARG_PLANET_NUMBER = "planet_number";

    public PlanetFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
}
