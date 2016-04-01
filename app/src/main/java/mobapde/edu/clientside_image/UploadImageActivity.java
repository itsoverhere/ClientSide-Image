package mobapde.edu.clientside_image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadImageActivity extends AppCompatActivity {

    Button buttonUpload;
    ImageView ivImage;
    EditText etTitle;

    final static int SELECT_PICTURE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        buttonUpload = (Button) findViewById(R.id.button_upload);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        etTitle = (EditText) findViewById(R.id.et_title);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , selectedImageUri);
                    ivImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new SendImage().execute(selectedImageUri);
            }
        }
    }


    public class SendImage extends AsyncTask<Uri, Void, String> {

        String etTitleText;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            etTitleText = etTitle.getText().toString();
        }

        @Override
        protected String doInBackground(Uri... params) {
            Uri selectedImageUri = params[0];

            OkHttpClient client = new OkHttpClient.Builder().build();
            MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", etTitleText)
                    .addFormDataPart("file", "imagefilename",
                            RequestBody.create(MEDIA_TYPE_JPEG, byteArray))
                    .build();

            Request request = new Request.Builder()
                    .url(ServerInfo.IP_ADDRESS + ServerInfo.UPLOAD_IMAGE)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("success")){
                Toast.makeText(getBaseContext(), "Image successfully uploaded.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getBaseContext(), "Error in upload.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
