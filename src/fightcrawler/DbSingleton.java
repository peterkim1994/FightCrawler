/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fightcrawler;

import Models.Fighter;
import Models.UFCEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author peter
 */
public class DbSingleton {

    private final String user = "root";
    private final String password = "password";
    private final String connString = "jdbc:mysql://localhost:3306/ufc_store";
//    db_driver   = com.mysql.jdbc.Driver;
    private DbSingleton db;
    private Connection conn;

    private PreparedStatement getEvent;
    private PreparedStatement insertEvent;

    private PreparedStatement getFighter;
    private PreparedStatement insertFighter;

    public DbSingleton() throws SQLException {
        this.conn = DriverManager.getConnection(this.connString, user, password);
        this.initPrepedStatements();
    }

    public void initPrepedStatements() throws SQLException {
        getEvent = this.conn.prepareStatement("SELECT * FROM ufc_event WHERE event_date = ?");
        insertEvent = this.conn.prepareStatement("INSERT INTO ufc_event (event_date, country,event_name) VALUES(?,?,?)");
        getFighter = this.conn.prepareStatement("SELECT * FROM fighters WHERE fighters.name = ?");
        insertFighter = this.conn.prepareStatement("INSERT INTO fighters (name, stance, reach, weight, height, dob, country) VALUES(?,?,?,?,?,?,?)");
    }

    public static void main(String[] args) {
        try {
            DbSingleton x = new DbSingleton();
        } catch (SQLException ex) {
            Logger.getLogger(DbSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void recordEvent(UFCEvent event) {
        int eventId = getEventId(event.date);
        if (eventId == 0) {
            try {
                insertEvent.setDate(1, Date.valueOf(event.date));
                insertEvent.setString(2, event.country);
                insertEvent.setString(3, event.EventName);
                insertEvent.executeUpdate();
                eventId = getEventId(event.date);
            } catch (SQLException ex) {
                Logger.getLogger(DbSingleton.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
        event.setId(eventId);
    }

    public void insertFighter(Fighter fighter) {
        int fighterId = getFighterId(fighter);
        try {
            if (fighterId == 0) {
                insertFighter.setString(1, fighter.name);
                insertFighter.setString(2, fighter.stance);
                insertFighter.setInt(3, fighter.reach);
                insertFighter.setInt(4, fighter.weight);
                insertFighter.setInt(5, fighter.height);
                insertFighter.setInt(6, fighter.DOB);
                insertFighter.setString(7, fighter.country);
                insertFighter.executeUpdate();
                fighterId = getFighterId(fighter);
          
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }   
        fighter.setId(fighterId);
    }

    public int getFighterId(Fighter fighter) {
        try {
            getFighter.setString(1, fighter.name);
            ResultSet rs = getFighter.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int getEventId(LocalDate date) {
        try {
            this.getEvent.setDate(1, Date.valueOf(date));
            ResultSet rs = this.getEvent.executeQuery();
            if (rs.next()) {
                System.out.println("contains event: true");
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("contains event: false");
        return 0;
    }

}
