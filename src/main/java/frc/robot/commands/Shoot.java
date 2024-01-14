// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

public class Shoot extends Command {
  IO io;

  public Shoot(IO io) {
    this.io = io;
    addRequirements(io.limelight, io.intake, io.flywheel);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    // TODO: Get tag height 
    double distance = io.limelight.distance();
    double height = io.limelight.tagPose()[1] - io.chassis.getPose().getY();
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
    return (!io.intake.loaded());
  }
}
