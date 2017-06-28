package czv.cozavooz.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import czv.cozavooz.MainActivity;
import czv.cozavooz.R;

public class TakePictureFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_picture, container, false);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            activity.getCurrentCameraFragment().takePicture();
            activity.changeCurrentLayout(R.id.snapshotView);
            }
        });
        return view;
    }
}
