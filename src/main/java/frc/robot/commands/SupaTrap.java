// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Flywheel;
import frc.robot.utility.IO;

public class SupaTrap extends SequentialCommandGroup {
  public SupaTrap(IO io) {
    addRequirements(io.shooter, io.intake);
    ProfiledShooter profiledShoot = new ProfiledShooter(io, Flywheel.PASS_OFF_ANGLE);
    addCommands(
        new ParallelRaceGroup(profiledShoot,
          new SequentialCommandGroup(

            // Ensure the Flywheel is at the angle
            new InstantCommand(() -> profiledShoot.setAngle(Flywheel.PASS_OFF_ANGLE)),
            new WaitUntilCommand(() -> Math.abs(io.profiledShoot.controller.getPositionError()) < 10),
            
            // Takes the note
            new InstantCommand(() -> io.shooter.helperVoltage(3)),
            new InstantCommand(() -> io.intake.speed(-1.0)),
            new WaitCommand(0.1),

            // Setting the Flywheel to the trap angle & extending elevator position
            new InstantCommand(() -> profiledShoot.setAngle(135.0)),
            new InstantCommand(() -> io.climber.setElevatorPos(38.0)),
            new InstantCommand(() -> io.intake.speed(0)),
            new InstantCommand(() -> io.shooter.helperVoltage(0)),

            // Hang Down
            new InstantCommand(() -> io.climber.setHangPos(Climber.HANG_DOWN_POS)),
            new WaitUntilCommand(() -> Math.abs(io.climber.hangError()) < 9),
            
            // Shotting Note in Trap
            new InstantCommand(() -> io.shooter.helperVoltage(5)),
            new InstantCommand(() -> io.shooter.flywheelVoltage(-16)),
            new WaitCommand(0.1),
            new WaitUntilCommand(() -> Math.abs(io.climber.elevatorError()) < 10),
            
            // Reset the Shooter (& leave the room for us to lift the hang)
            new InstantCommand(() -> io.shooter.helperVoltage(0)),
            new InstantCommand(() -> io.shooter.flywheelVoltage(0))
            )));
  }
}