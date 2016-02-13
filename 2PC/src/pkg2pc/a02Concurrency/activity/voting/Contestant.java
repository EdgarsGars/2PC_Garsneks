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
public class Contestant {

    static int currId = 0;
    String name;
    volatile int points;
    int id;

    Contestant() {
        this.points = 1000;
        this.id = currId++;
    }

    public void addPoints(int points) {
       // synchronized (this) {
            this.points += points;
       // }
    }

    public int getPoints() {

        return this.points;
    }

    public int getId() {
        return this.id;
    }
}
