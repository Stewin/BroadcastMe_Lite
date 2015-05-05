package ch.hslu.mobpro.projekt.broadcastmelite;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * Created by Stefan on 29.04.2015.
 */
public class SettingsFragment extends Fragment {

    private final String INTERVALL_PREFERENCE = "intervall";
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        spinner.setSelection(preference.getInt(INTERVALL_PREFERENCE, 0));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
                editor = preference.edit();
                editor.putInt(INTERVALL_PREFERENCE, i);
                editor.commit();

                //Restart Service
                getActivity().stopService(new Intent(getActivity(), BackgroundService.class));
                getActivity().startService(new Intent(getActivity(), BackgroundService.class));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return rootView;
    }

}
