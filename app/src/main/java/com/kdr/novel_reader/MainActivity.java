package com.kdr.novel_reader;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.novel_reader.R;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.novel_reader.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Context context_main;

    String current_novel_url;
    MenuItem current_item = null;

    PyObject currentNovelClass = null;

    PyObject novelBigTitle;
    List<PyObject> novelContents = null;
    PyObject novelContent;


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context_main = this;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View nav_header_view = navigationView.getHeaderView(0);
        Button button = (Button) nav_header_view.findViewById(R.id.button);
        TextInputLayout textInputLayout = (TextInputLayout) nav_header_view.findViewById(R.id.textInputLayout);


        TextView novelView = (TextView) findViewById(R.id.novelView);
        novelView.setMovementMethod(new ScrollingMovementMethod());


        if(!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();
        final PyObject novel_reader = py.getModule("novel_reader");

        final Menu menu = navigationView.getMenu();
        menu.clear();

        Button novelPrevBtn = (Button) findViewById(R.id.novelPrevBtn);
        Button novelNxtBtn = (Button) findViewById(R.id.novelNxtBtn);


        final Handler btnClickHandler = new Handler() {
            public void handleMessage(Message msg) {
                getSupportActionBar().setTitle(novelBigTitle.toString());
                menu.clear();
                for (int i = 0; i< novelContents.size(); i++) {
                    menu.add(Menu.NONE, i, Menu.NONE, "#"+(i+1)+"  "+ novelContents.get(i).toString());
                }
                removeDialog(1);
            }
        };

        final Handler navMenuClickHandler = new Handler() {
            public void handleMessage(Message msg) {
                novelView.setText(novelContent.toString());
                novelView.scrollTo(0, 0);
                drawer.close();
                removeDialog(1);
            }
        };





        novelPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_item != null) {
                    int currId = current_item.getItemId();
                    if (currId != 0) {
                        showDialog(1);

                        if (current_item != null) {
                            SpannableString w = new SpannableString(current_item.getTitle().toString());
                            w.setSpan(new ForegroundColorSpan(Color.WHITE), 0, w.length(), 0);
                            current_item.setTitle(w);
                        }

                        MenuItem item = navigationView.getMenu().getItem(currId-1);
                        SpannableString s = new SpannableString(item.getTitle().toString());
                        s.setSpan(new ForegroundColorSpan(Color.CYAN), 0, s.length(), 0);
                        item.setTitle(s);
                        current_item = item;


                        new Thread() {
                            @Override
                            public void run() {
                                novelContent = currentNovelClass.callAttr("get_content", currId-1);

                                Message msg = navMenuClickHandler.obtainMessage();
                                navMenuClickHandler.sendMessage(msg);
                            }
                        }.start();
                    }
                }
            }
        });

        novelNxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_item != null && novelContents != null) {
                    int currId = current_item.getItemId();
                    if (currId != novelContents.size()-1) {

                        showDialog(1);

                        if (current_item != null) {
                            SpannableString w = new SpannableString(current_item.getTitle().toString());
                            w.setSpan(new ForegroundColorSpan(Color.WHITE), 0, w.length(), 0);
                            current_item.setTitle(w);
                        }

                        MenuItem item = navigationView.getMenu().getItem(currId+1);
                        SpannableString s = new SpannableString(item.getTitle().toString());
                        s.setSpan(new ForegroundColorSpan(Color.CYAN), 0, s.length(), 0);
                        item.setTitle(s);
                        current_item = item;


                        new Thread() {
                            @Override
                            public void run() {
                                novelContent = currentNovelClass.callAttr("get_content",currId+1);

                                Message msg = navMenuClickHandler.obtainMessage();
                                navMenuClickHandler.sendMessage(msg);
                            }
                        }.start();

                    }
                }
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);

                new Thread() {
                    @Override
                    public void run() {
                        current_item = null;
                        String novelURL = textInputLayout.getEditText().getText().toString();
                        current_novel_url = novelURL;

                        currentNovelClass = novel_reader.callAttr("NovelReader", novelURL);

                        novelBigTitle = currentNovelClass.callAttr("get_big_title");
                        novelContents = currentNovelClass.callAttr("get_small_titles").asList();

                        Message msg = btnClickHandler.obtainMessage();
                        btnClickHandler.sendMessage(msg);
                    }
                }.start();

            }

        });





        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                showDialog(1);

                if (current_item != null) {
                    SpannableString w = new SpannableString(current_item.getTitle().toString());
                    w.setSpan(new ForegroundColorSpan(Color.WHITE), 0, w.length(), 0);
                    current_item.setTitle(w);
                }

                int itemId = item.getItemId();
                SpannableString s = new SpannableString(item.getTitle().toString());
                s.setSpan(new ForegroundColorSpan(Color.CYAN), 0, s.length(), 0);
                item.setTitle(s);
                current_item = item;


                new Thread() {
                    @Override
                    public void run() {
                        novelContent = currentNovelClass.callAttr("get_content", itemId);

                        Message msg = navMenuClickHandler.obtainMessage();
                        navMenuClickHandler.sendMessage(msg);
                    }
                }.start();

                return true;
            }
        });
    }





    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog dialog = new ProgressDialog(this, R.style.dialogStyle);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setMessage("소설을 불러오는 중.. 잠시만 기다려주세요.");

        return dialog;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Intent set_Intent = new Intent(MainActivity.this, Property_Settings.class);
                startActivity(set_Intent);
                break;

            case R.id.dev_help:
                Uri uri = Uri.parse("https://github.com/kdrkdrkdr/novel_reader");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                break;

        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}