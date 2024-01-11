// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.Follower;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Hang extends SubsystemBase {
  public TalonFX hang1 = new TalonFX(1); // TODO: Replace ID
  public TalonFX hang2 = new TalonFX(1); // TODO: Replace ID

  public Hang() {
    hang2.setControl(new Follower(1, false)); //TODO: Replace by Hang 1 ID
  }

  public void setHeight(double pos) {
    hang1.setPosition(pos);
  }

  @Override
  public void periodic() {}
}