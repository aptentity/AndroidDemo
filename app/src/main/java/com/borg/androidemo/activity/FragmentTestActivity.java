package com.borg.androidemo.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.borg.androidemo.R;
import com.borg.androidemo.fragment.DetailsFragment;
import com.borg.androidemo.fragment.TitlesFragment;

public class FragmentTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        TitlesFragment titleFragment = new TitlesFragment();
        DetailsFragment detailsFragment = new DetailsFragment();
        fragmentTransaction.add(R.id.titles, titleFragment);
        fragmentTransaction.add(R.id.details, detailsFragment);
        fragmentTransaction.commit();
    }
}
