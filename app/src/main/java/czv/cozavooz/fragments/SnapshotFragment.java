package czv.cozavooz.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import czv.cozavooz.R;
import czv.cozavooz.SnapshotChangeListener;

public class SnapshotFragment extends BaseFragment implements SnapshotChangeListener {

    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snapshot, container, false);
        imageView = (ImageView) view.findViewById(R.id.snapshot);
        activity.addSnapshotChangeListener(this);
        return view;
    }

    @Override
    public void onChange(final Bitmap bitmap) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
        //imageView.setImageBitmap(bitmap);
    }
}
