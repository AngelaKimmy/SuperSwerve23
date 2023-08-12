// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.autos;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.DemoDrive;
import frc.robot.commands.DemoSteer;
import frc.robot.subsystems.Drivetrain;

// Place your moves here!

public class DemoAuto extends SequentialCommandGroup {
  Drivetrain subDrivetrain;
  boolean isOpenLoop;
  double desiredXSpeed = 1; // meters per second
  double desiredYSpeed = 1; // meters per second
  double timeoutSeconds = 2;
  double desiredAngleDegrees = 45;

  public DemoAuto(Drivetrain subDrivetrain) {
    this.subDrivetrain = subDrivetrain;
    isOpenLoop = true;

    addCommands(
        Commands.runOnce(() -> subDrivetrain.resetYaw()), // Set our current rotation to wherever we currently are

        new DemoDrive(subDrivetrain, 1, 0, 2, isOpenLoop),

        new DemoSteer(subDrivetrain, 270),

        new DemoSteer(subDrivetrain, 315),

        new DemoDrive(subDrivetrain, .5, .5, 1, isOpenLoop),
        new DemoSteer(subDrivetrain, 45),
        new DemoDrive(subDrivetrain, .5, .5, 1, isOpenLoop),
    new DemoSteer(subDrivetrain, 145),
    new DemoDrive(subDrivetrain, .5, .5, 1, isOpenLoop),
    new DemoSteer(subDrivetrain, 45),
    new DemoDrive(subDrivetrain, .5, .5, 1, isOpenLoop)
    
        ); 
  }
}
