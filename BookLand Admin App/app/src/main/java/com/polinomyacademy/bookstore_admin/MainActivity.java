package com.polinomyacademy.bookstore_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button btn_seeBuyerRequest;
    private Button btn_seeSellerRequest;
    private Button btn_handleCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setToolbar("Admin");
    }

    private void setToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        TextView tv_title = toolbar.findViewById(R.id.tv_titleToolbar);
        tv_title.setText(title);
    }

    private void initViews() {
        btn_handleCategory = findViewById(R.id.btn_handleCategory);
        btn_seeSellerRequest = findViewById(R.id.btn_sellerRequest);
        btn_seeBuyerRequest = findViewById(R.id.btn_buyerRequest);

        btn_handleCategory.setOnClickListener(v -> openActivity(CategoryActivity.class));
        btn_seeSellerRequest.setOnClickListener(v -> openActivity(SellerActivity.class));
        btn_seeBuyerRequest.setOnClickListener(v -> openActivity(BuyerActivity.class));

    }

    private void openActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_signOut) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}