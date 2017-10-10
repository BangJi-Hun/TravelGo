package jihun.travelgo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConquestActivity extends Activity {

    private Button btnTakePicture;
    private Button btnPicUp;
    private ImageView ivPicture;
    private String imagePath;
    private Uri imageUri;
    private static String sightsName = null;
    private static int flagCon = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conquest);

        //명소 이름 받기
        final Intent intent = getIntent();
        sightsName = intent.getStringExtra("sightsName");

        //받은 명소 체크
        Toast.makeText(ConquestActivity.this, sightsName, Toast.LENGTH_SHORT).show();

        btnTakePicture = (Button) findViewById(R.id.brn_camera);
        btnPicUp = (Button) findViewById(R.id.btn_picup);
        ivPicture = (ImageView) findViewById(R.id.user_pic);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Camera Application이 있으면
                if (isExistCameraApplication()) {

                    // Camera Application을 실행한다.
                    Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // 찍은 사진을 보관할 파일 객체를 만들어서 보낸다.
                    File picture = savePictureFile();
                    if (picture != null) {
                        cameraApp.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture));
                        startActivityForResult(cameraApp, 10000);
                    }

                } else {
                    Toast.makeText(ConquestActivity.this, "카메라 앱을 설치하세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        btnPicUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flagCon == 1) {
                    Toast.makeText(ConquestActivity.this, sightsName + "를 정복하였습니다! ", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ConquestActivity.this, SightsActivity.class);
                    i.putExtra("conClear", 1);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(ConquestActivity.this, "명소 사진을 다시 찍어주세요! ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 사진찍기 버튼을 누른 후 잘찍고 돌아왔다면
        if (requestCode == 10000 && resultCode == RESULT_OK) try {
            BitmapFactory.Options factory = new BitmapFactory.Options();

            // 비트맵 이미지로 가져온다
            factory.inJustDecodeBounds = false;
            factory.inPurgeable = true;

            Bitmap image = BitmapFactory.decodeFile(imagePath, factory);

            // 이미지를 상황에 맞게 회전시킨다
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            image = rotate(image, exifDegree);

            // 변환된 이미지 사용
            ivPicture.setImageBitmap(image);
            flagCon = 1;
        } catch (Exception e) {
            Toast.makeText(this, "오류발생 : " + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
            flagCon = 0;
        }

    }

    /**
     * EXIF정보를 회전각도로 변환하는 메서드
     *
     * @param exifOrientation EXIF 회전각
     * @return 실제 각도
     */
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap  비트맵 이미지
     * @param degrees 회전 각도
     * @return 회전된 이미지
     */
    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    /**
     * Android에 Camera Application이 설치되어있는지 확인한다.
     *
     * @return 카메라 앱이 있으면 true, 없으면 false
     */
    private boolean isExistCameraApplication() {

        // Android의 모든 Application을 얻어온다.
        PackageManager packageManager = getPackageManager();

        // Camera Application
        Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // MediaStore.ACTION_IMAGE_CAPTURE을 처리할 수 있는 App 정보를 가져온다.
        List cameraApps = packageManager.queryIntentActivities(
                cameraApp, PackageManager.MATCH_DEFAULT_ONLY);

        // 카메라 App이 적어도 한개 이상 있는지 리턴
        return cameraApps.size() > 0;
    }

    /**
     * 카메라에서 찍은 사진을 외부 저장소에 저장한다.
     *
     * @return
     */

    private File savePictureFile() {

        // 외부 저장소 쓰기 권한을 얻어온다.
        PermissionRequester.Builder requester = new PermissionRequester.Builder(this);

        int result = requester.create().request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                20000,
                new PermissionRequester.OnClickDenyButtonListener() {
                    @Override
                    public void onClick(Activity activity) {
                    }
                });

        // 사용자가 권한을 수락한 경우
        if (result == PermissionRequester.ALREADY_GRANTED
                || result == PermissionRequester.REQUEST_PERMISSION) {

            // 사진 파일의 이름을 만든다.
            // Date는 java.util 을 Import 한다.
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "IMG_" + timestamp;

            /**
             * 사진파일이 저장될 장소를 구한다.
             * 외장메모리에서 사진을 저장하는 폴더를 찾아서
             * 그곳에 MYAPP 이라는 폴더를 만든다.
             */
            File pictureStorage = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "Travel Go!/");

            // 만약 장소가 존재하지 않는다면 폴더를 새롭게 만든다.
            if (!pictureStorage.exists()) {

                /**
                 * mkdir은 폴더를 하나만 만들고,
                 * mkdirs는 경로상에 존재하는 모든 폴더를 만들어준다.
                 */
                pictureStorage.mkdirs();
            }

            try {
                File file = File.createTempFile(fileName, ".jpg", pictureStorage);

                // ImageView에 보여주기위해 사진파일의 절대 경로를 얻어온다.
                imagePath = file.getAbsolutePath();

                // 찍힌 사진을 "갤러리" 앱에 추가한다.
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(imagePath);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 사용자가 권한을 거부한 경우
        else {
        }

        return null;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Conquest Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}


