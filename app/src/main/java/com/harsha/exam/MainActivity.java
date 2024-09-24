package com.harsha.exam;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription;
    private TextView btnCopy, btnShare;
    private TextView btnCopy2, btnShare2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Android Assignment");

        tvTitle = findViewById(R.id.title);
        tvDescription = findViewById(R.id.description);
        btnCopy = findViewById(R.id.tvCopy2);
        btnShare = findViewById(R.id.tvShare2);
        btnCopy2 = findViewById(R.id.tvCopy3);
        btnShare2 = findViewById(R.id.tvShare3);

        // Fetch API data
        fetchApiData();

        // Copy text functionality
        btnCopy.setOnClickListener(view -> {
            String textToCopy = tvTitle.getText().toString() + "\n" + tvDescription.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MainActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();
        });
        btnCopy2.setOnClickListener(view -> {
            String textToCopy = tvTitle.getText().toString() + "\n" + tvDescription.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MainActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();
        });


        // Share text functionality
        btnShare.setOnClickListener(view -> {
            String textToShare = tvTitle.getText().toString() + "\n" + tvDescription.getText().toString();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            startActivity(Intent.createChooser(shareIntent, "Share text via"));
        });
        btnShare2.setOnClickListener(view -> {
            String textToShare = tvTitle.getText().toString() + "\n" + tvDescription.getText().toString();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            startActivity(Intent.createChooser(shareIntent, "Share text via"));
        });

    }

    private void fetchApiData() {
        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier((hostname, session) -> true) // Disable hostname verification (for testing only)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.jsonkeeper.com/") // Ensure the URL ends with /
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ApiResponse> call = apiService.getApiData();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getChoices() != null && !apiResponse.getChoices().isEmpty()) {
                        // Get the content string from the response
                        String content = apiResponse.getChoices().get(0).getMessage().getContent();
                        Log.d("API_CONTENT", "Content: " + content);

                        // Parse the content JSON
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(content, JsonObject.class);

                        // Retrieve titles and description
                        List<String> titles = gson.fromJson(jsonObject.get("titles"), List.class);
                        String description = jsonObject.get("description").getAsString();

                        // Set the title and description
                        if (!titles.isEmpty()) {
                            tvTitle.setText(titles.get(0)); // Set the first title
                        }
                        tvDescription.setText(description);
                    }
                } else {
                    Log.e("API_ERROR", "Response was not successful or body is null");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
            }
        });
    }
}
