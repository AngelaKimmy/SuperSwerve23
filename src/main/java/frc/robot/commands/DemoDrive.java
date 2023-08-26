// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class DemoDrive extends CommandBase {
  Drivetrain subDrivetrain;
  double xVelocity;
  double yVelocity;
  double desiredTime;
  double endTime;
  boolean isOpenLoop;

  public DemoDrive(Drivetrain subDrivetrain, double xVelocity, double yVelocity, double desiredTime,
      boolean isOpenLoop) {
    this.subDrivetrain = subDrivetrain;
    this.xVelocity = xVelocity;
    this.yVelocity = yVelocity;
    this.desiredTime = desiredTime;
    this.isOpenLoop = isOpenLoop;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    endTime = Timer.getFPGATimestamp() + desiredTime;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    subDrivetrain.drive(new Translation2d(xVelocity, yVelocity), 0, isOpenLoop);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    subDrivetrain.neutralDriveOutputs();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Timer.getFPGATimestamp() > endTime;
  }
}
