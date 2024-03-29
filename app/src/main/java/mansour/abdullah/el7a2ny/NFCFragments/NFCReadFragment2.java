package mansour.abdullah.el7a2ny.NFCFragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import mansour.abdullah.el7a2ny.ActivitiesAndFragments.NFCActivity;
import mansour.abdullah.el7a2ny.Listener;
import mansour.abdullah.el7a2ny.ParamedicApp.ParamedicFragments.ParamedicNFC;
import mansour.abdullah.el7a2ny.ParamedicApp.ParamedicMainActivity;
import mansour.abdullah.el7a2ny.ParamedicApp.ParamedicNFCActivity;
import mansour.abdullah.el7a2ny.R;

public class NFCReadFragment2 extends DialogFragment
{
    public static final String TAG = NFCReadFragment.class.getSimpleName();

    public static NFCReadFragment2 newInstance()
    {
        return new NFCReadFragment2();
    }

    private Listener mListener;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_read,container,false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mListener = (ParamedicNFCActivity)context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener.onDialogDismissed();
    }

    public void onNfcDetected(Ndef ndef)
    {
        readFromNFC(ndef);
    }

    private void readFromNFC(Ndef ndef)
    {
        try
        {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            String message = new String(ndefMessage.getRecords()[0].getPayload());
            Log.d(TAG, "readFromNFC: "+message);

            String string = message;

            String[] parts = string.split(",");
            String id = parts[0];
            String name = parts[1];
            String ec = parts[2];
            String type = parts[3];
            String disease = parts[4];

            //mTvMessage.setText(id + "\n" + name + "\n" + ec + "\n" + type + "\n" + disease);

            this.mListener.nfc_id(id);
            this.mListener.patient_name(name);
            this.mListener.patient_number(ec);
            this.mListener.patient_bloodtype(type);
            this.mListener.patient_disease(disease);

            dismiss();

            ndef.close();

        } catch (IOException | FormatException e)
        {
            e.printStackTrace();
        }
    }
}
