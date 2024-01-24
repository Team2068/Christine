package frc.robot.modules;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkLowLevel.PeriodicFrame;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import com.reduxrobotics.sensors.canandcoder.Canandcoder;


public class HeliumSwerveModule implements SwerveModule {
    public final CANSparkMax driveMotor;
    public final CANSparkMax steerMotor;
    public final Canandcoder steerEncoder; 

    final double WHEEL_DIAMETER = 0.10033; // Metres
    final double DRIVE_REDUCTION = (15.0 / 32.0) * (10.0 / 60.0);
    final double STEER_REDUCTION = (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0);
    final double DRIVE_CONVERSION_FACTOR = Math.PI * WHEEL_DIAMETER * DRIVE_REDUCTION;

    double desiredAngle;

    final double PI2 = 2.0 * Math.PI; 

    public HeliumSwerveModule(ShuffleboardLayout tab, int driveID, int steerID, int steerCANID, double offse){
        driveMotor = new CANSparkMax(driveID, MotorType.kBrushless);
        steerMotor = new CANSparkMax(steerID, MotorType.kBrushless);
        steerEncoder = new Canandcoder(steerCANID);

        Canandcoder.Settings settings = new Canandcoder.Settings();
        settings.setInvertDirection(true);
        steerEncoder.clearStickyFaults();
        steerEncoder.resetFactoryDefaults(false);
        steerEncoder.setSettings(settings);

        steerMotor.setSmartCurrentLimit(20);
        driveMotor.setSmartCurrentLimit(40);

        steerMotor.setIdleMode(IdleMode.kBrake);
        driveMotor.setIdleMode(IdleMode.kCoast);

        driveMotor.getEncoder().setPositionConversionFactor(DRIVE_CONVERSION_FACTOR);
        driveMotor.getEncoder().setVelocityConversionFactor(DRIVE_CONVERSION_FACTOR / 60.0);

        steerMotor.getEncoder().setPositionConversionFactor(Math.PI * STEER_REDUCTION);
        steerMotor.getEncoder().setVelocityConversionFactor(Math.PI * STEER_REDUCTION / 60);
        steerMotor.getEncoder().setPosition(steerAngle());
        driveMotor.setInverted(true);
        steerMotor.setInverted(false);

        driveMotor.enableVoltageCompensation(12);

        steerMotor.getPIDController().setP(0.1);
        steerMotor.getPIDController().setI(0.0);
        steerMotor.getPIDController().setD(1.0);

        steerMotor.setPeriodicFramePeriod(PeriodicFrame.kStatus0,100);
        steerMotor.setPeriodicFramePeriod(PeriodicFrame.kStatus1,20);
        steerMotor.setPeriodicFramePeriod(PeriodicFrame.kStatus2,20);

        tab.addDouble("Absolute Angle", () -> Math.toDegrees(steerAngle())); 
        tab.addDouble("Current Angle", () -> Math.toDegrees(steerMotor.getEncoder().getPosition()));
        tab.addDouble("Target Angle", () -> Math.toDegrees(desiredAngle));
        tab.addBoolean("Active", steerEncoder::isConnected);
    }

    public void resetDrivePosition() {
        driveMotor.getEncoder().setPosition(0.0);
    }

    public void resetSteerPosition(){
        steerMotor.getEncoder().setPosition(steerAngle());
    }

    public void resetAbsolute(){
        steerEncoder.setAbsPosition(0,250);
    }

    public double drivePosition(){
        return driveMotor.getEncoder().getPosition();
    }

    public double steerAngle(){
        return (steerEncoder.getAbsPosition() * PI2) % PI2;
    }

    public void driveVolts(double Volts){

        driveMotor.setVoltage(Volts);
    }

    public void stop(){
        driveMotor.stopMotor();
        steerMotor.stopMotor();
    }

    public void set(double driveVolts, double targetAngle){
        resetSteerPosition();
        
        // TODO: Check if the values we pass are even able to be negative

        // Put in range of [0, 2Pi)
        targetAngle %= PI2;
        targetAngle += (targetAngle < 0.0) ? PI2 : 0.0;

        desiredAngle = targetAngle;

        double diff = targetAngle - steerAngle();

        // TODO: check how the PID handles 0 -> 135 as the unmodified angle (335 as the modified angle)
        if (diff > (Math.PI/2.0) || diff < -(Math.PI/2.0)){ // move to a closer angle and drive backwards 
            targetAngle = (targetAngle + Math.PI) % PI2; 
            driveVolts *= -1.0;
        }

        driveMotor.setVoltage(driveVolts);
        steerMotor.getPIDController().setReference(targetAngle, ControlType.kPosition);
    }

}