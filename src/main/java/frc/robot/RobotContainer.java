// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
<<<<<<< HEAD
import frc.robot.Utility.IO;
import frc.robot.commands.Pickup;
=======
import frc.robot.utility.IO;
>>>>>>> main

public class RobotContainer {
  SendableChooser<Runnable> bindings = new SendableChooser<Runnable>();
  SendableChooser<Command> autos = new SendableChooser<Command>();

  public IO io = new IO(bindings, autos);

  public RobotContainer() {
    NamedCommands.registerCommand("Pickup", new Pickup());
    io.configGlobal();
    io.configTeleop();
  }

  public void addAutos(){}

  public Command getAutonomousCommand() {
    return autos.getSelected();
  }
}
