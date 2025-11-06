package com.example.dreamanalyzer;

import android.view.*;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DateFormat;
import java.util.List;

public class DreamAdapter extends RecyclerView.Adapter<DreamAdapter.ViewHolder> {
    private final List<Dream> dreams;

    public DreamAdapter(List<Dream> dreams) { this.dreams = dreams; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dream, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dream d = dreams.get(position);
        holder.text.setText(d.getText());
        holder.mood.setText("Mood: " + d.getMood());
        String date = DateFormat.getDateTimeInstance().format(d.getTimestamp());
        holder.time.setText(date);
    }

    @Override
    public int getItemCount() { return dreams.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text, mood, time;
        ViewHolder(View v) {
            super(v);
            text = v.findViewById(R.id.item_text);
            mood = v.findViewById(R.id.item_mood);
            time = v.findViewById(R.id.item_time);
        }
    }
}
