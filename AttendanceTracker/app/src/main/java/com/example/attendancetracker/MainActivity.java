package com.example.attendancetracker;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(null);

        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        Event ev1 = new Event(Color.RED, 1621814400000L, "Absent on CoE 198");
        compactCalendar.addEvent(ev1);

        Event ev2 = new Event(Color.GREEN, 1621209600000L, "Present on all classes");
        compactCalendar.addEvent(ev2);

        Event ev3 = new Event(Color.GREEN, 1621987200000L, "Present on all classes");
        compactCalendar.addEvent(ev3);

        Event ev4 = new Event(Color.RED, 1622160000000L, "Absent on CoE 151 and CoE 134");
        compactCalendar.addEvent(ev4);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();

                if (dateClicked.toString().compareTo("Mon May 24 00:00:00 GMT 2021") == 0) {
                    Toast.makeText(context, "Absent on CoE 198", Toast.LENGTH_SHORT).show();
                }
                else if (dateClicked.toString().compareTo("Mon May 17 00:00:00 GMT 2021") == 0) {
                    Toast.makeText(context, "Present on all classes", Toast.LENGTH_SHORT).show();
                }
                else if (dateClicked.toString().compareTo("Wed May 26 00:00:00 GMT 2021") == 0) {
                    Toast.makeText(context, "Present on all classes", Toast.LENGTH_SHORT).show();
                }
                else if (dateClicked.toString().compareTo("Fri May 28 00:00:00 GMT 2021") == 0) {
                    Toast.makeText(context, "Absent on CoE 151 and CoE 134", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "No Attendance for that day", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }
}