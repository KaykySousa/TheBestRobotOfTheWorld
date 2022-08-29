package robotetec;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

public class ProgrammingAboveAll extends AdvancedRobot {
	static int velocity = 25;
	static double wallMargin = 36;
	static double enemyMargin = 25;

	int scanDirection = 1;
	int moveDirection = 1;
	boolean track = false;
	boolean isWall = false;
	boolean onHitByWall = false;

	double angle;
	double enemyX;
	double enemyY;

	ScannedRobotEvent enemy;

	public void run() {

		setAllColors(Color.black);
		setBulletColor(Color.red);
		setScanColor(Color.darkGray);

		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		while (true) {

			setTurnRadarLeft(360 * scanDirection);

			if (enemy != null) {
				angle = ((getHeading() + enemy.getBearing()) % 360) * (Math.PI / 180);

				enemyX = (double) (getX() + Math.sin(angle) * enemy.getDistance());
				enemyY = (double) (getY() + Math.cos(angle) * enemy.getDistance());

				if (enemyY <= enemyMargin || enemyX <= enemyMargin || enemyY >= getBattleFieldHeight() - enemyMargin
						|| enemyX >= getBattleFieldWidth() - wallMargin) {
					isWall = true;
				} else {
					isWall = false;
				}
			}

			if (getY() <= wallMargin) {
				setAhead(0);
				setTurnRight(0 - getHeading());
				if (getHeading() == 0) {
					setAhead(velocity);
				}
			}

			if (getX() <= wallMargin) {
				setAhead(0);
				setTurnRight(90 - getHeading());
				if (getHeading() == 90) {
					setAhead(velocity);
				}
			}

			if (getY() >= getBattleFieldHeight() - wallMargin) {
				setAhead(0);
				setTurnRight(180 - getHeading());
				if (getHeading() == 180) {
					setAhead(velocity);
				}
			}

			if (getX() >= getBattleFieldWidth() - wallMargin) {
				setAhead(0);
				setTurnRight(270 - getHeading());
				if (getHeading() == 270) {
					setAhead(velocity);
				}
			}

			if (isWall) {
				if (enemy != null) {
					if (getX() <= wallMargin || getX() >= getBattleFieldWidth() - wallMargin) {
						setAhead(0);
					} else {
						if (enemyX >= 100) {
							setAhead(0);
							setTurnRight(270 - getHeading());
							if (getHeading() == 270) {
								setAhead(velocity);
							}
						} else if (enemyX <= 100) {
							setAhead(0);
							setTurnRight(90 - getHeading());
							if (getHeading() == 90) {
								setAhead(velocity);
							}
						}
					}

				}

			} else {
				if (track) {
					setAhead(velocity);
				}
			}

			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {

		enemy = e;

		scanDirection *= -1;

		if (!isWall) {
			if (e.getDistance() > 150) {
				track = true;
				setTurnRight(e.getBearing());
			} else {
				track = false;
			}
		}

		setTurnGunRight(getHeading() - getGunHeading() + e.getBearing());

		if (getGunHeat() != 0 || Math.abs(getGunTurnRemaining()) > 5) {
			return;
		}

		if (isWall) {
			if (getX() <= wallMargin || getX() >= getBattleFieldWidth() - wallMargin) {
				if (enemyX <= wallMargin || enemyX >= getBattleFieldWidth() - wallMargin) {
					setFire(5);
				}
			}

			if (getY() <= wallMargin || getY() >= getBattleFieldHeight() - wallMargin) {
				if (enemyY <= wallMargin || enemyY >= getBattleFieldHeight() - wallMargin) {
					setTurnLeft(90);
					setAhead(400);
				}
			}
		} else {
			if (getEnergy() >= 50) {
				setFire(400 / e.getDistance());
			} else {
				setFire(Math.min(400 / e.getDistance(), 3));
			}
		}

	}

	public void onHitByBullet(HitByBulletEvent e) {
		if (!isWall) {
			setTurnRight(e.getBearing() + 90);
			ahead(300);
		} else if (!(getX() <= wallMargin || getX() >= getBattleFieldHeight() - wallMargin)) {
			setTurnRight(e.getBearing() + 90);
			ahead(300);
		}
	}

	public void onHitRobot(HitRobotEvent e) {
		if (e.getBearing() > -90 && e.getBearing() <= 90) {
			back(100);
		} else {
			ahead(100);
		}
	}
}