package com.zidi.flowidentification_demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.zidi.flowidentification_demo.Util.PathUtil;
import com.zidi.flowidentification_demo.network.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private static final int REQUEST_PERMISSIONS = 100;

    private ImageView imageView;
    private Button selectImageBtn, takePhotoBtn;

    private Uri imageUri;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imageView = findViewById(R.id.image_preview);
        selectImageBtn = findViewById(R.id.btn_select_image);
        takePhotoBtn = findViewById(R.id.btn_take_photo);

        checkPermissions();

        selectImageBtn.setOnClickListener(v -> pickImageFromGallery());
        takePhotoBtn.setOnClickListener(v -> takePhoto());
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                imageFile = createImageFile();
                imageUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider",
                        imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "flower_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                imageUri = data.getData();
                imageFile = new File(PathUtil.getPath(this, imageUri));
            }
            imageView.setImageURI(imageUri);
            uploadImageToServer();
        }
    }

    private void uploadImageToServer() {
        if (imageFile == null || !imageFile.exists()) {
            Toast.makeText(this, "No valid image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = imageFile.getName().toLowerCase();
        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png"))) {
            Toast.makeText(this, "Only JPG and PNG allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        Log.d("UPLOAD_DEBUG", "uploadImageToServer() 被调用了！");
        RetrofitClient.getInstance().getUploadApi().uploadImage(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("UPLOAD_DEBUG", "Response code: " + response.code());
                Log.d("UPLOAD_DEBUG", "Success: " + response.isSuccessful());
                Toast.makeText(UploadActivity.this,
                        response.isSuccessful() ? "Upload success!" : "Upload failed!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("UPLOAD_DEBUG", "Upload failed: " + t.getMessage());
                Toast.makeText(UploadActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void checkPermissions() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        boolean needRequest = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                needRequest = true;
                break;
            }
        }
        if (needRequest) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        }
    }
}
