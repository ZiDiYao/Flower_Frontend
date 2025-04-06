package com.zidi.flowidentification_demo;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DashboardActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAMERA = 1001;
    private static final int REQUEST_CODE_GALLERY = 1002;
    private Uri photoUri;  // To store the URI of the photo taken by the camera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Button to trigger flower identification
        Button identifyBtn = findViewById(R.id.btn_identify);
        identifyBtn.setOnClickListener(v -> showImagePickDialog());
    }

//     Show a dialog to let the user choose between camera and gallery
    private void showImagePickDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();  // Launch camera
                    } else {
                        openGallery(); // Open gallery
                    }
                })
                .show();
    }

    // Open device camera to take a new photo
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile;
        try {
            // Create a file to store the captured image
            photoFile = createImageFile();
            // Get URI using FileProvider for secure access
            photoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot open camera", Toast.LENGTH_SHORT).show();
        }
    }

    // Open gallery to choose an existing image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    // Create a temporary image file with a timestamp-based name
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir("images");
        return File.createTempFile("IMG_" + timeStamp, ".jpg", storageDir);
    }

    // Handle results from camera or gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        Uri imageUri = null;

        // Get image URI from camera
        if (requestCode == REQUEST_CODE_CAMERA) {
            imageUri = photoUri;
        }
        // Or get image URI from gallery selection
        else if (requestCode == REQUEST_CODE_GALLERY && data != null) {
            imageUri = data.getData();
        }

        // If a valid image is selected
        if (imageUri != null) {
            String mimeType = getContentResolver().getType(imageUri);
            if (mimeType != null &&
                    (mimeType.equals("image/jpeg") || mimeType.equals("image/png"))) {
                // Start PreviewActivity and pass the image URI
                Intent intent = new Intent(this, PreviewActivity.class);
                intent.putExtra("image_uri", imageUri.toString());
                startActivity(intent);
            } else {
                Toast.makeText(this, " Only JPG and PNG are supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show();
        }
    }

}
