/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2pc.a02Concurrency.activity.voting;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Janis
 */
class VoteCounter extends Thread {

    static int currId = 0;
    Voting vote;
    Contestant can[];
    int id;
    int voteCount;

    VoteCounter(Voting vote) {
        this.vote = vote;
        this.can = vote.can;
        this.id = currId++;
        this.voteCount = 0;
    }

    public void run() {
        while (voteCount < 1000000) {

            int ar[] = CallRobot.makeCall(can.length);

            movePoints(can[ar[0]], can[ar[1]]);
            //  System.out.println("Bot nr. " + id + " : Moved points from " + can[ar[0]].id + " to " + can[ar[1]].id);
            //  try {
            //      Thread.sleep(100);
            //  } catch (InterruptedException ex) {
            //      Logger.getLogger(VoteCounter.class.getName()).log(Level.SEVERE, null, ex);
            //  }

            voteCount++;
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - Voting.sartTime;
        System.out.println("Bot nr. " + id + " Elapsed time: " + elapsedTime);
        System.out.println("Points: 1: " + can[0].getPoints() + " 2: " + can[1].getPoints());
    }

    /**
     * Move points from a to b by synchronising memory access "a" gets 1% of "b"
     * points. Subtract the same amount of points from "b"
     *
     * @param a gains 1% of a points
     * @param b looses 1% of own points
     */
    public void movePoints(Contestant a, Contestant b) {
        Contestant first = a.getId() > b.getId() ? a : b;
        Contestant second = a.getId() < b.getId() ? a : b;

        synchronized (first) {
            synchronized (second) {
                int points = (int) (b.getPoints() * 0.01);
                a.addPoints(points);
                b.addPoints(-points);
            }
        }
        /*
        Contestant[] c = new Contestant[]{a, b};
        synchronized (c) {
            int points = (int) (c[1].getPoints() * 0.01);
            c[0].addPoints(points);
            c[1].addPoints(-points);
        }
         */
    }
}
