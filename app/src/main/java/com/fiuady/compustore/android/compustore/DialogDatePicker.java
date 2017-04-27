package com.fiuady.compustore.android.compustore;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Kuro on 25/04/2017.
 */

public class DialogDatePicker extends DialogFragment {
    public interface DialogDateListener {
        void onDialogDateSet(DialogFragment dialog, String tag, int year, int month, int day);
    }
    public static String MIN_DATE = "com.fiuady.compustore.android.compustore.dialogDatePicker.mindate";
    public static String MAX_DATE = "com.fiuady.compustore.android.compustore.dialogDatePicker.maxdate";

    private DialogDateListener mListener;
    private String tag;
    public DialogDatePicker ()
    {
        mListener = null;
    }
    static DialogDatePicker newInstance (String tag, int year, int month, int day, Bundle minmaxDate)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("YEAR", year);
        bundle.putInt("MONTH", month);
        bundle.putInt("DAY", day);
        bundle.putString("TAG", tag);
        bundle.putBundle("MINMAXDATE", minmaxDate);
        DialogDatePicker dialogDatePicker = new DialogDatePicker();
        dialogDatePicker.setArguments(bundle);
        return dialogDatePicker;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        Bundle args = getArguments();
        //final Calendar c = Calendar.getInstance();
        //int year = c.get(Calendar.YEAR);
        //int month = c.get(Calendar.MONTH);
        //int day = c.get(Calendar.DAY_OF_MONTH);
        int year = args.getInt("YEAR");
        int month = args.getInt("MONTH");
        int day = args.getInt("DAY");
        tag = args.getString("TAG");
        Bundle bundle = args.getBundle("MINMAXDATE");
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            }
        }, year, month, day);
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogDateSet(DialogDatePicker.this, tag, datePickerDialog.getDatePicker().getYear(),
                        datePickerDialog.getDatePicker().getMonth(),
                        datePickerDialog.getDatePicker().getDayOfMonth());
            }
        });
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        if(bundle!=null)
        {
            Calendar minDate = (Calendar) bundle.getSerializable(MIN_DATE);
            Calendar maxDate = (Calendar) bundle.getSerializable(MAX_DATE);
            datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime().getTime());
        }

        return datePickerDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (DialogDateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe de implementar DialogDateListener para que detecte los clicks del dialogo");
        }
    }

   //public void onDateSet(DatePicker view, int year, int month, int day) {
   //    if(mListener!=null) {
   //        //mListener.onDialogDateSet(DialogDatePicker.this, tag, year, month, day);
   //    }
   //}
}
