package com.ga.sqlliteStudents.database.model;

public class Student {
    //* In this class we define the SQLite table name, column names and create table SQL query *//
    //* along with getter / setter methods. *//


    public static final String TABLE_NAME = "students";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STUDENT = "student";  // used in CREATE TABLE
    public static final String COLUMN_GRADE = "grade";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String student;
    private String grade;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_STUDENT + " TEXT,"
                    + COLUMN_GRADE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Student() {
    }

    public Student(int id, String student, String grade, String timestamp) {
        this.id = id;
        this.student = student;
        this.grade = grade;
        this.timestamp = timestamp;
    }

    public int getId() {

        return id;
    }

    public String getStudent() {

        return student;
    }

    public void setStudent(String student) {

        this.student = student;
    }

    public String getGrade() {

        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}