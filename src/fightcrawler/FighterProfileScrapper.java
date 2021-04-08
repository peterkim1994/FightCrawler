/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fightcrawler;

import Models.Fighter;
import utils.Cleaner;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author peter
 */
public class FighterProfileScrapper {

    private DbSingleton db;

    public FighterProfileScrapper(DbSingleton db) {
        this.db = db;
    }

    public void scrapeFighterProfile(String profileUrl) throws IOException {
        Document profile = Jsoup.connect(profileUrl).get();
        Element name = profile.getElementsByClass("b-content__title-highlight").get(0);
        String fighterName = Cleaner.removeApostrophe(name.text());       
        Fighter fighter = new Fighter(fighterName);        
        getFighterCountry(fighter);
        try {
            Elements statVals = profile.getElementsByClass("b-list__box-list-item b-list__box-list-item_type_block");        
            String height = statVals.get(0).ownText();
            int feet = Cleaner.parseInt(Cleaner.splitThenExtract(height," ",0));
            int inches = Cleaner.parseInt(Cleaner.splitThenExtract(height," ",1));
            fighter.height = (int) (2.54 * ((feet * 12) + inches));            
            String weight = Cleaner.splitThenExtract(statVals.get(1).ownText()," ", 0);
            fighter.weight = Cleaner.parseInt(weight);     
            fighter.DOB = Cleaner.reformatDate(statVals.get(3).ownText()).getYear();            
            Elements statVals2 = profile.getElementsByClass("b-list__box-list-item  b-list__box-list-item_type_block"); 
            fighter.stance = statVals2.get(0).ownText().trim();     
            fighter.reach = (int) (2.54 * Cleaner.parseInt(statVals.get(2)));
        } catch (NumberFormatException e) {
            System.out.println("scraping other website for " + fighter.name);
            scrapeReachFromOtherWebsite(fighter);
        } catch (DateTimeParseException e) {
            throw new UnsupportedOperationException("DOB" + fighterName + " couldnt be found and lacked data");
        }
        db.insertFighter(fighter);
    }

    public String getFighterCountry(Fighter fighter) {
        Elements biographyValues = null;
        Document fighterPage = getOtherProfilePage(fighter); // URL shortened!        
        try {
             biographyValues = fighterPage.getElementsByClass("c-bio__text");
            fighter.country = Cleaner.splitThenExtract(biographyValues.get(1), ",", 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            fighter.country = Cleaner.splitThenExtract(biographyValues.get(1), ",", 0);
        }catch(NullPointerException e){
            fighter.country = "Brazil";
        } 
        return fighter.country;
    }

    public Document getOtherProfilePage(Fighter fighter) {
        try {
            String url = "https://www.ufc.com/athlete/" + Cleaner.whiteSpaceToHyphen(fighter.name);
            Document fighterPage = Jsoup.connect(url).get(); // URL shortened! 
            return fighterPage;
        } catch (IOException e) {
            System.out.println("INVALID URL NAME -- " + fighter.name);
        }
        return null;
    }

    public void scrapeReachFromOtherWebsite(Fighter fighter) {
        Document fighterPage = getOtherProfilePage(fighter);
        Elements biographyLabels = fighterPage.getElementsByClass("c-bio__label");
        Elements biographyValues = fighterPage.getElementsByClass("c-bio__text");
        for (int i = 0; i < biographyValues.size(); i++) {
            String label = biographyLabels.get(i).text().trim();
            String value = biographyValues.get(i).text().trim();
            if (label.contains("Reach") && !label.contains("LEG REACH")) {
                fighter.reach = (int) (2.54 * Integer.parseInt(value));
                System.out.println(fighter.name + "  has reach" + fighter.reach);          
                return;
            } else {
                continue;
            }
        }
        fighter.reach = 0;
    }

}
