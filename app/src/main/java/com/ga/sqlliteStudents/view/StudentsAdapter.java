package com.ga.sqlliteStudents.view;

import android.content.Context;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ga.sqlliteStudents.database.model.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ga.sqlliteStudents.R;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.MyViewHolder> {

    private Context context;
    private List<Student> studentsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView student;
        public TextView grade; //******************


        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            student = view.findViewById(R.id.student);   // id is from students_list_row.xml
            grade = view.findViewById(R.id.grade);    //*********************************
            dot = view.findViewById(R.id.dot);     //id is from students_list_row.xml
            timestamp = view.findViewById(R.id.timestamp);
        }
    }


    public StudentsAdapter(Context context, List<Student> studentsList) {  //receives student list from MainActiviy.java
        this.context = context;
        this.studentsList = studentsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.students_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Student student = studentsList.get(position);

        holder.student.setText(student.getStudent());

        holder.grade.setText(student.getGrade());  //***************************

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(student.getTimestamp()));
    }

    @Override
    public int getItemCount() {

        return studentsList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}