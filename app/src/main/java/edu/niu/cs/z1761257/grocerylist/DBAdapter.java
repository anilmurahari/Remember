package edu.niu.cs.z1761257.grocerylist;

/**
 * Created by Pravin on 5/5/16.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;


public class DBAdapter {

    //constants
    private static final String TAG="DBAdapter";

    private static final String KEY_ROWID = "_id",
            KEY_NAME = "name",
            KEY_STATUS = "status";

    private static final String[] ALL_KEYS = new String[]{KEY_ROWID,KEY_NAME,KEY_STATUS};

    public static final int COL_ROWID = 0,
            COL_NAME = 1,
            COL_STATUS = 2;

    private static final String DATABASE_NAME = "MyDB",
            DATABASE_TABLE = "grocTable";

    private static final int DATABASE_VERSION = 1;

    //query
    private static final String CREATE_SQL = "create table " + DATABASE_TABLE
            + "(" + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_NAME + " text not null, "
            + KEY_STATUS + " integer not null " + ");";

    private DBHelper myDBHelper;
    private SQLiteDatabase db;

    //create context
    public DBAdapter(Context context){
        myDBHelper = new DBHelper (context);
    }

    public DBAdapter open(){
        db = myDBHelper.getWritableDatabase();
        return this;
    }//end of open

    //close dbhelper
    public void close(){
        myDBHelper.close();
    }

    //function to insert into row
    public long insertRow( String name, Integer status ){
        ContentValues rowValues = new ContentValues();
        rowValues.put(KEY_NAME, name);
        rowValues.put(KEY_STATUS, status);

        return db.insert(DATABASE_TABLE, null, rowValues);

    }//end of insertRow


    //function to delete row
    public boolean deleteRow( long rowID ){
        String where = KEY_ROWID + "=" + rowID;

        return db.delete(DATABASE_TABLE,where, null)!= 0;
    }//end of deleteRow

    //function to delete all rows
    public void deleteAll(){
        Cursor cursor = getAllRows();
        long rowID = cursor.getColumnIndexOrThrow(KEY_ROWID);

        boolean result = cursor.moveToFirst();
        while(result){
            deleteRow(cursor.getInt((int)rowID));
            result = cursor.moveToNext();
        }
    }//end of deleteAll

    //function to update item
    public void updateItem(Grocery_Item item)
    {
        String str = "update " + DATABASE_TABLE + " set " + KEY_STATUS + "=" + item.getStatus() + " where " + KEY_ROWID + " = " + item.getId();
        Log.d(TAG,str);
        db.execSQL(str);
    }

    public Cursor getAllRows(){
        String where = null;
        Cursor cursor = db.query(true, DATABASE_TABLE, ALL_KEYS, where, null, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getRow(long rowID){
        String where = KEY_ROWID + "=" + rowID;
        Cursor cursor = db.query(true, DATABASE_TABLE, ALL_KEYS, where, null, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }//end of getRow

    //Inner Class
    private static class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from "+ oldVersion+ " to "+ newVersion
                    + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE );
            onCreate(db);
        }
    }//end of DBHelper


}//end of DBAdapter
