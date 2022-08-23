package com.example.next0001;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class DocActivity extends AppCompatActivity {

    TabHost tabHost;
    long docId = 0;
    EditText docNumber;
    EditText docDate;
    EditText docClient;
    EditText docCloud;
    EditText docComment;
    EditText docClientNum;
    EditText docClientDate;

    FeedReaderDbHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);

        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();
        setupTab(getString(R.string.doc_main), R.id.doc_main);
        setupTab(getString(R.string.doc_items), R.id.doc_list_goods);
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.black));
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            docId = extras.getLong("id");
            Toast.makeText(this, "Doc activity " + String.valueOf(docId), Toast.LENGTH_SHORT).show();
        }

        docNumber = this.findViewById(R.id.editTextDocNumber); //getView().findViewById(R.id.editTextDocNumber);
        docDate = this.findViewById(R.id.editTextDocDate);
        docClient = this.findViewById(R.id.editTextDocClientName);
        docCloud = this.findViewById(R.id.editTextDocCloud);
        docComment = this.findViewById(R.id.editTextDocComments);
        docClientNum = this.findViewById(R.id.editTextDocClientNum);
        docClientDate = this.findViewById(R.id.editTextDocClientDate);

        //SQLiteOpenHelper databaseHelper;
        //FeedReaderDbHelper db;

        databaseHelper = new FeedReaderDbHelper(this);
        db = databaseHelper.getReadableDatabase();
        // если 0, то добавление
        if (docId > 0) {
            // получаем элемент по id из бд
            userCursor = db.rawQuery("select * from " + FeedReaderDbHelper.FeedDocs.TABLE_DOC + " where " +
                    FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
            userCursor.moveToFirst();

            int colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_NUMBER);
            docNumber.setText(userCursor.getString(colIndex));
            colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DATE);
            docDate.setText(userCursor.getString(colIndex));
            colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_NAME);
            docClient.setText(userCursor.getString(colIndex));
            colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLOUD);
            docCloud.setText(userCursor.getString(colIndex));
            colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_COMMENT);
            docComment.setText(userCursor.getString(colIndex));
            colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_NUM);
            docClientNum.setText(userCursor.getString(colIndex));
            colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_DATE);
            docClientDate.setText(userCursor.getString(colIndex));

            userCursor.close();
        } else {
            // скрываем кнопку удаления
            //delButton.setVisibility(View.GONE);
        }

        // TABLE HEADER OUT
        TableLayout tblLayout = null;

        tblLayout = (TableLayout) findViewById(R.id.doc_table_list_goods);

         /*   TableRow tableRow = new TableRow(this);

            TextView textView1 = new TextView(this);
            TextView textView2 = new TextView(this);
            TextView textView3 = new TextView(this);
            TextView textView4 = new TextView(this);

            textView1.setText("№");
            tableRow.addView(textView1, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            textView2.setText("Номенклатура");
            tableRow.addView(textView2, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 22));
            textView3.setText("Замовлено");
            tableRow.addView(textView3, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2));
            textView4.setText("Фактично");
            tableRow.addView(textView4, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2));

        tblLayout.addView(tableRow);
*/
            // DATES OUT IN TABLE

        if (docId > 0) {
            // получаем элемент по id из бд
            userCursor = db.rawQuery("select * from " + FeedReaderDbHelper.FeedDocUnits.TABLE_DOC_UNITS + " where " +
                    FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
            userCursor.moveToFirst();
        }

        Toast.makeText(this, String.valueOf(userCursor.getCount()), Toast.LENGTH_SHORT).show();

        for (int i = 0; i < userCursor.getCount(); i++) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            tableRow.setLayoutParams(params1);

            TextView textView1 = new TextView(this);
            TextView textView2 = new TextView(this);
            TextView textView3 = new TextView(this);
            EditText textEdit4 = new EditText(this);

            textView1.setTextSize(16);
            textView1.setText(String.valueOf(i+1) + ".");
            textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView1.setId(Integer.parseInt("1"));
            tableRow.addView(textView1, new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.MATCH_PARENT, 0.1F));

            int colIndex1 = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_NAME_DESC);
            textView2.setTextSize(16);
            textView2.setText(userCursor.getString(colIndex1));
            textView2.setId(Integer.parseInt("2"));
            tableRow.addView(textView2, new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.MATCH_PARENT, 0.67F));

            colIndex1 = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_COUNT);
            textView3.setTextSize(16);
            textView3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView3.setText(userCursor.getString(colIndex1));
            textView3.setId(Integer.parseInt("3"));
            tableRow.addView(textView3, new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.MATCH_PARENT, 0.15F));

            colIndex1 = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_FACT_COUNT);
            textEdit4.setTextSize(16);
            textEdit4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textEdit4.setInputType(InputType.TYPE_CLASS_NUMBER);
            textEdit4.setText(userCursor.getString(colIndex1));
            textEdit4.setId(Integer.parseInt("4"));
            tableRow.addView(textEdit4, new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.MATCH_PARENT, 0.15F));

            tblLayout.addView(tableRow);



            /*TableRow tableRow2 = new TableRow(this);
            TableRow.LayoutParams params11 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            tableRow2.setLayoutParams(params11);

            TextView textView11 = new TextView(this);
            TextView textView12 = new TextView(this);
            TextView textView13 = new TextView(this);
            EditText textEdit14 = new EditText(this);

            textView11.setTextSize(16);
            textView11.setText(String.valueOf(i+2) + ".");
            tableRow2.addView(textView11, new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.MATCH_PARENT, 0.1F));

            int colIndex11 = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_NAME_DESC);
            textView12.setTextSize(16);
            textView12.setText(userCursor.getString(colIndex11));
            tableRow2.addView(textView12, new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.MATCH_PARENT, 0.5F));

            colIndex11 = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_COUNT);
            textView13.setTextSize(16);
            textView13.setText(userCursor.getString(colIndex11));
            tableRow2.addView(textView13, new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.MATCH_PARENT, 0.2F));

            colIndex11 = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_COUNT);
            textEdit14.setTextSize(16);
            textEdit14.setInputType(InputType.TYPE_CLASS_NUMBER);
            textEdit14.setText(userCursor.getString(colIndex11));
            tableRow2.addView(textEdit14, new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.MATCH_PARENT, 0.2F));

            tblLayout.addView(tableRow2);*/

            userCursor.moveToNext();
        }
        userCursor.close();

    }

    private void setupTab(String title, int id) {
        TabHost.TabSpec spec = tabHost.newTabSpec(title);
        spec.setContent(id);
        spec.setIndicator(title);
        tabHost.addTab(spec);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.docmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_close:
                super.finish();
                //this.finish();
                //closeActivity();
                break;
            case R.id.menu_doc_active:
                if (docId > 0) {
                    databaseHelper = new FeedReaderDbHelper(this);
                    db = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ACTIVE, 1);
                    db.update(FeedReaderDbHelper.FeedDocs.TABLE_DOC, values, FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});

                    if(docComment.length() != 0){
                        values.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_COMMENT, String.valueOf(docComment.getText()));
                        db.update(FeedReaderDbHelper.FeedDocs.TABLE_DOC, values, FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
                    }
                    if(docClientNum.length() != 0){
                        values.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_NUM, String.valueOf(docClientNum.getText()));
                        db.update(FeedReaderDbHelper.FeedDocs.TABLE_DOC, values, FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
                    }
                    if(docClientDate.length() != 0){
                        values.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_DATE, String.valueOf(docClientDate.getText()));
                        db.update(FeedReaderDbHelper.FeedDocs.TABLE_DOC, values, FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
                    }
                    //db.close();

                    //databaseHelper = new FeedReaderDbHelper(this);
                    //db = databaseHelper.getWritableDatabase();
                    //ContentValues values = new ContentValues();

                    TableLayout tblLayout = null;
                    tblLayout = (TableLayout) findViewById(R.id.doc_table_list_goods);
                    for (int i = 0, j = tblLayout.getChildCount(); i < j; i++) {
                        View view = tblLayout.getChildAt(i);
                        if (view instanceof TableRow) {
                            Toast.makeText(this, "TR: " + String.valueOf(i), Toast.LENGTH_SHORT).show();
                            //for (int t = 0; t < ((TableRow) view).getChildCount(); t++) {
                            View tv = ((TableRow) view).getChildAt(3);
                            TextView tva = (TextView) tv;
                            if(tva.getText().length() != 0){
                                ContentValues valuesUnits = new ContentValues();
                                CharSequence CCC = tva.getText();
                                Float Ra = Float.valueOf(String.valueOf(CCC));
                                valuesUnits.put(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_FACT_COUNT, Ra);
                                db.update(FeedReaderDbHelper.FeedDocUnits.TABLE_DOC_UNITS, valuesUnits, FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_DOC_ID + " = ? AND " + FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_NUMB + " = ?", new String[]{String.valueOf(docId), String.valueOf(i+1)});
                                Toast.makeText(this, CCC, Toast.LENGTH_SHORT).show();
                            }
                            //}
                        }
                    }
                    db.close();
                }
                Toast.makeText(this, String.valueOf(docId), Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_doc_unactive:
                if (docId > 0) {
                    databaseHelper = new FeedReaderDbHelper(this);
                    db = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ACTIVE, 0);
                    db.update(FeedReaderDbHelper.FeedDocs.TABLE_DOC, values, FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
                    db.close();
                }
                Toast.makeText(this, String.valueOf(docId), Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_doc_save:
                // each product item - get fact_count
                // and find & save to sql table unit

                if (docId > 0) {
                    databaseHelper = new FeedReaderDbHelper(this);
                    db = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    if(docComment.length() != 0){
                        values.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_COMMENT, String.valueOf(docComment.getText()));
                        db.update(FeedReaderDbHelper.FeedDocs.TABLE_DOC, values, FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
                    }
                    if(docClientNum.length() != 0){
                        values.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_NUM, String.valueOf(docClientNum.getText()));
                        db.update(FeedReaderDbHelper.FeedDocs.TABLE_DOC, values, FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
                    }
                    if(docClientDate.length() != 0){
                        values.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_DATE, String.valueOf(docClientDate.getText()));
                        db.update(FeedReaderDbHelper.FeedDocs.TABLE_DOC, values, FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
                    }

                    TableLayout tblLayout = null;
                    tblLayout = (TableLayout) findViewById(R.id.doc_table_list_goods);
                    for (int i = 0, j = tblLayout.getChildCount(); i < j; i++) {
                        View view = tblLayout.getChildAt(i);
                        if (view instanceof TableRow) {
                            Toast.makeText(this, "TR: " + String.valueOf(i), Toast.LENGTH_SHORT).show();
                            //for (int t = 0; t < ((TableRow) view).getChildCount(); t++) {
                                View tv = ((TableRow) view).getChildAt(3);
                                TextView tva = (TextView) tv;
                                if(tva.getText().length() != 0){
                                    ContentValues valuesUnits = new ContentValues();
                                    CharSequence CCC = tva.getText();
                                    Float Ra = Float.valueOf(String.valueOf(CCC));
                                    valuesUnits.put(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_FACT_COUNT, Ra);
                                    db.update(FeedReaderDbHelper.FeedDocUnits.TABLE_DOC_UNITS, valuesUnits, FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_DOC_ID + " = ? AND " + FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_NUMB + " = ?", new String[]{String.valueOf(docId), String.valueOf(i+1)});
                                    Toast.makeText(this, CCC, Toast.LENGTH_SHORT).show();
                                }
                            //}
                        }
                    }
                    db.close();
                }
                /*if (docId > 0) {
                    databaseHelper = new FeedReaderDbHelper(this);
                    db = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ACTIVE, 1);
                    db.update(FeedReaderDbHelper.FeedDocs.TABLE_DOC, values, FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
                    db.close();
                }
                Toast.makeText(this, String.valueOf(docId), Toast.LENGTH_SHORT).show();*/
                break;
            default:
                Toast.makeText(this, "itemId: " + String.valueOf(item.getTitle()), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}