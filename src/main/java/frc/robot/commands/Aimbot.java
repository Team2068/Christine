// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import frc.robot.Utility.Constants;
import frc.robot.Utility.IO;

public class Aimbot extends PIDCommand {
  
  // TODO: REPLACE ALL THESE PLACEHOLDER VALUES
  public static double AIMBOT_OFFSET_FORWARD = 5.67;
  public static double AIMBOT_OFFSET_BACKWARD = -11.13;
  public static double AimbotSpeed = 123456;
  public static double minimumAdjustment = 2.5;
  IO io;

  public Aimbot(IO io) {
    super(
        new PIDController(0, 0, 0),
        () -> io.limelight.getTargetData().horizontalOffset,
        () -> 0, // TODO: replace with actual target offset
        output -> {
          io.chassis.drive(new ChassisSpeeds(0, output * AimbotSpeed * Constants.DRIVE_MAX_VELOCITY_METERS_PER_SECOND, 0));
        });
        addRequirements(io.chassis, io.limelight);
  }

  @Override
  public boolean isFinished() {
    return Math.abs(getController().getPositionError()) < minimumAdjustment || io.limelight.getTargetData().hasTargets;
  }

  @Override
  public void end(boolean interrupted) {
    io.chassis.stop();
  }
}
