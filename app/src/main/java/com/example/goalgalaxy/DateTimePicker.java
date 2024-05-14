package com.example.goalgalaxy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.Calendar;
import java.util.Locale;

public class DateTimePicker extends DialogFragment {

    Button btnDatePicker, btnTimePicker, btnOk;
    private EditText inDate, inTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int Year, Month, Day, Hour, Minute;
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

    public void setContext(Context context) {
        this.context = context;
    }

    public void clearDateTime() {
        // Проверяем, что контекст не является null
        if (context != null) {
            // Удаляем сохраненные данные
            SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            // Устанавливаем текущую дату и время
            Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            // Обновляем отображение даты и времени, если оно есть
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

    // Метод для загрузки даты и времени из SharedPreferences
    private void loadDateTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mYear = prefs.getInt(KEY_YEAR, mYear);
        mMonth = prefs.getInt(KEY_MONTH, mMonth);
        mDay = prefs.getInt(KEY_DAY, mDay);
        mHour = prefs.getInt(KEY_HOUR, mHour);
        mMinute = prefs.getInt(KEY_MINUTE, mMinute);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminder_layout, container, false);

        loadDateTime(requireContext());


        final Calendar calendar = Calendar.getInstance();
        if (!isDateSet) {
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
        }
        if (!isTimeSet){
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
        }
        loadDateTime(requireContext());


        btnDatePicker = view.findViewById(R.id.btn_date);
        btnTimePicker = view.findViewById(R.id.btn_time);
        btnOk = view.findViewById(R.id.btn_ok);
        inDate = view.findViewById(R.id.in_date);
        inTime = view.findViewById(R.id.in_time);

        inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", mDay, mMonth, mYear));
        inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", mHour, mMinute));

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Установите выбранную дату в диалоге, используя значения mYear, mMonth и mDay
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                                isDateSet = true;
                                inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", mDay, mMonth + 1, mYear));
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });



        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mHour = hourOfDay;
                                mMinute = minute;
                                isTimeSet = true;
                                inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", mHour, mMinute));
                            }
                        }, mHour, mMinute, false);

                timePickerDialog.show();
            }
        });

        if (isDateSet) {
            inDate.setText(String.format(Locale.getDefault(), "%d/%d/%d", mDay, mMonth + 1, mYear));
        }
        if (isTimeSet) {
            inTime.setText(String.format(Locale.getDefault(), "%02d:%02d", mHour, mMinute));
        }


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDateTime(requireContext());
                dismiss();
            }
        });

        return view;
    }


    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());

        // Установите прозрачный фон для окна диалога
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Установите макет для диалога
        dialog.setContentView(R.layout.reminder_layout);

        // Установите ширину и высоту для окна диалога, чтобы оно занимало весь экран
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setLayout(params.width, params.height);

        return dialog;
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
