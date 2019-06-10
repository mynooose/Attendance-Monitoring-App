package com.example.firestorecheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String GOOGLE_ACCOUNT = "google_account";
    public CircularProgressBar circularProgressBar;
    TextView tv, checkHereThis, checkHereLast;
    String TAG = "nimk";
    String HardCoddedAddress = "78:11:dc:35:7e:5d";
    String HardCoddedAddress_other = "78:11:dc:35:7e:5e";
    String HardCoddedAddress_noida = "48:ee:0c:45:ba:c6";
    //boolean did_exists;
    String fetched_did, eomString;
    Long epTime;
    long todayPunchInHr = 0;
    long todayPunchInMin = 0;
    long todayPunchOutHr = 0;
    long todayPunchOutMin = 0;
    long working_mins = 0;
    CompactCalendarView compactCalendar;
    private TextView profileName, profileEmail, todayTime;
    private ImageView profileImage;
    private Button signOut, present, OutButton, todayButton, checkAttendanceButton, checkLastAttendanceButton, eomButton;
    private GoogleSignInClient googleSignInClient;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //nimk
        circularProgressBar = findViewById(R.id.yourCircularProgressbar);
        todayTime = findViewById(R.id.today_time);
        //profileEmail = findViewById(R.id.profile_email);
        //profileImage = findViewById(R.id.profile_image);
        present = findViewById(R.id.Present);
        OutButton = findViewById(R.id.out_button);
        todayButton = findViewById(R.id.today_hours);


        //setSupportActionBar(toolbar);
/*        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        // NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        //nimk
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        View headerView = navigationView.getHeaderView(0);

        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        //Picasso.get().load(googleSignInAccount.getPhotoUrl()).centerInside().fit().into(profileImage);
        //profileName.setText(googleSignInAccount.getDisplayName());
        // profileEmail.setText(googleSignInAccount.getEmail());
        TextView headerName = headerView.findViewById(R.id.profile_name);
        headerName.setText(googleSignInAccount.getDisplayName());
        TextView headerEmail = headerView.findViewById(R.id.profile_email);
        headerEmail.setText(googleSignInAccount.getEmail());
        ImageView headerImage = headerView.findViewById(R.id.profile_image);
        Picasso.get().load(googleSignInAccount.getPhotoUrl()).centerInside().fit().into(headerImage);
        Log.d(TAG, String.valueOf(googleSignInAccount.getPhotoUrl()));

        getMacId();
        setDataOnView();
        getTimeToday();
        final String DID_Local = getDID();

/*        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        Event ev;
        // ev = new Event(Color.BLUE, calendar.getTimeInMillis(), "Teachers' Professional Day");
        compactCalendar.addEvent(new Event(Color.BLUE, calendar.getTimeInMillis(), "Teachers' Professional Day"));*/

        //pull to refresh
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {
                getTimeToday();
                pullToRefresh.setRefreshing(false);
            }
        });


        present.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (getMacId() != null && (getMacId().equals(HardCoddedAddress) || getMacId().equals(HardCoddedAddress_other) || getMacId().equals(HardCoddedAddress_noida))) {
                    final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

                    //fetched_did = "";
                    //did_exists = false;
                    //DID check
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    final int minute = calendar.get(Calendar.MINUTE);
                    final int second = calendar.get(Calendar.SECOND);
                    final int day = calendar.get(Calendar.DAY_OF_MONTH);
                    final int month = calendar.get(Calendar.MONTH) + 1;
                    final int year = calendar.get(Calendar.YEAR);
                    final long epochTime = calendar.getTimeInMillis();

                    final Map<String, Object> did_data = new HashMap<>();
                    did_data.put("DID", DID_Local);
                    did_data.put("Uri", String.valueOf(googleSignInAccount.getPhotoUrl()));
                    did_data.put("Name", Objects.requireNonNull(googleSignInAccount.getDisplayName()));

                    final Map<String, Object> employee_data = new HashMap<>();
                    employee_data.put("hour", hour);
                    employee_data.put("minute", minute);
                    employee_data.put("second", second);
                    employee_data.put("day", day);
                    employee_data.put("month", month);
                    employee_data.put("year", year);
                    employee_data.put("email_id", googleSignInAccount.getEmail());
                    employee_data.put("hour_out", -1);
                    employee_data.put("minute_out", -1);
                    employee_data.put("epoch_time", epochTime);
                    employee_data.put("today", 0);


                    Log.d(TAG, "1DocumentSnapshot successfully written!");
                    //nimk did check
                    db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Log.d(TAG, "23DocumentSnapshot successfully written!");
                                    if (documentSnapshot.exists()) {
                                        Log.d(TAG, "2DocumentSnapshot successfully written!");
                                        // boolean did_exists = false;
                                        //Toast.makeText(getApplicationContext(), "Weppic : Doc exists", Toast.LENGTH_SHORT).show();

                                        fetched_did = documentSnapshot.getString("DID");
                                    } else {
                                        fetched_did = "";
                                        Log.d(TAG, "23DocumentSnapshot doesn't exists!");
                                        //Toast.makeText(getApplicationContext(), "Weppic : Doc doesn't exists", Toast.LENGTH_SHORT).show();
                                    }
                                    if (TextUtils.isEmpty(fetched_did) || DID_Local.equals(fetched_did)) {
                                        Log.d(TAG, "3DocumentSnapshot successfully written!");
                                        if (TextUtils.isEmpty(fetched_did)) {
                                            //did_exists = true;
                                            db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                    .set(did_data);
                                            Toast.makeText(getApplicationContext(), "Weppic : First time user !", Toast.LENGTH_SHORT).show();

                                            //Toast.makeText(getApplicationContext(), "Weppic : Device is registered with us", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //did_exists = false;
                                            //nimk adding DID for first time
                                            Log.d(TAG, "did matches");
                                        }
                                        //nimk duplicate date entry check
                                        db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                .collection("Dates").document(date).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.getResult().exists()) {
                                                            getTimeToday();
                                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                                            Toast.makeText(getApplicationContext(), "Weppic : Attendance already recorded earlier. Please get back to work :)", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            //nimk recording entry
                                                            db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                                    .collection("Dates").document(date)
                                                                    .set(employee_data)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                            Toast.makeText(getApplicationContext(), "Weppic : Attendance successfully recorded! ", Toast.LENGTH_SHORT).show();
                                                                            getTimeToday();
                                                                            //nimk kill activity
                                                                         /* Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                                                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                                                                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            startActivity(homeIntent);*/

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w(TAG, "Error writing document", e);
                                                                        }
                                                                    });

                                                            //tv.setText("Present : Attendance recorded successfully !");
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Weppic : This is not your registered device. Please check with your HR TEAM !", Toast.LENGTH_SHORT).show();
                                    }
                                    //Toast.makeText(getApplicationContext(), "Weppic 1: First time user", Toast.LENGTH_SHORT).show();
                                }

                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

                    //checkHereLast.setText(fetched_did);


                } else {
                    Toast.makeText(getApplicationContext(), "Absent : Please turn on Location & connect to office WiFi !", Toast.LENGTH_SHORT).show();
                } //if-else mac_address wala
                //tv.setText("Absent : Please turn on Location & connect to office WiFi !");
            } //onclick present wala
        });


