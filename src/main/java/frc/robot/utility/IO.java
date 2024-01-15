package frc.robot.utility;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.*;
import frc.robot.subsystems.*;

public class IO {
    final CommandXboxController driveController = new CommandXboxController(0);
    final CommandXboxController mechController = new CommandXboxController(1);

    public final DriveSubsystem chassis = new DriveSubsystem();
    public final Limelight limelight = new Limelight();
    public final Flywheel flywheel = new Flywheel();
    public final Intake intake = new Intake();
    public final Hang hang = new Hang();

    SendableChooser<Command> autoSelector;

    public IO(SendableChooser<Runnable> bindings, SendableChooser<Command> selector){
        bindings.setDefaultOption("Teleop Automated", this::configTeleop);
        bindings.setDefaultOption("Teleop Manual", this::configManual);
        bindings.addOption("Testing", this::configTesting);
        autoSelector = selector;
    }

    public void configGlobal(){
        chassis.setDefaultCommand(new DefaultDrive(chassis, driveController));
        flywheel.setDefaultCommand(new Shoot(this, limelight.tagPose()[1], limelight.distance()));
        
        DriverStation.silenceJoystickConnectionWarning(true);
    }

    public void configTeleop(){
        driveController.a().onTrue(new InstantCommand(chassis::resetOdometry));
        driveController.b().onTrue(new InstantCommand(chassis::toggleSlowMode));
        driveController.x().onTrue(new InstantCommand(chassis::syncEncoders));

        driveController.leftBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = 0));
        driveController.rightBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = chassis.Field_Oriented));
        driveController.rightTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = chassis.Fixed_Point_Tracking));
        driveController.leftTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = chassis.Fixed_Alignment));

        mechController.a().onTrue(new Aimbot(this));
        mechController.b().onTrue(new InstantCommand(intake::toggle));
        mechController.x().onTrue(new Shoot(this, 0, 0)); // REPLACE TARGET HEIGHT
        mechController.y().onTrue(new Climb(this));
    }

    public void configManual(){
        driveController.a().onTrue(new InstantCommand(chassis::resetOdometry));
        driveController.b().onTrue(new InstantCommand(chassis::toggleSlowMode));
        driveController.x().onTrue(new InstantCommand(chassis::syncEncoders));

        driveController.leftBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = 0));
        driveController.rightBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = chassis.Field_Oriented));
        driveController.rightTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = chassis.Fixed_Point_Tracking));
        driveController.leftTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = chassis.Fixed_Alignment));

        mechController.a().onTrue(null);
        mechController.b().onTrue(null);
        mechController.x().onTrue(null);
        mechController.y().onTrue(null);
    }

    public void configTesting(){
        driveController.a().onTrue(new InstantCommand(chassis::resetOdometry));
        driveController.b().onTrue(new InstantCommand(chassis::resetSteerPositions));
        driveController.x().onTrue(new InstantCommand(chassis::syncEncoders));
        driveController.y().onTrue(autoSelector.getSelected());

        driveController.leftBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = 0));
        driveController.rightBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = chassis.Field_Oriented));
        driveController.leftTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = chassis.Fixed_Point_Tracking));
        driveController.rightTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = chassis.Fixed_Alignment));

        driveController.povDownLeft().onTrue(new InstantCommand(chassis::resetAbsolute));
        driveController.povUpLeft().onTrue(new InstantCommand(chassis::disableChassis));
        driveController.povDownRight().onTrue(new InstantCommand(chassis::activeChassis));
    }

    // TODO: Create A System's check command to put in testing
}
