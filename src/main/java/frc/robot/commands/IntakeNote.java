// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

public class IntakeNote extends Command {

  IO io;

  public IntakeNote(IO io) {
    this.io = io;

    addRequirements(io.intake);
  }

  @Override
  public void initialize() {
    io.intake.open();
    io.intake.setSpeed(-0.5);
    io.flywheel.setPosition(0);
  }

  @Override
  public void execute() {}

  @Override
  public void end(boolean interrupted) {
    io.intake.close();
    io.intake.stop();
  }

  @Override
  public boolean isFinished() {
    return (io.intake.loaded());
  }
}
