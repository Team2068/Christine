// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

/** Add your docs here. */
public class SwerveModule {
    public TalonFX driveMotor;
    public CANSparkMax steerMotor;
    public Canandcoder steerEncoder;

    final double WHEEL_DIAMETER = 0.10033; // Metres
    final double DRIVE_REDUCTION = (15.0 / 32.0) * (10.0 / 60.0);
    final double STEER_REDUCTION = (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0);
    final double DRIVE_CONVERSION_FACTOR = Math.PI * WHEEL_DIAMETER * DRIVE_REDUCTION;
    final double PI2 = 2 * Math.PI;

    double desiredAngle;

    public SwerveModule(Shuffleboard tab, int driveID, int steerID, int steerEncoderID, double offset) {
        driveMotor = new TalonFX(driveID);
        steerMotor = new CANSparkMax(steerID, MotorType.kBrushless);
        steerEncoder = new Canandcoder(steerEncoderID);

        CanandcoderSettings settings = new CanandcoderSettings();
        settings.setInvertDirection(true);

        steerEncoder.clearStickyFaults();
        steerEncoder.setFactoryDefaults(false);
        steerEncoder.setSettings(settings);

        TalonFXConfiguration configs = new TalonFXConfiguration();
        driveMotor.getConfigurator().apply(configs);

        steerMotor.setSmartCurrentLimit(20);
        CurrentLimitsConfigs currentConfigs = new CurrentLimitsConfigs();
        configs.withCurrentLimits(currentConfigs.withSupplyCurrentLimit(40));//driveMotor.configSupplyCurrentLimit();

        steerMotor.setIdleMode(IdleMode.kBrake);
        driveMotor.setNeutralMode(NeutralModeValue.Coast);

        //drive motor positoin velocity conversion factors

        steerMotor.getEncoder().setPositionConversionFactor(Math.PI * STEER_REDUCTION);
        steerMotor.getEncoder().setVelocityConversionFactor(Math.PI * STEER_REDUCTION / 60);
        steerMotor.getEncoder().setPosition(steerAngle());
        
        driveMotor.setInverted(true);
        steerMotor.setInverted(false);

        //drive motor voltage compensation

        steerMotor.getPIDController().setP(0.1);
        steerMotor.getPIDController().setI(0);
        steerMotor.getPIDController().setD(1);

        steerMotor.setPeriodicTimeFrame(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100);
        steerMotor.setPeriodicTimeFrame(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20);
        steerMotor.setPeriodicTimeFrame(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 20);

        public void resetDrivePosition() {
            driveMotor.setPosition(0);
        }

        public void resetSteerPosition() {
            driveMotor.setPosition(steerAngle());
        }

        public void resetAbsolute() {
            steerEncoder.setAbsolute(0, 250);
        }

        public double drivePosition() {
            return driveMotor.getPosition();
        }

        public double steerPosition() {
            return (steerEncoder.getAbsPosition() * PI2) % PI2;
        }

        public void set(double driveVolts, double targetAngle) {

        }
    }
    
}
