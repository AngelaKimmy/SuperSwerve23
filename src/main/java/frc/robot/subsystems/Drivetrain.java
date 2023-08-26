// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.HashMap;

import com.kauailabs.navx.frc.AHRS;
import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.PIDConstants;
import com.pathplanner.lib.auto.SwerveAutoBuilder;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.constDrivetrain;
import frc.robot.RobotMap.mapDrivetrain;
import frc.robot.RobotPreferences.prefDrivetrain;
import frc.robot.RobotPreferences.prefVision;

public class Drivetrain extends SubsystemBase {
  private SwerveModule[] modules;
  private SwerveDrivePoseEstimator swervePoseEstimator;
  private SwerveDriveKinematics swerveKinematics;
  public SwerveAutoBuilder swerveAutoBuilder;
  private AHRS navX;
  private boolean isFieldRelative;
  private PIDController steerPID;

  public PathPlannerTrajectory exampleAuto;

  public Drivetrain() {
    isFieldRelative = true;

    modules = new SwerveModule[] {
        new SwerveModule(0, mapDrivetrain.FRONT_LEFT_DRIVE_CAN, mapDrivetrain.FRONT_LEFT_STEER_CAN,
            mapDrivetrain.FRONT_LEFT_ABSOLUTE_ENCODER_CAN, constDrivetrain.FRONT_LEFT_ABS_ENCODER_OFFSET),
        new SwerveModule(1, mapDrivetrain.FRONT_RIGHT_DRIVE_CAN, mapDrivetrain.FRONT_RIGHT_STEER_CAN,
            mapDrivetrain.FRONT_RIGHT_ABSOLUTE_ENCODER_CAN, constDrivetrain.FRONT_RIGHT_ABS_ENCODER_OFFSET),
        new SwerveModule(2, mapDrivetrain.BACK_LEFT_DRIVE_CAN, mapDrivetrain.BACK_LEFT_STEER_CAN,
            mapDrivetrain.BACK_LEFT_ABSOLUTE_ENCODER_CAN, constDrivetrain.BACK_LEFT_ABS_ENCODER_OFFSET),
        new SwerveModule(3, mapDrivetrain.BACK_RIGHT_DRIVE_CAN, mapDrivetrain.BACK_RIGHT_STEER_CAN,
            mapDrivetrain.BACK_RIGHT_ABSOLUTE_ENCODER_CAN, constDrivetrain.BACK_RIGHT_ABS_ENCODER_OFFSET),
    };
    swerveKinematics = constDrivetrain.SWERVE_KINEMATICS;

    steerPID = new PIDController(
        prefDrivetrain.steerP.getValue(),
        prefDrivetrain.steerI.getValue(),
        prefDrivetrain.steerD.getValue());

    navX = new AHRS();

    // Both the NavX and the absolute encoders need time to initialize
    Timer.delay(2.5);
    navX.reset();
    resetModulesToAbsolute();
    configure();
  }

