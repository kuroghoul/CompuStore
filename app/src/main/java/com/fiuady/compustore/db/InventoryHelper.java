package com.fiuady.compustore.db;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

/**
 * Created by Kuro on 02/04/2017.
 */

public final class InventoryHelper extends SQLiteOpenHelper {
    private static InventoryHelper mInstance=null;

    private static final String DATABASE_NAME = "inventory.db";
    private static final int SCHEMA_VERSION = 1;
    private Context context;

    public static InventoryHelper getInventoryHelper(Context ctx)
    {
        if (mInstance == null) {
            mInstance = new InventoryHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }


    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private InventoryHelper(Context context) {

        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
        this.context = context;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("PRAGMA foreign_keys = ON");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        applySQLFile(db, "schema.sql");
        applySQLFile(db, "initial-data.sql");
        backupDatabaseFile(context);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
    public void applySQLFile(SQLiteDatabase db, String filename)
    {
        BufferedReader br=null;
        try{

            InputStream is = context.getAssets().open(filename);
            br = new BufferedReader(new InputStreamReader(is));

            StringBuilder statement = new StringBuilder();
            for(String line; (line = br.readLine()) != null; ){

                line = line.trim();
                if(!TextUtils.isEmpty(line) && !line.startsWith("--")){
                    statement.append(line);
                }

                if(line.endsWith(";")){
                    db.execSQL(statement.toString());
                    statement.setLength(0);
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (br != null){
                try{
                    br.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
    }
    public static void backupDatabaseFile(Context context){
        try{
            File ext = Environment.getExternalStorageDirectory();
            if(ext.canWrite()){
                String currentDBPath = "/data/data/" + context.getPackageName()+"/databases/inventory.db";
                String backupDBPath = "inventory-bk.db";

                File currentDB = new File(currentDBPath);
                File backupDB = new File(ext, backupDBPath);
                if(currentDB.exists()){
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();

                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
