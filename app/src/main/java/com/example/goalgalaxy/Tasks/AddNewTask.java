package com.example.goalgalaxy.Tasks;

import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.goalgalaxy.Authentication.LoginActivity;
import com.example.goalgalaxy.DialogCloseListener;
import com.example.goalgalaxy.Fragments.TasksFragment;
import com.example.goalgalaxy.Model.ToDoModel;
import com.example.goalgalaxy.R;
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
    private boolean isDateChanged = false;
    private boolean isTimeChanged = false;
    private TasksFragment.OnTaskAddedListener mOnTaskAddedListener;
    private OnTaskAddedListener mListener;
    private AddNewTask addNewTask;



    public interface OnTaskAddedListener {
        void onTaskAdded();
    }

    public interface AddNewTaskProvider {
        AddNewTask getAddNewTask();
    }

    public AddNewTask getAddNewTask() {
        return addNewTask;
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
            Month = bundle.getInt("month") - 1;
            Day = bundle.getInt("day");
            Hour = bundle.getInt("hour");
            Minute = bundle.getInt("minute");
        } else {
            Calendar calendar = Calendar.getInstance();
            Year = calendar.get(Calendar.YEAR);
            Month = calendar.get(Calendar.MONTH);
            Day = calendar.get(Calendar.DAY_OF_MONTH);
            Hour = calendar.get(Calendar.HOUR_OF_DAY);
            Minute = calendar.get(Calendar.MINUTE);
        }




        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == newTaskText.getText()) {
                    isTaskTextChanged = true;
                    isDescriptionTextChanged = false;
                } else if (s == newTaskDescription.getText()) {
                    isDescriptionTextChanged = true;
                    isTaskTextChanged = false;
                }

                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        newTaskText.addTextChangedListener(textWatcher);
        newTaskDescription.addTextChangedListener(textWatcher);

        checkFieldsForEmptyValues();

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePicker dateTimePickerFragment = new DateTimePicker();
                dateTimePickerFragment.setDateTimeListener(new DateTimePicker.DateTimeListener() {
                    @Override
                    public void onDateTimeSet(int year, int month, int day, int hour, int minute) {
                        updateDateTime(year, month, day, hour, minute);
                    }
                });

                dateTimePickerFragment.setDefaultDateTime(Year, Month, Day, Hour, Minute);
                dateTimePickerFragment.setAddNewTask(AddNewTask.this);
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
                    task.setYear(Year);
                    task.setMonth(Month+1);
                    task.setDay(Day);
                    task.setHour(Hour);
                    task.setMinute(Minute);
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

        if (TextUtils.isEmpty(text)) {
            newTaskSaveButton.setEnabled(false);
            newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.darker_gray));
            newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
        } else if ((!TextUtils.isEmpty(text) && isDescriptionTextChanged) || (isTaskTextChanged && TextUtils.isEmpty(description) || isTaskTextChanged || (!TextUtils.isEmpty(text) && isTimeChanged) || (!TextUtils.isEmpty(text) && isDateChanged))){
            newTaskSaveButton.setEnabled(true);
            newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.third));
        }
    }



    private void updateDateTime(int year, int month, int day, int hour, int minute) {
        Year = year;
        Month = month;
        Day = day;
        Hour = hour;
        Minute = minute;
    }



    public void setDateChanged(boolean changed) {
        isDateChanged = changed;
        checkFieldsForEmptyValues();
    }

    public void setTimeChanged(boolean changed) {
        isTimeChanged = changed;
        checkFieldsForEmptyValues();
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
