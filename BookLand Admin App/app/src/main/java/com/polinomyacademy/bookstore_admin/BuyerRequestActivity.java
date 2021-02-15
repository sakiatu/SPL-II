package com.polinomyacademy.bookstore_admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class BuyerRequestActivity extends AppCompatActivity {
    private static final int REQUEST_PHONE_CALL = 123;
    private RecyclerView recyclerView;
    private BuyerBookAdapter adapter;
    private ArrayList<Book> itemList = new ArrayList<>();
    private String buyerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_request);

        buyerId = getIntent().getStringExtra("buyerId");
        setToolbar("Requests");
        retrieveConfirmedBooks(buyerId);
        initRecyclerView();
    }

    private void setToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        TextView tv_title = toolbar.findViewById(R.id.tv_titleToolbar);
        tv_title.setText(title);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new BuyerBookAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemButtonClickListener(this::handleMarkSoldButtonClick);
    }

    private void handleMarkSoldButtonClick(int position) {
        String key = itemList.get(position).getId();
        FirebaseFirestore.getInstance().collection("Books")
                .document(key)
                .update("sold", true);
        FirebaseFirestore.getInstance().collection("bookedBook")
                .document(buyerId).update(key, FieldValue.delete());

        itemList.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(this, "Marked as Sold", Toast.LENGTH_SHORT).show();

        if (itemList.size() == 0)
            FirebaseFirestore.getInstance().collection("bookedBook")
                    .document(buyerId).delete();

    }

    private void retrieveConfirmedBooks(String buyerId) {
        FirebaseFirestore.getInstance().collection("bookedBook")
                .document(buyerId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    itemList.clear();
                    Map<String, Object> map = snapshot.getData();

                    for (String key : map.keySet()) {
                        FirebaseFirestore.getInstance().collection("Books")
                                .document(key).get()
                                .addOnSuccessListener(snap -> {
                                    if (snap.exists()) {
                                        Book book = snap.toObject(Book.class);
                                        book.setId(key);
                                        itemList.add(book);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buyer_req_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_call)
            callBuyer();
        return super.onOptionsItemSelected(item);
    }


    private void callBuyer() {
        String number = getIntent().getStringExtra("phone");
        if (number != null && number.length() >= 11) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else {
                startActivity(callIntent);
            }
        } else Toast.makeText(this, "Number Not found Or Invalid", Toast.LENGTH_SHORT).show();
    }

}