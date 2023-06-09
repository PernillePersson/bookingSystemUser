package com.example.bookingsystemuser.model;


import com.example.bookingsystemuser.model.objects.Booking;
import com.example.bookingsystemuser.model.objects.Company;
import com.example.bookingsystemuser.model.objects.Forløb;
import com.example.bookingsystemuser.model.objects.Organisation;

import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public interface BookingDAO {
    public List<Booking> getAllBooking();
    public void addBooking(String fn, String ln,
                           String mail, int phone, char bt,
                           char catering, LocalDate bd,
                           String bk, Time st, Time et, int p);

    public void updateBooking(int id, char bt, char catering, LocalDate bd,
                              Time st, Time et);

    public void cancelBooking(Booking b) throws SQLException;

    public boolean bookingCodeExists(String bc);

    public Booking getMyBooking(String bk);

    public List<Booking> showBooking(LocalDate date);

    public List<Booking> sendEmailNotification();

    public void addForløb(String bk, int f);

    public List<Forløb> getAllForløb();

    public Forløb getForløb(int id);

}
