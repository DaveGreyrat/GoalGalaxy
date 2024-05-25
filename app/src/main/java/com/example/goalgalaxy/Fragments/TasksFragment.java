package com.example.goalgalaxy.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goalgalaxy.Adapter.ToDoAdapter;
import com.example.goalgalaxy.AddNewTask;
import com.example.goalgalaxy.DateTimePicker;
import com.example.goalgalaxy.MainActivity;
import com.example.goalgalaxy.Model.ToDoModel;
import com.example.goalgalaxy.R;
import com.example.goalgalaxy.RecyclerItemTouchHelper;
import com.example.goalgalaxy.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

public class TasksFragment extends Fragment {

    private DatabaseHandler db;

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList;
    private OnTaskUpdatedListener mListener;
    private MainActivity mActivity;
    private DateTimePicker dateTimePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        dateTimePicker = new DateTimePicker();
        dateTimePicker.setContext(requireContext());

        db = new DatabaseHandler(requireActivity());
        db.openDatabase();

        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        tasksAdapter = new ToDoAdapter(db, (MainActivity) requireActivity());
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.clearDateTime();
                AddNewTask.newInstance().show(requireActivity().getSupportFragmentManager(), AddNewTask.TAG);
            }
        });


        loadTasks();

        return view;
    }

    public void loadTasks() {
        taskList = db.getIncompleteTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
    }

    public interface OnTaskAddedListener {
        void onTaskAdded();
    }

    public void onTaskAdded() {
        loadTasks();
    }

    public interface OnTaskUpdatedListener {
        void onTaskUpdated();
    }

    public void setOnTaskUpdatedListener(OnTaskUpdatedListener listener) {
        mListener = listener;
    }

    public void setMainActivity(MainActivity activity) {
        mActivity = activity;
    }
}
