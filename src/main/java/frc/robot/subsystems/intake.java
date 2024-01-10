// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class intake extends SubsystemBase {
   public TalonFX pivot = new TalonFX(0);
   public CANSparkMax intakeMotor = new CANSparkMax(0, MotorType.kBrushless);
   public DigitalInput beambreak = new DigitalInput(0);
  
  public intake() {} // TODO: see if any configuration is needed for the Pivot
 
  public void open(){
    pivot.setPosition(0);
  }

  public void close(){
    pivot.setPosition(0);
  }

  public void setSpeed(double speed){
    intakeMotor.set(speed);
  }

  public void stop(){
    intakeMotor.stopMotor();
  }

  public boolean loaded() {
    return beambreak.get();
  }

  @Override
  public void periodic() {}
}
