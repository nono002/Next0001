package com.example.next0001;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.material.internal.ContextUtils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {

    //private WriteFileToFTP WFFTP;
    private ReadFileFromFTP RFFTP;
    private XmlPullParserFactory xmlFactoryObject;
    private XmlPullParser myParser;

    final String SAVED_TEXT_IP = "SAVED_TEXT_IP";
    final String SAVED_TEXT_PORT = "SAVED_TEXT_PORT";
    final String SAVED_TEXT_Login = "SAVED_TEXT_Login";
    final String SAVED_TEXT_Password = "SAVED_TEXT_Password";
    final String SAVED_TEXT_Path = "SAVED_TEXT_Path";
    final String SAVED_TEXT_To1C = "SAVED_TEXT_To1C";
    final String SAVED_TEXT_From1C = "SAVED_TEXT_From1C";
    final String SAVED_TEXT_Request1C = "SAVED_TEXT_Request1C";

    SharedPreferences sPref;

    String savedIP;
    String savedPort;
    String savedLogin;
    String savedPassword;
    String savedPath;
    String savedTo1C;
    String savedFrom1C;
    String savedRequest1C;

    ////DBHelper dbHelper;
    FeedReaderDbHelper dbFeedReaderDbHelper;
    SQLiteDatabase db, dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // downloads settings
        loadSettings();

        dbFeedReaderDbHelper = new FeedReaderDbHelper(this);
        db = dbFeedReaderDbHelper.getWritableDatabase();
        dbr = dbFeedReaderDbHelper.getReadableDatabase();

        //System.out.println("Oh. Start!!!");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
           case R.id.menu_postuplenia:
               Intent intentListDocs = new Intent(this, ListDocsActivity.class);
               startActivity(intentListDocs);

               //setContentView(R.layout.activity_settings);
               Toast.makeText(this, "Надходження", Toast.LENGTH_SHORT).show();
               break;
           case R.id.menu_get:
               try {

                   RFFTP = new ReadFileFromFTP();
                   RFFTP.execute(savedFrom1C);

                   db.delete(FeedReaderDbHelper.FeedDocs.TABLE_DOC, null, null);
                   db.delete(FeedReaderDbHelper.FeedDocUnits.TABLE_DOC_UNITS, null, null);

                   ContentValues cv = new ContentValues();

                   xmlFactoryObject = XmlPullParserFactory.newInstance();
                   myParser = xmlFactoryObject.newPullParser();
                   myParser.setInput(openFileInput(savedFrom1C), null);

                   int event = myParser.getEventType();
                   while (event != XmlPullParser.END_DOCUMENT)  {
                       String tagName = myParser.getName();
                       switch (event){
                           case XmlPullParser.START_TAG:
                               if("DOC".equalsIgnoreCase(tagName)){
                                   String doc_type = myParser.getAttributeValue(null,"DOC_TYPE");
                                   String doc_num = myParser.getAttributeValue(null,"DOC_NUM");
                                   String doc_guid = myParser.getAttributeValue(null,"DOC_ID");
                                   String doc_number = myParser.getAttributeValue(null,"NUMBER");
                                   String doc_date = myParser.getAttributeValue(null,"DATE");
                                   String doc_client_name = myParser.getAttributeValue(null,"CLIENT_NAME");
                                   String doc_client_guid = myParser.getAttributeValue(null,"CLIENT_ID");
                                   String doc_cloud = myParser.getAttributeValue(null,"CLOUD");
                                   String doc_cloud_guid = myParser.getAttributeValue(null,"CLOUD_ID");
                                   String comment = myParser.getAttributeValue(null,"COMMENT");
                                   String doc_sum = myParser.getAttributeValue(null, "DOC_SUM");
                                   Toast.makeText(this, doc_num + "\n" + doc_client_name + "\n" + comment, Toast.LENGTH_SHORT).show();

                                   cv.clear();
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_TYPE, doc_type);
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_NUM, doc_num);
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_GUID, doc_guid);
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_NUMBER, doc_number);
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DATE, doc_date);
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_NAME, doc_client_name);
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_GUID, doc_client_guid);
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLOUD, doc_cloud);
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLOUD_GUID, doc_cloud_guid);
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_COMMENT, comment);
                                   cv.put(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_SUM, doc_sum);
                                   db.insert(FeedReaderDbHelper.FeedDocs.TABLE_DOC, null, cv);
                                   //Toast.makeText(this, "+++ %%%", Toast.LENGTH_SHORT).show();
                                   Log.d("!!!! LOG !!!!", "--- Insert dates into database ---");

                                   Integer _id = dbFeedReaderDbHelper.getLastId(db, FeedReaderDbHelper.FeedDocs.TABLE_DOC);

                                   //Toast.makeText(this, String.valueOf(_id), Toast.LENGTH_SHORT).show();

                                   while (event != XmlPullParser.END_DOCUMENT){
                                       event = myParser.next();
                                       tagName = myParser.getName();
                                       switch(event){
                                           case XmlPullParser.START_TAG:
                                               if("UNIT".equalsIgnoreCase(tagName)){
                                                   String unit_number = myParser.getAttributeValue(null,"INV_NUMB");
                                                   String unit_name = myParser.getAttributeValue(null,"INV_NAME");
                                                   String unit_name_desc = myParser.getAttributeValue(null,"INV_NAME_DESC");
                                                   String unit_guid = myParser.getAttributeValue(null,"INV_ID");
                                                   String unit_cost = myParser.getAttributeValue(null,"COST");
                                                   String unit_count = myParser.getAttributeValue(null,"COUNT");
                                                   Toast.makeText(this, unit_name + "\n" + unit_cost + " грн\n" + unit_count +" шт", Toast.LENGTH_SHORT).show();

                                                   //ContentValues cv = new ContentValues();
                                                   cv.clear();
                                                   cv.put(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_DOC_ID, _id);
                                                   cv.put(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_NUMB, unit_number);
                                                   cv.put(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_NAME, unit_name);
                                                   cv.put(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_NAME_DESC, unit_name_desc);
                                                   cv.put(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_GUID, unit_guid);
                                                   cv.put(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_COST, unit_cost);
                                                   cv.put(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_COUNT, unit_count);
                                                   db.insert(FeedReaderDbHelper.FeedDocUnits.TABLE_DOC_UNITS, null, cv);
                                                   Log.d("!!!! AAA !!!!", "Insert row");
                                               }
                                               break;
                                       }
                                       if (event == XmlPullParser.END_TAG) {
                                           if(tagName.equals("DOC")){
                                                break;
                                           }
                                       }
                                   }
                               }
                               break;
                           case XmlPullParser.END_TAG:
                               //if(tagName.equals("DOC")){
                               //    String doc_num = myParser.getAttributeValue(null,"DOC_NUM");
                               //    Toast.makeText(this, doc_num, Toast.LENGTH_SHORT).show();
                               //}
                               break;
                       }
                       event = myParser.next();
                   }

               } catch (FileNotFoundException e) {
                   e.printStackTrace();
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (XmlPullParserException e) {
                   e.printStackTrace();
               }

               break;
           case R.id.menu_pull:
               //String SAVED_TEXT_Path = "SAVED_TEXT_Path";

               String SAVED_TEXT_To1C = "SAVED_TEXT_To1C";
               sPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
               final String textTo1C = sPref.getString(SAVED_TEXT_To1C, "");

               //String userName = "0987654321";
               //String password = "1234567890";
               try {
                   //FileOutputStream fos = new  FileOutputStream("userData.xml");
                   FileOutputStream fileos= getApplicationContext().openFileOutput(textTo1C, Context.MODE_PRIVATE);
                   XmlSerializer xmlSerializer = Xml.newSerializer();
                   StringWriter writer = new StringWriter();
                   xmlSerializer.setOutput(writer);
                   xmlSerializer.startDocument("UTF-8", true);

                   dbr = dbFeedReaderDbHelper.getReadableDatabase();
                   Cursor userCursor = dbr.rawQuery("select * from " + FeedReaderDbHelper.FeedDocs.TABLE_DOC + " where " +
                           FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ACTIVE + "=?", new String[]{String.valueOf("1")});
                   //userCursor.moveToFirst();
                   xmlSerializer.startTag(null, "DOCS");
                   while (userCursor.moveToNext()){
                       xmlSerializer.startTag(null, "DOC");
                       int colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_ID);
                       String docId = userCursor.getString(colIndex);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_TYPE);
                       String docType = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null,"DOC_TYPE",docType);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_NUM);
                       String docNum = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null,"DOC_NUM",docNum);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_GUID);
                       String docGuid = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null, "DOC_ID", docGuid);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_NUMBER);
                       String docNumber = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null, "NUMBER", docNumber);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DATE);
                       String docDate = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null, "DATE", docDate);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_NAME);
                       String docClient = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null, "CLIENT_NAME", docClient);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLIENT_GUID);
                       String docClientId = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null, "CLIENT_GUID", docClientId);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLOUD);
                       String docCloud = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null, "CLOUD", docCloud);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_CLOUD_GUID);
                       String docCloudId = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null, "CLOUD_ID", docCloudId);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_COMMENT);
                       String docComment = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null, "COMMENT", docComment);
                       colIndex = userCursor.getColumnIndex(FeedReaderDbHelper.FeedDocs.COLUMN_NAME_DOC_SUM);
                       String docSum = userCursor.getString(colIndex);
                       xmlSerializer.attribute(null, "DOC_SUM", docSum);

                       Cursor itemCursor = db.rawQuery("select * from " + FeedReaderDbHelper.FeedDocUnits.TABLE_DOC_UNITS + " where " +
                               FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_DOC_ID + "=?", new String[]{String.valueOf(docId)});
                       //userCursor.moveToFirst();
                       xmlSerializer.startTag(null, "UNITS");
                       while(itemCursor.moveToNext()){
                           xmlSerializer.startTag(null, "UNIT");
                           colIndex = itemCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_NUMB);
                           String docInvNumb = itemCursor.getString(colIndex);
                           xmlSerializer.attribute(null, "INV_NUMB", docInvNumb);
                           colIndex = itemCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_NAME);
                           String docInvName = itemCursor.getString(colIndex);
                           xmlSerializer.attribute(null, "INV_NAME", docInvName);
                           colIndex = itemCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_NAME_DESC);
                           String docInvNameDesc = itemCursor.getString(colIndex);
                           xmlSerializer.attribute(null, "INV_NAME_DESC", docInvNameDesc);
                           colIndex = itemCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_INV_GUID);
                           String docInvGuid = itemCursor.getString(colIndex);
                           xmlSerializer.attribute(null, "INV_ID", docInvGuid);
                           colIndex = itemCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_COST);
                           String docInvCost = itemCursor.getString(colIndex);
                           xmlSerializer.attribute(null, "COST", String.valueOf(docInvCost));
                           colIndex = itemCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_COUNT);
                           String docInvCount = itemCursor.getString(colIndex);
                           xmlSerializer.attribute(null, "COUNT", String.valueOf(docInvCount));
                           colIndex = itemCursor.getColumnIndex(FeedReaderDbHelper.FeedDocUnits.COLUMN_NAME_FACT_COUNT);
                           String docInvFactCount = itemCursor.getString(colIndex);
                           xmlSerializer.attribute(null, "FACT_COUNT", String.valueOf(docInvFactCount == null?"0":docInvFactCount));

                           xmlSerializer.endTag(null, "UNIT");
                       }
                        xmlSerializer.endTag(null, "UNITS");
                       xmlSerializer.endTag(null, "DOC");
                   }
                   xmlSerializer.endTag(null, "DOCS");

                   xmlSerializer.endDocument();
                   xmlSerializer.flush();
                   String dataWrite = writer.toString();
                   fileos.write(dataWrite.getBytes());
                   fileos.close();

                   WriteFileToFTP WFFTP;
                   WFFTP = new WriteFileToFTP();
                   WFFTP.execute(textTo1C);

                   Log.d("7777777777777", "onOptionsItemSelected: Ok write XML");
               }
               catch (FileNotFoundException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
               catch (IllegalArgumentException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
               catch (IllegalStateException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
               catch (IOException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
               break;
           case R.id.menu_settings:
               Intent intentSettings = new Intent(this, SettingsActivity.class);
               startActivity(intentSettings);

               //setContentView(R.layout.activity_settings);
               Toast.makeText(this, "Налаштування", Toast.LENGTH_SHORT).show();
               break;
           case R.id.menu_reload:
               Toast.makeText(this, "Оновити", Toast.LENGTH_SHORT).show();
               break;
           case R.id.menu_exit:
               this.finish();
//           case R.id.menu_start:
//               this.getActivity().onBackPressed();
//               break;
           default:
               Toast.makeText(this, "itemId: " + String.valueOf(item.getTitle()), Toast.LENGTH_SHORT).show();
               break;
       }

        //Toast.makeText(this, "itemId: " + String.valueOf(item.getItemId()), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    class ReadFileFromFTP extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Початок завантаження", Toast.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        protected Void doInBackground(String... url) {
            //Toast.makeText(MainActivity.this, "Початок завантаження", Toast.LENGTH_SHORT).show();
            FTPClient mFTPClient = null;
            mFTPClient = new FTPClient();
            Context context;
            Handler handler =  new Handler(MainActivity.this.getMainLooper());
            try {
                mFTPClient.connect(savedIP, Integer.parseInt(savedPort));
                boolean status = mFTPClient.login(savedLogin, savedPassword);

                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(MainActivity.this, "З'єднання встановлено",Toast.LENGTH_LONG).show();
                    }
                });
                //Toast.makeText(MainActivity.this, "Зєднання встановлено", Toast.LENGTH_SHORT).show();

                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();


                OutputStream out = openFileOutput(url[0], Context.MODE_PRIVATE);
                mFTPClient.retrieveFile("/" + savedPath + "/" + url[0], out);
                out.close();
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(MainActivity.this, "Файл завантажено",Toast.LENGTH_LONG).show();
                    }
                });

                //InputStream in = openFileInput(url[0]);
                //mFTPClient.storeFile("/"+textPath.getText()+"/"+url[0], in);
                //textA = textStatus.getText().toString();
                //textStatus.setText(textA + "FILE WRITE FTP;)");

                mFTPClient.logout();
                mFTPClient.disconnect();
                return null;
            }catch(FileNotFoundException e){
                e.printStackTrace();
                //Toast.makeText(MainActivity.this, "Файл не знайдено", Toast.LENGTH_SHORT).show();
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(MainActivity.this, "Файл не знайдено",Toast.LENGTH_LONG).show();
                    }
                });
            }catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(MainActivity.this, "Зєднання не встановлено", Toast.LENGTH_SHORT).show();
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(MainActivity.this, "З'єднання не встановлено",Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        public void onPostExecute(String fileName) {
            super.onPostExecute(null);
            Toast.makeText(MainActivity.this, "Файл завантажено", Toast.LENGTH_SHORT).show();
        }
    }

    void loadSettings(){
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        savedIP = sPref.getString(SAVED_TEXT_IP, "");
        savedPort = sPref.getString(SAVED_TEXT_PORT, "");
        savedLogin = sPref.getString(SAVED_TEXT_Login, "");
        savedPassword = sPref.getString(SAVED_TEXT_Password, "");
        savedPath = sPref.getString(SAVED_TEXT_Path, "");
        savedTo1C = sPref.getString(SAVED_TEXT_To1C, "");
        savedFrom1C = sPref.getString(SAVED_TEXT_From1C, "");
        savedRequest1C = sPref.getString(SAVED_TEXT_Request1C, "");
    }

    class WriteFileToFTP extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //textStatus.setText("Start");
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        protected Void doInBackground(String... url) {
            //textStatus.setText("Start");
            FTPClient mFTPClient = null;
            mFTPClient = new FTPClient();
            try {
                mFTPClient.connect("193.107.74.102", 21100);
                boolean status = mFTPClient.login("ObmenFTP", "Rd@h@0405");
                //textA = textStatus.getText().toString();
                //textStatus.setText(textA + "GOOD CONNECTION;)");

                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();

                InputStream in = openFileInput(url[0]);
                mFTPClient.storeFile("/"+"0001"+"/"+url[0], in);
                //textA = textStatus.getText().toString();
                //textStatus.setText(textA + "FILE WRITE FTP;)");
                in.close();

                mFTPClient.logout();
                mFTPClient.disconnect();
                return null;
            }catch(FileNotFoundException e){
                e.printStackTrace();
                //textStatus.setText("FILE NOT FOUND:(");
            }catch (IOException e) {
                e.printStackTrace();
                //textStatus.setText("BAD CONNECTION:(");
            }
            return null;
        }

        public void onPostExecute(String fileName) {
            super.onPostExecute(null);
        }
    }


}