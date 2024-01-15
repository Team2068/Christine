// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.utility.IO;

public class DistanceDrive extends Command {
  IO io;
  double distance;

  /** Creates a new distanceDrive. */
  public DistanceDrive(IO io, double distance) {
    this.io = io;
    addRequirements(io.chassis, io.limelight);
    this.distance = distance;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    io.chassis.drive(new ChassisSpeeds(0, 0.5, 0));
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    io.chassis.drive(new ChassisSpeeds(0, 0, 0));
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return io.limelight.distance() < distance;
  }
}
