package my.project.jansoriproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainTransportation extends Fragment {

    ViewGroup main_fragment_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_fragment_view = (ViewGroup) inflater.inflate(R.layout.fragment_main_transportation, container, false);

        main_fragment_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToAttract(v);
            }
        });

        return main_fragment_view;
    }

    public void goToAttract(View v)
    {
        Intent intent = new Intent(getActivity(), PublicTransportationActivity.class);
        startActivity(intent);
    }
}