//out button click
        OutButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (getMacId() != null && (getMacId().equals(HardCoddedAddress) || getMacId().equals(HardCoddedAddress_other) || getMacId().equals(HardCoddedAddress_noida))) {
                    final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);


                    //Calendar thngs
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    final int minute = calendar.get(Calendar.MINUTE);
                    final int second = calendar.get(Calendar.SECOND);
                    final int day = calendar.get(Calendar.DAY_OF_MONTH);
                    final int month = calendar.get(Calendar.MONTH) + 1;
                    final int year = calendar.get(Calendar.YEAR);
                    final long epochTime = calendar.getTimeInMillis();
                    //fetched_did = "";
                    //did_exists = false;
                    //DID check
                    final Map<String, Object> did_data = new HashMap<>();
                    did_data.put("DID", DID_Local);

                    final Map<String, Object> employee_data = new HashMap<>();
/*                    employee_data.put("hour", hour);
                    employee_data.put("minute", minute);
                    employee_data.put("second", second);
                    employee_data.put("day", day);
                    employee_data.put("month", month);
                    employee_data.put("year", year);
                    employee_data.put("email_id", googleSignInAccount.getEmail());*/
                    employee_data.put("hour_out", -1);
                    employee_data.put("minute_out", -1);
                    employee_data.put("today", 0);
                    /*                    employee_data.put("epoch_time",epochTime);*/


                    Log.d(TAG, "1DocumentSnapshot successfully written!");
                    //nimk did check
                    db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Log.d(TAG, "23DocumentSnapshot successfully written!");
                                    if (documentSnapshot.exists()) {
                                        Log.d(TAG, "2DocumentSnapshot successfully written!");
                                        // boolean did_exists = false;
                                        //Toast.makeText(getApplicationContext(), "Weppic : Doc exists", Toast.LENGTH_SHORT).show();

                                        fetched_did = documentSnapshot.getString("DID");
                                    } else {
                                        fetched_did = "";
                                        Log.d(TAG, "23DocumentSnapshot doesn't exists!");
                                        //Toast.makeText(getApplicationContext(), "Weppic : Doc doesn't exists", Toast.LENGTH_SHORT).show();
                                    }
                                    if (TextUtils.isEmpty(fetched_did) || DID_Local.equals(fetched_did)) {
                                        Log.d(TAG, "3DocumentSnapshot successfully written!");
                                        if (TextUtils.isEmpty(fetched_did)) {
                                            //did_exists = true;
                                            db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                    .set(did_data);
                                            Toast.makeText(getApplicationContext(), "Weppic : First time user !", Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(getApplicationContext(), "Weppic : Device is registered with us", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d(TAG, "did matches");
                                            //did_exists = false;
                                            //nimk adding DID for first time
                                        }

                                        //updating working hours
                                        DocumentReference docRef = db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                .collection("Dates").document(date);
                                        //Query attendanceQuery = docRef.whereEqualTo("month", month);
                                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    todayPunchInHr = documentSnapshot.getLong("hour");
                                                    todayPunchInMin = documentSnapshot.getLong("minute");
                                                    working_mins = (hour - todayPunchInHr) * 60 + (minute - todayPunchInMin);

                                                    db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                            .collection("Dates").document(date)
                                                            .update("today", working_mins)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "working_mins successfully written!");
                                                                    //Toast.makeText(getApplicationContext(), "Weppic : Out time recorded successfully ! ", Toast.LENGTH_SHORT).show();

                                                                    //nimk kill activity
                                                                         /* Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                                                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                                                                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            startActivity(homeIntent);*/

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error writing document", e);
                                                                }
                                                            });

                                                } else {
                                                    //Toast.makeText(getApplicationContext(), "Weppic : Please punch-in first !", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "DocumentSnapshot failed in todayButton!");
                                                }
                                            }

                                        });

                                        //nimk recording entry
                                        db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                .collection("Dates").document(date)
                                                .update("hour_out", hour, "minute_out", minute)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        Toast.makeText(getApplicationContext(), "Weppic : Out time recorded successfully ! ", Toast.LENGTH_SHORT).show();
                                                        getTimeToday();
                                                        //nimk kill activity
                                                                         /* Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                                                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                                                                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            startActivity(homeIntent);*/

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error writing document", e);
                                                    }
                                                });

                                        //tv.setText("Present : Attendance recorded successfully !");

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Weppic : This is not your registered device. Please check with your HR TEAM !", Toast.LENGTH_SHORT).show();
                                    }
                                    //Toast.makeText(getApplicationContext(), "Weppic 1: First time user", Toast.LENGTH_SHORT).show();
                                }

                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

                    //checkHereLast.setText(fetched_did);


                } else {
                    Toast.makeText(getApplicationContext(), "Absent : Please turn on Location & connect to office WiFi !", Toast.LENGTH_SHORT).show();
                } //if-else mac_address wala
                //tv.setText("Absent : Please turn on Location & connect to office WiFi !");
            } //onclick present wala
        });

