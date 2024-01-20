// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

<<<<<<< HEAD
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;

public class Pickup extends Command {
  /** Creates a new Pickup. */
  public Intake intake = new Intake();
=======
import edu.wpi.first.wpilibj2.command.CommandBase;

public class Pickup extends CommandBase {
  /** Creates a new Pickup. */
>>>>>>> 955c611fade44587762345266774982758d873b0
  public Pickup() {
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
<<<<<<< HEAD
  public void execute() {
    intake.open();
    intake.setSpeed(.5);    ///TODO fix the speed and direction 
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    intake.stop();
  }
=======
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}
>>>>>>> 955c611fade44587762345266774982758d873b0

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
<<<<<<< HEAD
    return intake.loaded();
=======
    return false;
>>>>>>> 955c611fade44587762345266774982758d873b0
  }
}
