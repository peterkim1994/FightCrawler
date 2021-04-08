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
public class FightStats {

    public int fightId;
    public int fighterId;
    public int round;
    public int knockDowns;
    public int sigStrkAtmps;
    public int sigStrkLanded;
    public int sigStrkAcc;
    public int totalStrikesAtmps;
    public int totalStrikesLanded;
    public int takeDownAtmps;
    public int takeDownsLanded;
    public int takeDownAcc;
    public int subAtmps;
    public int reversals;
    public int controlTimeSecs;

    public void calcTakeDownAcc() {
        if (this.takeDownAtmps == 0 || this.takeDownsLanded == 0) {
            this.takeDownAcc = 0;
        } else {
            this.takeDownAcc = (int)(((double)this.takeDownsLanded / (double)this.takeDownAtmps)* 100);
        }
    }

    public void calcSigStrikeAcc() {
        if (this.sigStrkAtmps == 0 || this.sigStrkLanded == 0) {
            this.sigStrkAtmps = 0;
        } else {
            this.sigStrkAcc = (int)(((double)this.sigStrkLanded /(double) this.sigStrkAtmps)* 100) ;
        }
    }

    @Override
    public String toString() {
        return "FightStats{" + "fight=" + fightId + ", fighter=" + fighterId + ", round=" + round +
                ", knockDowns=" + knockDowns + ", sigStrkAtmps=" + sigStrkAtmps + ", sigStrkLanded=" + 
                sigStrkLanded + ", sigStrkAcc=" + sigStrkAcc + ", totalStrikesAtmps=" + totalStrikesAtmps + 
                ", totalStrikesLanded=" + totalStrikesLanded + ", takeDownAtmps=" + takeDownAtmps + ", takeDownsLanded=" 
                + takeDownsLanded + ", takeDownAcc=" + takeDownAcc + ", subAtmps=" + subAtmps + ", reversals=" + 
                reversals + ", controlTimeSecs=" + controlTimeSecs + '}';
    }
    
    
}
