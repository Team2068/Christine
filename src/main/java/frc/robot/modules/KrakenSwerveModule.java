// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.modules;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.reduxrobotics.sensors.canandcoder.Canandcoder;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public class KrakenSwerveModule {
    public TalonFX driveMotor;
    public CANSparkMax steerMotor;
    public Canandcoder steerEncoder;

    final double WHEEL_DIAMETER = 0.10033; // Meters
    final double DRIVE_REDUCTION = (15.0 / 32.0) * (10.0 / 60.0);
    final double STEER_REDUCTION = (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0);
    final double DRIVE_CONVERSION_FACTOR = Math.PI * WHEEL_DIAMETER * DRIVE_REDUCTION;
    final double PI2 = 2 * Math.PI;

    double desiredAngle;

    public KrakenSwerveModule(ShuffleboardLayout tab, int driveID, int steerID, int steerEncoderID, double offset) {
        driveMotor = new TalonFX(driveID);
        steerMotor = new CANSparkMax(steerID, MotorType.kBrushless);
        steerEncoder = new Canandcoder(steerEncoderID);

        Canandcoder.Settings settings = new Canandcoder.Settings();
        settings.setInvertDirection(true);

        steerEncoder.clearStickyFaults();
        steerEncoder.resetFactoryDefaults(false);
        steerEncoder.setSettings(settings);

        TalonFXConfiguration configs = new TalonFXConfiguration();

        CurrentLimitsConfigs currentConfigs = new CurrentLimitsConfigs();
        configs.withCurrentLimits(currentConfigs.withSupplyCurrentLimit(40));//driveMotor.configSupplyCurrentLimit();

        driveMotor.getConfigurator().apply(configs);
        steerMotor.setSmartCurrentLimit(20);

        steerMotor.setIdleMode(IdleMode.kBrake);
        driveMotor.setNeutralMode(NeutralModeValue.Coast);

        //drive motor positoin velocity conversion factors

        steerMotor.getEncoder().setPositionConversionFactor(Math.PI * STEER_REDUCTION);
        steerMotor.getEncoder().setVelocityConversionFactor(Math.PI * STEER_REDUCTION / 60);
        steerMotor.getEncoder().setPosition(steerAngle());
        
        driveMotor.setInverted(true);
        steerMotor.setInverted(false);

        //driveMotor.setControl(new VoltageOut(12));

        steerMotor.getPIDController().setP(0.1);
        steerMotor.getPIDController().setI(0);
        steerMotor.getPIDController().setD(1);

        steerMotor.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus0, 100);
        steerMotor.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus1, 20);
        steerMotor.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus2, 20);

        tab.addDouble("Absolute Angle", () -> Math.toDegrees(steerAngle())); 
        tab.addDouble("Current Angle", () -> Math.toDegrees(steerMotor.getEncoder().getPosition()));
        tab.addDouble("Target Angle", () -> Math.toDegrees(desiredAngle));
        tab.addBoolean("Active", steerEncoder::isConnected);
    
    }
        public void resetDrivePosition() {
            driveMotor.setPosition(0.0);
        }

        public void resetSteerPosition() {
            driveMotor.setPosition(steerAngle());
        }

        public void resetAbsolute() {
            steerEncoder.setAbsPosition(0.0, 250);
        }

        public double drivePosition() {
            return driveMotor.getPosition().getValueAsDouble();
        }

        public double steerAngle() {
            return (steerEncoder.getAbsPosition() * PI2) % PI2;
        }

        public void set(double driveVolts, double targetAngle) {
            resetSteerPosition();

            targetAngle %= PI2;
            targetAngle += (targetAngle < 0.0) ? PI2 : 0.0;

            desiredAngle = targetAngle;

            double diff = targetAngle - steerAngle();

            if (diff > (Math.PI / 2.0) || diff < -(Math.PI / 2.0)) {
                targetAngle = (targetAngle + Math.PI) % PI2;
                driveVolts *= -1;
            }

            driveMotor.setVoltage(driveVolts);
            steerMotor.getPIDController().setReference(targetAngle, ControlType.kPosition);
        }
    
    
}
