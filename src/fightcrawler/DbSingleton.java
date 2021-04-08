/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fightcrawler;

import Models.Fight;
import Models.FightStats;
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
    private PreparedStatement insertTotalStrikeStats;
    private PreparedStatement checkFight;
    private PreparedStatement insertFight;
    
    public DbSingleton() throws SQLException {
        this.conn = DriverManager.getConnection(this.connString, user, password);
        this.initPrepedStatements();
    }

    public void initPrepedStatements() throws SQLException {
        getEvent = this.conn.prepareStatement("SELECT * FROM ufc_event WHERE event_date = ?");
        insertEvent = this.conn.prepareStatement("INSERT INTO ufc_event (event_date, country,event_name) VALUES(?,?,?)");
        getFighter = this.conn.prepareStatement("SELECT * FROM fighters WHERE fighters.name = ?");
        insertFighter = this.conn.prepareStatement("INSERT INTO fighters (name, stance, reach, weight, height, dob, country) VALUES(?,?,?,?,?,?,?)");              
        checkFight = this.conn.prepareStatement("SELECT * FROM fights WHERE fighter1 = ? AND fighter2 = ? AND event = ?");
        insertFight = this.conn.prepareStatement("INSERT INTO fights (event, fighter1, fighter 2, duration, winner, method, bonus, gender) VALUES (?,?,?,?,?,?,?,?)");
        
        
        insertTotalStrikeStats = this.conn.prepareStatement(
                "INSERT INTO fight_stats (fight, fighter, round, knockDowns, sig_strikes_attempted, sig_strikes_landed, sig_strike_acc,total_strikes_attempted, total_strikes_landed"
                +"take_down_attempts, take_downs_landed, take_down_acc, sub_attempts, reversals, control_time VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" 
        );
    
    }
    
    public int getFightId(Fight fight) {
        try {
            checkFight.setInt(1,fight.fighter1Id);
            checkFight.setInt(2,fight.fighter2Id);
            checkFight.setInt(3,fight.eventId);
            ResultSet rs = checkFight.executeQuery();
            if(rs.next()){
                fight.setId(rs.getInt(1));
                return fight.getId();
            }            
        } catch (SQLException ex) {
            Logger.getLogger(DbSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public int insertFight(Fight fight){
        int fightId = 0;
        try {           
            fightId = getFightId(fight);
            if(fightId == 0){
                insertFight.setInt(1,fight.eventId);
                insertFight.setInt(2, fight.fighter1Id);
                insertFight.setInt(3, fight.fighter2Id);
                insertFight.setInt(4, fight.durationSeconds);
                insertFight.setInt(5, fight.winner);
                insertFight.setString(6, fight.method);
                insertFight.setString(7, fight.bonus.toString());
                insertFight.executeUpdate();
                fight.setId(getFightId(fight));
                fightId = fight.getId();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fightId;
    }
    
    
    public void insertTotalStrikeStats(FightStats stats){
        try {
            insertTotalStrikeStats.setInt(1, stats.fightId);
            insertTotalStrikeStats.setInt(2, stats.fighterId);
            insertTotalStrikeStats.setInt(3, stats.round);
            insertTotalStrikeStats.setInt(4, stats.knockDowns);
            insertTotalStrikeStats.setInt(5, stats.sigStrkAtmps);
            insertTotalStrikeStats.setInt(6, stats.sigStrkLanded);
            insertTotalStrikeStats.setInt(7, stats.sigStrkAcc);
            insertTotalStrikeStats.setInt(8, stats.totalStrikesAtmps);
            insertTotalStrikeStats.setInt(9, stats.totalStrikesLanded);
            insertTotalStrikeStats.setInt(10, stats.takeDownAtmps);
            insertTotalStrikeStats.setInt(11, stats.takeDownsLanded);
            insertTotalStrikeStats.setInt(12, stats.takeDownAcc);
            insertTotalStrikeStats.setInt(13, stats.reversals);
            insertTotalStrikeStats.setInt(14, stats.controlTimeSecs);
            insertTotalStrikeStats.executeUpdate();           
            
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
