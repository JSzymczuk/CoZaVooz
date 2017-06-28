package czv.cozavooz.fragments;

import android.app.Fragment;
import android.content.Context;

import czv.cozavooz.MainActivity;

abstract class BaseFragment extends Fragment {

    protected MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }
}