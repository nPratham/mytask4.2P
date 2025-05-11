package com.example.mytask42p;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;

    RecyclerView recyclerView;
    ArrayList<Usersitem> usersItemArrayList;
    UsersRecyclerAdapter adapter;

    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize the RecyclerView and DBHelper
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this); // Initialize DBHelper
        usersItemArrayList = new ArrayList<>();

        adapter = new UsersRecyclerAdapter(this, usersItemArrayList);
        recyclerView.setAdapter(adapter);

        // Load tasks from SQLite when activity starts
        loadTasksFromDatabase();

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(view -> {
            ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
            viewDialogAdd.showDialog(MainActivity.this);
        });
    }

    // Load tasks from database and populate RecyclerView
    private void loadTasksFromDatabase() {
        Cursor cursor = dbHelper.getdata();
        if (cursor != null) {
            usersItemArrayList.clear();
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));

                usersItemArrayList.add(new Usersitem(id, title, description, dueDate));
            }
            adapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    public class ViewDialogAdd {
        public void showDialog(Context context) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_user);

            // Find views
            EditText editTitle = dialog.findViewById(R.id.textTitle);
            EditText editDescription = dialog.findViewById(R.id.textDescription);
            Button buttonDueDate = dialog.findViewById(R.id.textDueDate);
            Button buttonAdd = dialog.findViewById(R.id.buttonAdd);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonAdd.setText("ADD");

            buttonDueDate.setOnClickListener(view -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context,
                        (view1, year1, monthOfYear, dayOfMonth) -> {
                            String selectedDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                            buttonDueDate.setText(selectedDate);
                        },
                        year, month, day);
                datePickerDialog.show();
            });

            buttonCancel.setOnClickListener(view -> dialog.dismiss());

            buttonAdd.setOnClickListener(view -> {
                String id = "task" + new Date().getTime();
                String title = editTitle.getText().toString().trim();
                String desc = editDescription.getText().toString().trim();
                String dueDate = buttonDueDate.getText().toString().trim();

                // Check if all fields are filled
                if (title.isEmpty() || desc.isEmpty() || dueDate.isEmpty() || dueDate.equals("Select Due Date")) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Insert new task into SQLite
                    boolean isInserted = dbHelper.insertuserdata(id, title, desc, dueDate);
                    if (isInserted) {
                        // Add new task to the list
                        usersItemArrayList.add(new Usersitem(id, title, desc, dueDate));
                        adapter.notifyItemInserted(usersItemArrayList.size() - 1);
                        Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error adding task", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
