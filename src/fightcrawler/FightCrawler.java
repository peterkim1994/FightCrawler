/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fightcrawler;

import Models.Bonus;
import Models.Fight;
import Models.FightStats;
import Models.FightStrikeStats;
import Models.Fighter;
import Models.UFCEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Cleaner;
import utils.devUtils;

/**
 *
 * @author peter
 */
public class FightCrawler {

    /**
     * Izzy vs COSTA for cut off for bonuses
     */
    private DbSingleton db;
    private FighterProfileScrapper fightScrapper;

    public FightCrawler(DbSingleton db, FighterProfileScrapper fightScrapper) {
        this.db = db;
        this.fightScrapper = fightScrapper;
    }

    public void scrapeFight(String fightUrl, Fight fight) {
        try {
            Document page = Jsoup.connect(fightUrl).get();
            Elements fighterDetails = page.getElementsByClass("b-fight-details__person");            
            
            Fighter fighter1 = scrapeFighter(fighterDetails, true);
            Fighter fighter2 = scrapeFighter(fighterDetails, false);
            fight.fighter1Id =  db.getFighterId(fighter1);
            fight.fighter2Id =  db.getFighterId(fighter2);
            
            System.out.println("scraping fight :" + fighter1.name + "-" + fighter1.getId() + " vs " + fighter2.name + "-" + fighter2.getId());
            if(db.getFightId(fight) != 0){
                return;
            } 
            
            String fighter1Outcome = fighterDetails.get(0).getElementsByClass("b-fight-details__person-status").text();
            assignFightOutcome(fighter1Outcome, fight); //set fight objects outcome attribute state
            String infoHeader = page.getElementsByClass("b-fight-details__fight-title").get(0).text().trim();
            checkForBonus( page.getElementsByClass("b-fight-details__fight-title"), fight);
            fight.gender = (infoHeader.contains("Women")) ? "female" : "male";
            Elements fightInfo = page.getElementsByClass("b-fight-details__text-item");
            fight.durationSeconds = getFightDuration(fightInfo);
            
            System.out.println(fight);
            db.insertFight(fight);
            
            int totalRounds = Integer.parseInt(fightInfo.get(0).ownText().trim());
            Elements fightStatTables = page.getElementsByClass("b-fight-details__table js-fight-table");
            
            Elements totalStrikes = fightStatTables.get(0).getElementsByClass("b-fight-details__table-row");
            Elements strikesByTarget = fightStatTables.get(1).getElementsByClass("b-fight-details__table-row");
            // removing table header
            fightStatTables.remove(0);
            totalStrikes.remove(0);
            strikesByTarget.remove(0);                    
            for (int columnNum = 0; columnNum < totalRounds; columnNum++) {
                totalStrikes.remove(0);//removing irelevant rows     
                strikesByTarget.remove(0);
                int round = columnNum +1;
                Element totalFigs = totalStrikes.get(columnNum);     
                Element targetFigs = strikesByTarget.get(columnNum);
                extractTotalStrikeStats(totalFigs, fight.getId(), fight.fighter1Id, true, round);
                extractTotalStrikeStats(totalFigs, fight.getId(), fight.fighter2Id, false, round);
                extractStrikesByTarget(targetFigs, fight.getId(), fight.fighter1Id, true, round);
                extractStrikesByTarget(targetFigs, fight.getId(), fight.fighter2Id, false, round);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(EventCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }catch(UnsupportedOperationException e){
            System.err.println("fight not scrapped");
        }
    }

    public void extractTotalStrikeStats(
            Element roundTotalStrikes,
            int fightEventId,
            int fighterId,
            boolean redGloves,
            int round
    ) {
        //                 
        Elements cols = roundTotalStrikes.getElementsByClass("b-fight-details__table-text");
        // removing fighterNames from table data
        cols.remove(0);
        cols.remove(0);

        FightStats fightStats = new FightStats();
        fightStats.fightId = fightEventId;
        fightStats.fighterId = fighterId;
        fightStats.round = round;

        int ithCol = (redGloves) ? 1 : 0; // every ith col is not needed depending on if its fighter 1 or 2
        int totalCols = cols.size();
        for (int i = 0; i < totalCols / 2; i++) {
            cols.remove(i + ithCol); 
        }

        fightStats.knockDowns = Integer.parseInt(cols.get(0).text());
        String sigStrikes = cols.get(1).text();
        fightStats.sigStrkAtmps = getAttempted(sigStrikes);
        fightStats.sigStrkLanded = getLanded(sigStrikes);

        fightStats.calcSigStrikeAcc();

        String totalStrikes = cols.get(3).text();
        fightStats.totalStrikesAtmps = getAttempted(totalStrikes);
        fightStats.totalStrikesLanded = getLanded(totalStrikes);

        String takeDowns = cols.get(4).text();
        fightStats.takeDownAtmps = getAttempted(takeDowns);
        fightStats.takeDownsLanded = getLanded(takeDowns);
        fightStats.calcTakeDownAcc();

        fightStats.subAtmps = Cleaner.parseInt(cols.get(6));
        fightStats.reversals = Cleaner.parseInt(cols.get(7));
        fightStats.controlTimeSecs = getTotalSeconds(cols.get(8).text());
       // System.out.println(fightStats);        
        db.insertTotalStrikeStats(fightStats);
    }

    public void extractStrikesByTarget(
            Element targets,
            int fightEventId,
            int fighterId,
            boolean redGloves,
            int round
    ) {
        Elements cols = targets.getElementsByClass("b-fight-details__table-text");
        // removing name column
        cols.remove(0);
        cols.remove(0);
        
        FightStrikeStats strikeTargets = new FightStrikeStats();
        strikeTargets.fightId = fightEventId;
        strikeTargets.fighterId = fighterId;
        strikeTargets.round = round;

        int ithCol = (redGloves) ? 1 : 0; // every ith col is not needed depending on if its fighter 1 or 2
        int totalCols = cols.size();
        for (int i = 0; i < totalCols / 2; i++) {
            cols.remove(i + ithCol);
        }
        String headStrikes = cols.get(2).text();
        strikeTargets.head = getAttempted(headStrikes);
        strikeTargets.headLanded = getLanded(headStrikes);
        
        String bodyStrikes = cols.get(3).text();
        strikeTargets.body = getAttempted(bodyStrikes);
        strikeTargets.bodyLanded = getLanded(bodyStrikes);
        
        String legStrikes = cols.get(4).text();
        strikeTargets.leg = getAttempted(legStrikes);
        strikeTargets.leg_landed = getLanded(legStrikes);
        
        String distanceStrikes = cols.get(5).text();
        strikeTargets.distance = getAttempted(distanceStrikes);
        strikeTargets.distanceLanded = getLanded(distanceStrikes);
        
        String clinchStrikes = cols.get(6).text();
        strikeTargets.clinch = getAttempted(clinchStrikes);
        strikeTargets.clinchLanded = getLanded(clinchStrikes);
        
        String groundStrikes = cols.get(7).text();
        strikeTargets.ground = getAttempted(groundStrikes);
        strikeTargets.groundLanded = getLanded(groundStrikes);        
        db.insertStrikeTargetStats(strikeTargets);
    //    System.out.println(strikeTargets);        
    }

    public Fighter scrapeFighter(Elements fighterHeaders, boolean redGloves) throws IOException, UnsupportedOperationException {
        int index = (redGloves) ? 0 : 1;
        String fighterName = fighterHeaders.get(index).getElementsByClass("b-fight-details__person-name").text().trim();
        fighterName = Cleaner.removeApostrophe(fighterName);
        String fighterProfileLink = fighterHeaders.get(index).getElementsByClass("b-link b-fight-details__person-link").get(0).attr("href");
   //     System.out.println(fighterProfileLink);
        Fighter fighter = new Fighter(fighterName);
        int fighterId = db.getFighterByName(fighterName);
        if( fighterId == 0){
            fightScrapper.scrapeFighterProfile(fighterProfileLink);  
            fighter.setId(db.getFighterId(fighter));
        }else{
            fighter.setId(fighterId);
        }        
        return fighter;
    }

    public void assignFightOutcome(String fighter1Outcome, Fight fight) {
        if (fighter1Outcome.contains("W")) {
            fight.winner = 1;
        } else if (fighter1Outcome.contains("L")) {
            fight.winner = 2;
        } else if(fighter1Outcome.contains("D")) {
            fight.winner = 0;
        }else{ // DQ/No-Contest
            fight.winner = -1;
        }
    }

    public void checkForBonus(Elements fighterDetails, Fight fight) {
        String rawHtml = fighterDetails.get(0).getElementsByClass("b-fight-details__fight-title").outerHtml();
        if (rawHtml.contains("rackcdn.com/perf.png")) {
            fight.bonus = Bonus.PON;
        } else if (rawHtml.contains("rackcdn.com/fight.png")) {
           fight.bonus = Bonus.FON;
        } else if (rawHtml.contains("rackcdn.com/sub.png")){
            fight.bonus = Bonus.SON;
        }else if(rawHtml.contains("rackcdn.com/ko.png")){
            fight.bonus = Bonus.KON;
        }
        else {
            fight.bonus = Bonus.NONE;
        }
    }

    public int getFightDuration(Elements fightInfo) {
        int rounds = Integer.parseInt(fightInfo.get(0).ownText().trim());
        String time = fightInfo.get(1).ownText();
        int secondsInLastRound = getTotalSeconds(time);
        int totalSeconds = (300 * (rounds - 1)) + secondsInLastRound;
        return totalSeconds;
    }

    public int getTotalSeconds(String time) {
        int mins = Integer.parseInt(Cleaner.splitThenExtract(time, ":", 0));
        int seconds = Integer.parseInt(Cleaner.splitThenExtract(time, ":", 1));
        int totalSeconds = (mins * 60) + seconds;
        return totalSeconds;
    }

    public static int getAttempted(String figure) {
        return Integer.parseInt(Cleaner.splitThenExtract(figure, " ", 2));
    }

    public static int getLanded(String figure) {        
        return Integer.parseInt(Cleaner.splitThenExtract(figure, " ", 0));
    }

}
