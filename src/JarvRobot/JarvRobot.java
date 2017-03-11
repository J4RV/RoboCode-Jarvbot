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
    boolean tooClose = false;
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
            if (noTargetTurns > 2)
                randomTurn();
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        noTargetTurns = 0;
        double angle = track(e);
        if (angle <= 3) {
            shoot(e);
            execute();
        }
        if(e.getDistance()<250){
            tooClose = true;
        } else {
            tooClose = false;
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
        double angle = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading() - turnDegrees * 0.15));

        setTurnGunRight(angle);
        setTurnRight(turnDegrees * 0.28);

        return angle;
    }

    public void shoot(ScannedRobotEvent e) {
        if (getGunHeat() == 0) {
            double power = 3.2;
            int maxDistance = 450;

            if (e.getDistance() <= maxDistance) {
                //Balas más rápidas a más distancias, balas fuertes y lentas a melee
                power = power * (1 - e.getDistance() / maxDistance);
                power = clamp(power, 0.05, 3);

                fire(power);
            }
        }
    }

    public void randomMovement() {
        double distance = random.nextDouble() * 50 + 1;
        distance *= tooClose ? -1 : 1;

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
        setTurnGunLeft(turnDegrees*0.2);
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
