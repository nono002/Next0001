package com.example.next0001;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ListDocsActivity extends AppCompatActivity {

    ListView userList;
    TextView header;
    FeedReaderDbHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_docs);

        header = findViewById(R.id.header);
        userList = findViewById(R.id.list);

        databaseHelper = new FeedReaderDbHelper(this); //getApplicationContext());

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DocActivity.class);
                intent.putExtra("id", id);
                Toast.makeText(ListDocsActivity.this, String.valueOf(id), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора
        userCursor =  db.rawQuery("select " + FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + ", " + FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_NUM + ", " + FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_NAME + " || " + "' [ '" + " || " + FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_SUM  + " || " + "' грн. ]'" + " as Pole0 from "+ FeedReaderDbHelper.FeedDocs.TABLE_DOC, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_NUM, "Pole0"};
        // создаем адаптер, передаем в него курсор
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        header.setText("Всіх заявок: " +  userCursor.getCount());
        userList.setAdapter(userAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                super.finish();
                //this.finish();
                //closeActivity();
                break;
            default:
                Toast.makeText(this, "itemId: " + String.valueOf(item.getTitle()), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}