package com.example.goalgalaxy.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goalgalaxy.Adapter.ToDoAdapter;
import com.example.goalgalaxy.AddNewTask;
import com.example.goalgalaxy.MainActivity;
import com.example.goalgalaxy.Model.ToDoModel;
import com.example.goalgalaxy.R;
import com.example.goalgalaxy.RecyclerItemTouchHelper;
import com.example.goalgalaxy.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CompletedFragment extends Fragment {


    private DatabaseHandler db;

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;

    private List<ToDoModel> taskList;
    private Context context;
    private OnTaskUpdatedListener mListener;
    private MainActivity mActivity;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);




        db = new DatabaseHandler(requireActivity());
        db.openDatabase();

        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        tasksAdapter = new ToDoAdapter(db, (MainActivity) requireActivity());
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        loadCompletedTasks();

        return view;



    }

    public void loadCompletedTasks() {
        taskList = db.getCompletedTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
    }

    public interface OnTaskUpdatedListener {
        void onTaskUpdated();
    }

    public void onTaskUpdated() {
        // Обновите список задач в вашем фрагменте CompletedFragment
        loadCompletedTasks();
    }

    public void setOnTaskUpdatedListener(OnTaskUpdatedListener listener) {
        mListener = listener;
    }

    public void setMainActivity(MainActivity activity) {
        mActivity = activity;
    }


}

