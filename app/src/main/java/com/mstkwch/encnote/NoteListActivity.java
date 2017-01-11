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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FilenameFilter;

public class NoteListActivity extends AppCompatActivity
        implements EditFileNameDialogFragment.EditFileNameDialogListener {

    private  String[] lstFileNameBody;
    private AdapterView.OnItemClickListener mMessageClickHandler = null;

    static final int REQ_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mMessageClickHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Log.d(Constants.LOG_TAG, "hello, listview-item is clicked.");
                //String fileName =  arrayAdapter.getItem(position);
                String fileName = lstFileNameBody[position];
                Log.d(Constants.LOG_TAG, "item pos=" + String.valueOf(position) + ", value=" + fileName);
                Intent intent = new Intent(NoteListActivity.this, EditText02Activity.class);
                intent.putExtra(Constants.INTENT_KEY_FILENAME, fileName);
                intent.putExtra(Constants.INTENT_KEY_EDITPROC, Constants.INTENT_VAL_EDITPROC_OPEN);

                startActivity(intent);
            }
        };
        ListView listVw = (ListView) findViewById(R.id.content_main_listView);
        listVw.setOnItemClickListener(mMessageClickHandler);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.new_file01) {
            Log.d(Constants.LOG_TAG, "menu new file01 click.");
            EditFileNameDialogFragment dialog = new EditFileNameDialogFragment();
            dialog.show(getFragmentManager(), "EditFileNameDialogFragment");
        } else if (id == R.id.import01) {
            Log.d(Constants.LOG_TAG, "menu import01 click.");

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");
            startActivityForResult(intent, REQ_CODE);

            //exportToFile();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * SDカードの所定のディレクトリ配下のファイル名の配列取得
     * @return
     */
    private String[] listAppDir() {
        String appDirPath = Environment.getExternalStorageDirectory() + "/" + Constants.BASE_DATA_DIR;

        File appDir = new File(appDirPath);
        if (!appDir.exists()) {
            if (appDir.mkdir()) {
                Log.i(Constants.LOG_TAG, "cannot create " + appDirPath );
            }
        }

        FilenameFilter fl = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                int index = filename.lastIndexOf(".");//拡張子の"."を探す

                //"."以下の文字列を取り出して全て小文字に
                String ext = filename.substring(index+1).toLowerCase();
                //拡張子が"txt"と一致すれば取り出す
                if(ext.equals(Constants.DEFAULT_FILE_EXT) == true) {
                    return true;
                }
                //それ以外のファイルはリストアップしない
                return false;

            }
        };

        String[] lstFile = appDir.list(fl);
        String [] lstTitle = new String[lstFile.length];

        for (int i=0; i<lstFile.length; i++) {
            lstTitle[i] = lstFile[i].replace("." + Constants.DEFAULT_FILE_EXT, "");
        }

        return lstTitle;
    }
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.LOG_TAG, "NoteListActivity.onActivityResult()");

        if (requestCode == REQ_CODE ) {
            if (resultCode == RESULT_OK) {
                String s = data.getDataString();
                Log.d(Constants.LOG_TAG, "data.getDataString()=" + s);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.LOG_TAG, "MainActivity.onResume()");
        //ここで一覧の内容を再検索、再表示
        //List<String> lstFilePath = null;
        //String[] lstFileNameBody = null;
        if (isExternalStorageWritable()) {
            lstFileNameBody = listAppDir();
        } else {
            lstFileNameBody = new String[0];
        }

        //ArrayAdapter<String> arrayAdapter
        //        = new ArrayAdapter<String>( this, R.layout.content_main_rowitem, (String[])lstFilePath.toArray(new String[lstFilePath.size()]) );

        final ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<String>( this, R.layout.content_note_list_rowitem, lstFileNameBody);

        ListView listVw = (ListView) findViewById(R.id.content_main_listView);
        listVw.setAdapter(arrayAdapter);

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d(Constants.LOG_TAG, "MainActivity.onDialogPositiveClick start.");
        /*
        View vw = dialog.getView();
        if (vw == null) {
            Log.d(Constants.LOG_TAG, "vw is null!");
        }
        EditText eText = (EditText) vw.findViewById(R.id.edtFilename);
        String fileName = eText.getText().toString();
        */
        EditFileNameDialogFragment efdlg = (EditFileNameDialogFragment) dialog;
        String fileNameBody = efdlg.getFilename();
        Log.d(Constants.LOG_TAG, "fileName=" + fileNameBody);

        Intent intent = new Intent(NoteListActivity.this, EditText02Activity.class);
        intent.putExtra(Constants.INTENT_KEY_FILENAME, fileNameBody);
        intent.putExtra(Constants.INTENT_KEY_EDITPROC, Constants.INTENT_VAL_EDITPROC_NEW);
        startActivity(intent);
        // finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d(Constants.LOG_TAG, "MainActivity.onDialogNegativeClick start.");
        //finish();
    }

}
