package com.example.goalgalaxy.Adapter;

import android.content.Context;
import android.content.Intent;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goalgalaxy.Tasks.AddNewTask;
import com.example.goalgalaxy.Fragments.TasksFragment;
import com.example.goalgalaxy.MainActivity;
import com.example.goalgalaxy.Model.ToDoModel;
import com.example.goalgalaxy.R;
import com.example.goalgalaxy.Utils.DatabaseHandler;

import java.util.List;
import java.util.Locale;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private final DatabaseHandler db;
    private final MainActivity activity;

    private static final int VIEW_TYPE_TASK = 0;
    private static final int VIEW_TYPE_REMINDER = 1;
    private TasksFragment.OnTaskUpdatedListener mListener;


    public void setOnTaskUpdatedListener(TasksFragment.OnTaskUpdatedListener listener) {
        mListener = listener;
    }

    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
        this.db.openDatabase(); // Открываем базу данных один раз в конструкторе
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
        final ToDoModel item = todoList.get(position);
        if (item != null) {
            holder.task.setText(item.getTask());
            holder.description.setText(item.getDescription());
            holder.date.setText(String.format(Locale.getDefault(), "%d/%d", item.getMonth(), item.getDay()));
            holder.time.setText(String.format(Locale.getDefault(), "%02d:%02d", item.getHour(), item.getMinute()));
            if (item.isReminder()) {
                holder.inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", item.getYear(), item.getMonth(), item.getDay()));
                holder.inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", item.getHour(), item.getMinute()));
            }
            holder.task.setOnCheckedChangeListener(null); // Отключаем слушатель перед обновлением чекбокса
            holder.task.setChecked(toBoolean(item.getStatus()));
            holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int newStatus = isChecked ? 1 : 0;
                    db.updateStatus(item.getId(), newStatus);
                    Intent intent = new Intent("UPDATE_DATA");
                    LocalBroadcastManager.getInstance(holder.itemView.getContext()).sendBroadcast(intent);
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
        return todoList != null ? todoList.size() : 0;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        if (item != null) {
            db.deleteTask(item.getId()); // Удаляем задачу по ее идентификатору
            todoList.remove(position);
            notifyItemRemoved(position);
        } else {
            Log.e("ToDoAdapter", "Task at position " + position + " is null");
        }
    }


    public void updateItem(int position, boolean isChecked) {
        ToDoModel item = todoList.get(position);
        item.setStatus(isChecked ? 1 : 0);
        db.updateStatus(item.getId(), isChecked ? 1 : 0);
        notifyItemChanged(position);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        if (item != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("id", item.getId());
            bundle.putString("task", item.getTask());
            bundle.putString("description", item.getDescription());
            bundle.putInt("year", item.getYear());
            bundle.putInt("month", item.getMonth());
            bundle.putInt("day", item.getDay());
            bundle.putInt("hour", item.getHour());
            bundle.putInt("minute", item.getMinute());
            AddNewTask fragment = new AddNewTask();
            fragment.setArguments(bundle);
            fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView description, date, time;
        EditText inDate, inTime;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            description = view.findViewById(R.id.taskDescription);
            date = view.findViewById(R.id.taskDate);
            time = view.findViewById(R.id.taskTime);
            inDate = view.findViewById(R.id.in_date);
            inTime = view.findViewById(R.id.in_time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ToDoModel item = todoList.get(position);
        return item.isReminder() ? VIEW_TYPE_REMINDER : VIEW_TYPE_TASK;
    }

    public Context getContext() {
        return activity; // Возвращаем контекст активности
    }
}
