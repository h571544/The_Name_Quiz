package com.example.namequizapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.namequizapp.models.Person;
import com.example.namequizapp.R;
import com.example.namequizapp.utils.SharedData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class AddImageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_RESULT = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Button addImagePerson;
    private ImageButton upload, camera;
    private Bitmap bitmap;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        addImagePerson = findViewById(R.id.add_PersonName);
        upload = findViewById(R.id.upload_button);
        camera = findViewById(R.id.camera_button);

        addImagePerson.setOnClickListener(this);
    }

    /**
     * Uses a intent: Use MediaStore Content Provider and fetches the gallery content
     *
     * @param v
     */
    public void uploadImage(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_RESULT);
    }

    /**
     * Uses a intent: Use MediaStore Content Provider and fethes the camera
     *
     * @param v
     */
    public void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView imageToUpload = findViewById(R.id.imageView_add);

        if (requestCode == RESULT_LOAD_RESULT && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageToUpload.setImageBitmap(bitmap);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imageToUpload.setImageBitmap(bitmap);
            String title = "FreddaTEST";
            String description = "Teste";
            try {
                //Implement Runnable for getContentResolver()?
                String fileURL = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, title, description);
                Toast.makeText(this, fileURL, Toast.LENGTH_SHORT).show();
                if (fileURL == null) {
                    Log.d("Still", "Image insert failed.");
                    return;
                } else {
                    Uri picUri = Uri.parse(fileURL);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, picUri));
                }
            } catch (Exception e) {
                Log.e("Still", "Error writing file", e);
            }

        }
    }

    public byte[] convertToByteArray(Bitmap bitmap) {

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
        byte[] bitmapdata = blob.toByteArray();

        return bitmapdata;
    }

    @Override
    public void onClick(View v) {
        TextView errorText = findViewById(R.id.error_string);
        EditText editText = findViewById(R.id.name_edit);
        ImageView imageView = findViewById(R.id.imageView_add);
        SharedData app = (SharedData) getApplication();
        name = editText.getText().toString();
        byte[] image = convertToByteArray(bitmap);

        Person person = new Person(0, image, name);

        editText.setText("");
        errorText.setText("");
        imageView.setImageBitmap(null);

        //Log.i("Name: ", "" + p.getName());
        //Log.i("Bitmap: ", "" + p.getBitmap());
        if ((bitmap == null) || name.isEmpty()) {
            if (name.isEmpty()) {
                editText.setError("You must type in a name!");
            } else {
                errorText.setText("You must choose a picture.");
            }

        } else {
            errorText.setText("");
            //app.sharedData.add(p);
            Toast.makeText(this, "The person has been added to the database, check it out!", Toast.LENGTH_SHORT).show();
            MainActivity.quizRoomDatabase.personDAO().insertPerson(person);
            System.out.println("Added to database");

        }

    }

}
