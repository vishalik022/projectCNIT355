package com.example.dreamanalyzer;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.Map;

public class DreamLogActivity extends AppCompatActivity {
    private EditText dreamText;
    private Spinner moodSpinner;
    private Button analyzeBtn, saveBtn;
    private AIAnalyzer ai;
    private FirebaseHelper firebaseHelper;
    private FirebaseAuth auth;
    private Map<String,Object> latestAiTags = null;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_log);
        dreamText = findViewById(R.id.dreamText);
        moodSpinner = findViewById(R.id.moodSpinner);
        analyzeBtn = findViewById(R.id.analyzeBtn);
        saveBtn = findViewById(R.id.saveBtn);
        progress = findViewById(R.id.progressBar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.moods_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(adapter);

        ai = new AIAnalyzer();
        firebaseHelper = new FirebaseHelper();
        auth = FirebaseAuth.getInstance();

        analyzeBtn.setOnClickListener(v -> analyze());
        saveBtn.setOnClickListener(v -> saveDream());
    }

    private void analyze() {
        String text = dreamText.getText().toString();
        if (text.isEmpty()) { Toast.makeText(this, "Enter dream text", Toast.LENGTH_SHORT).show(); return; }
        progress.setVisibility(View.VISIBLE);
        ai.analyzeDream(text, new AIAnalyzer.Callback() {
            @Override
            public void onResult(Map<String, Object> analysis) {
                progress.setVisibility(View.GONE);
                latestAiTags = analysis;
                Toast.makeText(DreamLogActivity.this, "Analysis complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                progress.setVisibility(View.GONE);
                Toast.makeText(DreamLogActivity.this, "AI analysis failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveDream() {
        String text = dreamText.getText().toString();
        String mood = moodSpinner.getSelectedItem().toString();
        if (text.isEmpty()) { Toast.makeText(this, "Enter dream text", Toast.LENGTH_SHORT).show(); return; }

        String uid = auth.getCurrentUser().getUid();
        Dream dream = new Dream(null, uid, text, System.currentTimeMillis(), mood, latestAiTags);
        firebaseHelper.saveDream(dream, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Dream saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Save failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
