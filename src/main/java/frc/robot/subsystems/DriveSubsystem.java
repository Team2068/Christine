package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import frc.robot.modules.HeliumSwerveModule;
import frc.robot.modules.SwerveModule;
import frc.robot.utility.Constants.DriveConstants;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class DriveSubsystem extends SubsystemBase {
    public static double MAX_VOLTAGE = 5;
    public int drive_mode = 0;

    
    public static final int Field_Oriented = 1;
    public static final int Fixed_Point_Tracking = 2;
    public static final int Fixed_Alignment = 3;

    public static final double MAX_VELOCITY_METERS_PER_SECOND = 3;
    public static final double MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND = (MAX_VELOCITY_METERS_PER_SECOND /
            Math.hypot(DriveConstants.DRIVETRAIN_WHEELBASE_METERS / 2,
                    DriveConstants.DRIVETRAIN_TRACKWIDTH_METERS / 2));

    private final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
            new Translation2d(DriveConstants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
                    DriveConstants.DRIVETRAIN_WHEELBASE_METERS / 2.0),
            new Translation2d(DriveConstants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
                    -DriveConstants.DRIVETRAIN_WHEELBASE_METERS / 2.0),
            new Translation2d(-DriveConstants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
                    DriveConstants.DRIVETRAIN_WHEELBASE_METERS / 2.0),
            new Translation2d(-DriveConstants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
                    -DriveConstants.DRIVETRAIN_WHEELBASE_METERS / 2.0));

    public final Pigeon2 pigeon2 = new Pigeon2(DriveConstants.PIGEON_ID);

    private final SwerveDriveOdometry odometry;
    private final SwerveModule frontLeftModule;
    private final SwerveModule backLeftModule;
    private final SwerveModule frontRightModule;
    private final SwerveModule backRightModule;

    private ChassisSpeeds chassisSpeeds = new ChassisSpeeds();

    private boolean slowMode = false;

    private boolean active = true;

    // SwerveAutoBuilder autoBuilder;

    public DriveSubsystem() {
        DriveConstants.setOffsets();
        ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");
        frontLeftModule = new HeliumSwerveModule(
                tab.getLayout("Front Left Module", BuiltInLayouts.kList)
                        .withSize(2, 4)
                        .withPosition(0, 0),
                DriveConstants.FRONT_LEFT_DRIVE_MOTOR,
                DriveConstants.FRONT_LEFT_TURN_MOTOR,
                DriveConstants.FRONT_LEFT_ENCODER,
                DriveConstants.FRONT_LEFT_ENCODER_OFFSET);
        frontRightModule = new HeliumSwerveModule(
                tab.getLayout("Front Right Module", BuiltInLayouts.kList)
                        .withSize(2, 4)
                        .withPosition(2, 0),
                DriveConstants.FRONT_RIGHT_DRIVE_MOTOR,
                DriveConstants.FRONT_RIGHT_TURN_MOTOR,
                DriveConstants.FRONT_RIGHT_ENCODER,
                DriveConstants.FRONT_RIGHT_ENCODER_OFFSET);
        backLeftModule = new HeliumSwerveModule(
                tab.getLayout("Back Left Module", BuiltInLayouts.kList)
                        .withSize(2, 4)
                        .withPosition(4, 0),
                DriveConstants.BACK_LEFT_DRIVE_MOTOR,
                DriveConstants.BACK_LEFT_TURN_MOTOR,
                DriveConstants.BACK_LEFT_ENCODER,
                DriveConstants.BACK_LEFT_ENCODER_OFFSET);
        backRightModule = new HeliumSwerveModule(
                tab.getLayout("Back Right Module", BuiltInLayouts.kList)
                        .withSize(2, 4)
                        .withPosition(6, 0),
                DriveConstants.BACK_RIGHT_DRIVE_MOTOR,
                DriveConstants.BACK_RIGHT_TURN_MOTOR,
                DriveConstants.BACK_RIGHT_ENCODER,
                DriveConstants.BACK_RIGHT_ENCODER_OFFSET);

        odometry = new SwerveDriveOdometry(kinematics, rotation(), getModulePositions(), new Pose2d(0, 0, new Rotation2d()));

        // autoBuilder = new SwerveAutoBuilder(this::getPose, this::resetOdometry, new PIDConstants(AutoConstants.kPXController, 0, 0.01), new PIDConstants(AutoConstants.kPThetaController, 0, 0.01), this::drive, Paths.eventMap, true, this);

        zeroGyro();
    }

    public void zeroGyro() {
        pigeon2.setYaw(0);
    }

    public Rotation2d rotation() {
        return pigeon2.getRotation2d();
    }

    public double absoluteRotation() {
        double rot = Math.abs(pigeon2.getYaw().getValue()) % 360.0 * ((pigeon2.getYaw().getValue() < 0.0) ? -1.0 : 1.0);
        return (rot < 0.0) ? rot + 360.0 : rot; 
    }

    public void drive(ChassisSpeeds chassisSpeeds) {
        this.chassisSpeeds = chassisSpeeds;
    }

    public void stop(){ chassisSpeeds = new ChassisSpeeds();}

    private SwerveModulePosition getModulePosition(SwerveModule module) {
        return new SwerveModulePosition(module.drivePosition(), Rotation2d.fromRadians(module.steerAngle()));
    }

    public SwerveModulePosition[] getModulePositions() {
        SwerveModulePosition[] pos = {
                getModulePosition(frontLeftModule),
                getModulePosition(frontRightModule),
                getModulePosition(backLeftModule),
                getModulePosition(backRightModule)
        };
        SmartDashboard.putNumber("FL Distance", pos[0].distanceMeters);
        SmartDashboard.putNumber("FR Distance", pos[1].distanceMeters);
        SmartDashboard.putNumber("BL Distance", pos[2].distanceMeters);
        SmartDashboard.putNumber("BR Distance", pos[3].distanceMeters);
        return pos;
    }

    public Pose2d getPose() {
        return odometry.getPoseMeters();
    }

    public void resetOdometry() {
        zeroGyro();
        resetPosition();
        odometry.resetPosition(rotation(), getModulePositions(), new Pose2d());
    }

    public void resetOdometry(Pose2d pose) {
        zeroGyro();
        resetPosition();
        odometry.resetPosition(rotation(), getModulePositions(), pose);
    }

    public void resetPosition() {
        frontLeftModule.resetDrivePosition();
        frontRightModule.resetDrivePosition();
        backLeftModule.resetDrivePosition();
        backRightModule.resetDrivePosition();
    }

    public void syncEncoders() {
        frontLeftModule.resetSteerPosition();
        frontRightModule.resetSteerPosition();
        backLeftModule.resetSteerPosition();
        backRightModule.resetSteerPosition();
    }

    public void resetAbsolute(){
        frontLeftModule.resetAbsolute();
        backLeftModule.resetAbsolute();
        frontRightModule.resetAbsolute();
        backRightModule.resetAbsolute();
    }
    
    public void resetSteerPositions() {
        frontLeftModule.set(0, 0);
        frontRightModule.set(0, 0);
        backLeftModule.set(0, 0);
        backRightModule.set(0, 0);
    }

    public void setModuleStates(SwerveModuleState[] states) {
        SwerveDriveKinematics.desaturateWheelSpeeds(states, MAX_VELOCITY_METERS_PER_SECOND);
        
        frontLeftModule.set((states[0].speedMetersPerSecond / MAX_VELOCITY_METERS_PER_SECOND) * MAX_VOLTAGE,
                states[0].angle.getRadians());
        frontRightModule.set((states[1].speedMetersPerSecond / MAX_VELOCITY_METERS_PER_SECOND) * MAX_VOLTAGE,
                states[1].angle.getRadians());
        backLeftModule.set((states[2].speedMetersPerSecond / MAX_VELOCITY_METERS_PER_SECOND) * MAX_VOLTAGE,
                states[2].angle.getRadians());
        backRightModule.set((states[3].speedMetersPerSecond / MAX_VELOCITY_METERS_PER_SECOND) * MAX_VOLTAGE,
                states[3].angle.getRadians());
    }

    public boolean isSlowMode() {
        return slowMode;
    }

    public void toggleSlowMode() {
        slowMode = !slowMode;
    }

    public ChassisSpeeds getChassisSpeeds() {
        return chassisSpeeds;
    }

    public void activeChassis(){
        active = true;
    }

    public void disableChassis(){
        active = false;

        frontLeftModule.stop();
        frontRightModule.stop();
        backLeftModule.stop();
        backRightModule.stop();
    }

    // public Command followPath(PathPlannerTrajectory path) {
    //     return autoBuilder.followPath(path).beforeStarting(() -> resetOdometry(PathPlannerTrajectory.transformTrajectoryForAlliance(path, DriverStation.getAlliance()).getInitialHolonomicPose()));
    // }

    // public Command followPathWithEvents(PathPlannerTrajectory path) {
    //     return autoBuilder.followPathWithEvents(path).beforeStarting(() -> resetOdometry(PathPlannerTrajectory.transformTrajectoryForAlliance(path, DriverStation.getAlliance()).getInitialHolonomicPose()));
    // }

    // public Command followPathGroupWithEvents(List<PathPlannerTrajectory> path) {
    //     return autoBuilder.fullAuto(path).beforeStarting(() -> resetOdometry(PathPlannerTrajectory.transformTrajectoryForAlliance(path.get(0), DriverStation.getAlliance()).getInitialHolonomicPose()));
    // }

    // public Command followPathGroup(List<PathPlannerTrajectory> path) {
    //     return autoBuilder.followPathGroupWithEvents(path);
    // }

    public void periodic() {
        if (active) setModuleStates(kinematics.toSwerveModuleStates(chassisSpeeds));
        Pose2d pose = odometry.update(rotation(), getModulePositions());

        // TODO: Wrap This Into A List, auto-order it too
        SmartDashboard.putData(pigeon2);
        SmartDashboard.putNumber("X position", pose.getX());
        SmartDashboard.putNumber("Y position", pose.getY());

        SmartDashboard.putNumber("Odometry rotation", rotation().getDegrees());
        SmartDashboard.putNumber("Pigeon Yaw", pigeon2.getYaw().getValue());
        SmartDashboard.putNumber("Pigeon Pitch", pigeon2.getPitch().getValue());
        SmartDashboard.putNumber("Pigeon Roll", pigeon2.getRoll().getValue());

        var drive_mode_display = "";
        switch(drive_mode){
            case 0: drive_mode_display = "Robot-Oriented";
            break;
            
            case 1: drive_mode_display = "Field-Oriented";
            break;
            
            case 2: drive_mode_display = "Fixed-Point Tracking";
            break;

            case 3: drive_mode_display = "Fixed Alignment";
            break;
        }

        SmartDashboard.putString("Drive Mode", drive_mode_display);
        SmartDashboard.putString("Drive Speed", slowMode ? "Slow" : "Normal");
    }
}
