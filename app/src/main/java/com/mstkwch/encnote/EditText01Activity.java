package com.mstkwch.encnote;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class EditText01Activity extends AppCompatActivity
        implements ConfirmDialogFragment.ConfirmDialogListener {
//public class EditText01Activity extends FragmentActivity {

    private static final String SEED = "ABCDEF";

    private String fileName;
    private int editProc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text01);

        Intent intent = getIntent();
        fileName = intent.getStringExtra(Constants.INTENT_KEY_FILENAME);
        editProc = intent.getIntExtra(Constants.INTENT_KEY_EDITPROC, Constants.INTENT_VAL_EDITPROC_NEW);
        Log.d("TestApp03", "onCreate fileName=" + fileName);

        try {
            String content = "";
            if (editProc == Constants.INTENT_VAL_EDITPROC_OPEN) {
                content = loadFile(fileName);
            }
            EditText editText = (EditText) findViewById(R.id.edit_text01_txtEdit);
            editText.setText(content);

        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("TestApp01", "loadFile Error", e);
        }


    }

    public void ok(View view) {
        Log.d("TestApp03", "EditText01Activity.Ok start.");
        Log.d("TestApp03", "fileName=" + fileName);

        ConfirmDialogFragment cdf = new ConfirmDialogFragment();
        cdf.show(getFragmentManager(), "confirm saving");

    }

    public void cancel(View view) {
        Log.d("TestApp03", "EditText01Activity.cancel start.");
            //- ただの読み込みはできているので、復号化すること -> not yet
            //- readLineではなく、もっと丁寧な処理にすることを次にやる -> done.
        finish();
    }

    private String loadFile(String filename) throws Exception {

        String filePath = Environment.getExternalStorageDirectory() + "/TestApp03/" + filename;
        File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            char[] buff = new char[1024];
            int retRead;
            StringBuffer strBuff = new StringBuffer();

            retRead = br.read(buff, 0, 1024);
            while(0 <= retRead) {
                strBuff.append(buff, 0, retRead);
                retRead = br.read(buff, 0, 1024);
            }
            br.close();

            String deStr = MyCrypto.decrypt(SEED, strBuff.toString());

            return deStr;

    }

    private void saveFile(String filename){
        String filePath = Environment.getExternalStorageDirectory() + "/TestApp03/" + filename;
        File file = new File(filePath);
        file.getParentFile().mkdir();

        FileOutputStream fos;
        try {
            //fos = new FileOutputStream(file, true);
            fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            //EditText txtEdit = (EditText) findViewById(R.id.my2nd_txtTextBody);
            //txtEdit.setText(filePath);

            String plainStr = ((EditText) findViewById(R.id.edit_text01_txtEdit)).getText().toString();

            String encStr = MyCrypto.encrypt(SEED, plainStr);
            bw.write(encStr);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            Log.e("TestApp03", filePath + ": Error!!", e);
            //EditText txtEdit = (EditText) findViewById(R.id.edit_text01_txtEdit);
            //txtEdit.setText(filePath + ": Error!!" + e.getLocalizedMessage());
        }
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d("TestApp03", "EditText01Activity.onDialogPositiveClick start.");
        saveFile(fileName);

        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d("TestApp03", "EditText01Activity.onDialogNegativeClick start.");
        finish();
    }
}
