// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
   public TalonFX pivot = new TalonFX(5); // TODO: Replace ID
   public CANSparkMax intake = new CANSparkMax(6, MotorType.kBrushless); // TODO: Replace ID
   public DigitalInput beam_break = new DigitalInput(0); // TODO: Replace ID
   public boolean intakeOpen;

  public Intake() {}
 
  public void open(){
    pivot.setPosition(0); // PLACEHOLDER
    intakeOpen = true;
  }

  public void close(){
    pivot.setPosition(0); 
    intakeOpen = false;
  }

  public void toggle(){
    if (intakeOpen) close();
    else open();
  }

  public void setSpeed(double speed){
    intake.set(speed);
  }

  public void stop(){
    intake.stopMotor();
  }

  public boolean loaded() {
    return beam_break.get();
  }

  @Override
  public void periodic() {
    SmartDashboard.putBoolean("Beam Break", loaded());
  }
}
