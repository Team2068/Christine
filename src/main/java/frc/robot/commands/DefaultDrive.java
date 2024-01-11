package frc.robot.commands;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveSubsystem;

import java.util.function.DoubleSupplier;

public class DefaultDrive extends CommandBase {
    private final DriveSubsystem chassis;

    private final DoubleSupplier x_supplier;
    private final DoubleSupplier y_supplier;
    private final DoubleSupplier rotation_supplier;
    private final SlewRateLimiter xLimiter = new SlewRateLimiter(4);
    private final SlewRateLimiter yLimiter = new SlewRateLimiter(4);

    private CommandXboxController controller;

    public DefaultDrive(DriveSubsystem chassis, ChassisSpeeds chassisSpeeds) {
        this(chassis, () -> chassisSpeeds.vxMetersPerSecond, () -> chassisSpeeds.vyMetersPerSecond, () -> chassisSpeeds.omegaRadiansPerSecond);
    }

    public DefaultDrive(DriveSubsystem chassis, CommandXboxController controller) {
        this.chassis = chassis;
        this.controller = controller;

        this(chassis, () -> -modifyAxis(driveController.getLeftY()) * chassis.MAX_VELOCITY_METERS_PER_SECOND,
        () -> -modifyAxis(driveController.getLeftX()) * chassis.MAX_VELOCITY_METERS_PER_SECOND,
        () -> -modifyAxis(driveController.getRightX())* chassis.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND));
    }
  
    public DefaultDriveCommand(DriveSubsystem driveSubsystem,
        DoubleSupplier translationXSupplier,
        DoubleSupplier translationYSupplier,
        DoubleSupplier rotationSupplier) {
        
        this.driveSubsystem = driveSubsystem;
        this.m_translationXSupplier = translationXSupplier;
        this.m_translationYSupplier = translationYSupplier;
        this.m_rotationSupplier = rotationSupplier;

        addRequirements(driveSubsystem);
    }
    
    @Override
    public void execute() {
        double xSpeed = xLimiter.calculate(x_supplier.getAsDouble());
        double ySpeed = yLimiter.calculate(y_supplier.getAsDouble());
        double rotationSpeed = rotation_supplier.getAsDouble() * 0.7;

        // double xSpeed = x_supplier.getAsDouble() * 0.5;
        // double ySpeed = y_supplier.getAsDouble() * 0.5;

        driveSubsystem.drive((driveSubsystem.isFieldOriented()) ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rotationSpeed, driveSubsystem.rotation()) : new ChassisSpeeds(xSpeed, ySpeed, rotationSpeed));
    }

    @Override
    public void end(boolean interrupted) {
        driveSubsystem.stop();
    }

    private static double deadband(double value, double deadband) {
        if (Math.abs(value) <= deadband)
            return 0.0;
        deadband *= (value > 0.0) ? 1 : -1;
        return (value + deadband) / (1.0 + deadband);
    }

    private static double modifyAxis(double value) {
        value = deadband(value, 0.05); // Deadband
        value = Math.copySign(value * value, value); // Square the axis
        return value;
    }
}