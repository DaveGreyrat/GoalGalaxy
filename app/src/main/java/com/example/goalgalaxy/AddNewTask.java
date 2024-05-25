package com.example.goalgalaxy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.goalgalaxy.Fragments.TasksFragment;
import com.example.goalgalaxy.Model.ToDoModel;
import com.example.goalgalaxy.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";

    private EditText newTaskText;
    private EditText newTaskDescription;
    private Button newTaskSaveButton;
    private Button reminder;
    private DatabaseHandler db;

    private int Year, Month, Day, Hour, Minute;

    private boolean isUpdate = false;
    private Bundle bundle;
    private boolean isTaskTextChanged = false;
    private boolean isDescriptionTextChanged = false;
    private TasksFragment.OnTaskAddedListener mOnTaskAddedListener;
    private OnTaskAddedListener mListener;


    public interface OnTaskAddedListener {
        void onTaskAdded();
    }
    public void setOnTaskAddedListener(TasksFragment.OnTaskAddedListener listener) {
        mOnTaskAddedListener = listener;
    }
    public static AddNewTask newInstance() {
        return new AddNewTask();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        requireDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = view.findViewById(R.id.newTaskText);
        newTaskDescription = view.findViewById(R.id.newTaskDescription);
        newTaskSaveButton = view.findViewById(R.id.newTaskButton);
        reminder = view.findViewById(R.id.reminderButton);

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            newTaskText.setText(bundle.getString("task"));
            newTaskDescription.setText(bundle.getString("description"));
            Year = bundle.getInt("year");
            Month = bundle.getInt("month");
            Day = bundle.getInt("day");
            Hour = bundle.getInt("hour");
            Minute = bundle.getInt("minute");
        } else {
            // Initialize date and time to current values if it's a new task
            Calendar calendar = Calendar.getInstance();
            Year = calendar.get(Calendar.YEAR);
            Month = calendar.get(Calendar.MONTH) + 1;
            Day = calendar.get(Calendar.DAY_OF_MONTH);
            Hour = calendar.get(Calendar.HOUR_OF_DAY);
            Minute = calendar.get(Calendar.MINUTE);
        }




        // Add TextWatcher for both fields
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Set flags to true when text changes
                if (s == newTaskText.getText()) {
                    isTaskTextChanged = true;
                } else if (s == newTaskDescription.getText()) {
                    isDescriptionTextChanged = true;
                }

                // Check if any field is empty and update save button state
                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        newTaskText.addTextChangedListener(textWatcher);
        newTaskDescription.addTextChangedListener(textWatcher);

        // Check fields initially
        checkFieldsForEmptyValues();

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show DateTimePicker fragment
                DateTimePicker dateTimePickerFragment = new DateTimePicker();
                dateTimePickerFragment.setDateTimeListener(new DateTimePicker.DateTimeListener() {
                    @Override
                    public void onDateTimeSet(int year, int month, int day, int hour, int minute) {
                        // После выбора даты и времени обновляем отображаемую информацию в AddNewTask
                        updateDateTime(year, month, day, hour, minute);
                    }
                });
                // Передайте сохраненное время в DateTimePicker перед открытием его диалогового окна

                dateTimePickerFragment.setDefaultDateTime(Year, Month - 1, Day, Hour, Minute);

                dateTimePickerFragment.show(getChildFragmentManager(), "dateTimePicker");


            }
        });


        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                String description = newTaskDescription.getText().toString();

                if (isUpdate) {
                    db.updateTask(bundle.getInt("id"), text, description, Year, Month+1, Day, Hour, Minute);
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setDescription(description);
                    task.setDateY(Year);
                    task.setDateM(Month);
                    task.setDateD(Day);
                    task.setTimeH(Hour);
                    task.setTimeM(Minute);
                    task.setStatus(0);
                    db.insertTask(task);
                }

                Intent intent = new Intent("task_added");
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);

                dismiss();
            }
        });
    }

    private void checkFieldsForEmptyValues() {
        String text = newTaskText.getText().toString();
        String description = newTaskDescription.getText().toString();

        // Check if we are creating a new task or updating an existing one
        if (!isUpdate) {
            // If creating a new task, check only the task field
            if (TextUtils.isEmpty(text)) {
                newTaskSaveButton.setEnabled(false);
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.darker_gray));
                newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
            } else {
                newTaskSaveButton.setEnabled(true);
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.third));
            }
        } else {
            // If updating an existing task, check both task and description fields
            if (TextUtils.isEmpty(text) || TextUtils.isEmpty(description) ||
                    (!isTaskTextChanged && !isDescriptionTextChanged)) {
                newTaskSaveButton.setEnabled(false);
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.darker_gray));
                newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
            } else {
                newTaskSaveButton.setEnabled(true);
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.third));
            }
        }
    }


    private void updateDateTime(int year, int month, int day, int hour, int minute) {
        // Здесь вы можете сохранить выбранную дату и время в вашем фрагменте AddNewTask
        Year = year;
        Month = month;
        Day = day;
        Hour = hour;
        Minute = minute;

        // Затем вы можете использовать эти данные, как вам угодно
    }



    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Intent intent = new Intent("UPDATE_DATA");
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        }
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
    }

}
