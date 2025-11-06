package com.example.dreamanalyzer;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String DREAMS = "dreams";

    public void saveDream(Dream dream, OnCompleteListener<Void> listener) {
        CollectionReference col = db.collection(DREAMS);
        DocumentReference docRef = dream.getId() == null ? col.document() : col.document(dream.getId());
        dream.setId(docRef.getId());
        docRef.set(dream).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) Log.e(TAG, "saveDream failed", task.getException());
            if (listener != null) listener.onComplete(task);
        });
    }

    public void fetchUserDreams(String userId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection(DREAMS)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(listener);
    }
}
