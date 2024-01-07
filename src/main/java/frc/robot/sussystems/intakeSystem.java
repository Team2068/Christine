// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.sussystems;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class intakeSystem extends SubsystemBase {
  public TalonFX intake;

  public TalonFX hang1;
  public TalonFX hang2;

  /** Creates a new intakeSystem. */
  public intakeSystem() {
    intake = new TalonFX(69);

    hang1 = new TalonFX(420);
    hang2 = new TalonFX(240);
    hang2.setControl(new Follower(420, false));

  }

  public void setHangPosition(boolean isUp) {
    hang1.setPosition(Math.toDegrees((isUp) ? 60 : 0));
  }

  public void setIntakeSpeed(double speed) {
    intake.set(speed);

  }
}
