package com.example.goalgalaxy.Model;

import java.util.Date;

public class ToDoModel {
    private int id, status, dateY, dateM, dateD, timeH, timeM;
    private String task, description;
    private boolean isReminder;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getDateY() {
        return dateY;
    }

    public void setDateY(int dateY) {
        this.dateY = dateY;
    }


    public int getDateM() {
        return dateM;
    }

    public void setDateM(int dateM) {
        this.dateM = dateM;
    }


    public int getDateD() {
        return dateD;
    }

    public void setDateD(int dateD) {
        this.dateD = dateD;
    }


    public int getTimeH() {
        return timeH;
    }

    public void setTimeH(int timeH) {
        this.timeH = timeH;
    }


    public int getTimeM() {
        return timeM;
    }

    public void setTimeM(int timeM) {
        this.timeM = timeM;
    }


    public boolean isReminder() {
        return isReminder;
    }

    public void setReminder(boolean reminder) {
        isReminder = reminder;
    }
}
