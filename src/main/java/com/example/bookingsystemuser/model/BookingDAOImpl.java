package com.example.bookingsystemuser.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOImpl implements BookingDAO {
    private Connection con;

    public BookingDAOImpl() throws SQLException {
        con = ConnectionSingleton.getInstance().getConnection();
    }

    @Override
    public List<Booking> getAllBooking() {
        List<Booking> allBookings = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Booking;");
            ResultSet rs = ps.executeQuery();

            Booking b;
            while (rs.next()){
                int id = rs.getInt(1);
                String fName = rs.getString(2);
                String lName = rs.getString(3);
                String org = rs.getString(4);
                String mail = rs.getString(5);
                int phone = Integer.parseInt(rs.getString(6));
                char bType = rs.getString(7).charAt(0);
                char catering = rs.getString(8).charAt(0);
                LocalDate bDate = LocalDate.parse(rs.getString(9));
                LocalDate created = LocalDate.parse(rs.getString(10));
                String bCode = rs.getString(11);
                Time startTime = rs.getTime(12);
                Time endTime = rs.getTime(13);
                int participants = rs.getInt(14);


                b = new Booking(id, fName, lName, org, mail, phone,
                        bType, catering, bDate, created, bCode, startTime, endTime, participants);
                allBookings.add(b);

            }
        } catch (SQLException e){
            System.err.println("Kan ikke finde booking " + e.getMessage());
        }
        return allBookings;
    }

    @Override
    public void addBooking(String fn, String ln, String org, String mail, int phone, char bt, char catering,
                           LocalDate bd, String bk, Time st, Time et, int p) {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO Booking VALUES(?,?,?,?,?,?,?," +
                    "?, GETDATE(), ?, ?, ?, ?)");
            ps.setString(1, fn);
            ps.setString(2, ln);
            ps.setString(3, org);
            ps.setString(4, mail);
            ps.setInt(5, phone);
            ps.setString(6, String.valueOf(bt));
            ps.setString(7, String.valueOf(catering));
            ps.setDate(8, Date.valueOf(bd));
            ps.setString(9, bk);
            ps.setTime(10, st);
            ps.setTime(11, et);
            ps.setInt(12, p);

            ps.executeUpdate();
        }catch(SQLException e){
            System.err.println("Kunne ikke oprette booking");
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void updateBooking(int id, char bt, char catering, LocalDate bd, Time st, Time et) {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Booking SET bookingType = ?, catering = ?, bookingDate = ?, startTime = ?, endTime = ? WHERE bookingID = ?;");

            ps.setString(1, String.valueOf(bt));
            ps.setString(2, String.valueOf(catering));
            ps.setDate(3, Date.valueOf(bd));
            ps.setTime(4, st);
            ps.setTime(5, et);
            ps.setInt(6, id);

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Kan ikke opdatere booking " + e.getMessage());
        }
    }

    @Override
    public void cancelBooking(Booking b) throws SQLException {
        try {
            PreparedStatement ps1 = con.prepareStatement("DELETE FROM Note WHERE bookingID = ?");
            ps1.setInt(1, b.getId());
            ps1.executeUpdate();

            PreparedStatement ps = con.prepareStatement("DELETE FROM Booking WHERE bookingID = ?");
            ps.setInt(1, b.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kunne ikke slette booking" + e.getMessage());
        }
    }

    @Override
    public boolean bookingCodeExists(String bc) {
        List<String> bookingCodes = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT bookingCode FROM Booking WHERE bookingCode = ?;");
            ps.setString(1, bc);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                String s = rs.getString(1);
                bookingCodes.add(s);
            }
        }catch (SQLException e){
            System.err.println("Kunne ikke finde bookingcode " + e.getMessage());
        }

        if (bookingCodes.size() == 0)
            return false;
        else return true;
    }

    @Override
    public Booking getMyBooking(String bk) {
        Booking b = null;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Booking WHERE bookingCode = ?;");
            ps.setString(1, bk);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt(1);
                String fName = rs.getString(2);
                String lName = rs.getString(3);
                String org = rs.getString(4);
                String mail = rs.getString(5);
                int phone = Integer.parseInt(rs.getString(6));
                char bType = rs.getString(7).charAt(0);
                char catering = rs.getString(8).charAt(0);
                LocalDate bDate = LocalDate.parse(rs.getString(9));
                LocalDate created = LocalDate.parse(rs.getString(10));
                String bCode = rs.getString(11);
                Time startTime = rs.getTime(12);
                Time endTime = rs.getTime(13);
                int part = rs.getInt(14);


                b = new Booking(id, fName, lName, org, mail, phone,
                        bType, catering, bDate, created, bCode, startTime, endTime, part);

            }
        } catch (SQLException e) {
            System.err.println("Kan ikke finde booking " + e.getMessage());
        }
        return b;
    }

    @Override
    public List<Booking> showBooking(LocalDate date) {
        List<Booking> showBookings = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Booking WHERE bookingDate >= ? AND bookingDate < DATEADD(week, 1, ?);");
            ps.setDate(1, Date.valueOf(date));
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();

            Booking b;
            while (rs.next()){
                int id = rs.getInt(1);
                String fName = rs.getString(2);
                String lName = rs.getString(3);
                String org = rs.getString(4);
                String mail = rs.getString(5);
                int phone = Integer.parseInt(rs.getString(6));
                char bType = rs.getString(7).charAt(0);
                char catering = rs.getString(8).charAt(0);
                LocalDate bDate = LocalDate.parse(rs.getString(9));
                LocalDate created = LocalDate.parse(rs.getString(10));
                String bCode = rs.getString(11);
                Time startTime = rs.getTime(12);
                Time endTime = rs.getTime(13);
                int part = rs.getInt(14);


                b = new Booking(id, fName, lName, org, mail, phone,
                        bType, catering, bDate, created, bCode, startTime, endTime, part);
                showBookings.add(b);

            }
        } catch (SQLException e){
            System.err.println("Kan ikke finde booking " + e.getMessage());
        }
        return showBookings;
    }

    @Override
    public List<Booking> sendEmailNotification() {

        List<Booking> emailList = new ArrayList<>();

        try{
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Booking WHERE bookingDate > DATEADD(day, 6, GETDATE()) AND bookingDate < DATEADD(week, 1, GETDATE())");
            ResultSet rs = ps.executeQuery();

            Booking b;
            while(rs.next())
            {
                int id = rs.getInt(1);
                String fName = rs.getString(2);
                String lName = rs.getString(3);
                String org = rs.getString(4);
                String mail = rs.getString(5);
                int phone = Integer.parseInt(rs.getString(6));
                char bType = rs.getString(7).charAt(0);
                char catering = rs.getString(8).charAt(0);
                LocalDate bDate = LocalDate.parse(rs.getString(9));
                LocalDate created = LocalDate.parse(rs.getString(10));
                String bCode = rs.getString(11);
                Time startTime = rs.getTime(12);
                Time endTime = rs.getTime(13);
                int part = rs.getInt(14);

                b = new Booking(id, fName, lName, org, mail, phone,
                        bType, catering, bDate, created, bCode, startTime, endTime, part);
                emailList.add(b);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return emailList;
    }

    @Override
    public void addForløb(){
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO BookingForløb VALUES(?,?)");
            //ps.setInt(1, b.getId());
            //ps.setInt
            ps.executeUpdate();


        } catch (SQLException e) {
            System.err.println("Kunne ikke slette booking" + e.getMessage());
        }
    }

}
