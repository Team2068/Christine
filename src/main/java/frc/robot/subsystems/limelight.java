// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

// These classes do not exist on current robot code
// import frc.robot.Constants;
// import frc.robot.RobotState;
// import frc.robot.Constants.ShooterConstants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.function.Consumer;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class limelight extends SubsystemBase {
    
  public final static double LIMELIGHT_HEIGHT = 34.5 * 2.54; // Converting from inches to cm
  public final static double LIMELIGHT_ANGLE = 20;

  public final Runnable[] ids = {};

  public limelight(int ledMode, int streamMode) { // Methods are undefined for type limelight
    setLedMode(0);
    setStreamMode(0);
  }

  // Structure for targeted data
  public class TargetData {
    public boolean hasTargets = false;
    public double horizontalOffset = 0;
    public double verticalOffset = 0;
    public double targetArea = 0;
    public double skew = 0;
    public double latency = 0;
    public double shortSideLength = 0;
    public double longSideLength = 0;
    public double horizontalSideLength = 0;
    public double verticalSideLength = 0;
  }

  private TargetData targetData = new TargetData();

  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");

  @Override
  public void periodic() {
    table = NetworkTableInstance.getDefault().getTable("limelight");
    
    SmartDashboard.putString("Stream Mode", (StreamMode() == 0) ? "Main" : "Secondary");
    SmartDashboard.putNumber("Distance", Distance());

    updateTargetData(table);
  }

  private void updateTargetData(NetworkTable table) {
    targetData.hasTargets = table.getEntry("tv").getBoolean(false);
    targetData.horizontalOffset = table.getEntry("tx").getDouble(0.0);
    targetData.verticalOffset = table.getEntry("ty").getDouble(0.0);
    targetData.targetArea = table.getEntry("ta").getDouble(0.0);
    targetData.skew = table.getEntry("ts").getDouble(0.0);
    targetData.latency = table.getEntry("tl").getDouble(0.0);
    targetData.shortSideLength = table.getEntry("tshort").getDouble(0.0);
    targetData.longSideLength = table.getEntry("tlong").getDouble(0.0);
    targetData.horizontalSideLength = table.getEntry("thor").getDouble(0.0);
    targetData.verticalSideLength = table.getEntry("tvert").getDouble(0.0);
  }

  public TargetData getTargetData() {
    return targetData;
  }

  // R^2 = 0.9687
  public double linearRPM() {
    return 2.86381 * Distance() + 2438; // y = mx + b
  }

  // R^2 = 0.9203
  public double curveRPM() {
    double distance = Distance();
    double squared = distance * distance;
    return 0.0523285 * squared + 3101.62; // y = mx^2 + b
  }

  // R^2 = 0.9761
  public double logRPM() {
    return Math.log(Distance())/Math.log(1.00075) -4333.96;
  }

  // It's very inaccurate of objects that are same height as the robot
  public double Distance() {
    TargetData targetData = getTargetData();
    double a2 = targetData.verticalOffset;
    double a1 = LIMELIGHT_ANGLE;
    double h1 = LIMELIGHT_HEIGHT;
    double h2 = 103 * 2.54; // UpperHub height in inches 

    double result = h2 - h1;
    double radians = Math.toRadians(a1 + a2);
    double distance = result / Math.tan(radians);

    return Math.abs(distance);
  }

  public void toggleStreamMode(){
    setStreamMode((StreamMode() + 1) % 4);
  }

  public void setCameraMode(int newCameraMode) {
    table.getEntry("camMode").setNumber(newCameraMode);
  }

  public void setLedMode(int newLedMode) {
    table.getEntry("ledMode").setNumber(newLedMode);
  }

  public void setPipeline(int newPipeline) {
    table.getEntry("pipeline").setNumber(newPipeline);
  }

  public void setStreamMode(int newStreamMode) {
    table.getEntry("stream").setNumber(newStreamMode);
  }

  public int LedMode() {
    return (int) table.getEntry("ledMode").getDouble(0.0);
  }

  public int Pipeline() {
    return (int) table.getEntry("pipeline").getDouble(0.0);
  }

  public int StreamMode() {
    return (int) table.getEntry("stream").getDouble(0.0);
  }

  public double[] Botpose() {
    return table.getEntry("stream").getDoubleArray( (double[]) null);
  }

  public int AprilTagID() {
    return (int) table.getEntry("tid").getInteger(-1);
  }

  // Calculated Shooting?
  public double calculatedShooterRPM() {
    double distance = Distance();
    double[] distTab = new double[5];
    double[] rpmTab = new double[5];

    int low = 0;
    int high = 0;

    for (int i = 0; i < distTab.length>>1; i++) {
      if (distance < distTab[i]){ //If lower > dist -> upper bound found
        high = i; 
        low = i-1;
        break;
      }

      if (distance > distTab[distTab.length - 1- i]){ //If higher < dist -> higer is lower bound
        low = distTab.length - i -1;
        high = low + 1;
        break;
      }
    }

    if (low == -1)
      return rpmTab[0]+(distance-distTab[0])/(distTab[1]-distTab[0])*(rpmTab[1]-rpmTab[0]);
    
    if (high == distTab.length)
      return rpmTab[3]+(distance-distTab[3])/(distTab[4]-distTab[3])*(rpmTab[4]-rpmTab[3]);
    
    return rpmTab[low] + (distance - distTab[low])*((rpmTab[high]- rpmTab[low])/(distTab[high]- distTab[low]));

  }


}