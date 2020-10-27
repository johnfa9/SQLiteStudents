package com.ga.sqlliteStudents.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.ga.sqlliteStudents.database.model.Student;

/**
 * SQLiteOpenHelper - This class perform CRUD operations (Create, Read, Update and Delete) on the database.
 * class takes care of opening the database if it exists, creating it if it does not, and upgrading it as necessary.
 * https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "students_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Student.CREATE_TABLE);  //SQL String to create table
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Student.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertStudent(String student, String grade) {    //****************** insertStudent(String student )
        /* ContentValues() is used to define the column name and its data to be stored.
        Here, we are just setting the note value only ignoring `id` and `timestamp`
        as these two will be inserted automatically.
        */

        /* Every time the database connection has to be closed once you are done with
        database access. Calling db.close() closes the connection.
         */

       // Once the note is inserted, the `id` of newly inserted note will be returned.

        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();  //imported from Android lib
        //is used to define the column name and its data to be stored
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Student.COLUMN_STUDENT, student);
        values.put(Student.COLUMN_GRADE, grade);    //*******************


        // insert row
        long id = db.insert(Student.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Student getStudent(long id) {
        // getStudent() takes already existed note `id` and fetches the note object.
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Student.TABLE_NAME,
                new String[]{Student.COLUMN_ID, Student.COLUMN_STUDENT, Student.COLUMN_GRADE, Student.COLUMN_TIMESTAMP},
                Student.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Student student = new Student(
                cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUDENT)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_GRADE)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return student;
    }

    public List<Student> getAllStudents() {
        //getAllNotes() fetches all the notes in descending order by timestamp.
        List<Student> students = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Student.TABLE_NAME + " ORDER BY " +
                Student.COLUMN_TIMESTAMP + " DESC";

        //String selectQuery = "SELECT id, student, grade, timestamp FROM students ORDER BY timestamp DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)));
                student.setStudent(cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUDENT)));
                student.setGrade(cursor.getString(cursor.getColumnIndex(Student.COLUMN_GRADE)));
                student.setTimestamp(cursor.getString(cursor.getColumnIndex(Student.COLUMN_TIMESTAMP)));

                students.add(student);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return students;
    }

    public int getStudentsCount() {
        //getNotesCount() returns the count of notes stored in database.
        String countQuery = "SELECT  * FROM " + Student.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;}

    public int updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Student.COLUMN_STUDENT, student.getStudent());
        values.put(Student.COLUMN_GRADE, student.getGrade());    //****************

        // updating row
        return db.update(Student.TABLE_NAME, values, Student.COLUMN_ID + " = ?",
                new String[]{String.valueOf(student.getId())});
    }

    public void deleteStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Student.TABLE_NAME, Student.COLUMN_ID + " = ?",
                new String[]{String.valueOf(student.getId())});
        db.close();
    }
}
