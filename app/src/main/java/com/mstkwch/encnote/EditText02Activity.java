package com.mstkwch.encnote;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class EditText02Activity extends AppCompatActivity
        implements ConfirmDialogFragment.ConfirmDialogListener {

    private static final String SEED = "ABCDEF";

    private String fileNameBody;
    private int editProc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text02);
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        fileNameBody = intent.getStringExtra(Constants.INTENT_KEY_FILENAME);
        editProc = intent.getIntExtra(Constants.INTENT_KEY_EDITPROC, Constants.INTENT_VAL_EDITPROC_NEW);
        Log.d(Constants.LOG_TAG, "onCreate fileName=" + fileNameBody);

        EditText editText = (EditText) findViewById(R.id.edit_text02_txtEdit);
        try {
            String content = "";
            switch (editProc) {
                case Constants.INTENT_VAL_EDITPROC_OPEN:
                    content = loadEncFile(fileNameBody + "." + Constants.DEFAULT_FILE_EXT);
                case Constants.INTENT_VAL_EDITPROC_IMPORT:
                    //ここにplain-textを読み込む処理を入れる
            }
//            if (editProc == Constants.INTENT_VAL_EDITPROC_OPEN) {
//            }
            editText.setText(content);

        } catch (Exception e) {
            //e.printStackTrace();
            Log.e(Constants.LOG_TAG, "loadEncFile Error", e);
            editText.setText("Error: " + e.toString());
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_text02, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.export01) {
            Log.d(Constants.LOG_TAG, "menu export01 click.");

            String plainStr = ((EditText) findViewById(R.id.edit_text02_txtEdit)).getText().toString();

            //保存
            saveFile(fileNameBody + "_plain.txt", plainStr);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ok(View view) {
        Log.d(Constants.LOG_TAG, "EditText02Activity.Ok start.");
        Log.d(Constants.LOG_TAG, "fileName=" + fileNameBody);

        ConfirmDialogFragment cdf = new ConfirmDialogFragment();
        cdf.show(getFragmentManager(), "confirm saving");

    }

    public void cancel(View view) {
        Log.d(Constants.LOG_TAG, "EditText02Activity.cancel start.");
        //- ただの読み込みはできているので、復号化すること -> not yet
        //- readLineではなく、もっと丁寧な処理にすることを次にやる -> done.
        finish();
    }


    /**
     * @param filePath
     * @return
     * @throws Exception
     */
    private String loadFileRaw(String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);

        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);

        char[] buff = new char[1024];

        int retRead;
        StringBuffer strBuff = new StringBuffer();

        retRead = br.read(buff, 0, 1024);
        while (0 <= retRead) {
            strBuff.append(buff, 0, retRead);
            retRead = br.read(buff, 0, 1024);
        }
        br.close();

        return strBuff.toString();
    }

    private String loadEncFile(String filename) throws Exception {

        String filePath = Environment.getExternalStorageDirectory() + "/" + Constants.BASE_DATA_DIR + "/" + filename;

        //RAISE EXCEPTION NO_SUCH_FIELD_ERROR
        //File exPubDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        //Log.d(Constants.LOG_TAG, "exPubDir=" + exPubDir.getAbsolutePath());

        /*
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
        */
        String encContents = loadFileRaw(filePath);


        //String deStr = MyCrypto.decrypt(SEED, strBuff.toString());
        MyCrypto2 crypt = new MyCrypto2(SEED);
        String deStr = crypt.decrypt(encContents);

        return deStr;

    }

    private void saveFile(String filename, String contents){
        String filePath = Environment.getExternalStorageDirectory() + "/" + Constants.BASE_DATA_DIR + "/" + filename;
        File file = new File(filePath);
        file.getParentFile().mkdir();

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);

            bw.write(contents);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, filePath + ": Error!!", e);
            //EditText txtEdit = (EditText) findViewById(R.id.edit_text01_txtEdit);
            //txtEdit.setText(filePath + ": Error!!" + e.getLocalizedMessage());
        }
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d(Constants.LOG_TAG, "EditText02Activity.onDialogPositiveClick start.");

        //編集内容を取りだす
        String plainStr = ((EditText) findViewById(R.id.edit_text02_txtEdit)).getText().toString();

        //暗号化処理
        MyCrypto2 crypt = new MyCrypto2(SEED);
        String encStr = crypt.encrypt(plainStr);

        //保存
        saveFile(fileNameBody + "." + Constants.DEFAULT_FILE_EXT, encStr);

        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d(Constants.LOG_TAG, "EditText02Activity.onDialogNegativeClick start.");
        finish();
    }

}
