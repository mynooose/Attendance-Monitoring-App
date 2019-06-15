package com.example.firestorecheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListCalanderActivity extends AppCompatActivity {

    public ArrayList<String> emailArray = new ArrayList<>();
    //String[] nameArray = {"Banti","Amit","Ankit","Mk","CEO"};
    public ArrayList<String> imageArray = new ArrayList<>();
    public ArrayList<String> nameArray = new ArrayList<>();
    ListView listView;

/*    String[] infoArray = {
            "8 tentacled monster",
            "Delicious in rolls",
            "Great for jumpers",
            "Nice in a stew",
            "Great for shoes",
            "Scary."
    };*/

/*    Integer[] imageArray = {R.drawable.banty,
            R.drawable.amit,
            R.drawable.ankit,
            R.drawable.mk,
            R.drawable.mukesh,
            };*/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_calander_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Employees");

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("Employees");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        emailArray.add(document.getId());
                        nameArray.add(document.getString("Name"));
                        imageArray.add(document.getString("Uri"));
                    }
                    CustomListAdapter whatever = new CustomListAdapter(ListCalanderActivity.this, nameArray, emailArray, imageArray);
                    listView = findViewById(R.id.listviewID);
                    listView.setAdapter(whatever);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                long id) {
                            Intent intent = new Intent(ListCalanderActivity.this, CalendarActivityAdmin.class);
                            String message = emailArray.get(position);
                            intent.putExtra("email", message);
                            startActivity(intent);
                        }
                    });

                } else {
                    //error
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                super.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
