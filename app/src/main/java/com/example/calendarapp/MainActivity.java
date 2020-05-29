package com.example.calendarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.calendarapp.calendar.CalendarView;
import com.example.calendarapp.calendar.ChooseDateChangeListener;

import org.joda.time.LocalDate;

public class MainActivity extends AppCompatActivity {
private CalendarView calendarView;
private TextView tvPre,tvNext;
private TextView tvDate,tvChoose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView=findViewById(R.id.cv);
        calendarView.setChooseDateChangeListener(new ChooseDateChangeListener() {
            @Override
            public void chooseDateChange(LocalDate chooseDate) {
                 if (chooseDate!=null){
                     tvChoose.setText(chooseDate.getYear()+"年"+chooseDate.getMonthOfYear()+"月"+chooseDate.getDayOfMonth()+"日");
                 }
            }
        });
        tvPre=findViewById(R.id.tv_pre);
        tvPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.preMonth();
                tvDate.setText(calendarView.getCurrentYear()+"年"+calendarView.getCurrentMonth()+"月");
            }
        });
        tvNext=findViewById(R.id.tv_next);
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.nextMonth();
                tvDate.setText(calendarView.getCurrentYear()+"年"+calendarView.getCurrentMonth()+"月");
            }
        });
        tvDate=findViewById(R.id.tv_date);
        tvDate.setText(calendarView.getCurrentYear()+"年"+calendarView.getCurrentMonth()+"月");
        tvChoose=findViewById(R.id.tv_choose);
        calendarView.addSpecialChoose(LocalDate.parse("2020-04-29"));
        calendarView.addSpecialChoose(LocalDate.parse("2020-05-26"));
        calendarView.addSpecialChoose(LocalDate.parse("2020-05-29"));
        calendarView.addSpecialChoose(LocalDate.parse("2020-04-12"));
    }
}
