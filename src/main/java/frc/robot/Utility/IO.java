package frc.robot.Utility;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.InstantCommand;
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

    public IO(SendableChooser<Runnable> bindings){
        bindings.setDefaultOption("Teleop Automated", this::configTeleop);
        bindings.setDefaultOption("Teleop Manual", this::configManual);
        bindings.addOption("Testing", this::configTesting);
    }

    public void configGlobal(){
        chassis.setDefaultCommand(new DefaultDrive(chassis, driveController));
        flywheel.setDefaultCommand(new Shoot(this, limelight.tagPose()[1], limelight.distance()));
        
        DriverStation.silenceJoystickConnectionWarning(true);
    }

    public void configTeleop(){
        mechController.a().onTrue(new Aimbot(this));
        mechController.b().onTrue(new InstantCommand(intake::toggle));
        mechController.x().onTrue(new Shoot(this, 0, 0)); // REPLACE TARGET HEIGHT
        mechController.y().onTrue(new Climb(this));
    }

    public void configManual(){

    }

    public void configTesting(){

    }

    // TODO: Create A System's check command to put in testing
}