  public void configure() {
    for (SwerveModule mod : modules) {
      mod.configure();
    }
    swervePoseEstimator = new SwerveDrivePoseEstimator(
        swerveKinematics,
        navX.getRotation2d(),
        getModulePositions(),
        new Pose2d(),
        VecBuilder.fill(
            Units.feetToMeters(prefDrivetrain.measurementStdDevsFeet.getValue()),
            Units.feetToMeters(prefDrivetrain.measurementStdDevsFeet.getValue()),
            Units.degreesToRadians(prefDrivetrain.measurementStdDevsDegrees.getValue())),
        VecBuilder.fill(
            Units.feetToMeters(prefVision.measurementStdDevsFeet.getValue()),
            Units.feetToMeters(prefVision.measurementStdDevsFeet.getValue()),
            Units.degreesToRadians(prefVision.measurementStdDevsDegrees.getValue())));

    swerveAutoBuilder = new SwerveAutoBuilder(
        this::getPose,
        this::resetPoseToPose,
        swerveKinematics,
        new PIDConstants(prefDrivetrain.autoDriveP.getValue(),
            prefDrivetrain.autoDriveI.getValue(),
            prefDrivetrain.autoDriveD.getValue()),
        new PIDConstants(prefDrivetrain.autoSteerP.getValue(),
            prefDrivetrain.autoSteerI.getValue(),
            prefDrivetrain.autoSteerD.getValue()),
        this::setModuleStatesAuto,
        new HashMap<>(),
        constDrivetrain.AUTO_USE_ALLIANCE_COLOR,
        this);

    exampleAuto = PathPlanner.loadPath("examplePath", new PathConstraints(
        Units.feetToMeters(prefDrivetrain.autoMaxSpeedFeet.getValue()),
        Units.feetToMeters(prefDrivetrain.autoMaxAccelFeet.getValue())));

    steerPID.setPID(
        prefDrivetrain.steerP.getValue(),
        prefDrivetrain.steerI.getValue(),
        prefDrivetrain.steerD.getValue());
    steerPID.setTolerance(Units.inchesToMeters(prefDrivetrain.steerPIDTolerance.getValue()));
    steerPID.enableContinuousInput(-Math.PI, Math.PI);
    steerPID.reset();

  }

  /**
   * Reset all of the steer motors to the absolute encoder values.
   */
  public void resetModulesToAbsolute() {
    for (SwerveModule mod : modules) {
      mod.resetSteerMotorToAbsolute();
    }
  }

  /**
   * Get the rotation of the drivetrain using the NavX.
   * 
   * @return Rotation of drivetrain in radians
   */
  public Rotation2d getRotation() {
    return Rotation2d.fromRadians(MathUtil.angleModulus(navX.getRotation2d().getRadians()));
  }

  /**
   * Get the position (distance, angle) of each module.
   * 
   * @return An Array of Swerve module positions
   */
  public SwerveModulePosition[] getModulePositions() {
    SwerveModulePosition[] positions = new SwerveModulePosition[4];

    for (SwerveModule mod : modules) {
      positions[mod.moduleNumber] = mod.getModulePosition();
    }

    return positions;
  }

  /**
   * Set the state of the modules
   * 
   * @param desiredModuleStates Desired states to set the modules to
   * @param isOpenLoop          Are the modules being set based on open loop or
   *                            closed loop control
   * 
   */
  public void setModuleStates(SwerveModuleState[] desiredModuleStates, boolean isOpenLoop) {
    // Lowers the speeds so that they are actually achievable
    SwerveDriveKinematics.desaturateWheelSpeeds(desiredModuleStates, constDrivetrain.MAX_MODULE_SPEED);

    for (SwerveModule mod : modules) {
      mod.setModuleState(desiredModuleStates[mod.moduleNumber], isOpenLoop);
    }
  }

  /**
   * Set the state of the modules in autonomous. Always set with open-loop
   * control.
   * 
   * @param desiredModuleStates Desired states to set the modules to
   * 
   */
  private void setModuleStatesAuto(SwerveModuleState[] desiredModuleStates) {
    setModuleStates(desiredModuleStates, false);
  }

  /**
   * Drive the drivetrain
   * 
   * @param translation Desired translational velocity in meters per second
   * @param rotation    Desired rotational velocity in radians per second
   * @param isOpenLoop  Are the modules being set based on open loop or closed
   *                    loop control
   * 
   */
  public void drive(Translation2d translation, double rotation, boolean isOpenLoop) {
    ChassisSpeeds chassisSpeeds;

    if (isFieldRelative) {
      chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
          translation.getX(),
          translation.getY(),
          rotation,
          getRotation());
    } else {
      chassisSpeeds = new ChassisSpeeds(
          translation.getX(),
          translation.getY(),
          rotation);
    }

