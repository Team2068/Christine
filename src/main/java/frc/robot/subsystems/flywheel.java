//made by team 2069

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class flywheel extends SubsystemBase {
  public TalonFX pivotMotor = new TalonFX(1);
  public CANSparkMax flywheelMotor = new CANSparkMax(5, MotorType.kBrushless);


  public void open(){
    pivotMotor.setPosition(0);
  }
  public void close(){
    pivotMotor.setPosition(0);
  }

  public void setflywheelSpeed(double speed){
    pivotMotor.set(speed);
  }

  /** Creates a new flywheel. */
  public flywheel() {
    
  }

  public void setSpeed(double speed){
    
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}