package mansour.abdullah.el7a2ny;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import mansour.abdullah.el7a2ny.Models.PatientModel;

public class DetailsActivity extends AppCompatActivity
{
    String KEY;

    Button edit_profile_btn,signout_btn,savechanges_btn;

    ImageView profilepicture,callmobile,datepick;
    TextView fullname_txt,nfcid_txt;
    EditText email_field,fullname_field,mobile_field,closemobile_field,address_field,nfcid_field,personalid_field,birthday_field,medicaldiagnosis_field,pharmaceutical_field;
    Spinner bloodtypes;
    String mobile,profile_image_url,NFC;
    String full_name_txt,email_txt,mobile_txt,address_txt,closest_txt,nfc_id_txt,personal_id_txt,date_txt;

    String medicaldiagnosis_txt,pharmaceutical_txt;

    RotateLoading rotateLoading;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        KEY = getIntent().getStringExtra(PatientsFragment.EXTRA_PATIENT_KEY);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        init();

        callmobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone(mobile);
            }
        });

        savechanges_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v)
            {
                full_name_txt = fullname_field.getText().toString();
                email_txt = email_field.getText().toString();
                mobile_txt = mobile_field.getText().toString();
                closest_txt = closemobile_field.getText().toString();
                address_txt = address_field.getText().toString();
                nfc_id_txt = nfcid_field.getText().toString();
                personal_id_txt = personalid_field.getText().toString();
                date_txt = birthday_field.getText().toString();

                medicaldiagnosis_txt = medicaldiagnosis_field.getText().toString();
                pharmaceutical_txt = pharmaceutical_field.getText().toString();

                if (TextUtils.isEmpty(medicaldiagnosis_txt))
                {
                    Toast.makeText(getApplicationContext(), "please enter medical diagnosis", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pharmaceutical_txt))
                {
                    Toast.makeText(getApplicationContext(), "please enter pharmaceutical", Toast.LENGTH_SHORT).show();
                    return;
                }

                medicaldiagnosis_field.setEnabled(false);
                pharmaceutical_field.setEnabled(false);
                savechanges_btn.setEnabled(false);
                edit_profile_btn.setEnabled(true);

                UpdatePatientProfile(full_name_txt,email_txt,personal_id_txt,nfc_id_txt,date_txt,closest_txt,mobile_txt,bloodtypes.getSelectedItemPosition(),address_txt,profile_image_url,medicaldiagnosis_txt,pharmaceutical_txt);
            }
        });

        edit_profile_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                medicaldiagnosis_field.setEnabled(true);
                pharmaceutical_field.setEnabled(true);
                savechanges_btn.setEnabled(true);
                edit_profile_btn.setEnabled(false);
            }
        });

        rotateLoading.start();
        returndata(KEY);
    }

    public void init()
    {
        edit_profile_btn = findViewById(R.id.edit_profile_btn);
        signout_btn = findViewById(R.id.signout_btn);
        savechanges_btn = findViewById(R.id.savechanges_btn);

        fullname_txt = findViewById(R.id.fullname_txt);
        nfcid_txt = findViewById(R.id.nfcid_txt);

        profilepicture = findViewById(R.id.patient_profile_picture);
        callmobile = findViewById(R.id.phonenumber_btn);
        datepick = findViewById(R.id.date_picker);

        email_field = findViewById(R.id.email_field);
        fullname_field = findViewById(R.id.fullname_field);
        mobile_field = findViewById(R.id.mobile_field);
        closemobile_field = findViewById(R.id.closest_mobile_field);
        address_field = findViewById(R.id.address_field);
        nfcid_field = findViewById(R.id.nfc_id_field);
        personalid_field = findViewById(R.id.personal_id_field);
        birthday_field = findViewById(R.id.datebirth_field);
        medicaldiagnosis_field = findViewById(R.id.medical_diagnosis_field);
        pharmaceutical_field = findViewById(R.id.pharmaceutical_field);

        bloodtypes = findViewById(R.id.blood_spinner);
        rotateLoading = findViewById(R.id.rotateloading);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.bloodtypes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        bloodtypes.setAdapter(adapter1);

        bloodtypes.setEnabled(false);
        email_field.setEnabled(false);
        fullname_field.setEnabled(false);
        mobile_field.setEnabled(false);
        closemobile_field.setEnabled(false);
        address_field.setEnabled(false);
        nfcid_field.setEnabled(false);
        personalid_field.setEnabled(false);
        birthday_field.setEnabled(false);
        datepick.setEnabled(false);
        medicaldiagnosis_field.setEnabled(false);
        pharmaceutical_field.setEnabled(false);
        profilepicture.setEnabled(false);
        savechanges_btn.setEnabled(false);
    }

    public void returndata(String key)
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        mDatabase.child("AllUsers").child("Patients").child(key).addListenerForSingleValueEvent(
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // Get user value
                        PatientModel patientModel = dataSnapshot.getValue(PatientModel.class);

                        NFC = patientModel.getNFC_ID();

                        fullname_txt.setText(patientModel.getFullname());
                        nfcid_txt.setText(patientModel.getNFC_ID());
                        mobile = patientModel.getMobilenumber();
                        email_field.setText(patientModel.getEmail());
                        fullname_field.setText(patientModel.getFullname());
                        mobile_field.setText(patientModel.getMobilenumber());
                        closemobile_field.setText(patientModel.getClose_mobile_number());
                        address_field.setText(patientModel.getAddress());
                        nfcid_field.setText(NFC);
                        personalid_field.setText(patientModel.getPersonal_ID());
                        birthday_field.setText(patientModel.getBirthdate());
                        bloodtypes.setSelection(patientModel.getBloodtypes());

                        medicaldiagnosis_field.setText(patientModel.getMedical_diagnosis());
                        pharmaceutical_field.setText(patientModel.getPharmaceutical());

                        profile_image_url = patientModel.getImageurl();


                        Picasso.get()
                                .load(profile_image_url)
                                .placeholder(R.drawable.patient2)
                                .error(R.drawable.patient2)
                                .into(profilepicture);

                        rotateLoading.stop();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Toast.makeText(getApplicationContext(), "can\'t fetch data", Toast.LENGTH_SHORT).show();
                        rotateLoading.stop();
                    }
                });
    }

    public void UpdatePatientProfile(String fullname, String email, String personalid, String nfcid, String birthdate, String closemobile, String mobile, int bloodtype, String address, String imageurl,String med, String pharm)
    {
        PatientModel patientModel = new PatientModel(fullname,email,personalid,nfcid,birthdate,closemobile,mobile,address,imageurl,med,pharm,bloodtype);

        databaseReference.child("Patients").child(NFC).child(KEY).setValue(patientModel);
        databaseReference.child("AllUsers").child("Patients").child(KEY).setValue(patientModel);

        returndata(KEY);

        Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
    }

    private void dialContactPhone(final String phoneNumber)
    {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }
}
