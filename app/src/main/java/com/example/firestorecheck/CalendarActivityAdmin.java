package com.example.firestorecheck;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static java.lang.String.valueOf;

public class CalendarActivityAdmin extends AppCompatActivity {
    public static final String GOOGLE_ACCOUNT = "google_account";
    //checking github
    private static final String TAG = "MainActivity";
    CompactCalendarView compactCalendar;
    Long epTime;
    Long ttime;
    Long dday;
    TextView tv, checkHereFull, checkHereLast, checkHereHalf, checkHereHalfLast, monthTab;
    Toolbar mToolbar;
    private GoogleSignInClient googleSignInClient;
    private Button signOut, present, OutButton, todayButton, checkAttendanceButton, checkLastAttendanceButton, eomButton;
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private SimpleDateFormat monthOnScroll = new SimpleDateFormat("MM", Locale.getDefault());
    private String emailFromAdmin;

    private boolean shouldShow = false;
    private CompactCalendarView compactCalendarView;
    private ActionBar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity_admin);
        checkHereFull = findViewById(R.id.check_here_full);
        checkHereHalf = findViewById(R.id.check_here_half);
        //checkHereLast = findViewById(R.id.check_here_last);
        //checkHereHalfLast = findViewById(R.id.check_here_half_last);
        monthTab = findViewById(R.id.month_tab);
        checkAttendanceButton = findViewById(R.id.check_attendance);
        //checkLastAttendanceButton = findViewById(R.id.check_attendance_last);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int year = calendar.get(Calendar.YEAR);
        final long epochTime = calendar.getTimeInMillis();

        //mToolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        emailFromAdmin = getIntent().getStringExtra("email");

        getSupportActionBar().setTitle(emailFromAdmin);

        //this month calander filling
        thisMonthAttendance();

        compactCalendar = findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        Event ev;

        Date todayDate = new Date();
        monthTab.setText(dateFormatForMonth.format(todayDate));

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
/*                monthTab.setText(dateFormatForMonth.format(dateClicked));
                List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);
                Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(dateClicked));
                if (bookingsFromMap != null) {
                    Log.d(TAG, bookingsFromMap.toString());
                    //mutableBookings.clear();
                    for (Event booking : bookingsFromMap) {
                        //mutableBookings.add((String) booking.getData());
                    }
                    //adapter.notifyDataSetChanged();
                }*/
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                final int month = calendar.get(Calendar.MONTH) + 1;
                monthTab.setText(dateFormatForMonth.format(firstDayOfNewMonth));
                if (month == Integer.parseInt(monthOnScroll.format(firstDayOfNewMonth))) {
                    thisMonthAttendance();
                } else if (month - 1 == Integer.parseInt(monthOnScroll.format(firstDayOfNewMonth))) {
                    lastMonthAttendance();
                } else {
                    checkHereHalf.setText("nil");
                    checkHereFull.setText("nil");
                    //do nothing
                }

            }
        });


/*
        //epoch_time testing
        checkAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                final int month = calendar.get(Calendar.MONTH) + 1;
                final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

                CollectionReference colRef = db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                        .collection("Dates");
                Query attendanceQuery = colRef.whereEqualTo("month", month);


                attendanceQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int i = 0,j = 0;
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                epTime = doc.getLong("epoch_time");
                                ttime = doc.getLong("today");
                                if(ttime < 240){
                                    compactCalendar.addEvent(new Event(Color.GRAY, epTime, "Present Today"));
                                    continue;
                                }
                                else if(240 <= ttime && ttime < 480 ){
                                    j++;
                                    compactCalendar.addEvent(new Event(Color.MAGENTA, epTime, "Half day Today"));
                                }else if(ttime > 480){
                                    i++;
                                    //checkHereLast.setText(String.valueOf(i));
                                    compactCalendar.addEvent(new Event(Color.GREEN, epTime, "Present Today"));
                                }

                            }
                            checkHereHalf.setText(valueOf(j));
                            checkHereFull.setText(valueOf(i));

                        }
                    }


                });
            }
        });*/



