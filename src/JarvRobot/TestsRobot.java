package JarvRobot;

import robocode.AdvancedRobot;

import java.awt.*;

/**
 * Created by Main on 09/03/2017.
 */
public class TestsRobot extends AdvancedRobot {

    public void run() {
        setBodyColor(Color.red);
        setGunColor(Color.CYAN);
        setRadarColor(Color.red);
        setScanColor(Color.red);
        setBulletColor(Color.CYAN);

        //Moverse caóticamente
        while (true) {
            double turnDegrees = 50;

            setTurnLeft(turnDegrees);
            setTurnGunLeft(-turnDegrees/5);
            execute();
        }
    }
}
