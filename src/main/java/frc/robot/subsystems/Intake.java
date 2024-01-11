// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
   public TalonFX pivot = new TalonFX(0); // TODO: Replace ID
   public CANSparkMax intake = new CANSparkMax(0, MotorType.kBrushless); // TODO: Replace ID

  public Intake() { }
 
  public void open(){
    pivot.setPosition(0);
  }

  public void close(){
    pivot.setPosition(0);
  }

  public void setSpeed(double speed){
    intake.set(speed);
  }

  public void stop(){
    intake.stopMotor();
  }

  @Override
  public void periodic() {}
}
