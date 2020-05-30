package com.example.calendarapp.calendar;


import org.joda.time.LocalDate;

public interface IDrawCalendar {
    public void drawCalendar(CalendarView.CalendarAdapter.CalendarHolder holder, LocalDate curDate);

}
