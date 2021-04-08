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
public class FightStrikeStats {
    public int fightId;
    public int fighterId;
    public int round;
    public int head;
    public int headLanded;
    public int body;
    public int bodyLanded;
    public int leg;
    public int leg_landed;
    public int distance;
    public int distanceLanded;
    public int clinch;
    public int clinchLanded;
    public int ground;
    public int groundLanded;

    @Override
    public String toString() {
        return "FightStrikeStats{" + "fight=" + fightId + ", fighter=" + fighterId + ", round=" + round + ", head=" + head + ", headLanded=" + headLanded + ", body=" + body + ", bodyLanded=" + bodyLanded + ", leg=" + leg + ", leg_landed=" + leg_landed + ", distance=" + distance + ", distanceLanded=" + distanceLanded + ", clinch=" + clinch + ", clinchLanded=" + clinchLanded + ", ground=" + ground + ", groundLanded=" + groundLanded + '}';
    }
    
    
}
