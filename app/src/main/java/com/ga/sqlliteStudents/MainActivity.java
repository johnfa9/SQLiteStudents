package com.ga.sqlliteStudents;

import android.content.DialogInterface;
import android.os.Bundle;

import com.ga.sqlliteStudents.database.DatabaseHelper;
import com.ga.sqlliteStudents.database.model.Student;
import com.ga.sqlliteStudents.utils.MyDividerItemDecoration;
import com.ga.sqlliteStudents.utils.RecyclerTouchListener;
import com.ga.sqlliteStudents.view.StudentsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private StudentsAdapter mAdapter;
    private List<Student> studentsList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noStudentsView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar); //toolbar defined in activity_main
        setSupportActionBar(toolbar);  //displays title of activity

        //add 10/18
        coordinatorLayout = findViewById(R.id.coordinator_layout);  //id of Layout of activity_main.xml
        recyclerView = findViewById(R.id.recycler_view);  //id of recycler view in content_main.xml
        noStudentsView = findViewById(R.id.empty_students_view);  //id of textview in content_main.xml
        db = new DatabaseHelper(this);
        studentsList.addAll(db.getAllStudents());  //runs query to receive all notes from database


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                showStudentDialog(false, null, -1);  //show new note dialog
            }
        });

        //set adapter for RecyclerView
        mAdapter = new StudentsAdapter(this, studentsList);  //See StudentsAdapter class
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);  //references recylerView in content_main.xml
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, 16));  //see MyDividerItemDecoration class
        recyclerView.setAdapter(mAdapter);
        toggleEmptyStudents();

        //added 10/18
        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {

                showActionsDialog(position);
            }
        }));

    }

    //added 10/18
    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createStudent(String student, String grade) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertStudent(student, grade);

        // get the newly inserted note from db
        Student n = db.getStudent(id);

        if (n != null) {
            // adding new note to array list at 0 position
            studentsList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyStudents();
        }
    }

    /** added 10/18
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateStudent(String student, String grade, int position) {
        Student n = studentsList.get(position);
        // updating student text
        n.setStudent(student);
        n.setGrade((grade));


        // updating note in db
        db.updateStudent(n);   //update student in DatabaseHelper

        // refreshing the list
        studentsList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyStudents();
    }

    /** added 10/18
     * Deleting student from SQLite and removing the
     * item from the list by its position
     */
    private void deleteStudent(int position) {
        // deleting the student from db
        db.deleteStudent(studentsList.get(position));

        // removing the note from the list
        studentsList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyStudents();
    }

    /** added 10/18
     * Opens dialog with Edit - Delete options for an existing note when clicked
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        // executes when note is long clicked

        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};  //shows in dialog box

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");  //edit or delete existing note
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // edit existing note, see method below
                    showStudentDialog(true, studentsList.get(position), position);
                } else {
                    deleteStudent(position);  //delete an existing note
                }
            }
        });
        builder.show();  //show this dialog to edit or delete a note
    }

    /** added 10/18
     * Shows alert dialog with EditText options to enter new note / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showStudentDialog(final boolean shouldUpdate, final Student student,
                                   final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.student_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputStudent = view.findViewById(R.id.student);
        final Spinner inputGrade = view.findViewById(R.id.grade);


        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_student_title) : getString(R.string.lbl_edit_student_title));

        if (shouldUpdate && student != null) {
            inputStudent.setText(student.getStudent());
            int val = 0;
            String StudentGradeString;
            StudentGradeString = student.getGrade();
            val = getClassInt(StudentGradeString);  //gets position of spinner as number

            inputGrade.setSelection(val);  //sets the value of the spinner

        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save",
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputStudent.getText().toString())) {
                    Toast.makeText(MainActivity.this,
                            "Enter student!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating student
                if (shouldUpdate && student != null) {
                    // update note by it's id
                    updateStudent(inputStudent.getText().toString(),
                            inputGrade.getSelectedItem().toString(),position);
                } else {
                    // create new student, text of spinner is retrieved
                    createStudent(inputStudent.getText().toString(),
                            inputGrade.getSelectedItem().toString());
                }
            }
        });
    }
    //added 10/18
    private void toggleEmptyStudents() {
        // you can check studentsList.size() > 0

        if (db.getStudentsCount() > 0) {
            noStudentsView.setVisibility(View.GONE);
        } else {
            noStudentsView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //runs automatically

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //runs if click ActionBar on top

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public int getClassInt(String StudentGradeString){
        if (StudentGradeString.equals("Freshman")) {
            return 0;
        }
        else if (StudentGradeString.equals("Sophomore")) {
            return 1;
        }
        else if (StudentGradeString.equals("Junior")) {
            return 2;
        }
        else if (StudentGradeString.equals("Senior")) {
            return 3;
        }
        return 0;
    }

}