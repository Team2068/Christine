// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import frc.robot.Utility.Constants;
import frc.robot.Utility.IO;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class aimbotreal extends PIDCommand {
  IO io;
  public static double aimbotOffsetForward = 5.67;
  public static double aimbotOffsetBackward = -11.13;
  public static double aimbotSpeed = 10;
  public static double minimumAdjustment = 2.5;
  /** Creates a new aimbotreal. */
  public aimbotreal(IO io) {
    super(
        // The controller that the command will use
        new PIDController(0, 0, 0),
        // This should return the measurement
        () -> io.limelight.getTargetData().horizontalOffset,
        // This should return the setpoint (can also be a constant)
        () -> 0,
        // This uses the output
        output -> {
          io.chassis.drive(new ChassisSpeeds(0, output * aimbotSpeed * Constants.DRIVE_MAX_VELOCITY_METERS_PER_SECOND, 0));
        });
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(io.chassis, io.limelight);
    // Configure additional PID options by calling `getController` here.
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Math.abs(getController().getPositionError()) < minimumAdjustment || !io.limelight.getTargetData().hasTargets;
  }

  @Override
  public void end(boolean interrupted) {
    io.chassis.stop();
  }
}
