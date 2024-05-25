package com.example.goalgalaxy.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.goalgalaxy.Model.ToDoModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.util.Log;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String DESCRIPTION = "description";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK + " TEXT, "
            + DESCRIPTION + " TEXT, "
            + YEAR + " INTEGER, "
            + MONTH + " INTEGER, "
            + DAY + " INTEGER, "
            + HOUR + " INTEGER, "
            + MINUTE + " INTEGER, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(DESCRIPTION, task.getDescription());
        cv.put(YEAR, task.getDateY());
        cv.put(MONTH, task.getDateM());
        cv.put(DAY, task.getDateD());
        cv.put(HOUR, task.getTimeH());
        cv.put(MINUTE, task.getTimeM());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);
    }

    @SuppressLint("Range")
    public List<ToDoModel> getAllTasks(){
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cur.getInt(cur.getColumnIndex(ID)));
                    task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                    task.setDescription(cur.getString(cur.getColumnIndex(DESCRIPTION)));
                    task.setDateY(cur.getInt(cur.getColumnIndex(YEAR)));
                    task.setDateM(cur.getInt(cur.getColumnIndex(MONTH)));
                    task.setDateD(cur.getInt(cur.getColumnIndex(DAY)));
                    task.setTimeH(cur.getInt(cur.getColumnIndex(HOUR)));
                    task.setTimeM(cur.getInt(cur.getColumnIndex(MINUTE)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    taskList.add(task);
                } while(cur.moveToNext());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return taskList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        int rowsAffected = db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
        Log.d("DatabaseHandler", "Rows affected by updateStatus(): " + rowsAffected);
    }

    public void updateTask(int id, String task, String description, int dateY, int dateM, int dateD, int timeH, int timeM) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        cv.put(DESCRIPTION, description);
        cv.put(YEAR, dateY);
        cv.put(MONTH, dateM);
        cv.put(DAY, dateD);
        cv.put(HOUR, timeH);
        cv.put(MINUTE, timeM);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }

    @SuppressLint("Range")
    public List<ToDoModel> getIncompleteTodayTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // January is 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        try {
            cur = db.query(TODO_TABLE, null, YEAR + "=? AND " + MONTH + "=? AND " + DAY + "=? AND " + STATUS + "=?",
                    new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day), "0"}, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cur.getInt(cur.getColumnIndex(ID)));
                    task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                    task.setDescription(cur.getString(cur.getColumnIndex(DESCRIPTION)));
                    task.setDateY(cur.getInt(cur.getColumnIndex(YEAR)));
                    task.setDateM(cur.getInt(cur.getColumnIndex(MONTH)));
                    task.setDateD(cur.getInt(cur.getColumnIndex(DAY)));
                    task.setTimeH(cur.getInt(cur.getColumnIndex(HOUR)));
                    task.setTimeM(cur.getInt(cur.getColumnIndex(MINUTE)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    taskList.add(task);
                } while (cur.moveToNext());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return taskList;
    }

    @SuppressLint("Range")
    public List<ToDoModel> getIncompleteTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;

        try {
            cur = db.query(TODO_TABLE, null, STATUS + "=?",
                    new String[]{"0"}, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cur.getInt(cur.getColumnIndex(ID)));
                    task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                    task.setDescription(cur.getString(cur.getColumnIndex(DESCRIPTION)));
                    task.setDateY(cur.getInt(cur.getColumnIndex(YEAR)));
                    task.setDateM(cur.getInt(cur.getColumnIndex(MONTH)));
                    task.setDateD(cur.getInt(cur.getColumnIndex(DAY)));
                    task.setTimeH(cur.getInt(cur.getColumnIndex(HOUR)));
                    task.setTimeM(cur.getInt(cur.getColumnIndex(MINUTE)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    taskList.add(task);
                } while (cur.moveToNext());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return taskList;
    }

    @SuppressLint("Range")
    public List<ToDoModel> getCompletedTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;

        try {
            cur = db.query(TODO_TABLE, null, STATUS + "=?",
                    new String[]{"1"}, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cur.getInt(cur.getColumnIndex(ID)));
                    task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                    task.setDescription(cur.getString(cur.getColumnIndex(DESCRIPTION)));
                    task.setDateY(cur.getInt(cur.getColumnIndex(YEAR)));
                    task.setDateM(cur.getInt(cur.getColumnIndex(MONTH)));
                    task.setDateD(cur.getInt(cur.getColumnIndex(DAY)));
                    task.setTimeH(cur.getInt(cur.getColumnIndex(HOUR)));
                    task.setTimeM(cur.getInt(cur.getColumnIndex(MINUTE)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    taskList.add(task);
                } while (cur.moveToNext());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return taskList;
    }

}