/*        checkLastAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                final int month = calendar.get(Calendar.MONTH) + 1;
                final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

                CollectionReference colRef = db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                        .collection("Dates");
                Query attendanceQuery = colRef.whereEqualTo("month", month-1);


                attendanceQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int i = 0,j = 0;
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                epTime = doc.getLong("epoch_time");
                                ttime = doc.getLong("today");
                                if(ttime < 240){
                                    compactCalendar.addEvent(new Event(Color.GRAY, epTime, "Present Today"));
                                    continue;
                                }
                                else if(240 <= ttime && ttime < 480 ){
                                    j++;
                                    compactCalendar.addEvent(new Event(Color.MAGENTA, epTime, "Half day Today"));
                                }else if(ttime > 480){
                                    i++;
                                    //checkHereLast.setText(String.valueOf(i));
                                    compactCalendar.addEvent(new Event(Color.GREEN, epTime, "Present Today"));
                                }

                            }
                            checkHereHalfLast.setText(valueOf(j));
                            checkHereLast.setText(valueOf(i));

                        }
                    }


                });
            }
        });*/

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void thisMonthAttendance() {

        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);
        final int year = calendar.get(Calendar.YEAR);
        final long epochTime = calendar.getTimeInMillis();
        //int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        CollectionReference colRef = db.collection("Employees").document(Objects.requireNonNull(emailFromAdmin))
                .collection("Dates");
        Query attendanceQuery = colRef.whereEqualTo("month", month);


        attendanceQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 0, j = 0;

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        epTime = doc.getLong("epoch_time");
                        ttime = doc.getLong("today");
                        dday = doc.getLong("day");


                        if (ttime < 240) {
                            compactCalendar.addEvent(new Event(Color.GRAY, epTime, "not out punch Today"));

                        } else if (ttime < 480) {
                            if (dday != day)
                                j++;
                            compactCalendar.addEvent(new Event(Color.MAGENTA, epTime, "Half day Today"));
                        } else {
                            if (dday != day)
                                i++;
                            //checkHereLast.setText(String.valueOf(i));
                            compactCalendar.addEvent(new Event(Color.GREEN, epTime, "Present Today"));
                        }

                    }
                    if (day - 1 > 0) {
                        for (int k = 1; k <= day - 1; k++) {
                            calendar.set(year, month - 1, k);
                            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                            if (dayOfWeek == Calendar.SUNDAY) {
                                i++;
                            }
                        }
                    }
                    checkHereHalf.setText(valueOf(j));
                    checkHereFull.setText(valueOf(day - 1 - i - j));

                }
            }


        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void lastMonthAttendance() {
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);
        final int year = calendar.get(Calendar.YEAR);
        final long epochTime = calendar.getTimeInMillis();


        final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        CollectionReference colRef = db.collection("Employees").document(emailFromAdmin)
                .collection("Dates");
        Query attendanceQuery = colRef.whereEqualTo("month", month - 1);


        attendanceQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 0, j = 0;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        epTime = doc.getLong("epoch_time");
                        ttime = doc.getLong("today");
                        if (ttime < 240) {
                            compactCalendar.addEvent(new Event(Color.GRAY, epTime, "Present Today"));

                        } else if ( ttime < 480) {
                            j++;
                            compactCalendar.addEvent(new Event(Color.MAGENTA, epTime, "Half day Today"));
                        } else {
                            i++;
                            //checkHereLast.setText(String.valueOf(i));
                            compactCalendar.addEvent(new Event(Color.GREEN, epTime, "Present Today"));
                        }

                    }
                    calendar.set(year, month - 2, 1);
                    int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    for (int k = 1; k <= daysInMonth; k++) {
                        calendar.set(year, month - 2, k);
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                        if (dayOfWeek == Calendar.SUNDAY) {
                            i++;
                        }
                    }

                    checkHereHalf.setText(valueOf(j));
                    checkHereFull.setText(valueOf(daysInMonth - i - j));

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
