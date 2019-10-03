package com.postaplus.etrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarSpinnerAdapter extends BaseAdapter {

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd MMM yyyy");

    private LayoutInflater mInflater;
    private Calendar mCalendar;
    private int mDayCount;
    private int mLastRequestedDay =90;

    public CalendarSpinnerAdapter(Context context, int dayCount) {
        mInflater = LayoutInflater.from(context);
        mDayCount = dayCount;
        mCalendar = Calendar.getInstance();
    }

    @Override
    public int getCount() {
        return mDayCount;
    }

    @Override
    public Calendar getItem(int position) {
        mCalendar.add(Calendar.DAY_OF_YEAR, position - mLastRequestedDay);
        mLastRequestedDay = position;

        return mCalendar;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        Calendar item = getItem(position);
        Calendar it1= getItem(position);
        ((TextView) convertView).setText(mDateFormat.format(item.getTimeInMillis()));
        System.out.println("item:"+item.getTime());
      //  mDateFormat.format(item);
        System.out.println("convertView:"+((TextView) convertView).getText());

        return ((TextView) convertView);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        return getView(position, convertView, parent);

    }
    private String getSelectedDateAsString(Spinner dateSpinner) {
        Calendar selectedDate = (Calendar) dateSpinner.getSelectedItem();
        return new SimpleDateFormat("yyyy-dd-mm").format(selectedDate.getTimeInMillis());
    }

}
