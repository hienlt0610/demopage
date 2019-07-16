package dev.hienlt.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private File dbFolderPath;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, "demo", null, 1);
        this.context = context;
        dbFolderPath = new File("/data/data/" + context.getPackageName() + "/databases");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NEWS = "CREATE TABLE page (id VARCHAR (255) PRIMARY KEY, name VARCHAR, status BOOLEAN DEFAULT (1), priority INTEGER DEFAULT (0))";
        db.execSQL(CREATE_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // drop tables if exist
        db.execSQL("DROP TABLE IF EXISTS demo");
        // recreate tables
        onCreate(db);
    }

    public SQLiteDatabase openDatabase() {
        File dbPath = new File(dbFolderPath, "demo.db");
        if (!dbPath.exists()) {
            try {
                copyDatabase(context, dbPath);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
        return SQLiteDatabase.openDatabase(dbPath.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    private void copyDatabase(Context context, File dbFile) throws IOException {
        if (!dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }
        InputStream is = context.getAssets().open("demo.db");
        OutputStream os = new FileOutputStream(dbFile);

        byte[] buffer = new byte[1024];
        while (is.read(buffer) > 0) {
            os.write(buffer);
        }
        os.flush();
        os.close();
        is.close();
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM page ORDER BY priority DESC", null);
        try {
            while (cursor.moveToNext()) {
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                item.setName(cursor.getString(cursor.getColumnIndex("name")));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                int opened = cursor.getInt(cursor.getColumnIndex("opened"));
                item.setStatus(status == 1);
                item.setOpened(opened == 1);
                item.setPriority(cursor.getInt(cursor.getColumnIndex("priority")));
                items.add(item);
            }
        } finally {
            cursor.close();
        }
        db.close();
        return items;
    }

    public Item getNextItem(int currentItemId) {
        SQLiteDatabase db = openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM page WHERE priority < (SELECT priority FROM page WHERE id = " + currentItemId + ") AND status = 1 ORDER BY priority DESC LIMIT 1", null);
        try {
            if (cursor.moveToNext()) {
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                item.setName(cursor.getString(cursor.getColumnIndex("name")));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                item.setStatus(status == 1);
                item.setPriority(cursor.getInt(cursor.getColumnIndex("priority")));
                return item;
            }
        } finally {
            cursor.close();
            db.close();
        }
        return null;
    }

    public Item getItem(int itemId) {
        SQLiteDatabase db = openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM page WHERE id = " + itemId, null);
        try {
            if (cursor.moveToNext()) {
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                item.setName(cursor.getString(cursor.getColumnIndex("name")));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                int opened = cursor.getInt(cursor.getColumnIndex("opened"));
                item.setStatus(status == 1);
                item.setOpened(opened == 1);
                item.setPriority(cursor.getInt(cursor.getColumnIndex("priority")));
                return item;
            }
        } finally {
            cursor.close();
            db.close();
        }
        return null;
    }

    public boolean openItem(int itemId) {
        SQLiteDatabase db = openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("opened", 1);
        int count = db.update("page", contentValues, "id=" + itemId, null);
        return count > 0;
    }
}
