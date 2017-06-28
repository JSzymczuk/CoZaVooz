package czv.cozavooz;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import czv.cozavooz.fragments.CameraFragment;

public class MainActivity extends AppCompatActivity {

    private HashMap<Integer,FrameLayout> layouts;
    private FrameLayout currentLayout;

    private static final int DEFAULT_LAYOUT_ID = R.id.cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            layouts = new HashMap<>();
            ArrayList<Integer> ids = new ArrayList<Integer>() {{
                add(R.id.cameraView);
                add(R.id.snapshotView);
            }};

            for (int id : ids) {
                layouts.put(id, (FrameLayout) findViewById(id));
            }

            changeCurrentLayout(DEFAULT_LAYOUT_ID);

        }
        catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public void changeCurrentLayout(int id) {
        if (currentLayout != null) {
            currentLayout.setVisibility(View.GONE);
        }
        currentLayout = layouts.get(id);
        currentLayout.setVisibility(View.VISIBLE);
    }

    private CameraFragment currentCameraFragment;
    private Bitmap currentSnapshot;
    private List<SnapshotChangeListener> snapshotChangeListeners = new ArrayList<>();

    public void addSnapshotChangeListener(SnapshotChangeListener listener) {
        snapshotChangeListeners.add(listener);
    }

    public void setCurrentCameraFragment(CameraFragment cameraFragment) {
        this.currentCameraFragment = cameraFragment;
    }

    public CameraFragment getCurrentCameraFragment() {
        return currentCameraFragment;
    }

    public void setCurrentSnapshot(Bitmap bitmap) {
        currentSnapshot = bitmap;
        for (SnapshotChangeListener listener : snapshotChangeListeners) {
            listener.onChange(bitmap);
        }
    }



}
