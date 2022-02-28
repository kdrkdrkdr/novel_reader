package com.kdr.novel_reader;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.TypedArrayUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.novel_reader.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class User_Dictionary extends AppCompatActivity implements TextWatcher {
    private Context mContext;
    private ArrayList<Data> mArrayList, mFilteredList;//필터링할 데이터 담을 어레이리스트
    private Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private EditText edit_ja_name, edit_ko_name, edit_search;
    private Button btn_save, btn_import_dict, btn_apply_dict;
    private DBHelper mDbHelper;
    private SQLiteDatabase db;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    Python py;
    PyObject rq_func;

    public static int PICK_FILE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);


        setContentView (R.layout.user_dictionary);
        mContext = User_Dictionary.this;
        edit_ja_name = findViewById (R.id.edit_ja_name);
        edit_ko_name = findViewById (R.id.edit_ko_name);
        btn_save = findViewById (R.id.btn_save);
        btn_import_dict = findViewById(R.id.btn_import_dict);
        btn_apply_dict = findViewById(R.id.btn_apply_dict);
        mRecyclerView = findViewById (R.id.recycler);
        edit_search = findViewById (R.id.edit_search);
        edit_search.addTextChangedListener (this);


        if(!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        py = Python.getInstance();
        rq_func = py.getModule("_requirement_func");


        //DBHelper 객체를 선언해줍니다.
        mDbHelper = new DBHelper (mContext);

        //쓰기모드에서 데이터 저장소를 불러옵니다.
        db = mDbHelper.getWritableDatabase ();

        initRecyclerView ();

        //버튼 클릭이벤트
        //이름과 전화번호를 입력한 후 버튼을 클릭하면 어레이리스트에 데이터를 담고 리사이클러뷰에 띄운다.
        btn_save.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (edit_ja_name.getText ().length () == 0 && edit_ko_name.getText ().length () == 0) {
                    Toast.makeText (mContext, "일본어 이름, 한국어 이름을 입력해주세요.", Toast.LENGTH_SHORT).show ();
                } else {
                    String ja_name = edit_ja_name.getText ().toString ();
                    String ko_name = edit_ko_name.getText ().toString ();
                    edit_ja_name.setText ("");
                    edit_ko_name.setText ("");
                    Data data = new Data (ja_name, ko_name);

                    mArrayList.add (data);
                    mAdapter.notifyItemInserted (mArrayList.size () - 1);

                    //데이터를 테이블에 삽입합니다.
                    insertName(ja_name, ko_name);

                }
            }
        });


        // DB 초기화 후 사전에 있는거 넣기...
        btn_import_dict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/*");
                startActivityForResult(intent, PICK_FILE);
            }
        });


        btn_apply_dict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RESTORE-DICT", "사전 리로드 시작");
                rq_func.callAttr("RestoreCSV");
                Log.d("RESTORE-DICT", "사전 리로드 완료");
            }
        });






        //리사이클러뷰 클릭 이벤트
        mAdapter.setOnItemClickListener (new Adapter.OnItemClickListener () {

            //아이템 클릭시 토스트메시지0
            @Override
            public void onItemClick(View v, int position) {
                String ja_name, ko_name;

                if (edit_search.getText().toString().equals("")) {
                    ja_name = mArrayList.get (position).getJa_name();
                    ko_name = mArrayList.get (position).getKo_name();
                } else {
                    ja_name = mFilteredList.get(position).getJa_name();
                    ko_name = mFilteredList.get(position).getKo_name();
                    position = mArrayList.indexOf(new Data (ja_name, ko_name));
                }
                Toast.makeText (mContext, "일본이름 : " + ja_name + "\n한국이름 : " + ko_name, Toast.LENGTH_SHORT).show();
            }

            //수정
            @Override
            public void onEditClick(View v, int position) {
                String ja_name, ko_name;

                if (edit_search.getText().toString().equals("")) {
                    ja_name = mArrayList.get (position).getJa_name();
                    ko_name = mArrayList.get (position).getKo_name();
                } else {
                    ja_name = mFilteredList.get(position).getJa_name();
                    ko_name = mFilteredList.get(position).getKo_name();
                    position = mArrayList.indexOf(new Data (ja_name, ko_name));
                }

                editItem (ja_name, ko_name, position);

                searchFilter(edit_search.getText().toString());
            }

            //삭제
            @Override
            public void onDeleteClick(View v, int position) {
                String ja_name, ko_name;

                if (edit_search.getText().toString().equals("")) {
                    ja_name = mArrayList.get (position).getJa_name();
                    ko_name = mArrayList.get (position).getKo_name();
                } else {
                    ja_name = mFilteredList.get(position).getJa_name();
                    ko_name = mFilteredList.get(position).getKo_name();
                    position = mArrayList.indexOf(new Data (ja_name, ko_name));
                }

                deleteName(ja_name, ko_name);

                mArrayList.remove (position);
                mAdapter.notifyItemRemoved (position);

                searchFilter(edit_search.getText().toString());

            }



        });

    }

    //SQLite 데이터 수정
    //newJaName 은 수정된 값, oldJaName 수정전 값
    private void updateName(String oldJaName, String oldKoName, String newJaName, String newKoName){
        //수정된 값들을 values 에 추가한다.
        ContentValues values = new ContentValues();
        values.put(DBHelper.FeedEntry.COLUMN_NAME_JA, newJaName);
        values.put (DBHelper.FeedEntry.COLUMN_NAME_KO, newKoName);

        // WHERE 절 수정될 열을 찾는다.
        String selection = DBHelper.FeedEntry.COLUMN_NAME_JA + " LIKE ?" +
                " AND "+ DBHelper.FeedEntry.COLUMN_NAME_KO + " LIKE ?";
        String[] selectionArgs = { oldJaName, oldKoName };

        db.update(DBHelper.FeedEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    //SQLite 데이터 삭제
    private void deleteName(String name, String number) {
        //WHERE 절 삭제될 열을 찾는다.
        String selection = DBHelper.FeedEntry.COLUMN_NAME_JA + " LIKE ?" +
                " and " + DBHelper.FeedEntry.COLUMN_NAME_KO + " LIKE ?";
        String[] selectionArgs = {name, number};
        db.delete (DBHelper.FeedEntry.TABLE_NAME, selection, selectionArgs);
    }

    //SQLite 데이터 삽입
    private void insertName(String jaName, String koName) {
        //쿼리를 직접 작성해서 입력하거나 values를 만들어서 하는 방법이 있다
        //후자를 이용하겠다.
        ContentValues values = new ContentValues ();
        values.put (DBHelper.FeedEntry.COLUMN_NAME_JA, jaName);
        values.put (DBHelper.FeedEntry.COLUMN_NAME_KO, koName);
        db.insert (DBHelper.FeedEntry.TABLE_NAME, null, values);
//        String sql = "INSERT INTO "+DBHelper.FeedEntry.TABLE_NAME+" values("+name+", "+number+");";
//        db.execSQL(sql);


    }

    //데이터 불러오기
    //Cursor를 사용해서 데이터를 불러옵니다.
    //while문을 사용해서 불러온 데이터를 mArrayList에 삽입합니다.
    private void loadData() {

        @SuppressLint("Recycle") Cursor c = db.rawQuery ("SELECT * FROM " + DBHelper.FeedEntry.TABLE_NAME, null);
        while (c.moveToNext ()) {
//            Log.d (TAG, c.getString (c.getColumnIndex (DBHelper.FeedEntry._ID))
//                    + " name-"+c.getString(c.getColumnIndex(DBHelper.FeedEntry.COLUMN_NAME_NAME))
//                    + " number-"+c.getString(c.getColumnIndex(DBHelper.FeedEntry.COLUMN_NAME_NUMBER)));
            @SuppressLint("Range") String ja_name = c.getString (c.getColumnIndex (DBHelper.FeedEntry.COLUMN_NAME_JA));
            @SuppressLint("Range") String ko_name = c.getString (c.getColumnIndex (DBHelper.FeedEntry.COLUMN_NAME_KO));
            Data data = new Data (ja_name, ko_name);

            mArrayList.add (data);
        }

        mAdapter.notifyDataSetChanged();
    }

    //리사이클러뷰
    private void initRecyclerView() {
        //레이아웃메니저는 리사이클러뷰의 항목 배치를 어떻게 할지 정하고, 스크롤 동작도 정의한다.
        //수평/수직 리스트 LinearLayoutManager
        //그리드 리스트 GridLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager (mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager (layoutManager);
        mFilteredList = new ArrayList<> ();
        mArrayList = new ArrayList<> ();
        mAdapter = new Adapter (mContext, mArrayList);
        mRecyclerView.setAdapter (mAdapter);

        //저장된 데이터를 불러옵니다.
        loadData ();

    }

    //AlertDialog 를 사용해서 데이터를 수정한다.
    private void editItem(String jaName, String koName, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        View view = LayoutInflater.from (this).inflate (R.layout.dialog, null, false);
        builder.setView (view);

        final AlertDialog dialog = builder.create ();

        final Button btn_edit = view.findViewById (R.id.btn_edit);
        final Button btn_cancel = view.findViewById (R.id.btn_cancel);
        final EditText edit_ja_name = view.findViewById (R.id.edit_editJaName);
        final EditText edit_ko_name = view.findViewById (R.id.edit_editKoName);

        edit_ja_name.setText (jaName);
        edit_ko_name.setText (koName);


        // 수정 버튼 클릭
        //어레이리스트 값을 변경한다.
        btn_edit.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String editJaName = edit_ja_name.getText ().toString ();
                String editKoName = edit_ko_name.getText ().toString ();
                mArrayList.get (position).setJa_name(editJaName);
                mArrayList.get (position).setKo_name(editKoName);

                //데이터 수정
                updateName(jaName,koName,editJaName,editKoName);

                mAdapter.notifyItemChanged (position);
                dialog.dismiss ();
            }
        });

        // 취소 버튼 클릭
        btn_cancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                dialog.dismiss ();
            }
        });

        dialog.show ();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        mDbHelper.close ();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        mAdapter.getFilter ().filter (charSequence);
    }

    //에딧텍스트에 입력받는 값을 감지한다.
    @Override
    public void afterTextChanged(Editable editable) {
        String searchText = edit_search.getText().toString();
        searchFilter(searchText);
    }

    //에딧텍스트 값을 받아 mFilteredList에 데이터를 추가한다.
    public void searchFilter(String searchText) {
        mFilteredList.clear();

        for (int i = 0; i < mArrayList.size(); i++) {
            if (mArrayList.get(i).getJa_name().toLowerCase().contains(searchText.toLowerCase()) || mArrayList.get(i).getKo_name().toLowerCase().contains(searchText.toLowerCase())) {
                mFilteredList.add(mArrayList.get(i));
            }
        }
        mAdapter.listFilter (mFilteredList);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == PICK_FILE && resultCode==RESULT_OK) {
            Uri uri = resultData.getData();

            try {
                InputStream in = getContentResolver().openInputStream(uri);
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();

                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                String content = total.toString();

                rq_func.callAttr("LoadNewDatabase", content);
                initRecyclerView();
                Toast.makeText(mContext, "이름 사전 적용 완료!", Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }






}


