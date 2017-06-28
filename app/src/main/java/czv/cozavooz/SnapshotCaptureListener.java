package czv.cozavooz;

import android.graphics.Bitmap;

public interface SnapshotCaptureListener {
    void captureStarted();
    void captureCompleted(Bitmap bitmap);
}