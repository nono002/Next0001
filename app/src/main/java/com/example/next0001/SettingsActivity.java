package com.example.next0001;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.ToNetASCIIInputStream;
import org.apache.commons.net.tftp.TFTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.CollationElementIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnBack;
    Button btnSave;
    Button btnCheck;
    Button btnTest;
    Button btnWriteFile;
    Button btnReadFile;

    EditText textIP;
    EditText textPort;
    EditText textLogin;
    EditText textPassword;
    EditText textPath;
    EditText textTo1C;
    EditText textFrom1C;
    EditText textRequest1C;

    TextView textStatus;
    InitFTP A;
    WriteFileToFTP WFFTP;
    ReadFileFromFTP RFFTP;
    MyTask mt;
    String textA;

    final String SAVED_TEXT_IP = "SAVED_TEXT_IP";
    final String SAVED_TEXT_PORT = "SAVED_TEXT_PORT";
    final String SAVED_TEXT_Login = "SAVED_TEXT_Login";
    final String SAVED_TEXT_Password = "SAVED_TEXT_Password";
    final String SAVED_TEXT_Path = "SAVED_TEXT_Path";
    final String SAVED_TEXT_To1C = "SAVED_TEXT_To1C";
    final String SAVED_TEXT_From1C = "SAVED_TEXT_From1C";
    final String SAVED_TEXT_Request1C = "SAVED_TEXT_Request1C";
    SharedPreferences sPref;

    private ProgressDialog pd;
    private MyFTPClientFunctions ftpclient = null;

    private final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private final int MANAGE_EXTERNAL_STORAGE_PERMISSION_CODE = 101;
    private final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 102;
    private final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 103;

    // this you must get from settings preferences
    //private final static String fileTo1C = "To1C.xxx";
    //private final static String fileFrom1C = "From1C.xxx";
    //private final static String fileRequest1C = "Request1C.xxx";

    private Object Environment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack = (Button) findViewById(R.id.settings_back);
        btnSave = (Button) findViewById(R.id.settings_save);
        btnCheck = (Button) findViewById(R.id.settings_check);
        btnTest = (Button) findViewById(R.id.btn_test);
        btnWriteFile = (Button) findViewById(R.id.btn_writeFile);
        btnReadFile = (Button) findViewById(R.id.btn_readFile);

        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        btnTest.setOnClickListener(this);
        btnWriteFile.setOnClickListener(this);
        btnReadFile.setOnClickListener(this);

        textIP = (EditText) findViewById(R.id.settingsIP);
        textPort = (EditText) findViewById(R.id.settingsPort);
        textLogin = (EditText) findViewById(R.id.settingsLogin);
        textPassword = (EditText) findViewById(R.id.settingsPassword);
        textPath = (EditText) findViewById(R.id.settingsPath);
        textFrom1C = (EditText) findViewById(R.id.settingsFrom1C);
        textTo1C = (EditText) findViewById(R.id.settingsTo1C);
        textRequest1C = (EditText) findViewById(R.id.settingsRequest1C);

        textStatus = (TextView) findViewById(R.id.textStatus);

        textStatus.setText("Working...");


        loadSettings();
        Toast.makeText(this, "Settings loaded", Toast.LENGTH_SHORT).show();

        /* part without thread for FTP (AsyncTask)
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }*/

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settingsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings_back:
                Toast.makeText(this, "Settings NOT saved", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
            case R.id.menu_settings_save:
                saveSettings();
                Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
            case R.id.menu_settings_test:
                A = new InitFTP();
                A.execute();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void loadSettings(){
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String savedIP = sPref.getString(SAVED_TEXT_IP, "");
        textIP.setText(savedIP);
        String savedPort = sPref.getString(SAVED_TEXT_PORT, "");
        textPort.setText(savedPort);
        String savedLogin = sPref.getString(SAVED_TEXT_Login, "");
        textLogin.setText(savedLogin);
        String savedPassword = sPref.getString(SAVED_TEXT_Password, "");
        textPassword.setText(savedPassword);
        String savedPath = sPref.getString(SAVED_TEXT_Path, "");
        textPath.setText(savedPath);
        String savedTo1C = sPref.getString(SAVED_TEXT_To1C, "");
        textTo1C.setText(savedTo1C);
        String savedFrom1C = sPref.getString(SAVED_TEXT_From1C, "");
        textFrom1C.setText(savedFrom1C);
        String savedRequest1C = sPref.getString(SAVED_TEXT_Request1C, "");
        textRequest1C.setText(savedRequest1C);
    }

    void saveSettings(){
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT_IP, textIP.getText().toString());
        ed.putString(SAVED_TEXT_PORT, textPort.getText().toString());
        ed.putString(SAVED_TEXT_Login, textLogin.getText().toString());
        ed.putString(SAVED_TEXT_Password, textPassword.getText().toString());
        ed.putString(SAVED_TEXT_Path, textPath.getText().toString());
        ed.putString(SAVED_TEXT_To1C, textTo1C.getText().toString());
        ed.putString(SAVED_TEXT_From1C, textFrom1C.getText().toString());
        ed.putString(SAVED_TEXT_Request1C, textRequest1C.getText().toString());
        ed.commit();
    }

    public void onClick(View v) {
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        //this.finish();

        switch (v.getId()) {
            case R.id.btn_readFile:
                try {
                    /*/ открываем поток для чтения
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            openFileInput(fileTo1C)));
                    String str = "";
                    // читаем содержимое
                    while ((str = br.readLine()) != null) {
                        Toast.makeText(SettingsActivity.this, str, Toast.LENGTH_SHORT).show();
                    }*/

                    RFFTP = new ReadFileFromFTP();
                    RFFTP.execute(textFrom1C.getText().toString());

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            openFileInput(textFrom1C.getText().toString())));
                    String str = "";
                    // читаем содержимое
                    while ((str = br.readLine()) != null) {
                        Toast.makeText(SettingsActivity.this, str, Toast.LENGTH_SHORT).show();
                    }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_writeFile:
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new OutputStreamWriter(
                            openFileOutput(textTo1C.getText().toString(), MODE_PRIVATE)));
                    // пишем данные
                    bw.write(String.valueOf(textIP.getText()) + "\n");

                    // Текущее время
                    Date currentDate = new Date();
                    // Форматирование времени как "день.месяц.год"
                    DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                    String dateText = dateFormat.format(currentDate);
                    bw.write(dateText + "\n");
                    // Форматирование времени как "часы:минуты:секунды"
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String timeText = timeFormat.format(currentDate);
                    bw.write(timeText + "");

                    // закрываем поток
                    bw.close();
                    Toast.makeText(SettingsActivity.this, "File saved", Toast.LENGTH_SHORT).show();

                    WFFTP = new WriteFileToFTP();
                    WFFTP.execute(textTo1C.getText().toString());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.menu_settings_back: //R.id.settings_back:
                Toast.makeText(this, "Settings NOT saved", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
            case R.id.menu_settings_save: //R.id.settings_save:
                // save preferences
                saveSettings();
                Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
            case R.id.menu_settings_test: //R.id.settings_check:
                /*
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                            MANAGE_EXTERNAL_STORAGE_PERMISSION_CODE);
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                }

                boolean status = false;
                try {
                    FTPClient mFtpClient = new FTPClient();
                    mFtpClient.setConnectTimeout(10 * 1000);
                    mFtpClient.enterLocalPassiveMode();
                    mFtpClient.connect("193.107.74.102", 21100);
                    status = mFtpClient.login("ObmenFTP", "Rd@h@0405");
                    Toast.makeText(this, "Ok Connected", Toast.LENGTH_SHORT).show();

                    mFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    //mFtpClient.makeDirectory("$$$");
                    //mFtpClient.changeWorkingDirectory("/$$$");
                    //mFtpClient.storeFile("/$$$/test888.txt", null);

                    int reply = mFtpClient.getReplyCode();
                    FTPFile[] files = new FTPFile[0];
                    
                    
                    if(FTPReply.isPositiveCompletion(reply)){

                        files = mFtpClient.listFiles("/$$$");
                    }

                    for (int i=0; i<files.length; i++){
                        Toast.makeText(this, String.valueOf(files[i]), Toast.LENGTH_SHORT).show();
                    }

                    mFtpClient.logout();
                    mFtpClient.disconnect();
                    Toast.makeText(this, "Ok DisConnected", Toast.LENGTH_SHORT).show();

                    //if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                    //    mFtpClient.setFileType(FTP.ASCII_FILE_TYPE);
                    //    mFtpClient.enterLocalPassiveMode();
                    //    FTPFile[] mFileArray = mFtpClient.listFiles();
                    //    Log.e("Size", String.valueOf(mFileArray.length));
                    //}
                } catch (SocketException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Socket exception", Toast.LENGTH_SHORT).show();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unknown host exception", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "IO exception", Toast.LENGTH_SHORT).show();
                }
*/
                A = new InitFTP();
                A.execute();

                break;
            default:
                break;

        }

    }

    class WriteFileToFTP extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textStatus.setText("Start");
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        protected Void doInBackground(String... url) {
            textStatus.setText("Start");
            FTPClient mFTPClient = null;
            mFTPClient = new FTPClient();
            try {
                mFTPClient.connect("193.107.74.102", 21100);
                boolean status = mFTPClient.login("ObmenFTP", "Rd@h@0405");
                textA = textStatus.getText().toString();
                textStatus.setText(textA + "GOOD CONNECTION;)");

                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();

                InputStream in = openFileInput(url[0]);
                mFTPClient.storeFile("/"+textPath.getText()+"/"+url[0], in);
                textA = textStatus.getText().toString();
                textStatus.setText(textA + "FILE WRITE FTP;)");
                in.close();

                mFTPClient.logout();
                mFTPClient.disconnect();
                return null;
            }catch(FileNotFoundException e){
                e.printStackTrace();
                textStatus.setText("FILE NOT FOUND:(");
            }catch (IOException e) {
                e.printStackTrace();
                textStatus.setText("BAD CONNECTION:(");
            }
            return null;
        }

         public void onPostExecute(String fileName) {
            super.onPostExecute(null);
        }
    }

    class ReadFileFromFTP extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textStatus.setText("Start");
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        protected Void doInBackground(String... url) {
            textStatus.setText("Start");
            FTPClient mFTPClient = null;
            mFTPClient = new FTPClient();
            try {
                mFTPClient.connect("193.107.74.102", 21100);
                boolean status = mFTPClient.login("ObmenFTP", "Rd@h@0405");
                textA = textStatus.getText().toString();
                textStatus.setText(textA + "GOOD CONNECTION;)");

                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();


                OutputStream out = openFileOutput(url[0], Context.MODE_PRIVATE);
                mFTPClient.retrieveFile("/" + textPath.getText() + "/" + url[0], out);
                textA = textStatus.getText().toString();
                textStatus.setText(textA + "FILE READ FROM FTP;)");
                out.close();

                //InputStream in = openFileInput(url[0]);
                //mFTPClient.storeFile("/"+textPath.getText()+"/"+url[0], in);
                //textA = textStatus.getText().toString();
                //textStatus.setText(textA + "FILE WRITE FTP;)");

                mFTPClient.logout();
                mFTPClient.disconnect();
                return null;
            }catch(FileNotFoundException e){
                e.printStackTrace();
                textStatus.setText("FILE NOT FOUND:(");
            }catch (IOException e) {
                e.printStackTrace();
                textStatus.setText("BAD CONNECTION:(");
            }
            return null;
        }

        public void onPostExecute(String fileName) {
            super.onPostExecute(null);
        }
    }

    class InitFTP extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textStatus.setText("Start");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            textStatus.setText("Start");
            FTPClient mFTPClient = null;
            mFTPClient = new FTPClient();
            try {
                mFTPClient.connect("193.107.74.102", 21100);
                boolean status = mFTPClient.login("ObmenFTP", "Rd@h@0405");
                textStatus.setText("GOOD CONNECTION)");

                mFTPClient.changeWorkingDirectory("/" + textPath.getText());
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                //mFTPClient.makeDirectory(Directory);

                // permissions check
                //mFTPClient.storeFile("/1111.txt", null);

                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();

                mFTPClient.logout();
                mFTPClient.disconnect();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                textStatus.setText("BAD CONNECTION(");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //textStatus.setText("End");
        }

    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textStatus.setText("Begin");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            textStatus.setText("End");
        }
    }

    private void connectToFTPAdress(String host, String username, String password, Integer port) {
        pd = ProgressDialog.show(SettingsActivity.this, "", "Connecting...",
                true, false);

        new Thread(new Runnable() {
            public void run() {
                boolean status = false;
                status = ftpclient.ftpConnect(host, username, password, port);
                if (status == true) {
                    Toast.makeText(SettingsActivity.this, "Connection online!", Toast.LENGTH_SHORT).show();
                    //handler.sendEmptyMessage(0);
                    //Log.d(TAG, "Connection Success");
                    //handler.sendEmptyMessage(0);
                } else {
                    Toast.makeText(SettingsActivity.this, "Connection error!", Toast.LENGTH_SHORT).show();
                    //handler.sendEmptyMessage(-1);
                    //Log.d(TAG, "Connection failed");
                    //handler.sendEmptyMessage(-1);
                }
            }
        }).start();
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

}