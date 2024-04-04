package com.example.amusemate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flagsmith.FlagsmithClient;
import com.flagsmith.FlagsmithLoggerLevel;
import com.flagsmith.exceptions.FlagsmithClientError;
import com.flagsmith.models.Flags;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private TextView textArticleContent;
    private ImageView imageArticle;
    private Button buttonSomethingElse;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String apiKey = "YOUR_API_KEY";
        String apiUrl = "http://10.0.2.2:8000/api/v1/";

      FlagsmithClient fsClient = FlagsmithClient
                .newBuilder()
                .setApiKey(apiKey)
                .withApiUrl(apiUrl)
                .enableLogging(FlagsmithLoggerLevel.ERROR)
                .build();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonCenter = findViewById(R.id.buttonCenter);
        textArticleContent = findViewById(R.id.textArticleContent);
        imageArticle = findViewById(R.id.imageArticle);
        buttonSomethingElse = findViewById(R.id.buttonSomethingElse);
        buttonBack = findViewById(R.id.buttonBack);

        imageArticle.setVisibility(View.GONE);
        buttonSomethingElse.setVisibility(View.GONE);
        buttonBack.setVisibility(View.GONE);


        buttonCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Construct the URL with query parameters
                String baseUrl = "http://10.0.2.2:5000/";
                String excludedItems;
                boolean isEnabled;
                try {
                    Flags flags = fsClient.getIdentityFlags("production_user_123456");
                    isEnabled = flags.isFeatureEnabled("adultcontent");
                } catch (FlagsmithClientError e) {
                    throw new RuntimeException(e);
                }
                if (isEnabled==true){
                    excludedItems  = "0";
                }else{
                    excludedItems  = "0,1,2,3"; //exclude 18+ items
                }

                String url = baseUrl + "?suggested_items=" + excludedItems;

                new FetchRecommendationTask().execute(url);
            }
        });


        buttonSomethingElse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Construct the URL with empty suggested items
                String baseUrl = "http://10.0.2.2:5000/";
                String url = baseUrl + "?suggested_items=";

                // Fetch the recommendation with empty suggested items when the button is clicked
                new FetchRecommendationTask().execute(url);
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private class FetchRecommendationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String recommendation = "";

            // URL of the Flask server endpoint to fetch recommendation
            String urlString = urls[0];

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
                e.printStackTrace();
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

                ImageView imageHeader = findViewById(R.id.imageHeader);
                imageHeader.setVisibility(View.GONE);
                Button buttonCenter = findViewById(R.id.buttonCenter);
                buttonCenter.setVisibility(View.GONE);
                // Show the image
                imageArticle.setVisibility(View.VISIBLE);

                // Show the "Something else" and the "go back" button
                buttonSomethingElse.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
