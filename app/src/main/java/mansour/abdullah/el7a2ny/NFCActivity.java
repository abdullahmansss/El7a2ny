package mansour.abdullah.el7a2ny;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mansour.abdullah.el7a2ny.NFCFragments.NFCReadFragment;
import mansour.abdullah.el7a2ny.NFCFragments.NFCWriteFragment;
import mansour.abdullah.el7a2ny.PateintApp.PatientMainActivity;

public class NFCActivity extends AppCompatActivity implements Listener
{

    public static final String TAG = PatientMainActivity.class.getSimpleName();

    private EditText mEtMessage;
    private Button mBtWrite;
    private Button mBtRead;

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        initViews();
        initNFC();
    }

    private void initViews()
    {
        //mEtMessage = findViewById(R.id.et_message);
        mBtWrite = findViewById(R.id.btn_write);
        mBtRead = findViewById(R.id.btn_read);

        mBtWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWriteFragment();
            }
        });

        mBtRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReadFragment();
            }
        });
    }

    private void initNFC()
    {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private void showWriteFragment()
    {
        isWrite = true;

        mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);

        if (mNfcWriteFragment == null)
        {
            mNfcWriteFragment = NFCWriteFragment.newInstance();
        }
        mNfcWriteFragment.show(getSupportFragmentManager(),NFCWriteFragment.TAG);
    }

    private void showReadFragment()
    {
        mNfcReadFragment = (NFCReadFragment) getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);

        if (mNfcReadFragment == null)
        {

            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getSupportFragmentManager(),NFCReadFragment.TAG);
    }

    @Override
    public void onDialogDisplayed()
    {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed()
    {
        isDialogDisplayed = false;
        isWrite = false;
    }

    @Override
    public void nfc_id(String id)
    {

    }

    @Override
    public void patient_name(String name)
    {

    }

    @Override
    public void patient_number(String number)
    {

    }

    @Override
    public void patient_bloodtype(String bloodtype)
    {

    }

    @Override
    public void patient_disease(String disease)
    {

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: "+intent.getAction());

        if(tag != null)
        {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed)
            {
                if (isWrite)
                {
                    String messageToWrite = mEtMessage.getText().toString();
                    mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                    mNfcWriteFragment.onNfcDetected(ndef,messageToWrite);
                } else
                    {
                        mNfcReadFragment = (NFCReadFragment)getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                        mNfcReadFragment.onNfcDetected(ndef);
                    }
            }
        }
    }
}