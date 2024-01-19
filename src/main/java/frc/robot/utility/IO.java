package frc.robot.utility;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.*;
import frc.robot.subsystems.*;
import frc.robot.utility.Constants.DriveConstants;

public class IO {
    final CommandXboxController driveController = new CommandXboxController(0);
    final CommandXboxController mechController = new CommandXboxController(1);

    public final DriveSubsystem chassis = new DriveSubsystem();
    public final Limelight limelight = new Limelight();
    public final Flywheel flywheel = new Flywheel();
    public final Intake intake = new Intake();
    public final Hang hang = new Hang();
    public final LEDs leds = new LEDs();

    SendableChooser<Command> autoSelector;

    public IO(SendableChooser<Runnable> bindings, SendableChooser<Command> selector){
        bindings.setDefaultOption("Teleop Automated", this::configTeleop);
        bindings.addOption("Teleop Manual", this::configManual);
        bindings.addOption("Testing", this::configTesting);
        autoSelector = selector;
    }

    public void configGlobal(){
        chassis.setDefaultCommand(new DefaultDrive(chassis, driveController));
        
        DriverStation.silenceJoystickConnectionWarning(true);
    }

    public void configTeleop(){
        flywheel.setDefaultCommand(new Shoot(this, limelight.tagPose()[1], limelight.distance()));

        driveController.a().onTrue(new InstantCommand(chassis::resetOdometry));
        driveController.b().onTrue(new InstantCommand(chassis::toggleSlowMode));
        driveController.x().onTrue(new InstantCommand(chassis::syncEncoders));

        driveController.leftBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = 0));
        driveController.rightBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = DriveConstants.Field_Oriented));
        driveController.rightTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = DriveConstants.Fixed_Point_Tracking));
        driveController.leftTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = DriveConstants.Fixed_Alignment));

        mechController.a().toggleOnTrue(new Aimbot(this));
        mechController.b().onTrue(new InstantCommand(intake::toggle));
        mechController.x().onTrue(new Shoot(this, 0, 0)); // REPLACE TARGET HEIGHT
        mechController.y().toggleOnTrue(new Climb(this));
    }

    public void configManual(){
        driveController.a().onTrue(new InstantCommand(chassis::resetOdometry));
        driveController.b().onTrue(new InstantCommand(chassis::toggleSlowMode));
        driveController.x().onTrue(new InstantCommand(chassis::syncEncoders));

        driveController.leftBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = 0));
        driveController.rightBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = DriveConstants.Field_Oriented));
        driveController.rightTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = DriveConstants.Fixed_Point_Tracking));
        driveController.leftTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = DriveConstants.Fixed_Alignment));

        mechController.a().toggleOnTrue(new Aimbot(this));
        mechController.b().onTrue(new InstantCommand(intake::toggle));
        mechController.x().onTrue(new Shoot(this, limelight.tagPose()[1], limelight.distance()));

        mechController.leftBumper().onTrue(new InstantCommand(hang::raise));
        mechController.rightBumper().onTrue(new InstantCommand(hang::lower));
    }

    public void configTesting(){
        driveController.a().onTrue(new InstantCommand(chassis::resetOdometry));
        driveController.b().onTrue(new InstantCommand(chassis::resetSteerPositions));
        driveController.x().onTrue(new InstantCommand(chassis::syncEncoders));
        driveController.y().onTrue(autoSelector.getSelected());

        driveController.leftBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = 0));
        driveController.rightBumper().onTrue(new InstantCommand(() -> chassis.drive_mode = DriveConstants.Field_Oriented));
        driveController.leftTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = DriveConstants.Fixed_Point_Tracking));
        driveController.rightTrigger().onTrue(new InstantCommand(() -> chassis.drive_mode = DriveConstants.Fixed_Alignment));

        driveController.povDownLeft().onTrue(new InstantCommand(chassis::resetAbsolute));
        driveController.povUpLeft().onTrue(new InstantCommand(chassis::disableChassis));
        driveController.povDownRight().onTrue(new InstantCommand(chassis::activeChassis));
        driveController.povUpRight().onTrue(systemsCheck());
    }

    public Command systemsCheck(){ // Intake to shooting Test
        return new SequentialCommandGroup(
            new InstantCommand(intake::open, intake),
            new InstantCommand(() -> intake.setSpeed(-0.5), intake),
            new InstantCommand(() -> flywheel.setAngle(45), flywheel),
            new WaitCommand(1),

            new InstantCommand(intake::close, intake),
            new InstantCommand(() -> intake.setSpeed(1), intake),
            new InstantCommand(() -> flywheel.setSpeed(0.1), flywheel),
            new WaitCommand(0.5),
            
            new InstantCommand(flywheel::stop, flywheel),
            new InstantCommand(() -> flywheel.setAngle(0), flywheel),
            new InstantCommand(intake::stop, intake)
        );
    }
}
