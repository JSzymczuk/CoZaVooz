package czv.cozavooz;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import czv.cozavooz.fragments.CameraFragment;
import czv.cozavooz.fragments.YesNoSelectionFragment;

public class MainActivity extends AppCompatActivity implements SnapshotCaptureListener {

    private HashMap<Integer,FrameLayout> layouts;
    private FrameLayout currentLayout;

    private static final int DEFAULT_LAYOUT_ID = R.id.cameraView;

    private CameraFragment cameraFragment;
    private YesNoSelectionFragment yesNoSelectionFragment;

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

            yesNoSelectionFragment.addOnNoSelectionListener(new SelectionListener() {
                @Override
                public void onSelection() {
                    changeCurrentLayout(R.id.cameraView);
                    setCurrentSnapshot(null);
                }
            });

            yesNoSelectionFragment.addOnYesSelectionListener(new SelectionListener() {
                @Override
                public void onSelection() {
                    if (currentSnapshot != null) {
                        send(currentSnapshot);
                    }
                }
            });

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


    public void setCameraFragment(CameraFragment fragment) { cameraFragment = fragment; }
    public CameraFragment getCameraFragment() { return cameraFragment; }
    public void setYesNoSelectionFragment(YesNoSelectionFragment fragment) { yesNoSelectionFragment = fragment; }



    private static String getCurrentDateString() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private JSONObject getResponseJSONObject(String str) {
        try {
            return (JSONObject)(new JSONParser().parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void send(final Bitmap image) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //File file = new File(fpath);
                    //byte[] file = getImageBytes(fpath);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] file = stream.toByteArray();

                    if(file != null && file.length > 0) { //.exists() && !file.isDirectory()) {
                /*MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody("picture", file,
                    ContentType.create("application/x-www-form-urlencoded"), getCurrentDateString() + ".jpg");*/

                        //JSONObject json = new JSONObject();
                        //json.put("array", file);

                        HttpPost httppost = new HttpPost("http://smcars.pythonanywhere.com/compute");
                        //httppost.setEntity(builder.build());
                        //httppost.setEntity(new ByteArrayEntity(file));
                        //StringEntity se = new StringEntity("Zażółć gęślą jaźń, mordo!.", "UTF-8");
                        //se.setContentType("application/json");
                        //se.setContentType("text/plain");

                        ByteArrayEntity bae = new ByteArrayEntity(file);
                        bae.setContentEncoding("application/octet-stream");
                        httppost.setEntity(bae);

                        //CloseableHttpClient client = HttpClients.createDefault();
                        //CloseableHttpResponse response =  client.execute(httppost);

                        ResponseHandler handler = new ResponseHandler() {
                            @Override
                            public Object handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                                HttpEntity responseEntity = httpResponse.getEntity();

                                BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = br.readLine()) != null) {
                                    sb.append(line);
                                }

                                return getResponseJSONObject(sb.toString());
                            }
                        };

                        DefaultHttpClient client = new DefaultHttpClient();
                        client.execute(httppost, handler);

                        //HttpEntity responseEntity = response.getEntity();


                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                //return null;
            }
        }).start();
    }

    public static void feedback(JSONObject json, int status) {
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("id", (String) json.get("id"), ContentType.TEXT_PLAIN);
            builder.addTextBody("manufacturer", (String) json.get("manufacturerId"), ContentType.TEXT_PLAIN);
            builder.addTextBody("status", Integer.toString(status), ContentType.TEXT_PLAIN);

            HttpPost httppost = new HttpPost("http://smcars.pythonanywhere.com/inquiries/edit");
            httppost.setEntity(builder.build());
            HttpClients.createDefault().execute(httppost);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLayout(int id) { findViewById(id).setVisibility(View.VISIBLE); }
    public void hideLayout(int id) { findViewById(id).setVisibility(View.GONE); }

    @Override
    public void captureStarted() {
        //hideLayout(R.id.takePictureFragment);
        this.runOnUiThread(new Runnable() {
            @Override public void run() { hideLayout(R.id.cameraView); }
        });
    }

    @Override
    public void captureCompleted(final Bitmap bitmap) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setCurrentSnapshot(bitmap);
                //showLayout(R.id.snapshotView);
                changeCurrentLayout(R.id.snapshotView);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == CameraFragment.REQUEST_GALLERY_ID) {
                Uri selectedImageUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImageUri,filePathColumn,null,null,null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String selectedImagePath = cursor.getString(columnIndex);
                cursor.close();

                BitmapFactory.Options opts = new BitmapFactory.Options();

                Bitmap selectedImageBitmap = BitmapFactory.decodeFile(selectedImagePath,opts);

                captureCompleted(selectedImageBitmap);

                setCurrentSnapshot(selectedImageBitmap);
                changeCurrentLayout(R.id.snapshotView);
            }
        }
    }

    private Bitmap currentSnapshot;
    private List<SnapshotChangeListener> snapshotChangeListeners = new ArrayList();

    public void addSnapshotChangeListener(SnapshotChangeListener listener) {
        snapshotChangeListeners.add(listener);
    }

    public void setCurrentSnapshot(Bitmap bitmap) {
        currentSnapshot = bitmap;
        for (SnapshotChangeListener listener : snapshotChangeListeners) {
            listener.onChange(bitmap);
        }
    }
}
