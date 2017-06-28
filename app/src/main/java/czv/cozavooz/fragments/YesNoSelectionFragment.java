package czv.cozavooz.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import czv.cozavooz.R;
import czv.cozavooz.SelectionListener;

public class YesNoSelectionFragment extends BaseFragment {

    private final List<SelectionListener> yesListeners;
    private final List<SelectionListener> noListeners;

    public YesNoSelectionFragment() {
        yesListeners = new ArrayList<SelectionListener>();
        noListeners = new ArrayList<SelectionListener>();
    }

    public void addOnYesSelectionListener(SelectionListener listener) { yesListeners.add(listener); }
    public void addOnNoSelectionListener(SelectionListener listener) { noListeners.add(listener); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yes_no, container, false);

        view.findViewById(R.id.confirmYes).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                for (SelectionListener l : yesListeners) { l.onSelection(); }
            }
        });
        view.findViewById(R.id.confirmNo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                for (SelectionListener l : noListeners) { l.onSelection(); }
            }
        });

        //// ZABRAĆ TO STĄD!!!!!! - to powinna być ogólna klasa

        addOnNoSelectionListener(new SelectionListener() {
            @Override
            public void onSelection() {
                activity.changeCurrentLayout(R.id.cameraView);
            }
        });

        return view;
    }
}
