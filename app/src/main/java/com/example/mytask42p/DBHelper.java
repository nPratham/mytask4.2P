package com.example.mytask42p;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "TaskManager.db";

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("CREATE TABLE tasks(id TEXT PRIMARY KEY, title TEXT, description TEXT, dueDate TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        MyDB.execSQL("DROP TABLE IF EXISTS tasks");
    }

    public Boolean insertuserdata(String id, String title, String description, String dueDate) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("dueDate", dueDate);

        long result = MyDB.insert("tasks", null, contentValues);
        return result != -1;
    }

    public Boolean updateuserdata(String id, String title, String description, String dueDate) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("dueDate", dueDate);

        Cursor cursor = MyDB.rawQuery("SELECT * FROM tasks WHERE id = ?", new String[]{id});
        if (cursor.getCount() > 0) {
            long result = MyDB.update("tasks", contentValues, "id=?", new String[]{id});
            return result != -1;
        } else {
            return false;
        }
    }

    public Boolean deletedata(String id) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM tasks WHERE id = ?", new String[]{id});
        if (cursor.getCount() > 0) {
            long result = MyDB.delete("tasks", "id=?", new String[]{id});
            return result != -1;
        } else {
            return false;
        }
    }

    public Cursor getdata() {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        return MyDB.rawQuery("SELECT * FROM tasks", null);
    }
}
