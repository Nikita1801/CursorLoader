package com.example.cursorloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    ListView lvData;
    DataBase db;
    SimpleCursorAdapter scAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBase(this);
        db.open();

        String[] from = new String[] {DataBase.COLUMN_IMG, DataBase.COLUMN_TXT};
        int[] to  = new int[] {R.id.ivImg, R.id.tvText};

        scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);

        registerForContextMenu(lvData);

        getSupportLoaderManager().initLoader(0, null, this);
    }
    public void onButtonClick(View view) {
        db.addRec("sometext" + (scAdapter.getCount() + 1), R.drawable.ic_launcher_background);
        getSupportLoaderManager().getLoader(0).forceLoad();
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()  == CM_DELETE_ID){
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            db.delRec(acmi.id);
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    static class MyCursorLoader extends CursorLoader{
        DataBase db;
        public MyCursorLoader(Context context, DataBase db) {
            super(context);
            this.db = db;
        }
        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllData();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cursor;
        }
    }
}