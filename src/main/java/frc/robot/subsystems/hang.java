// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class hang extends SubsystemBase {
  public CANSparkMax hang1;
  public CANSparkMax hang2;

  ;
  public hang() {

    hang1 = new CANSparkMax(69, MotorType.kBrushless);
    hang2 = new CANSparkMax(420, MotorType.kBrushless);

    hang1.getEncoder().setPositionConversionFactor(123/456);
    hang2.getEncoder().setPositionConversionFactor(123/456);


    hang1.getPIDController().setP(0);
    hang1.getPIDController().setI(0);
    hang1.getPIDController().setD(0);

    hang2.getPIDController().setP(0);
    hang2.getPIDController().setI(0);
    hang2.getPIDController().setD(0);
  }

  public void setPos(double pos) {
    hang1.getEncoder().setPosition(pos);
    hang2.getEncoder().setPosition(pos);
  }

  @Override
  public void periodic() {

  }
}