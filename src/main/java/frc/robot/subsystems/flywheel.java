//made by team 2069

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.ControlRequest;

public class flywheel extends SubsystemBase {
  TalonFX flywheel1 = new TalonFX(1);
  TalonFX flywheel2 = new TalonFX(2);

  /** Creates a new flywheel. */
  public flywheel() {
    flywheel2.setControl(new Follower(1, true));
    
  }

  public void setSpeed(double speed){
    flywheel1.set(speed);
    flywheel2.set(speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}