/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

/**
 *
 * @author peter
 */
public class Fight {
     private int id;
     public int eventId;
     public int fighter1Id;
     public int fighter2Id;
     public String weightClass;
     public int rounds;
     public int durationSeconds;
     public int winner;
     public String method;
     public Bonus bonus;
     public String gender;
     
     public Fight(int eventId){
         this.eventId = eventId;
         winner = 1;
     }
     
     public int getId() {
         return id;
     }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Fight{" + "eventId=" + eventId + ", fighter1Id=" + fighter1Id + ", fighter2Id=" + fighter2Id + ", weightClass=" + weightClass + ", rounds=" + rounds + ", durationSeconds=" + durationSeconds + ", winner=" + winner + ", method=" + method + ", bonus=" + bonus + ", gender=" + gender + '}';
    }
     
     
}
