package gurpreetsk.me.selfiegeek;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.kinvey.android.Client;

import gurpreetsk.me.selfiegeek.service.UploadService;

import static gurpreetsk.me.selfiegeek.utils.Constants.CAMERA_STILL;
import static gurpreetsk.me.selfiegeek.utils.Constants.MY_PERMISSIONS_REQUEST_ACCESS_CAMERA;
import static gurpreetsk.me.selfiegeek.utils.Constants.SERVICE_EXTRA;
import static gurpreetsk.me.selfiegeek.utils.Constants.SERVICE_FILENAME_EXTRA;
import static gurpreetsk.me.selfiegeek.utils.permissions.askCameraPermission;

public class TakeImageActivity extends AppCompatActivity {

    private static final String TAG = "TakeImageActivity";
    MaterialCamera camera;

    Client mKinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_image);

        camera = new MaterialCamera(this);
        mKinveyClient = new Client.Builder(getApplicationContext()).build();

        int cameraPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED )
                askCameraPermission(this);
        else {
            camera.stillShot().start(CAMERA_STILL);
            TextView tv = (TextView)findViewById(R.id.textview_image_activity);
            tv.setText(getResources().getString(R.string.thanks_for_camera_permission));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Received recording or error from MaterialCamera
        if (requestCode == CAMERA_STILL) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Image upload started", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onActivityResult: " + data.getDataString());
                String name = data.getDataString().substring(71, 90);
                getFileFromCacheAndUpload(name, data.getData());
                finish();
            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    private void getFileFromCacheAndUpload(final String fileName, Uri imagePath) {

        if (imagePath != null) {
            Intent FileUploadIntent = new Intent(TakeImageActivity.this, UploadService.class);
            FileUploadIntent.putExtra(SERVICE_EXTRA, imagePath.toString());
            FileUploadIntent.putExtra(SERVICE_FILENAME_EXTRA, fileName);
            startService(FileUploadIntent);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    camera.stillShot().start(CAMERA_STILL);
                } else {
                    Toast.makeText(this, "Camera access permission needed to take image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
