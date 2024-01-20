// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Utility.IO;
import frc.robot.commands.Pickup;
import frc.robot.commands.ScoreInAmp;
import frc.robot.commands.Score;

public class RobotContainer {
  public IO io;

  public RobotContainer() {
    NamedCommands.registerCommand("Pickup", new Pickup());
    NamedCommands.registerCommand( "ScorenAmp", new ScoreInAmp());
    NamedCommands.registerCommand("Score", new Score());

    io.configGlobal();
    io.configTeleop();
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
