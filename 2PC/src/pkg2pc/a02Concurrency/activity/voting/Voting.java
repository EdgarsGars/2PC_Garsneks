/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2pc.a02Concurrency.activity.voting;

/**
 *
 * @author Janis
 */
public class Voting {

    static final int numCandidates = 2;
    static final int numBots = 4;
    public Contestant[] can;
    static long sartTime = 0;

    /**
     * @param args the command line arguments
     */
    Voting() {
        can = new Contestant[numCandidates];
        for (int i = 0; i < can.length; i++) {
            can[i] = new Contestant();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        sartTime = System.currentTimeMillis();

        Voting vote = new Voting();
        VoteCounter counters[] = new VoteCounter[numBots];
        for (int i = 0; i < numBots; i++) {
            counters[i] = new VoteCounter(vote);
            counters[i].start();
        }

        for (VoteCounter counter : counters) {
            counter.join();
        }

        double sum = 0;
        for (int i = 0; i < vote.can.length; i++) {
            System.out.println("Contestant " + vote.can[i].getPoints());
            sum += vote.can[i].getPoints();
        }
        System.out.println("Total = " + sum);
    }

}