    SwerveModuleState[] desiredModuleStates = swerveKinematics.toSwerveModuleStates(chassisSpeeds);
    setModuleStates(desiredModuleStates, isOpenLoop);
  }

  /**
   * Rotate to a given angle in degrees
   * 
   * @param rotation   Desired rotation in field-relative degrees
   * @param isOpenLoop Are the modules being set based on open loop or closed loop
   *                   control
   * 
   */
  public void rotateToAngle(double desiredRotation, boolean isOpenLoop) {
    // Tell the PID controller where it needs to go
    steerPID.setSetpoint(Units.degreesToRadians(desiredRotation));

    // Tell the PID where it currently is
    double angleFromCurrentAngle = steerPID.calculate(getRotation().getRadians());

    // Limit the speed so that it doesn't die
    angleFromCurrentAngle = MathUtil.clamp(angleFromCurrentAngle,
        -Units.degreesToRadians(prefDrivetrain.turnSpeed.getValue()),
        Units.degreesToRadians(prefDrivetrain.turnSpeed.getValue()));

    // Give it to the drive command
    drive(new Translation2d(0, 0), angleFromCurrentAngle, isOpenLoop);
  }

  /**
   * Sets all modules to neutral output
   */
  public void neutralDriveOutputs() {
    for (SwerveModule mod : modules) {
      mod.neutralDriveOutput();
    }
  }

  /**
   * Set the drive method to use field relative drive controls
   */
  public void setFieldRelative() {
    isFieldRelative = true;
  }

  /**
   * Set the drive method to use robot relative drive controls
   */
  public void setRobotRelative() {
    isFieldRelative = false;
  }

  /**
   * Updates the pose estimator with the current robot uptime, the gyro yaw, and
   * each swerve module position.
   * <p>
   * This method MUST be called every loop (or else pose estimator breaks)
   */
  public void updatePoseEstimator() {
    swervePoseEstimator.updateWithTime(
        Timer.getFPGATimestamp(),
        navX.getRotation2d(),
        getModulePositions());
  }

  /**
   * Return the current estimated pose from the pose estimator.
   * 
   * @return The current estimated pose
   */
  public Pose2d getPose() {
    return swervePoseEstimator.getEstimatedPosition();
  }

  /**
   * Reset the pose estimator's pose to a given pose.
   * 
   * @param pose The pose you would like to reset the pose estimator to
   */
  public void resetPoseToPose(Pose2d pose) {
    swervePoseEstimator.resetPosition(navX.getRotation2d(), getModulePositions(), pose);
  }

  /**
   * Resets the Yaw of the NavX, along with the angle adjustment of the NavX.
   */
  public void resetYaw() {
    navX.setAngleAdjustment(0);
    navX.reset();
  }

  /**
   * Sets value of the NavX's adjustment value, which is a constant added to the
   * NavX value. Used at the start of auto to match the setup of the robot, as
   * drive(); reads the NavX yaw to figure out it's angle.
   * 
   * @param adjustment Value to add to the current yaw
   */
  public void setNavXAngleAdjustment(double adjustment) {
    navX.setAngleAdjustment(adjustment);
  }

  @Override
  public void periodic() {
    updatePoseEstimator();
    SmartDashboard.putBoolean("Is Drivetrain Field Relative", isFieldRelative);
    for (SwerveModule mod : modules) {
      SmartDashboard.putNumber("Module " + mod.moduleNumber + " Speed",
          Units.metersToFeet(mod.getModuleState().speedMetersPerSecond));
      SmartDashboard.putNumber("Module " + mod.moduleNumber + " Distance",
          Units.metersToFeet(mod.getModulePosition().distanceMeters));
      SmartDashboard.putNumber("Module " + mod.moduleNumber + " Angle",
          mod.getModuleState().angle.getDegrees());
      SmartDashboard.putNumber("Module " + mod.moduleNumber + " Absolute Encoder Angle (WITH OFFSET)",
          mod.getAbsoluteEncoder());
      SmartDashboard.putNumber("Module " + mod.moduleNumber + " Absolute Encoder Raw Value",
          mod.getRawAbsoluteEncoder());
    }
  }
}