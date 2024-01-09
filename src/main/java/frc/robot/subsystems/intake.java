// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class intake extends SubsystemBase {
   public TalonFX pivotMotor = new TalonFX(0);
   public CANSparkMax intakeMotor = new CANSparkMax(0, MotorType.kBrushless);

  /** Creates a new conveyor. */
  public intake() {

  }
 
  public void open(){
    pivotMotor.setPosition(0);
  }

  public void close(){
    pivotMotor.setPosition(0);
  }

  public void setSpeed(double speed){
    intakeMotor.set(speed);
  }

  public void stop(){
    intakeMotor.stopMotor();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
