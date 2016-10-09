package me.hyman.webviewmonitor.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.hyman.webviewmonitor.util.Logger;

/**
 * Created by chengbin on 2016/10/9.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_name = "events";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_event = "event";
    public static final String COLUMN_type = "type";
    public static final String COLUMN_timestamp = "timestamp";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "collector.db";

    public static final String TABLE_CREATE = "create table events(_id integer primary key autoincrement, event text not null, type text not null, timestamp long not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Logger.w(MySQLiteHelper.class.getName() + ": Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_name);
        onCreate(db);
    }
}
