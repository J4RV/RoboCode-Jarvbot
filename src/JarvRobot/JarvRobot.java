package JarvRobot;

import jdk.nashorn.internal.runtime.regexp.joni.ScanEnvironment;
import robocode.*;
import robocode.Robot;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Jarv on 09/03/2017.
 */
public class JarvRobot extends AdvancedRobot {

    SecureRandom random;
    boolean runningAhead;
    double turnDegrees = 60;

    public JarvRobot() {
        random = new SecureRandom();
    }

    public void run() {
        setBodyColor(Color.red);
        setGunColor(Color.CYAN);
        setRadarColor(Color.red);
        setScanColor(Color.GREEN);
        setBulletColor(Color.red);

        //Moverse caóticamente
        while (true) {
            randomMovement();
            randomTurn();
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double angle = track(e);
        execute();
        if (angle <= 3){
            shoot(e);
            execute();
        }
        randomMovement();
    }

    public void onHitWall(HitWallEvent e) {
        //Patrás
        runningAhead = !runningAhead;
    }

    public void onHitRobot(HitRobotEvent e) {
        if (e.isMyFault()) {
            back(80);
        }
    }

    public double track(ScannedRobotEvent e) {
        //Rotación del arma, teniendo en cuenta la rotación de la base y el angulo del enemigo
        double angle = getHeading() + e.getBearing();
        angle -= getGunHeading();

        setTurnGunRight(angle);
        turnDegrees = angle;

        return angle;
    }

    public void shoot(ScannedRobotEvent e) {
        if (getGunHeat() == 0) {
            double power = 3;
            int maxDistance = 800;

            if (e.getDistance() <= maxDistance) {
                //Si el objetivo está casi quieto, disparar a máxima potencia
                if (e.getVelocity() <= 0.5) {
                    power = 3;
                } else {
                    //Balas más rápidas a más distancias, balas fuertes y lentas a melee
                    power = power * (1 - e.getDistance() / maxDistance);
                    power = clamp(power, 0.1, 3);
                }

                fire(power);
            }
        }
    }

    public void randomMovement(){
        int maxDistance = 600;
        int minDistance = 200;
        double distance = random.nextDouble() * (maxDistance - minDistance) + minDistance;

        if(runningAhead)
            setAhead(distance);
        else
            setBack(distance);

        randomTurn();
    }

    public void randomTurn(){
        //Pequeña posibilidad de cambiar de rotación
        if(random.nextDouble() >= 0.98){
            turnDegrees += Math.signum(turnDegrees) * 10;
            turnDegrees *= -1.02;
        }

        setTurnLeft(turnDegrees);
        setTurnGunRight(turnDegrees);
    }

    //Utilidad
    public static double clamp(double value, double min, double max) {
        double res = value;

        if(value > max){
            res = max;
        } else if (value < min){
            res = min;
        }

        return res;
    }
}
