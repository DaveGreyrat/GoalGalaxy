package com.example.goalgalaxy.Utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.goalgalaxy.MainActivity;
import com.example.goalgalaxy.Model.ToDoModel;
import com.example.goalgalaxy.NotificationReceiver;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    Context mContext;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("tasks");
        }
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
        if (db == null || !db.isOpen()) {
            db = this.getWritableDatabase();
        }
    }

    private void closeDatabase() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }


    public void insertTask(ToDoModel task, boolean syncWithFirebase, boolean isFromFirebase) {
        openDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(DESCRIPTION, task.getDescription());
        cv.put(YEAR, task.getYear());
        cv.put(MONTH, task.getMonth());
        cv.put(DAY, task.getDay());
        cv.put(HOUR, task.getHour());
        cv.put(MINUTE, task.getMinute());
        cv.put(STATUS, task.getStatus());

        // Проверяем, существует ли уже задача в локальной базе данных
        boolean taskExists = doesTaskExist(task.getId());

        if (!taskExists) {
            long id = db.insert(TODO_TABLE, null, cv);
            task.setId((int) id);
            setNotification(task);
        }

        if (syncWithFirebase && !isFromFirebase) {
            syncTaskToFirebase(task);
        }

        closeDatabase();
    }

    public void insertTask(ToDoModel task, boolean syncWithFirebase) {
        insertTask(task, syncWithFirebase, false);
    }

    public void insertTask(ToDoModel task) {
        insertTask(task, true);
    }

    private boolean doesTaskExist(int id) {
        Cursor cur = null;
        boolean exists = false;
        try {
            cur = db.query(TODO_TABLE, null, ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
            exists = (cur != null && cur.getCount() > 0);
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return exists;
    }


    @SuppressLint("Range")
    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        openDatabase();
        Cursor cur = null;
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cur.getInt(cur.getColumnIndex(ID)));
                    task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                    task.setDescription(cur.getString(cur.getColumnIndex(DESCRIPTION)));
                    task.setYear(cur.getInt(cur.getColumnIndex(YEAR)));
                    task.setMonth(cur.getInt(cur.getColumnIndex(MONTH)));
                    task.setDay(cur.getInt(cur.getColumnIndex(DAY)));
                    task.setHour(cur.getInt(cur.getColumnIndex(HOUR)));
                    task.setMinute(cur.getInt(cur.getColumnIndex(MINUTE)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    taskList.add(task);
                } while (cur.moveToNext());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
            closeDatabase();
        }
        return taskList;
    }

    public void updateStatus(int id, int status) {
        openDatabase();
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        int rowsAffected = db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
        if (rowsAffected > 0) {
            syncTaskStatusToFirebase(id, status); // Синхронизируем обновление статуса с Firebase
        }
        Log.d("DatabaseHandler", "Rows affected by updateStatus(): " + rowsAffected);
        closeDatabase();
    }


    public void updateTask(int id, String task, String description, int dateY, int dateM, int dateD, int timeH, int timeM) {
        openDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        cv.put(DESCRIPTION, description);
        cv.put(YEAR, dateY);
        cv.put(MONTH, dateM);
        cv.put(DAY, dateD);
        cv.put(HOUR, timeH);
        cv.put(MINUTE, timeM);
        int rowsAffected = db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
        if (rowsAffected > 0) {
            syncTaskToFirebase(new ToDoModel(id, task, description, dateY, dateM, dateD, timeH, timeM, 0)); // Sync the task update to Firebase
        }
        closeDatabase();

        ToDoModel updatedTask = getTaskById(id);
        if (updatedTask != null) {
            syncTaskToFirebase(updatedTask);
        }
    }

    public void deleteTask(int id) {
        openDatabase();
        int rowsAffected = db.delete(TODO_TABLE, ID + "= ?", new String[]{String.valueOf(id)});
        if (rowsAffected > 0) {
            deleteTaskFromFirebase(id);
            cancelNotification(id);
        }
        Log.d("DatabaseHandler", "Rows affected by deleteTask(): " + rowsAffected);
        closeDatabase();
    }

    private void syncTaskToFirebase(ToDoModel task) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("tasks");
            userTasksRef.child(String.valueOf(task.getId())).setValue(task);
        }
    }

    private void syncTaskStatusToFirebase(int id, int status) {
        if (currentUser != null) {
            // Получаем задачу из локальной базы данных по id
            ToDoModel task = getTaskById(id);
            if (task != null) {
                // Обновляем статус задачи
                task.setStatus(status);
                // Отправляем обновленную задачу на сервер
                databaseReference.child(String.valueOf(id)).setValue(task)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Log.d("DatabaseHandler", "Task status successfully updated in Firebase");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("DatabaseHandler", "Error updating task status in Firebase", e);
                            }
                        });
            }
        }
    }

    // Новый метод для получения задачи по id из локальной базы данных
    @SuppressLint("Range")
    private ToDoModel getTaskById(int id) {
        openDatabase();
        ToDoModel task = null;
        Cursor cur = null;
        try {
            cur = db.query(TODO_TABLE, null, ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                task = new ToDoModel();
                task.setId(cur.getInt(cur.getColumnIndex(ID)));
                task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                task.setDescription(cur.getString(cur.getColumnIndex(DESCRIPTION)));
                task.setYear(cur.getInt(cur.getColumnIndex(YEAR)));
                task.setMonth(cur.getInt(cur.getColumnIndex(MONTH)));
                task.setDay(cur.getInt(cur.getColumnIndex(DAY)));
                task.setHour(cur.getInt(cur.getColumnIndex(HOUR)));
                task.setMinute(cur.getInt(cur.getColumnIndex(MINUTE)));
                task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
            closeDatabase();
        }
        return task;
    }


    private void deleteTaskFromFirebase(int id) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("tasks");
            userTasksRef.child(String.valueOf(id)).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("DatabaseHandler", "Task successfully deleted from Firebase");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("DatabaseHandler", "Error deleting task from Firebase", e);
                        }
                    });
        }
    }

    public void syncFromFirebase() {
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("tasks");

        // Загружаем данные из Firebase и синхронизируем их с локальной базой данных
        userTasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clearLocalDatabase(); // Очищаем локальную базу данных перед синхронизацией
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ToDoModel task = snapshot.getValue(ToDoModel.class);
                    if (task != null) {
                        insertTask(task, false); // Вставляем задачи без синхронизации с Firebase
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DatabaseHandler", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


    @SuppressLint("Range")
    public List<ToDoModel> getIncompleteTodayTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        openDatabase();
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
                    task.setYear(cur.getInt(cur.getColumnIndex(YEAR)));
                    task.setMonth(cur.getInt(cur.getColumnIndex(MONTH)));
                    task.setDay(cur.getInt(cur.getColumnIndex(DAY)));
                    task.setHour(cur.getInt(cur.getColumnIndex(HOUR)));
                    task.setMinute(cur.getInt(cur.getColumnIndex(MINUTE)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    taskList.add(task);
                } while (cur.moveToNext());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
            closeDatabase();
        }
        return taskList;
    }

    @SuppressLint("Range")
    public List<ToDoModel> getIncompleteTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        openDatabase();
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
                    task.setYear(cur.getInt(cur.getColumnIndex(YEAR)));
                    task.setMonth(cur.getInt(cur.getColumnIndex(MONTH)));
                    task.setDay(cur.getInt(cur.getColumnIndex(DAY)));
                    task.setHour(cur.getInt(cur.getColumnIndex(HOUR)));
                    task.setMinute(cur.getInt(cur.getColumnIndex(MINUTE)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    taskList.add(task);
                } while (cur.moveToNext());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
            closeDatabase();
        }
        return taskList;
    }

    @SuppressLint("Range")
    public List<ToDoModel> getCompletedTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        openDatabase();
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
                    task.setYear(cur.getInt(cur.getColumnIndex(YEAR)));
                    task.setMonth(cur.getInt(cur.getColumnIndex(MONTH)));
                    task.setDay(cur.getInt(cur.getColumnIndex(DAY)));
                    task.setHour(cur.getInt(cur.getColumnIndex(HOUR)));
                    task.setMinute(cur.getInt(cur.getColumnIndex(MINUTE)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    taskList.add(task);
                } while (cur.moveToNext());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
            closeDatabase();
        }
        return taskList;
    }

    public void clearLocalDatabase() {
        openDatabase();
        db.delete(TODO_TABLE, null, null);
        closeDatabase();
    }

    public void setupFirebaseListener(MainActivity activity) {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                ToDoModel task = snapshot.getValue(ToDoModel.class);
                if (task != null) {
                    insertTask(task, false, true);
                    activity.getAdapter().addTask(task);
                    sendUpdateBroadcast(mContext);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                ToDoModel task = snapshot.getValue(ToDoModel.class);
                if (task != null) {
                    updateTaskInLocalDatabase(task);
                    activity.getAdapter().updateTask(task);
                    sendUpdateBroadcast(mContext);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                ToDoModel task = snapshot.getValue(ToDoModel.class);
                if (task != null) {
                    deleteTaskFromLocalDatabase(task.getId());
                    activity.getAdapter().removeTask(task.getId());
                    sendUpdateBroadcast(mContext);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Не требуется ничего делать для перемещения
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DatabaseHandler", "Firebase listener cancelled", error.toException());
            }
        });
    }


    private void updateTaskInLocalDatabase(ToDoModel task) {
        openDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(DESCRIPTION, task.getDescription());
        cv.put(YEAR, task.getYear());
        cv.put(MONTH, task.getMonth());
        cv.put(DAY, task.getDay());
        cv.put(HOUR, task.getHour());
        cv.put(MINUTE, task.getMinute());
        cv.put(STATUS, task.getStatus());
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(task.getId())});
        closeDatabase();
    }

    private void deleteTaskFromLocalDatabase(int id) {
        openDatabase();
        db.delete(TODO_TABLE, ID + "= ?", new String[]{String.valueOf(id)});
        closeDatabase();
    }

    private void sendUpdateBroadcast(Context context) {
        Intent intent = new Intent("UPDATE_DATA");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    public void setNotification(ToDoModel task) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, task.getYear());
        calendar.set(Calendar.MONTH, task.getMonth() - 1); // Январь - 0
        calendar.set(Calendar.DAY_OF_MONTH, task.getDay());
        calendar.set(Calendar.HOUR_OF_DAY, task.getHour());
        calendar.set(Calendar.MINUTE, task.getMinute());
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(mContext,NotificationReceiver.class);
        intent.putExtra("task", task.getTask());
        intent.putExtra("description", task.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void cancelNotification(int taskId) {
        Intent intent = new Intent(mContext, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }




}
