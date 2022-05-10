package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MViewHolder> {

    List<Task> tasksList;
    ClickListener listener;

    public RecyclerViewAdapter(List<Task> tasksList, ClickListener listener) {
        this.tasksList = tasksList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView = layoutInflater.inflate(R.layout.task_details, parent, false);
        MViewHolder viewHolder = new MViewHolder(listItemView, listener);
        return viewHolder;
    }


    // will be called multiple times to inject the data into the view holder object
    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {
        holder.title.setText(tasksList.get(position).getTitle());
        holder.body.setText(tasksList.get(position).getBody());
        holder.state.setText(tasksList.get(position).getState());
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    // ViewHolder Class
    final static class MViewHolder extends  RecyclerView.ViewHolder  {

        TextView title, body, state;
        ClickListener listener;

        public MViewHolder(@NonNull View itemView, ClickListener listener) {
            super(itemView);
            this.listener = listener;

            title = itemView.findViewById(R.id.text_title);
            body  = itemView.findViewById(R.id.text_body);
            state = itemView.findViewById(R.id.text_state);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onTaskItemClicked(getAdapterPosition());
                }
            });
        }
    }


    public interface ClickListener {
        void onTaskItemClicked (int position);
    }
}
