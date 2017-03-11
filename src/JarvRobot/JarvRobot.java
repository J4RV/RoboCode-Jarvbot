package JarvRobot;

import jdk.nashorn.internal.runtime.regexp.joni.ScanEnvironment;
import robocode.*;
import robocode.Robot;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Random;

import static robocode.util.Utils.normalRelativeAngleDegrees;

/**
 * Created by Jarv on 09/03/2017.
 */
public class JarvRobot extends AdvancedRobot {

    SecureRandom random;
    boolean runningAhead;
    double turnDegrees = 20;
    int noTargetTurns = 5;

    public JarvRobot() {
        random = new SecureRandom();
    }

    public void run() {
        setBodyColor(Color.red);
        setGunColor(Color.CYAN);
        setRadarColor(Color.red);
        setScanColor(Color.GREEN);
        setBulletColor(Color.red);

        randomTurn();

        //Moverse caóticamente
        while (true) {
            randomMovement();
            noTargetTurns++;
            if (noTargetTurns > 3)
                randomTurn();
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        noTargetTurns = 0;
        double angle = track(e);
        if (angle <= 2) {
            shoot(e);
            execute();
        }
        track(e);
        randomMovement();
    }

    public void onHitWall(HitWallEvent e) {
        //Patrás
        runningAhead = !runningAhead;
    }

    public void onHitRobot(HitRobotEvent e) {
        if (e.isMyFault()) {
            runningAhead = !runningAhead;
        }
    }

    public double track(ScannedRobotEvent e) {
        //Rotación del arma, teniendo en cuenta la rotación de la base y el angulo del enemigo
        double angle = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));

        setTurnGunRight(angle);
        setTurnRight(turnDegrees * 0.2);

        return angle;
    }

    public void shoot(ScannedRobotEvent e) {
        if (getGunHeat() == 0) {
            double power = 3.2;
            int maxDistance = 450;

            if (e.getDistance() <= maxDistance || e.getVelocity() == 0) {
                //Balas más rápidas a más distancias, balas fuertes y lentas a melee
                power = power * (1 - e.getDistance() / maxDistance);
                power = clamp(power, 0.05, 3);

                fire(power);
            }
        }
    }

    public void randomMovement() {
        double distance = 100;

        if (runningAhead)
            setAhead(distance);
        else
            setBack(distance);
    }

    public void randomTurn() {
        //Pequeña posibilidad de cambiar de rotación
        if (random.nextDouble() >= 0.99) {
            turnDegrees *= -1;
        }

        setTurnLeft(turnDegrees);
        setTurnGunRight(turnDegrees);
    }

    //Utilidad
    public static double clamp(double value, double min, double max) {
        double res = value;

        if (value > max) {
            res = max;
        } else if (value < min) {
            res = min;
        }

        return res;
    }
}
