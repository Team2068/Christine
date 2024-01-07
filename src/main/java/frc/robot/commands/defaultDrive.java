package frc.robot.commands;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveSubsystem;

import java.util.function.DoubleSupplier;

public class defaultDrive extends CommandBase {
    private final DriveSubsystem driveSubsystem;

    private final DoubleSupplier x_supplier;
    private final DoubleSupplier y_supplier;
    private final DoubleSupplier rotation_supplier;
    private final SlewRateLimiter xLimiter = new SlewRateLimiter(4);
    private final SlewRateLimiter yLimiter = new SlewRateLimiter(4);

    public defaultDrive(DriveSubsystem driveSubsystem, ChassisSpeeds chassisSpeeds) {
        this(driveSubsystem, () -> chassisSpeeds.vxMetersPerSecond, () -> chassisSpeeds.vyMetersPerSecond, () -> chassisSpeeds.omegaRadiansPerSecond);
    }

    public defaultDrive(DriveSubsystem driveSubsystem,
                               DoubleSupplier translationXSupplier,
                               DoubleSupplier translationYSupplier,
                               DoubleSupplier rotationSupplier) {
        this.driveSubsystem = driveSubsystem;
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

        driveSubsystem.drive((driveSubsystem.isFieldOriented()) ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rotationSpeed, driveSubsystem.rotation()) : new ChassisSpeeds(xSpeed, ySpeed, rotationSpeed));
    }

    @Override
    public void end(boolean interrupted) {
        driveSubsystem.stop();
    }
}