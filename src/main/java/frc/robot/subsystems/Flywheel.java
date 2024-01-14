//made by team 2069

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class Flywheel extends SubsystemBase {
  public TalonFX pivot = new TalonFX(1); // TODO: Replace ID
  public static CANSparkMax flywheel = new CANSparkMax(5, MotorType.kBrushless); // TODO: Replace ID

  public Flywheel() {}

  public void setAngle(double angle){
    pivot.setPosition(angle);
  }

  public static void setSpeed(double speed){
    flywheel.set(speed);
  }

  public static double pivotAngle(double tag_height, double tag_distance){
    return Math.atan(tag_height/tag_distance);
  }

  public static double RPM(double angle, double distance){
    double radius = 0; // TODO: find radius for the flywheel
    double conversion_factor = 60/(2*Math.PI*radius);
    double raw_rpm = conversion_factor * Math.sqrt(distance * -9.8 * Math.sin(2*angle)); // NOTE: 9.8 could be negative or positive, idk
    return Math.max(0,Math.min(6000.0, raw_rpm))/6000;
  }

  @Override
  public void periodic() {}
}