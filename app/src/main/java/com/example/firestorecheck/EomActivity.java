package com.example.firestorecheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class EomActivity extends AppCompatActivity {

    Toolbar mToolbar;
    String name;
    private ImageView eomImage;
    private TextView checKing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eom_layout);
        //checKing = findViewById(R.id.checking);

        //mToolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Employee Of the Month");

        //Intent intent = getIntent();
        if (getIntent().hasExtra("EOM"))
            name = getIntent().getStringExtra("EOM");
        if (TextUtils.isEmpty(name))
            return;
        //if(b!=null){
        //name = b.getString("EOM");
        //checKing.setText("You are star !!!");
        //if(name != null) {
        if (name.equals("Banty")) {
            eomImage = findViewById(R.id.eom_image);
            eomImage.setImageResource(R.drawable.banty);
        } else if (name.equals("Amit")) {
            eomImage = findViewById(R.id.eom_image);
            eomImage.setImageResource(R.drawable.amit);
        } else if (name.equals("Ankit")) {
            eomImage = findViewById(R.id.eom_image);
            eomImage.setImageResource(R.drawable.ankit);
        } else if (name.equals("Mukesh")) {
            eomImage = findViewById(R.id.eom_image);
            eomImage.setImageResource(R.drawable.mukesh);
        } else if (name.equals("mk")) {
            eomImage = findViewById(R.id.eom_image);
            eomImage.setImageResource(R.drawable.mk);
        } else {
            //Toast.makeText(getApplicationContext(), "No one ", Toast.LENGTH_SHORT).show();
        }
        //}
        // else
        //   Toast.makeText(getApplicationContext(), "No item ", Toast.LENGTH_SHORT).show();


        //}


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(EomActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
