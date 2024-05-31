package com.example.goalgalaxy.Tasks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.goalgalaxy.DialogCloseListener;
import com.example.goalgalaxy.R;

import java.util.Calendar;
import java.util.Locale;

public class DateTimePicker extends DialogFragment {

    Button btnDatePicker, btnTimePicker, btnOk;
    private EditText inDate, inTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int defaultYear, defaultMonth, defaultDay, defaultHour, defaultMinute;
    private boolean isDateSet = false;
    private boolean isTimeSet = false;
    private static final String PREFS_NAME = "DateTimePrefs";

    // Ключи для сохранения значений
    private static final String KEY_YEAR = "year";
    private static final String KEY_MONTH = "month";
    private static final String KEY_DAY = "day";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MINUTE = "minute";

    private Context context;
    private DateTimeListener dateTimeListener;
    private AddNewTask addNewTask;



    public void setAddNewTask(AddNewTask addNewTask) {
        this.addNewTask = addNewTask;
    }




    public interface DateTimeListener {
        void onDateTimeSet(int year, int month, int day, int hour, int minute);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setDateTimeListener(DateTimeListener listener) {
        this.dateTimeListener = listener;
    }

    // Установить значения по умолчанию для даты и времени
    public void setDefaultDateTime(int year, int month, int day, int hour, int minute) {
        defaultYear = year;
        defaultMonth = month;
        defaultDay = day;
        defaultHour = hour;
        defaultMinute = minute;
        isDateSet = true;
        isTimeSet = true;

        mYear = year;
        mMonth = month;
        mDay = day;
        mHour = hour;
        mMinute = minute;

        // Обновляем текст в соответствующих EditText, если они уже инициализированы
        if (inDate != null && inTime != null) {
            inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", defaultDay, defaultMonth + 1, defaultYear));
            inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", defaultHour, defaultMinute));
        }
    }



    public void clearDateTime() {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            if (inDate != null) {
                inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", mDay, mMonth + 1, mYear));
            }
            if (inTime != null) {
                inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", mHour, mMinute));
            }
        }
    }

    private void saveDateTime(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_YEAR, mYear);
        editor.putInt(KEY_MONTH, mMonth);
        editor.putInt(KEY_DAY, mDay);
        editor.putInt(KEY_HOUR, mHour);
        editor.putInt(KEY_MINUTE, mMinute);
        editor.apply();
    }

    private void loadDateTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mYear = prefs.getInt(KEY_YEAR, defaultYear);
        mMonth = prefs.getInt(KEY_MONTH, defaultMonth);
        mDay = prefs.getInt(KEY_DAY, defaultDay);
        mHour = prefs.getInt(KEY_HOUR, defaultHour);
        mMinute = prefs.getInt(KEY_MINUTE, defaultMinute);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminder_layout, container, false);


        btnDatePicker = view.findViewById(R.id.btn_date);
        btnTimePicker = view.findViewById(R.id.btn_time);
        btnOk = view.findViewById(R.id.btn_ok);
        inDate = view.findViewById(R.id.in_date);
        inTime = view.findViewById(R.id.in_time);

        // Установка текста в EditText с использованием переданных значений по умолчанию
        inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", defaultDay, defaultMonth + 1, defaultYear));
        inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", defaultHour, defaultMinute));

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.CustomDatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                                isDateSet = true;
                                addNewTask.setDateChanged(true);
                                inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", mDay, mMonth + 1, mYear));
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), R.style.CustomTimePickerDialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mHour = hourOfDay;
                                mMinute = minute;
                                isTimeSet = true;
                                addNewTask.setTimeChanged(true);
                                inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", mHour, mMinute));
                            }
                        }, mHour, mMinute, true);

                timePickerDialog.show();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDateTime(requireContext());
                if (dateTimeListener != null) {
                    dateTimeListener.onDateTimeSet(mYear, mMonth, mDay, mHour, mMinute);
                }
                dismiss();
            }
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.reminder_layout);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setLayout(params.width, params.height);

        inDate = dialog.findViewById(R.id.in_date);
        inTime = dialog.findViewById(R.id.in_time);

        // Установка текста в EditText с использованием переданных значений по умолчанию
        inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", defaultDay, defaultMonth + 1, defaultYear));
        inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", defaultHour, defaultMinute));

        return dialog;
    }

    public void updateDate(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
        inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", mDay, mMonth + 1, mYear));
    }

    public void updateTime(int hour, int minute) {
        mHour = hour;
        mMinute = minute;
        inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", mHour, mMinute));
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        saveDateTime(requireContext());
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
    }
}
