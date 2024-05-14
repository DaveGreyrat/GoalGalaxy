package com.example.goalgalaxy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.goalgalaxy.Authentication.LoginActivity;
import com.example.goalgalaxy.Model.ToDoModel;
import com.example.goalgalaxy.Utils.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.api.Authentication;

import java.time.Year;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private List<ToDoModel> todoList;
    private EditText newTaskText;
    private EditText newTaskDescription;

    private Button newTaskSaveButton;
    private Button reminder;

    private int Year, Month, Day, Hour, Minute;

    private DatabaseHandler db;
    private boolean isUpdate;
    private Bundle bundle;
    private DateTimePicker dateTimePickerFragment;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.new_task, container, false);

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


        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("ResourceType")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.darker_gray));
                    newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));

                }
                else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                    newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.third));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создайте экземпляр DateTimePicker
                dateTimePickerFragment = new DateTimePicker();
                // Показать фрагмент DateTimePicker
                dateTimePickerFragment.show(getChildFragmentManager(), "DateTimePickerDialogFragment");
            }
        });






        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                String description = newTaskDescription.getText().toString();

                if(finalIsUpdate){
                    db.updateTask(bundle.getInt("id"), text, description, Year, Month, Day, Hour, Minute);
                }
                else {
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

                dismiss();


            }
        });




    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }

}

