package pe.mobytes.examplemvvm1.ui.user;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pe.mobytes.examplemvvm1.R;
import pe.mobytes.examplemvvm1.di.Injectable;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements Injectable {


    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

}
