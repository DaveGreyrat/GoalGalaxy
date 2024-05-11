package com.example.goalgalaxy.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goalgalaxy.AddNewTask;
import com.example.goalgalaxy.MainActivity;
import com.example.goalgalaxy.Model.ToDoModel;
import com.example.goalgalaxy.R;
import com.example.goalgalaxy.Utils.DatabaseHandler;

import java.util.List;
import java.util.Locale;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private DatabaseHandler db;
    private MainActivity activity;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_TYPE_REMINDER) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reminder_layout, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_layout, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();

        final ToDoModel item = todoList.get(position);
        if (item != null) {
            holder.task.setText(item.getTask());
            holder.description.setText(item.getDescription());
            if (item.isReminder()) {
                holder.inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", item.getDateY(), item.getDateM(), item.getDateD()));
                holder.inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", item.getTimeH(), item.getTimeM()));
            }
            holder.task.setChecked(toBoolean(item.getStatus()));
            holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        db.updateStatus(item.getId(), 1);
                    } else {
                        db.updateStatus(item.getId(), 0);
                    }
                }
            });
        } else {
            Log.e("ToDoAdapter", "ToDoModel at position " + position + " is null");
        }
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putString("description", item.getDescription());
        bundle.putInt("year", item.getDateY());
        bundle.putInt("month", item.getDateM());
        bundle.putInt("day", item.getDateD());
        bundle.putInt("hour", item.getTimeH());
        bundle.putInt("minute", item.getTimeM());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView description;
        EditText inDate, inTime;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            description = view.findViewById(R.id.taskDescription);
            inDate = view.findViewById(R.id.in_date);
            inTime = view.findViewById(R.id.in_time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ToDoModel item = todoList.get(position);
        return item.isReminder() ? VIEW_TYPE_REMINDER : VIEW_TYPE_TASK;
    }

    private static final int VIEW_TYPE_TASK = 0;
    private static final int VIEW_TYPE_REMINDER = 1;
}
