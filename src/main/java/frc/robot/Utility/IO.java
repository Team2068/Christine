package frc.robot.Utility;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.*;

import frc.robot.Constants.*;
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
        bindings.setDefaultOption("Teleop", this::configTeleop)
        bindings.addOption("Testing", this::configTesting);
    }

    public void configGlobal(){
        chassis.setDefaultCommand(new defaultDrive(chassis, driveController));
        
        DriverStation.silenceJoystickConnectionWarning(true);
    }

    public void configTeleop(){}

    public void configTesting(){}

    // TODO: Create A System's check command to put in testing
}
