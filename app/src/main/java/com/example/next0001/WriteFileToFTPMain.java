package com.example.next0001;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

class WriteFileToFTPMain extends AsyncTask<String, Void, Void> {

    SharedPreferences sPref;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("WriteFileToFTPMain", "Start FTP Write");
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected Void doInBackground(String... url) {
        Log.d("WriteFileToFTPMain", "Start FTP Write");
        FTPClient mFTPClient = null;
        mFTPClient = new FTPClient();
        try {
            mFTPClient.connect("193.107.74.102", 21100);
            boolean status = mFTPClient.login("ObmenFTP", "Rd@h@0405");
            Log.d("WriteFileToFTPMain", "GOOD CONNECTION FTP");

            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            mFTPClient.enterLocalPassiveMode();

            String SAVED_TEXT_Path = "SAVED_TEXT_Path";
            sPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            String textPath = sPref.getString(SAVED_TEXT_Path, "");

            InputStream in = openFileInput(url[0]);
            mFTPClient.storeFile("/" + textPath + "/" + url[0], in);
            Log.d("WriteFileToFTPMain", "FILE WRITE FTP");
            in.close();

            mFTPClient.logout();
            mFTPClient.disconnect();
            return null;
        }catch(FileNotFoundException e){
            e.printStackTrace();
            Log.d("WriteFileToFTPMain", "FILE NOT FOUND:(");
        }catch (IOException e) {
            e.printStackTrace();
            Log.d("WriteFileToFTPMain", "BAD CONNECTION:(");
        }
        return null;
    }

    private InputStream openFileInput(String s) {
        return openFileInput(s);
    }

    private SharedPreferences getSharedPreferences(String myPref, int modePrivate) {
        return getSharedPreferences(myPref, modePrivate);
    }

    public void onPostExecute(String fileName) {
        super.onPostExecute(null);
    }


}

