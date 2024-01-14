// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Utility.IO;
import frc.robot.commands.Aimbot;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class climbingsequence extends SequentialCommandGroup {
  /** Creates a new climbingsequence. */
  public climbingsequence(IO io) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
      new InstantCommand(()->io.hang.setHeight(3)),
      new Aimbot(io),
      new distanceDrive(io, 0), // note change 0 later
      new InstantCommand(()->io.hang.setHeight(0))
    );
  }
}
