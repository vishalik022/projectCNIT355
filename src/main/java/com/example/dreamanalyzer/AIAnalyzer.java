package com.example.dreamanalyzer;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.*;
import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AIAnalyzer {
    private static final String TAG = "AIAnalyzer";

    private static final String GEMINI_API_KEY = "YOUR_API_KEY_HERE";

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + "gemini-2.5-flash-preview-09-2025:generateContent?key=" + GEMINI_API_KEY;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface Callback {
        void onResult(Map<String, Object> analysis);
        void onError(Exception e);
    }

    public void analyzeDream(String dreamText, Callback callback) {
        // Build the Gemini API request body
        JsonObject part = new JsonObject();
        part.addProperty("text",
                "Analyze this dream and respond in JSON with keys: " +
                        "sentiment (positive/neutral/negative), " +
                        "themes (list of short words), emotions (list), symbols (list). " +
                        "Dream: " + dreamText);

        JsonArray parts = new JsonArray();
        parts.add(part);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject bodyJson = new JsonObject();
        bodyJson.add("contents", contents);

        RequestBody body = RequestBody.create(
                bodyJson.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(GEMINI_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Gemini API call failed", e);
                mainHandler.post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(() ->
                            callback.onError(new IOException("Gemini API error: " + response.code())));
                    return;
                }

                String resp = response.body() != null ? response.body().string() : "";
                try {
                    JsonObject json = JsonParser.parseString(resp).getAsJsonObject();
                    JsonArray candidates = json.getAsJsonArray("candidates");
                    String text = "";

                    if (candidates != null && candidates.size() > 0) {
                        JsonObject first = candidates.get(0).getAsJsonObject();
                        JsonObject contentObj = first.getAsJsonObject("content");
                        JsonArray partsArr = contentObj.getAsJsonArray("parts");
                        text = partsArr.get(0).getAsJsonObject().get("text").getAsString();
                    }

                    Map<String, Object> analysis = new HashMap<>();
                    try {
                        JsonElement parsed = JsonParser.parseString(text);
                        if (parsed.isJsonObject()) {
                            analysis = gson.fromJson(parsed, HashMap.class);
                        } else {
                            analysis.put("raw", text);
                        }
                    } catch (Exception ex) {
                        analysis.put("raw", text);
                    }

                    Map<String, Object> finalAnalysis = analysis;
                    mainHandler.post(() -> callback.onResult(finalAnalysis));

                } catch (Exception e) {
                    Log.e(TAG, "Gemini response parse failed", e);
                    mainHandler.post(() -> callback.onError(e));
                }
            }
        });
    }
}
