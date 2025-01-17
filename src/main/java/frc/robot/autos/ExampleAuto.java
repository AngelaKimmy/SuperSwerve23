// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.autos;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.Drivetrain;

public class ExampleAuto extends SequentialCommandGroup {
  Drivetrain subDrivetrain;

  public ExampleAuto(Drivetrain subDrivetrain) {
    this.subDrivetrain = subDrivetrain;

    addCommands(
        Commands.runOnce(() -> subDrivetrain.resetYaw()),
        Commands.runOnce(() -> subDrivetrain
            .setNavXAngleAdjustment(subDrivetrain.exampleAuto.getInitialHolonomicPose().getRotation().getDegrees())),

        subDrivetrain.swerveAutoBuilder.fullAuto(subDrivetrain.exampleAuto)
            .withTimeout(subDrivetrain.exampleAuto.getTotalTimeSeconds()));
  }
}
