package com.example.dreamanalyzer;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.*;

public class ChartActivity extends AppCompatActivity {
    private LineChart chart;
    private FirebaseHelper firebaseHelper;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        chart = findViewById(R.id.lineChart);
        firebaseHelper = new FirebaseHelper();
        auth = FirebaseAuth.getInstance();
        loadAndPlot();
    }

    private void loadAndPlot() {
        String uid = auth.getCurrentUser().getUid();
        firebaseHelper.fetchUserDreams(uid, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Failed to load dreams for chart", Toast.LENGTH_SHORT).show();
                return;
            }
            QuerySnapshot snap = task.getResult();
            List<Entry> entries = new ArrayList<>();
            int i=0;
            for (var doc : snap.getDocuments()) {
                Dream d = doc.toObject(Dream.class);
                // Convert mood to a score: example mapping
                float score = moodToScore(d.getMood());
                entries.add(new Entry(i++, score));
            }
            LineDataSet set = new LineDataSet(entries, "Mood over time");
            LineData data = new LineData(set);
            chart.setData(data);
            chart.invalidate();
        });
    }

    private float moodToScore(String mood) {
        if (mood == null) return 2f;
        switch (mood.toLowerCase()) {
            case "happy": return 4f;
            case "content": return 3f;
            case "neutral": return 2f;
            case "anxious": return 1f;
            case "sad": return 0f;
            default: return 2f;
        }
    }
}
