// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HangNeo extends SubsystemBase {
  public CANSparkMax hang2 = new CANSparkMax(4, MotorType.kBrushless); // TODO: Replace ID
  public CANSparkMax hang1 = new CANSparkMax(1, MotorType.kBrushless); // TODO: Replace ID

  public HangNeo() {
    hang2.follow(hang1, false);
    hang1.getPIDController().setP(0.3);
    hang1.getPIDController().setI(0);
    hang1.getPIDController().setD(0);
    hang1.getEncoder().setPosition(0);
  }

  public void setHeight(double pos) {
    hang1.getPIDController().setReference(pos, ControlType.kPosition);
  }

  @Override
  public void periodic() {}
}