/*
// checking today's working hours
        todayButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);
                final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
                DocumentReference docRef = db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                        .collection("Dates").document(date);
                //Query attendanceQuery = docRef.whereEqualTo("month", month);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            todayPunchInHr = documentSnapshot.getLong("hour");
                            todayPunchInMin = documentSnapshot.getLong("minute");
                            todayPunchOutHr = documentSnapshot.getLong("hour_out");
                            todayPunchOutMin = documentSnapshot.getLong("minute_out");
                            //setting time
                            //todayTime.setText(Long.toString(todayHour) );
                            if (todayPunchOutHr < 0) {
                                long totalTime = (hour - todayPunchInHr) * 60 + (minute - todayPunchInMin);
                                //todayTime.setText(Long.toString(todayHour) );
                                todayTime.setText(Long.toString(totalTime / 60) + ":" + Long.toString(totalTime % 60));
                            } else {
                                long totalTime = (todayPunchOutHr - todayPunchInHr) * 60 + (todayPunchOutMin - todayPunchInMin);
                                //todayTime.setText(Long.toString(todayHour) );
                                todayTime.setText(Long.toString(totalTime / 60) + ":" + Long.toString(totalTime % 60));
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Weppic : Please punch-in first !", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot failed in todayButton!");
                        }
                    }

                });
            }
        });*/

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            //calender view
            final GoogleSignInAccount googleSignInAccountAdmin = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
            if ((googleSignInAccountAdmin.getEmail().equals("weppic.mk@gmail.com")) || (googleSignInAccountAdmin.getEmail().equals("weppicdesigns@gmail.com"))) {
                Intent intent = new Intent(NavigationActivity.this, ListCalanderActivity.class);
                GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
                intent.putExtra(CalendarActivity.GOOGLE_ACCOUNT, googleSignInAccount);
                startActivity(intent);
            } else {
                Intent intent = new Intent(NavigationActivity.this, CalendarActivity.class);
                GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
                intent.putExtra(CalendarActivity.GOOGLE_ACCOUNT, googleSignInAccount);
                startActivity(intent);
            }
        } else if (id == R.id.nav_slideshow) {
            //eom
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference colRef = db.collection("EOM");
            Query eomQuery = colRef.whereEqualTo("IsEOM", 1);
            eomQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d(TAG, "nini oncomplete!");
                    if (task.isSuccessful()) {
                        Log.d(TAG, "nini onsuccess!");

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            eomString = doc.getString("id");
                            Intent intent = new Intent(NavigationActivity.this, EomActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EOM", eomString);
                            startActivity(intent);
                            Log.d(TAG, "nini for!");
                            //checkHereLast.setText(String.valueOf(i));
                            //checkHereThis.setText(eomString);
                        }
                    }
                }
            });

        } else if (id == R.id.nav_share) {
            //signout
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //On Succesfull signout we navigate the user back to LoginActivity
                    Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(NavigationActivity.this, ContactUsActivity.class);
            startActivity(intent);
            //contact us

        } else if (id == R.id.nav_web) {
            //website
            Uri uri = Uri.parse("http://www.weppic.com"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public String getMacId() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //tv.setText(wifiInfo.getBSSID());
        Log.d(TAG, "getMacId: " + wifiInfo.getBSSID());
        //Toast.makeText(getApplicationContext(), wifiInfo.getBSSID(), Toast.LENGTH_LONG).show();
        return wifiInfo.getBSSID();
    }

    private void setDataOnView() {
        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        //Picasso.get().load(googleSignInAccount.getPhotoUrl()).centerInside().fit().into(profileImage);
        //profileName.setText(googleSignInAccount.getDisplayName());
        //profileEmail.setText(googleSignInAccount.getEmail());
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getTimeToday() {// checking today's working hours

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        DocumentReference docRef = db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                .collection("Dates").document(date);
        //Query attendanceQuery = docRef.whereEqualTo("month", month);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    todayPunchInHr = documentSnapshot.getLong("hour");
                    todayPunchInMin = documentSnapshot.getLong("minute");
                    todayPunchOutHr = documentSnapshot.getLong("hour_out");
                    todayPunchOutMin = documentSnapshot.getLong("minute_out");
                    //setting time
                    //todayTime.setText(Long.toString(todayHour) );
                    int animationDuration = 28800;
                    if (todayPunchOutHr < 0) {
                        long totalTime = (hour - todayPunchInHr) * 60 + (minute - todayPunchInMin);
                        //todayTime.setText(Long.toString(todayHour) );
                        int progress = (int) (totalTime * 6000) / animationDuration;
                        circularProgressBar.setProgressWithAnimation(progress, animationDuration);
                        if (totalTime % 60 < 10)
                            todayTime.setText(totalTime / 60 + ":0" + totalTime % 60);
                        else
                            todayTime.setText(totalTime / 60 + ":" + totalTime % 60);
                    } else {
                        long totalTime = (todayPunchOutHr - todayPunchInHr) * 60 + (todayPunchOutMin - todayPunchInMin);
                        //todayTime.setText(Long.toString(todayHour) );
                        int progress = (int) (totalTime * 6000) / animationDuration;
                        circularProgressBar.setProgressWithAnimation(progress, animationDuration);
                        if (totalTime % 60 < 10)
                            todayTime.setText(totalTime / 60 + ":0" + totalTime % 60);
                        else
                            todayTime.setText(totalTime / 60 + ":" + totalTime % 60);
                    }
                } else {
                    todayTime.setText("Absent");
                    Toast.makeText(getApplicationContext(), "Weppic : Please punch-in first !", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DocumentSnapshot failed in todayButton!");
                }
            }

        });

    }


    public String getDID() {
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //checkHereThis.setText(androidId);
        return androidId;
    }
}
