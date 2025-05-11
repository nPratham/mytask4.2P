package com.example.mytask42p;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<Usersitem> usersItemArrayList;
    DBHelper dbHelper;

    public UsersRecyclerAdapter(Context context, ArrayList<Usersitem> usersItemArrayList) {
        this.context = context;
        this.usersItemArrayList = usersItemArrayList;
        dbHelper = new DBHelper(context); // Initialize DBHelper
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usersitem task = usersItemArrayList.get(position);

        holder.textTittle.setText("Task Title: " + task.getTaskTittle());
        holder.textDescription.setText("Description: " + task.getTaskDescription());
        holder.textDueDate.setText("Due Date: " + task.getTaskDueDate());

        holder.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("UsersRecyclerAdapter", "Update Button Clicked");
                ViewDialogUpdate viewDialogUpdate = new ViewDialogUpdate();
                viewDialogUpdate.showDialog(context, task.getUserID(), task.getTaskTittle(), task.getTaskDescription(), task.getTaskDueDate(), position);
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("UsersRecyclerAdapter", "Delete Button Clicked");
                ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
                viewDialogConfirmDelete.showDialog(context, task.getUserID(), position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return usersItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textTittle;
        TextView textDescription;
        TextView textDueDate;

        Button buttonDelete;
        Button buttonUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textTittle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            textDueDate = itemView.findViewById(R.id.textDueDate);

            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
        }
    }

    public class ViewDialogUpdate {
        public void showDialog(Context context, String id, String tittle, String description, String dueDate, int position) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_user);

            EditText textTittle = dialog.findViewById(R.id.textTitle);
            EditText textDescription = dialog.findViewById(R.id.textDescription);
            TextView textDueDate = dialog.findViewById(R.id.textDueDate);

            textTittle.setText(tittle);
            textDescription.setText(description);
            textDueDate.setText(dueDate);

            textDueDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the current date
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    // Open DatePickerDialog
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    // Format the selected date as "yyyy-MM-dd"
                                    String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                    textDueDate.setText(selectedDate); // Display the selected date
                                }
                            },
                            year, month, day);
                    datePickerDialog.show();
                }
            });

            Button buttonUpdate = dialog.findViewById(R.id.buttonAdd);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonUpdate.setText("UPDATE");

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newTittle = textTittle.getText().toString();
                    String newDescription = textDescription.getText().toString();
                    String newDueDate = textDueDate.getText().toString();

                    if (newTittle.isEmpty() || newDescription.isEmpty() || newDueDate.isEmpty()) {
                        Toast.makeText(context, "Please Enter All data...", Toast.LENGTH_SHORT).show();
                    } else {
                        // Update the task in the local list
                        Usersitem updatedTask = usersItemArrayList.get(position);
                        updatedTask.setTaskTittle(newTittle);
                        updatedTask.setTaskDescription(newDescription);
                        updatedTask.setTaskDueDate(newDueDate);

                        // Update in the SQLite database
                        boolean isUpdated = dbHelper.updateuserdata(updatedTask.getUserID(), newTittle, newDescription, newDueDate);
                        if (isUpdated) {
                            notifyItemChanged(position);
                            Toast.makeText(context, "Task Updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to update task in database.", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    public class ViewDialogConfirmDelete {
        public void showDialog(Context context, String id, int position) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.view_dialog_confirm_delete);

            Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Delete the task from the database
                    boolean isDeleted = dbHelper.deletedata(id);
                    if (isDeleted) {
                        // Remove the task from the list
                        usersItemArrayList.remove(position);
                        // Notify the adapter to refresh the view
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Task Deleted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to delete task from database.", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}
