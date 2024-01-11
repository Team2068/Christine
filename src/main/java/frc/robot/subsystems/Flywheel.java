//made by team 2069

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class Flywheel extends SubsystemBase {
  public TalonFX pivot = new TalonFX(1); // TODO: Replace ID
  public CANSparkMax flywheel = new CANSparkMax(5, MotorType.kBrushless); // TODO: Replace ID

  public Flywheel() {}

  public void open(){
    pivot.setPosition(0);
  }

  public void close(){
    pivot.setPosition(0); // TODO: Make a value for this
  }

  public void setSpeed(double speed){
    flywheel.set(speed);
  }

  public double pivotAngle(double tag_height, double tag_distance){
    const double radius;
    const double conversion_factor = 60/(2*Math.PI*radius);
    return Math.atan(tag_height/tag_distance);
  }

  public double RPM(double angle){
    double raw_rpm = conversion_factor * Math.Sqrt(tag_distance * -9.8 * Math.sin(2*angle)); // NOTE: 9.8 could be negative or positive, idk
    return rpm = Math.max(0,Math.min(6000.0, raw_rpm));
  }

  @Override
  public void periodic() {}
}