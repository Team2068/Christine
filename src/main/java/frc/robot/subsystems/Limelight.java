// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.Consumer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Limelight extends SubsystemBase {
    
  public final static double LIMELIGHT_HEIGHT = 0; // TODO: Measure and note heigh in cm
  public final static double LIMELIGHT_ANGLE = 0; // TODO: Measure and note the angle in degrees

  public Limelight(int ledMode, int streamMode) { // Methods are undefined for type limelight
    setLedMode(0);
    setStreamMode(0);
  }

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

  // It's very inaccurate of objects that are same height as the robot
  public double distance() {
    TargetData targetData = getTargetData();
    double a2 = targetData.verticalOffset;
    double a1 = LIMELIGHT_ANGLE;
    double h1 = LIMELIGHT_HEIGHT;
    double h2 = tagPose()[1]; // [X,Y,Z,Roll,Pitch,Yaw]

    double result = h2 - h1;
    double radians = Math.toRadians(a1 + a2);
    double dist = result / Math.tan(radians);

    return Math.abs(dist);
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

  public int ledMode() {
    return (int) table.getEntry("ledMode").getDouble(0.0);
  }

  public int pipeline() {
    return (int) table.getEntry("pipeline").getDouble(0.0);
  }

  public int streamMode() {
    return (int) table.getEntry("stream").getDouble(0.0);
  }

  public double[] botpose() {
    return table.getEntry("stream").getDoubleArray( (double[]) null);
  }

  public int tagID() {
    return (int) table.getEntry("tid").getInteger(-1);
  }

  public double[] tagPose(){
    return table.getEntry("targetpose_robotspace").getDoubleArray( (double[]) null);
  }
}