package mobapde.edu.clientside_image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadImagesActivity extends AppCompatActivity {

    RecyclerView rvImage;
    Button buttonDownload;
    PostRecyclerViewAdapter postRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_images);

        rvImage = (RecyclerView) findViewById(R.id.rv_images);
        buttonDownload = (Button) findViewById(R.id.button_download);

        postRecyclerViewAdapter = new PostRecyclerViewAdapter(new ArrayList<Post>());
        postRecyclerViewAdapter.setLoadImageListener(new PostRecyclerViewAdapter.LoadImageListener() {
            @Override
            public void loadImage(PostRecyclerViewAdapter.PostViewHolder postViewHolder) {
                new DownloadImageTask().execute(postViewHolder);
            }
        });

        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrievePostsTask().execute();
            }
        });

        rvImage.setAdapter(postRecyclerViewAdapter);
        rvImage.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }

    public class RetrievePostsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient.Builder().build();

            Request request = new Request.Builder()
                    .url(ServerInfo.IP_ADDRESS + ServerInfo.RETRIEVE_POSTS)
                    .build();

            Response response = null;
            String imageString = "";
            try {
                response = client.newCall(request).execute();
                imageString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return imageString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ArrayList<Post> postArrayList = new ArrayList<Post>();

            try {
                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    postArrayList.add(
                            new Post(jsonObject.getString(Post.TITLE),
                                    jsonObject.getString(Post.IMAGE_URL)));
                }
                postRecyclerViewAdapter.setPostArrayList(postArrayList);
                postRecyclerViewAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DownloadImageTask extends AsyncTask<PostRecyclerViewAdapter.PostViewHolder, Void, Bitmap>{

        ImageView imageView;

        @Override
        protected Bitmap doInBackground(PostRecyclerViewAdapter.PostViewHolder... params) {

            imageView = params[0].ivImage;
            String imageUrl = params[0].getPost().getImageUrl();

            String urlString = ServerInfo.IP_ADDRESS + ServerInfo.GET_IMAGE + File.separator + imageUrl;
            OkHttpClient client = new OkHttpClient.Builder().build();

            Request request = new Request.Builder().url(urlString).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();

                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
}
