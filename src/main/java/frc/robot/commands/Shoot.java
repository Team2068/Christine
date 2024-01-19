// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Flywheel;
import frc.robot.utility.IO;

public class Shoot extends Command {
  
  IO io;
  double distance;
  double target_height;

  public Shoot(IO io, double target_height, double distance) {
    this.io = io;
    this.distance = distance;
    this.target_height = target_height;
    addRequirements(io.limelight, io.intake, io.flywheel);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    double height = target_height - Flywheel.height;
    double angle = Flywheel.pivotAngle(height,distance);
    io.flywheel.setAngle(angle);
    if (distance < 7) io.flywheel.setSpeed(Flywheel.RPM(angle, distance)); // NOTE: 7 (metres) is a placeholder 
  }

  @Override
  public void end(boolean interrupted) {
    io.flywheel.setSpeed(0);
    io.flywheel.setAngle(0);
  }

  @Override
  public boolean isFinished() {
    return !io.intake.loaded() || io.limelight.tagID() != 4 || io.limelight.tagID() != 7 || io.limelight.tagID() == -1;
  }
}
