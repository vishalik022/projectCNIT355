package com.example.dreamanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DreamAdapter adapter;
    private List<Dream> dreams = new ArrayList<>();
    private FirebaseHelper firebaseHelper;
    private FirebaseAuth auth;
    private Button newDreamBtn, chartBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        newDreamBtn = findViewById(R.id.newDreamBtn);
        chartBtn = findViewById(R.id.chartBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        adapter = new DreamAdapter(dreams);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        firebaseHelper = new FirebaseHelper();
        auth = FirebaseAuth.getInstance();

        newDreamBtn.setOnClickListener(v -> startActivity(new Intent(this, DreamLogActivity.class)));
        chartBtn.setOnClickListener(v -> startActivity(new Intent(this, ChartActivity.class)));
        logoutBtn.setOnClickListener(v -> { auth.signOut(); startActivity(new Intent(this, LoginActivity.class)); finish(); });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDreams();
    }

    private void loadDreams() {
        String uid = auth.getCurrentUser().getUid();
        firebaseHelper.fetchUserDreams(uid, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Failed to load dreams", Toast.LENGTH_SHORT).show();
                return;
            }
            dreams.clear();
            for (DocumentSnapshot ds : task.getResult()) {
                Dream d = ds.toObject(Dream.class);
                if (d != null) dreams.add(d);
            }
            adapter.notifyDataSetChanged();
        });
    }
}
