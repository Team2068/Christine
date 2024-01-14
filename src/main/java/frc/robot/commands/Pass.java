// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Utility.IO;
import frc.robot.subsystems.Flywheel;

public class Pass extends Command {
  IO io;

  public Pass(IO io) {
    this.io = io;
    addRequirements(io.limelight, io.intake, io.flywheel);
  }

  @Override
  public void initialize() {

  }

  @Override
  public void execute() {
    double distance = io.limelight.distance();
    double height = io.limelight.tagPose()[1] - Flywheel.height;
    double angle = Flywheel.pivotAngle(height,distance);
    io.flywheel.setAngle(angle);
    if (distance < 7) // NOTE: 7 (metres) is a placeholder 
      io.flywheel.setSpeed(Flywheel.RPM(angle, distance));
  }

  @Override
  public void end(boolean interrupted) {
    io.flywheel.setSpeed(0);
    io.flywheel.setAngle(0);
  }

  @Override
  public boolean isFinished() {
    return !io.intake.loaded() || io.limelight.tagID() == -1;
  }
}
