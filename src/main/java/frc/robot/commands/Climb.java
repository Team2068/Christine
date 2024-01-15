// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.utility.IO;

public class Climb extends SequentialCommandGroup {

  public Climb(IO io) {
    addCommands(
      new InstantCommand((io.hang::raise)),
      new Aimbot(io),
      new DistanceDrive(io, 0), // TODO: PLACEHOLDER
      new InstantCommand((io.hang::lower))
    );
  }
}
