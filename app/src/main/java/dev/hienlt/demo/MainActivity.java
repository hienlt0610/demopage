package dev.hienlt.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnItemClick {
    private DatabaseHelper databaseHelper;
    private RecyclerView rvItem;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);
        rvItem = findViewById(R.id.rv_item);
        rvItem.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter(this);
        itemAdapter.setOnItemClick(this);
        rvItem.setAdapter(itemAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Item item = itemAdapter.getItem(position);
        startActivity(DetailActivity.create(this, item.getId()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemAdapter.setItems(databaseHelper.getItems());
    }
}
