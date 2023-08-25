package frc.robot;

import com.frcteam3255.preferences.SN_DoublePreference;

import edu.wpi.first.math.util.Units;

public class RobotPreferences {
  public static final class prefDrivetrain {
    // This PID is implemented on each module, not the Drivetrain subsystem.
    public static final SN_DoublePreference modDriveF = new SN_DoublePreference("modDriveF", 0.045);
    public static final SN_DoublePreference modDriveP = new SN_DoublePreference("modDriveP", 0.1);
    public static final SN_DoublePreference modDriveI = new SN_DoublePreference("modDriveI", 0.0);
    public static final SN_DoublePreference modDriveD = new SN_DoublePreference("modDriveD", 1.0);

    public static final SN_DoublePreference modSteerP = new SN_DoublePreference("modSteerP", 0.3);
    public static final SN_DoublePreference modSteerI = new SN_DoublePreference("modSteerI", 0.0);
    public static final SN_DoublePreference modSteerD = new SN_DoublePreference("modSteerD", 6.0);

    public static final SN_DoublePreference autoDriveP = new SN_DoublePreference("autoDriveP", 2);
    public static final SN_DoublePreference autoDriveI = new SN_DoublePreference("autoDriveI", 0);
    public static final SN_DoublePreference autoDriveD = new SN_DoublePreference("autoDriveD", 0);

    public static final SN_DoublePreference autoSteerP = new SN_DoublePreference("autoSteerP", 0.5);
    public static final SN_DoublePreference autoSteerI = new SN_DoublePreference("autoSteerI", 0.0);
    public static final SN_DoublePreference autoSteerD = new SN_DoublePreference("autoSteerD", 0.0);

    // PID for Drivetrain
    public static final SN_DoublePreference steerP = new SN_DoublePreference("steerP", 8.0);
    public static final SN_DoublePreference steerI = new SN_DoublePreference("steerI", 0);
    public static final SN_DoublePreference steerD = new SN_DoublePreference("steerD", 0.2);
    // In Degrees
    public static final SN_DoublePreference steerPIDTolerance = new SN_DoublePreference("steerPIDTolerance", 2);

    public static final SN_DoublePreference minimumSteerSpeedPercent = new SN_DoublePreference("minimumSteerSpeed",
        0.01);

    // Translational speed (feet per second) while manually driving
    // MAX: 16.3 FPS (Due to gearing)
    public static final SN_DoublePreference driveSpeed = new SN_DoublePreference("driveSpeed", 16.3);

    // Rotational speed (degrees per second) while manually driving
    // MAX: 943.751 DPS (Due to gearing and robot size)
    public static final SN_DoublePreference turnSpeed = new SN_DoublePreference("turnSpeed", 360);

    public static final SN_DoublePreference autoMaxSpeedFeet = new SN_DoublePreference(
        "autoMaxSpeedFeet", 2);
    public static final SN_DoublePreference autoMaxAccelFeet = new SN_DoublePreference(
        "autoMaxAccelFeet", 1);

    // Pose estimator standard deviations for encoder & gyro data
    public static final SN_DoublePreference measurementStdDevsFeet = new SN_DoublePreference(
        "measurementStdDevsFeet", Units.metersToFeet(0.1));
    public static final SN_DoublePreference measurementStdDevsDegrees = new SN_DoublePreference(
        "measurementStdDevsDegrees", Units.metersToFeet(0.1));
  }

  public static final class prefVision {
    // Pose estimator standard deviations for vision data
    public static final SN_DoublePreference measurementStdDevsFeet = new SN_DoublePreference(
        "measurementStdDevsFeet", Units.metersToFeet(0.9));
    public static final SN_DoublePreference measurementStdDevsDegrees = new SN_DoublePreference(
        "measurementStdDevsDegrees", Units.metersToFeet(0.9));

  }
}
