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
                String mail = rs.getString(4);
                int phone = Integer.parseInt(rs.getString(5));
                char bType = rs.getString(6).charAt(0);
                char catering = rs.getString(7).charAt(0);
                LocalDate bDate = LocalDate.parse(rs.getString(8));
                LocalDate created = LocalDate.parse(rs.getString(9));
                String bCode = rs.getString(10);
                Time startTime = rs.getTime(11);
                Time endTime = rs.getTime(12);
                int participants = rs.getInt(13);


                b = new Booking(id, fName, lName, mail, phone,
                        bType, catering, bDate, created, bCode, startTime, endTime, participants);
                allBookings.add(b);

            }
        } catch (SQLException e){
            System.err.println("Kan ikke finde booking " + e.getMessage());
        }
        return allBookings;
    }

    @Override
    public void addBooking(String fn, String ln, String mail, int phone, char bt, char catering,
                           LocalDate bd, String bk, Time st, Time et, int p) {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO Booking VALUES(?,?,?,?,?,?," +
                    "?, GETDATE(), ?, ?, ?, ?)");
            ps.setString(1, fn);
            ps.setString(2, ln);
            ps.setString(3, mail);
            ps.setInt(4, phone);
            ps.setString(5, String.valueOf(bt));
            ps.setString(6, String.valueOf(catering));
            ps.setDate(7, Date.valueOf(bd));
            ps.setString(8, bk);
            ps.setTime(9, st);
            ps.setTime(10, et);
            ps.setInt(11, p);

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

            PreparedStatement ps2 = con.prepareStatement("DELETE FROM BookingOrg WHERE bookingID = ?");
            ps2.setInt(1, b.getId());
            ps2.executeUpdate();

            PreparedStatement ps3 = con.prepareStatement("DELETE FROM BookingForløb WHERE bookingID = ?");
            ps3.setInt(1, b.getId());
            ps3.executeUpdate();

            PreparedStatement ps4 = con.prepareStatement("DELETE FROM Company WHERE bookingID = ?");
            ps4.setInt(1, b.getId());
            ps4.executeUpdate();

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
                String mail = rs.getString(4);
                int phone = Integer.parseInt(rs.getString(5));
                char bType = rs.getString(6).charAt(0);
                char catering = rs.getString(7).charAt(0);
                LocalDate bDate = LocalDate.parse(rs.getString(8));
                LocalDate created = LocalDate.parse(rs.getString(9));
                String bCode = rs.getString(10);
                Time startTime = rs.getTime(11);
                Time endTime = rs.getTime(12);
                int part = rs.getInt(13);


                b = new Booking(id, fName, lName, mail, phone,
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
                String mail = rs.getString(4);
                int phone = Integer.parseInt(rs.getString(5));
                char bType = rs.getString(6).charAt(0);
                char catering = rs.getString(7).charAt(0);
                LocalDate bDate = LocalDate.parse(rs.getString(8));
                LocalDate created = LocalDate.parse(rs.getString(9));
                String bCode = rs.getString(10);
                Time startTime = rs.getTime(11);
                Time endTime = rs.getTime(12);
                int part = rs.getInt(13);


                b = new Booking(id, fName, lName, mail, phone,
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
                String mail = rs.getString(4);
                int phone = Integer.parseInt(rs.getString(5));
                char bType = rs.getString(6).charAt(0);
                char catering = rs.getString(7).charAt(0);
                LocalDate bDate = LocalDate.parse(rs.getString(8));
                LocalDate created = LocalDate.parse(rs.getString(9));
                String bCode = rs.getString(10);
                Time startTime = rs.getTime(11);
                Time endTime = rs.getTime(12);
                int part = rs.getInt(13);

                b = new Booking(id, fName, lName, mail, phone,
                        bType, catering, bDate, created, bCode, startTime, endTime, part);
                emailList.add(b);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return emailList;
    }

    @Override
    public void addForløb(String bk, int f) {
        try {
            int id = 0;
            PreparedStatement ps1 = con.prepareStatement("SELECT bookingID FROM Booking WHERE bookingCode = ?");
            ps1.setString(1, bk);
            ResultSet rs = ps1.executeQuery();

            while (rs.next()){
                id = rs.getInt(1);
            }


            PreparedStatement ps = con.prepareStatement("INSERT INTO BookingForløb VALUES(?,?)");
            ps.setInt(1, id);
            ps.setInt(2, f);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kunne ikke tilføje forløb til booking " + e.getMessage());
        }
    }

    @Override
    public List<Forløb> getAllForløb() {
        List<Forløb> allForløb = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Forløb;");
            ResultSet rs = ps.executeQuery();

            Forløb f;
            while (rs.next()){
                int id = rs.getInt(1);
                String forløb = rs.getString(2);


                f = new Forløb(id, forløb);
                allForløb.add(f);

            }
        } catch (SQLException e){
            System.err.println("Kan ikke finde forløb " + e.getMessage());
        }
        return allForløb;
    }

    @Override
    public Forløb getForløb(int id) {
        Forløb f = null;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT forløb.forløbID, forløb FROM Forløb \n" +
                    "JOIN BookingForløb ON BookingForløb.forløbID = Forløb.forløbID\n" +
                    "WHERE bookingID = ?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                int i = rs.getInt(1);
                String s = rs.getString(2);

                f = new Forløb(i, s);
            }

        }catch(SQLException e){
            System.out.println("Kunne ikke finde forløb " + e.getMessage());
        }
        return f;
    }

    @Override
    public void addOrg(String bk, int o) {
        try {
            int id = 0;
            PreparedStatement ps1 = con.prepareStatement("SELECT bookingID FROM Booking WHERE bookingCode = ?");
            ps1.setString(1, bk);
            ResultSet rs = ps1.executeQuery();

            while (rs.next()){
                id = rs.getInt(1);
            }

            PreparedStatement ps = con.prepareStatement("INSERT INTO BookingOrg VALUES(?,?)");
            ps.setInt(1, id);
            ps.setInt(2, o);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kunne ikke tilføje organisation til booking " + e.getMessage());
        }
    }

    @Override
    public List<Organisation> getAllOrg() {
        List<Organisation> allOrg = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Organisation;");
            ResultSet rs = ps.executeQuery();

            Organisation o;
            while (rs.next()){
                int id = rs.getInt(1);
                String forløb = rs.getString(2);


                o = new Organisation(id, forløb);
                allOrg.add(o);

            }
        } catch (SQLException e){
            System.err.println("Kan ikke finde forløb " + e.getMessage());
        }
        return allOrg;
    }

    @Override
    public Organisation getOrg(int id) {
        Organisation o = null;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT Organisation.organisationID, organisation FROM Organisation \n" +
                    "JOIN BookingOrg ON BookingOrg.organisationID = Organisation.organisationID\n" +
                    "WHERE bookingID = ?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                int i = rs.getInt(1);
                String s = rs.getString(2);

                o = new Organisation(i, s);
            }

        }catch(SQLException e){
            System.out.println("Kunne ikke finde organisation " + e.getMessage());
        }
        return o;
    }

    @Override
    public Company getCompany(int id) {
        Company c = null;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT companyID, company FROM Company WHERE bookingID = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                int i = rs.getInt(1);
                String s = rs.getString(2);

                c = new Company(i, s);
            }

        }catch(SQLException e){
            System.out.println("Kunne ikke finde note " + e.getMessage());
        }
        return c;
    }

    @Override
    public void addCompany(String bk, String c) {
        try {
            int id = 0;
            PreparedStatement ps1 = con.prepareStatement("SELECT bookingID FROM Booking WHERE bookingCode = ?");
            ps1.setString(1, bk);
            ResultSet rs = ps1.executeQuery();

            while (rs.next()){
                id = rs.getInt(1);
            }

            PreparedStatement ps = con.prepareStatement("INSERT INTO Company VALUES(?,?)");
            ps.setInt(1, id);
            ps.setString(2, c);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kunne ikke tilføje virksomhed til booking " + e.getMessage());
        }
    }

}
