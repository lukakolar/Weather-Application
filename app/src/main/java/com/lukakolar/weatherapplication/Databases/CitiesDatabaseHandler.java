package com.lukakolar.weatherapplication.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.lukakolar.weatherapplication.Constants;
import com.lukakolar.weatherapplication.Entity.CityWeatherObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CitiesDatabaseHandler extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private Context context;
    private DatabaseCallbacksInterface databaseCallbacksInterface;

    public CitiesDatabaseHandler(Context context, DatabaseCallbacksInterface databaseCallbacksInterface) {
        super(context, Constants.CITIES_DATABASE_NAME, null, Constants.CITIES_DATABASE_VERSION);
        this.context = context;
        this.databaseCallbacksInterface = databaseCallbacksInterface;
        Log.d("CitiesDatabaseHandler", "consctructor");
        CreateDatabase worker = new CreateDatabase();
        worker.execute();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public synchronized void close() {
        if (database != null)
            database.close();
        super.close();
    }

    public boolean isDatabaseOpen() {
        return database != null && database.isOpen();
    }

    private void createDataBase() {
        Log.d("CitiesDatabaseHandler", "createDatabase");
        boolean databaseExists = checkDataBaseExists();
        if(!databaseExists){
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBaseExists(){
        File databaseFile = new File(Constants.CITIES_DATABASE_FULL_PATH);
        return databaseFile.exists();
    }

    private void copyDataBase() throws IOException {
        Log.d("CitiesDatabaseHandler", "copyDatabase");
        InputStream inputStream = context.getApplicationContext().getAssets().open(Constants.CITIES_DATABASE_NAME);

        File parentDirectory = new File(Constants.CITIES_DATABASE_PATH);
        parentDirectory.mkdirs();
        File outputFile = new File(parentDirectory, Constants.CITIES_DATABASE_NAME);
        OutputStream outputStream = new FileOutputStream(outputFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer))>0){
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        Log.d("CitiesDatabaseHandler", "endOfCopyDatabase");
    }

    private void openDataBase()  {
        if (database == null || !isDatabaseOpen()) {
            Log.d("database", "OPENING");
            database = getWritableDatabase();
        } else{
            Log.d("database", "OPENED");
        }
    }

    public void getSuggestions(String searchTerm) {
        GetCities worker = new GetCities();
        worker.execute(searchTerm);
    }

    private class CreateDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("CitiesDatabaseHandler", "doInBackground");
            createDataBase();
            openDataBase();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            databaseCallbacksInterface.onDatabaseOpened();
        }
    }

    private class GetCities extends AsyncTask<String, Void, List<CityWeatherObject>> {
        private Cursor cursor;

        @Override
        protected List<CityWeatherObject> doInBackground(String... searchTerm) {
            List<CityWeatherObject> recordsList = new ArrayList<>();

            cursor = database.query(true, Constants.CITIES_DATABASE_TABLE_NAME, null,
                    Constants.CITIES_DATABASE_FIELD_NAME + " LIKE ?",
                    new String[]{searchTerm[0] + "%"}, null, null,
                    Constants.CITIES_DATABASE_FIELD_NAME, "0,5");

            if (cursor.moveToFirst()) {
                do {
                    Integer id = cursor.getInt(cursor.getColumnIndex(Constants.CITIES_DATABASE_FIELD_ID));
                    String objectName = cursor.getString(cursor.getColumnIndex(Constants.CITIES_DATABASE_FIELD_NAME));
                    CityWeatherObject item = new CityWeatherObject(id, objectName, null, null, null);
                    recordsList.add(item);

                } while (cursor.moveToNext());
            }
            return recordsList;
        }

        @Override
        protected void onPostExecute(List<CityWeatherObject> result) {
            cursor.close();
            databaseCallbacksInterface.onEntriesRetrieved(result);
        }
    }
}