package com.example.firestorecheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.valueOf;

public class ProfileActivity extends AppCompatActivity {
    public static final String GOOGLE_ACCOUNT = "google_account";
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
    CompactCalendarView compactCalendar;
    private TextView profileName, profileEmail;
    private ImageView profileImage;
    private Button signOut, present, OutButton, todayButton, checkAttendanceButton, checkLastAttendanceButton, eomButton;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileName = findViewById(R.id.profile_text);
        //profileEmail = findViewById(R.id.profile_email);
        //profileImage = findViewById(R.id.profile_image);
        signOut = findViewById(R.id.sign_out);
        present = findViewById(R.id.Present);
        //tv = findViewById(R.id.mac_addr);
        checkAttendanceButton = findViewById(R.id.check_attendance);
        checkLastAttendanceButton = findViewById(R.id.check_last_attendance);
        checkHereThis = findViewById(R.id.check_here_this);
        checkHereLast = findViewById(R.id.check_here_last);
        eomButton = findViewById(R.id.eom_button);
        OutButton = findViewById(R.id.out_button);
        todayButton = findViewById(R.id.today_hours);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int year = calendar.get(Calendar.YEAR);
        final long epochTime = calendar.getTimeInMillis();

        getMacId();
        setDataOnView();
        final String DID_Local = getDID();

        compactCalendar = findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        Event ev;
        // ev = new Event(Color.BLUE, calendar.getTimeInMillis(), "Teachers' Professional Day");
        compactCalendar.addEvent(new Event(Color.BLUE, calendar.getTimeInMillis(), "Teachers' Professional Day"));

        present.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (getMacId() != null && (getMacId().equals(HardCoddedAddress) || getMacId().equals(HardCoddedAddress_other) || getMacId().equals(HardCoddedAddress_noida))) {
                    final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

                    //fetched_did = "";
                    //did_exists = false;
                    //DID check
                    final Map<String, Object> did_data = new HashMap<>();
                    did_data.put("DID", DID_Local);

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
                                    if (fetched_did == "" || DID_Local.equals(fetched_did)) {
                                        Log.d(TAG, "3DocumentSnapshot successfully written!");
                                        if (fetched_did.equals(DID_Local)) {
                                            //did_exists = true;
                                            Log.d(TAG, "did matches");
                                            //Toast.makeText(getApplicationContext(), "Weppic : Device is registered with us", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //did_exists = false;
                                            //nimk adding DID for first time
                                            db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                    .set(did_data);
                                            Toast.makeText(getApplicationContext(), "Weppic : First time user !", Toast.LENGTH_SHORT).show();
                                        }
                                        //nimk duplicate date entry check
                                        db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                .collection("Dates").document(date).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.getResult().exists()) {
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
                                    if (fetched_did == "" || DID_Local.equals(fetched_did)) {
                                        Log.d(TAG, "3DocumentSnapshot successfully written!");
                                        if (fetched_did.equals(DID_Local)) {
                                            //did_exists = true;
                                            Log.d(TAG, "did matches");
                                            //Toast.makeText(getApplicationContext(), "Weppic : Device is registered with us", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //did_exists = false;
                                            //nimk adding DID for first time
                                            db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                    .set(did_data);
                                            Toast.makeText(getApplicationContext(), "Weppic : First time user !", Toast.LENGTH_SHORT).show();
                                        }

                                        //nimk recording entry
                                        db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                                                .collection("Dates").document(date)
                                                .update("hour_out", hour, "minute_out", minute)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        Toast.makeText(getApplicationContext(), "Weppic : Out time recorded successfully ! ", Toast.LENGTH_SHORT).show();

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


// checking today's working hours
        todayButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
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
                            //profileName.setText(Long.toString(todayHour) );
                            if (todayPunchOutHr < 0) {
                                long totalTime = (hour - todayPunchInHr) * 60 + (minute - todayPunchInMin);
                                //profileName.setText(Long.toString(todayHour) );
                                profileName.setText(totalTime / 60 + ":" + totalTime % 60);
                            } else {
                                long totalTime = (todayPunchOutHr - todayPunchInHr) * 60 + (todayPunchOutMin - todayPunchInMin);
                                //profileName.setText(Long.toString(todayHour) );
                                profileName.setText(totalTime / 60 + ":" + totalTime % 60);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Weppic : Please punch-in first !", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot failed in todayButton!");
                        }
                    }

                });
            }
        });


        //epoch_time testing
        checkAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

                CollectionReference colRef = db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                        .collection("Dates");
                Query attendanceQuery = colRef.whereEqualTo("month", month);


                attendanceQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                epTime = doc.getLong("epoch_time");
                                i++;
                                //checkHereLast.setText(String.valueOf(i));
                                compactCalendar.addEvent(new Event(Color.BLUE, epTime, "Present Today"));
                            }
                            checkHereThis.setText(valueOf(i));

                        }
                    }


                });
            }
        });

        //epoch_time testing
        checkLastAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

                CollectionReference colRef = db.collection("Employees").document(Objects.requireNonNull(googleSignInAccount.getEmail()))
                        .collection("Dates");
                Query attendanceQuery = colRef.whereEqualTo("month", month - 1);


                attendanceQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                epTime = doc.getLong("epoch_time");
                                i++;
                                //checkHereLast.setText(String.valueOf(i));
                                compactCalendar.addEvent(new Event(Color.BLUE, epTime, "Present Today"));
                            }
                            checkHereLast.setText(valueOf(i));

                        }
                    }


                });
            }
        });


        eomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                Intent intent = new Intent(ProfileActivity.this, EomActivity.class);
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


            }
        });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          /*
          Sign-out is initiated by simply calling the googleSignInClient.signOut API. We add a
          listener which will be invoked once the sign out is the successful
           */
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //On Succesfull signout we navigate the user back to LoginActivity
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });

    }


    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

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
        profileName.setText(googleSignInAccount.getDisplayName());
        //profileEmail.setText(googleSignInAccount.getEmail());
    }

    public String getDID() {
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //checkHereThis.setText(androidId);
        return androidId;
    }

}