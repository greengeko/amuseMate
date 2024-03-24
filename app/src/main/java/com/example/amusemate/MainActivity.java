package com.example.amusemate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button buttonCenter;
    private TextView textArticleContent;
    private ImageView imageArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCenter = findViewById(R.id.buttonCenter);
        textArticleContent = findViewById(R.id.textArticleContent);
        imageArticle = findViewById(R.id.imageArticle);

        // Hide the image initially
        imageArticle.setVisibility(View.GONE);

        buttonCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch the recommendation when the button is clicked
                new FetchRecommendationTask().execute();
            }
        });
    }

    private class FetchRecommendationTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String recommendation = "";

            // URL of the Flask server endpoint to fetch recommendation
            String urlString = "http://10.0.2.2:5000/";

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    recommendation = stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace(); // Print the stack trace of the exception
            }

            return recommendation;
        }

        @Override
        protected void onPostExecute(String recommendation) {
            try {
                JSONObject jsonResult = new JSONObject(recommendation);
                JSONObject activity = jsonResult.getJSONObject("activity");
                JSONArray columnNames = activity.getJSONArray("column_names");
                JSONArray data = activity.getJSONArray("data");

                // Construct the article content
                StringBuilder articleContent = new StringBuilder();
                for (int i = 0; i < data.length(); i++) {
                    JSONArray rowData = data.getJSONArray(i);
                    for (int j = 0; j < columnNames.length(); j++) {
                        articleContent.append(columnNames.getString(j)).append(": ");
                        articleContent.append(rowData.getString(j)).append("\n");
                    }
                    articleContent.append("\n");
                }

                // Update UI with the article content
                textArticleContent.setText(articleContent.toString());

                // Set a placeholder image (you can replace this with your own image)
                imageArticle.setImageResource(R.drawable.placeholder_image);

                // Show the image
                imageArticle.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
