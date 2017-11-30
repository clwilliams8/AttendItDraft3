package com.example.colto.attenditdraft3;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by colto on 11/19/2017.
 */

public class MyClassesStudentAdaptor extends RecyclerView.Adapter<MyClassesStudentAdaptor.UserViewHolder>  {

    private List<MyClassesModel> list;
    private StudentActivity studentActivity;
    private String KEY_NAME = "somekeyname";
    private FirebaseDatabase database;
    private DatabaseReference teacherRecord;
    private DatabaseReference studentRecord;

    public MyClassesStudentAdaptor(List<MyClassesModel> list) {
        this.list = list;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item3, parent, false));
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {

        MyClassesModel myClass = list.get(position);

        holder.itemClassName.setText(myClass.className);
        holder.itemClassStartTime.setText(myClass.classStartTime);
        holder.itemClassLateTime.setText(myClass.classLateTime);
        holder.itemClassAbsentTime.setText(myClass.classAbsentTime);
        holder.teacherName.setText(myClass.teacherName);
        holder.studentNameValue.setText(myClass.studentName);
        holder.day1.setText(myClass.day1);
        holder.day2.setText(myClass.day2);
        holder.day3.setText(myClass.day3);
        holder.day4.setText(myClass.day4);
        holder.day5.setText(myClass.day5);
        holder.day6.setText(myClass.day6);
        holder.day7.setText(myClass.day7);


        ////BUTTON NOT CLICKABLE UNTIL DAY AND TIME ARE CORRECT
        holder.signInButton.setEnabled(false);
        Boolean isCorrectDay = false;
        Boolean isCorrectTime = true;
        String currentWeekDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
        //DateFormat currentTime = new SimpleDateFormat("h:mm a"); //get the exact time in format "8:00am"
        Calendar cal = Calendar.getInstance();
        String timeGiven = holder.itemClassStartTime.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        Date time1 = null;
        try {
            time1 = sdf.parse(timeGiven); //start time of the class is set
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String currentTime = sdf.format(new Date());
        Date time2 = null;

        try {
            time2 = sdf.parse(currentTime); //currentTime
        } catch (ParseException e) {
            e.printStackTrace();
        }



        if (time1.compareTo(time2) <= 0) {

            //if the startTime of class is less than the currentTime, then not time for sign in
            isCorrectTime = false;

        } else if (time1.compareTo(time2) >= 0) {

            //if the startTime of class is greater than the currentTime, then its time for class
            isCorrectTime = true;

        } else {

            //Times are the same
            isCorrectTime = true;

        }



        //String realTime = sdf.format(cal.getTime());


        //If currentWeekDay is = any of the class days, then isCorrectDay is true.

        if(currentWeekDay.equals(holder.day1.getText().toString())
                | currentWeekDay.equals(holder.day2.getText().toString())
                | currentWeekDay.equals(holder.day3.getText().toString())
                | currentWeekDay.equals(holder.day4.getText().toString())
                | currentWeekDay.equals(holder.day5.getText().toString())
                | currentWeekDay.equals(holder.day6.getText().toString())
                | currentWeekDay.equals(holder.day7.getText().toString())) {
            isCorrectDay = true;
            holder.signInButton.setEnabled(true);
        }

        if(isCorrectDay && isCorrectTime){
            holder.signInButton.setEnabled(true);
        }


        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), 0, 0, "test?");
                menu.add(holder.getAdapterPosition(), 1, 0, "test2?");

            }
        });


    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    class  UserViewHolder extends RecyclerView.ViewHolder {

        TextView itemClassName, itemClassStartTime, itemClassLateTime, itemClassAbsentTime;
        TextView day1, day2, day3, day4, day5, day6, day7;
        TextView teacherName, studentNameValue;
        Button signInButton;


        public UserViewHolder(final View itemView) {
            super(itemView);

            itemClassName = (TextView) itemView.findViewById(R.id.itemClassNameForStudent);
            itemClassStartTime = (TextView) itemView.findViewById(R.id.itemClassTimesForStudent);
            itemClassLateTime = (TextView) itemView.findViewById(R.id.classLateTime);
            itemClassAbsentTime = (TextView) itemView.findViewById(R.id.classAbsentTime);
            day1 = (TextView) itemView.findViewById(R.id.day1);
            day2 = (TextView) itemView.findViewById(R.id.day2);
            day3 = (TextView) itemView.findViewById(R.id.day3);
            day4 = (TextView) itemView.findViewById(R.id.day4);
            day5 = (TextView) itemView.findViewById(R.id.day5);
            day6 = (TextView) itemView.findViewById(R.id.day6);
            day7 = (TextView) itemView.findViewById(R.id.day7);
            teacherName = (TextView) itemView.findViewById(R.id.teacherName);
            studentNameValue = (TextView) itemView.findViewById(R.id.studentNameValue);

            signInButton = (Button) itemView.findViewById(R.id.SignInButtonForStudents);


            //Toast.makeText(itemView.getContext(), "You may sign into " + itemClassName.getText().toString(), Toast.LENGTH_SHORT).show();

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //ONCLICK - FINGERPRINT AUTH.
                    Toast.makeText(itemView.getContext(), "Its the correct day of the week boy", Toast.LENGTH_SHORT).show();
                            //below is fingerprint stuff that didnt work

//                    KeyguardManager keyguardManager;
//                    keyguardManager = (KeyguardManager) itemView.getContext().getSystemService(Context.KEYGUARD_SERVICE);
//                    FingerprintManager fingerprintManager;
//                    fingerprintManager = (FingerprintManager) itemView.getContext().getSystemService(Context.FINGERPRINT_SERVICE);
//
//
//
//                    //Create Keystore object
//                    KeyStore keyStore;
//
//                    try {
//                            keyStore = KeyStore.getInstance("AndroidKeyStore");
//                    } catch (Exception e) {
//                            Log.e("Keystore", e.getMessage());
//                            return;
//                    }
//
//                    //Create KeyGen object
//                    KeyGenerator keyGenerator;
//
//                    try {
//                            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
//                    } catch (Exception e) {
//                        Log.e("KeyGenerator", e.getMessage());
//                        return;
//                    }
//
//
//                    try {   // BREAKS HERE
//                        keyStore.load(null);
//                        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(KEY_NAME,
//                                KeyProperties.PURPOSE_ENCRYPT |
//                                        KeyProperties.PURPOSE_DECRYPT)
//                                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//                                // Require the user to authenticate with a fingerprint to authorize every use
//                                // of the key
//                                .setUserAuthenticationRequired(true)
//                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            builder.setInvalidatedByBiometricEnrollment(false);
//                        }
//                        keyGenerator.init(builder.build());
//                        keyGenerator.generateKey();
//                        Toast.makeText(itemView.getContext(), "Place your finger on your phone's scanner.", Toast.LENGTH_LONG).show();
//
//                    } catch (Exception e) {
//                        Log.e("Generating keys", e.getMessage());
//                        return;
//                    } //End KeyGen Object
//
//
//
//                    //Create Cipher object
//                    Cipher cipher;
//
//                    try {
//                            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES
//                                    + "/" + KeyProperties.BLOCK_MODE_CBC
//                                    + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
//                    } catch (Exception e) {
//                        Log.e("Cipher", e.getMessage());
//                        return;
//                    } //End Cipher object
//
//                    try {
//                            keyStore.load(null);
//                            SecretKey key =  (SecretKey) keyStore.getKey(KEY_NAME, null);
//                            cipher.init(Cipher.ENCRYPT_MODE, key);
//                    } catch (Exception e) {
//                        Log.e("Secret key", e.getMessage());
//                        return;
//                    }// End Cipher Object
//
//
//                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
//
//                    CancellationSignal cancellationSignal = new CancellationSignal();
//
//                    fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0 ,new AuthenticationHandler(itemView.getContext()), null);


                    //ON AUTH - CREATE RECORD IF DOESN'T EXIST, UPDATE RECORD WITH PRESENT,LATE,ABSENT MARK

                            //Put records into database

                    //DATABASE - User signed in, so make a Teacher record and Student record.

                    database = FirebaseDatabase.getInstance();

                    //teacherRecord
                    teacherRecord = database.getReference("TeacherClass")
                            .child(teacherName.getText().toString())
                            .child(itemClassName.getText().toString())
                            .child("ClassRecord").child(studentNameValue.getText().toString());

                    //studentRecord
                    studentRecord = database.getReference("TeacherClass")
                            .child(teacherName.getText().toString())
                            .child(itemClassName.getText().toString())
                            .child("StudentsEnrolled").child(studentNameValue.getText().toString())
                            .child("StudentRecord");

                    //Create records:

                    




                    Toast.makeText(itemView.getContext(), "Made it thru ref", Toast.LENGTH_SHORT).show();



                }
            });

        }

    }
}
