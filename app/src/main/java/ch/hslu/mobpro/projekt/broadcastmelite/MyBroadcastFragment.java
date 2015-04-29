package ch.hslu.mobpro.projekt.broadcastmelite;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Stefan on 21.04.2015.
 */
public class MyBroadcastFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_mybroadcasts, container, false);

        return rootView;
    }

}
