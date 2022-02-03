package com.example.novel_reader;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
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
    String current_novel_url;
    MenuItem current_item = null;

    PyObject novelBigTitle;
    List<PyObject> novelContents;
    PyObject novelContent;





    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        final PyObject script = py.getModule("script");

        final Menu menu = navigationView.getMenu();
        menu.clear();





        final Handler btnClickHandler = new Handler() {
            public void handleMessage(Message msg) {
                getSupportActionBar().setTitle(novelBigTitle.toString());
                menu.clear();
                for (int i = 0; i< novelContents.size(); i++) {
                    menu.add(Menu.NONE, i, Menu.NONE, "#"+i+"  "+ novelContents.get(i).toString());
                }
                removeDialog(1);
            }
        };
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

                        novelBigTitle = script.callAttr("get_novel_big_title", novelURL);
                        novelContents = script.callAttr("get_novel_title", novelURL).asList();

                        Message msg = btnClickHandler.obtainMessage();
                        btnClickHandler.sendMessage(msg);
                    }
                }.start();

            }

        });



        // TODO: 이제 할 수 크롤링 최적화 시켜야함.

        final Handler navMenuClickHandler = new Handler() {
            public void handleMessage(Message msg) {
                novelView.setText(novelContent.toString());
                novelView.scrollTo(0, 0);
                drawer.close();
                removeDialog(1);
            }
        };
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
                        novelContent = script.callAttr("get_content", current_novel_url.toString(), itemId);

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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}