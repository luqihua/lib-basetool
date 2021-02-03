package com.lu.tool.widget.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.DatePicker;
import android.widget.TextView;

import com.lu.tool.util.DateFormatUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lu.basetool.R;

/**
 * 日期选择弹窗
 * Created  on 2017/2/20.
 * by luqihua
 */

public class DatePickerDialogUtil {

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static Calendar sCalendar = Calendar.getInstance();

    public static void showDialog(Context context, final TextView v, @NonNull final OnDatePickListener listener) {

        final String currentData = v.getText().toString();

        Calendar calendar = Calendar.getInstance();
        Date date = DateFormatUtil.str2Date(currentData);
        if (date == null) {
            calendar = sCalendar;
        } else {
            calendar.setTime(date);
        }

        DatePickerDialog dialog = new DatePickerDialog(context, R.style.date_picker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                try {
                    String dateStr = year + "-" + (month + 1) + "-" + dayOfMonth;
                    if (listener != null) {

                        listener.onDatePick(v, sDateFormat.parse(dateStr), dateStr);

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }


    public interface OnDatePickListener {
        void onDatePick(TextView v, Date date, String dateStr);
    }


}
