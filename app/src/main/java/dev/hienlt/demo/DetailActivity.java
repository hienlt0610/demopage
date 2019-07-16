package dev.hienlt.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String KEY_ID = "ID";
    private int itemId;
    private DatabaseHelper databaseHelper;
    private SharedPreferences preferences;

    public static Intent create(Context context, int itemId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(KEY_ID, itemId);
        return intent;
    }

    private void parseArgument() {
        itemId = getIntent().getIntExtra(KEY_ID, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        databaseHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences("myapp", MODE_PRIVATE);
        parseArgument();
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        renderUI();
    }

    private void renderUI() {
        openScreen(itemId);
        Item item = databaseHelper.getItem(itemId);
        if (item != null) {
            setTitle(item.getName());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_next:
                goToNext();
                break;
        }
    }

    private void goToNext() {
        Item nextItem = databaseHelper.getNextItem(itemId);
        if (nextItem != null) {
            openScreen(nextItem.getId());
            this.itemId = nextItem.getId();
            setTitle(nextItem.getName());
        } else {
            Toast.makeText(this, "Đây là trang cuối rồi", Toast.LENGTH_SHORT).show();
        }
    }

    private void openScreen(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case 1:
                fragment = new FragmentA();
                break;
            case 2:
                fragment = new FragmentB();
                break;
            case 3:
                fragment = new FragmentC();
                break;
            case 4:
                fragment = new FragmentD();
                break;
            case 5:
                fragment = new FragmentE();
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_content, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
        preferences.edit().putInt("item_id", itemId).apply();
        databaseHelper.openItem(itemId);
    }
}
