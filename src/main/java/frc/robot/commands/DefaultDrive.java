package frc.robot.commands;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.DriveSubsystem;

import java.util.function.DoubleSupplier;

public class DefaultDrive extends Command {
    private final DriveSubsystem chassis;

    private final DoubleSupplier x_supplier;
    private final DoubleSupplier y_supplier;
    private final DoubleSupplier rotation_supplier;
    private final SlewRateLimiter xLimiter = new SlewRateLimiter(4);
    private final SlewRateLimiter yLimiter = new SlewRateLimiter(4);

    public DefaultDrive(DriveSubsystem chassis, ChassisSpeeds chassisSpeeds) {
        this(chassis, () -> chassisSpeeds.vxMetersPerSecond, () -> chassisSpeeds.vyMetersPerSecond, () -> chassisSpeeds.omegaRadiansPerSecond);
    }

    public DefaultDrive(DriveSubsystem chassis, CommandXboxController controller) {
        this(chassis, () -> -modifyAxis(controller.getLeftY()) * chassis.MAX_VELOCITY_METERS_PER_SECOND,
        () -> -modifyAxis(controller.getLeftX()) * chassis.MAX_VELOCITY_METERS_PER_SECOND,
        () -> -modifyAxis(controller.getRightX())* chassis.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND);
    }
  
    public DefaultDrive(DriveSubsystem driveSubsystem,
        DoubleSupplier translationXSupplier,
        DoubleSupplier translationYSupplier,
        DoubleSupplier rotationSupplier) {
        
        this.chassis = driveSubsystem;
        this.x_supplier = translationXSupplier;
        this.y_supplier = translationYSupplier;
        this.rotation_supplier = rotationSupplier;

        addRequirements(driveSubsystem);
    }
    
    @Override
    public void execute() {
        double xSpeed = xLimiter.calculate(x_supplier.getAsDouble());
        double ySpeed = yLimiter.calculate(y_supplier.getAsDouble());
        double rotationSpeed = rotation_supplier.getAsDouble() * 0.7;

        // double xSpeed = x_supplier.getAsDouble() * 0.5;
        // double ySpeed = y_supplier.getAsDouble() * 0.5;

        chassis.drive((chassis.isFieldOriented()) ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rotationSpeed, chassis.rotation()) : new ChassisSpeeds(xSpeed, ySpeed, rotationSpeed));
    }

    @Override
    public void end(boolean interrupted) {
        chassis.stop();
    }

    private static double deadband(double value, double deadband) {
        if (Math.abs(value) <= deadband) return 0.0;
        deadband *= (value > 0.0) ? 1 : -1;
        return (value + deadband) / (1.0 + deadband);
    }

    private static double modifyAxis(double value) {
        value = deadband(value, 0.05); // Deadband
        value = Math.copySign(value * value, value); // Square the axis
        return value;
    }
}