package com.example.mytask42p;

public class Usersitem {

    String userID;
    String taskTittle;
    String taskDescription;
    String taskDueDate;

    public Usersitem() {
    }

    public Usersitem(String userID, String taskTittle, String taskDescription, String taskDueDate) {
        this.userID = userID;
        this.taskTittle = taskTittle;
        this.taskDescription= taskDescription;
        this.taskDueDate = taskDueDate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTaskTittle() {
        return taskTittle;
    }

    public void setTaskTittle(String taskTittle) {
        this.taskTittle= taskTittle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(String taskDueDate) {
        this.taskDueDate = taskDueDate;
    }
}
