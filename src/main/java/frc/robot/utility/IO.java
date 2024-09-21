package frc.robot.utility;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.*;
import frc.robot.subsystems.*;

public class IO extends SubsystemBase {
        public final CommandXboxController drive = new CommandXboxController(0);
        public final CommandXboxController mech = new CommandXboxController(1);

        public final Swerve chassis = new Swerve();
        // public final LEDs leds = new LEDs();
        public final Intake intake = new Intake();
        public final Limelight shooter_light = new Limelight("shooter");
        public final Flywheel shooter = new Flywheel();
        public final Climber climber = new Climber();
        public final ProfiledShooter profiledShoot = new ProfiledShooter(this, 64);

        public CommandScheduler scheduler = CommandScheduler.getInstance();

        public IO(SendableChooser<Runnable> bindings) {
                bindings.setDefaultOption("Single Player", this::config1Player);
                bindings.addOption("Two Player", this::config2Player);
                bindings.addOption("Testing", this::configTesting);
        }

        public void configGlobal(){
                profiledShoot.addRequirements(shooter);
                chassis.setDefaultCommand(new DefaultDrive(this, drive));
                shooter.setDefaultCommand(profiledShoot);
                DriverStation.silenceJoystickConnectionWarning(true);
        }

        public void config1Player() {
                profiledShoot.addRequirements(shooter);
                chassis.setDefaultCommand(new DefaultDrive(this, drive));
                shooter.setDefaultCommand(profiledShoot);

                drive.leftStick().onTrue(new InstantCommand(climber::resetEncoders));
                drive.rightStick().onTrue(new InstantCommand(() -> chassis.field_oritented = !chassis.field_oritented));

                drive.start().onTrue(new InstantCommand(chassis::resetOdometry));
                drive.back().onTrue(new InstantCommand(() -> chassis.setOdometry(shooter_light.poseEstimation())));

                drive.y().onTrue(new InstantCommand(profiledShoot::stop));
                drive.b().onTrue(new ConditionalCommand(new SupaTrap(this), new InstantCommand(() -> {
                        profiledShoot.setAngle(Flywheel.PASS_OFF_ANGLE);
                        climber.setHangPos(Climber.HANG_UP_POS);
                }), climber::HangUp));

                drive.x().onTrue(new InstantCommand(() -> { // Shooting
                        profiledShoot.setAngle(Flywheel.PASS_OFF_ANGLE);
                        shooter.flywheelVoltage(-14.0);
                        shooter.helperVoltage(12.0);
                })).onFalse(new InstantCommand(() -> {
                        intake.speed(0);
                        shooter.flywheelVoltage(0);
                        shooter.helperVoltage(0);
                }));

                drive.a().onTrue(new ConditionalCommand(new PassOff(this, false),
                                new InstantCommand(() -> intake.speed(-.5)),
                                intake::loaded)).onFalse(new InstantCommand(() -> {
                                        intake.speed(0);
                                        profiledShoot.stop();
                                }));

                drive.rightTrigger().onTrue(new InstantCommand(() -> { // Passing
                        profiledShoot.setAngle(Flywheel.PASS_OFF_ANGLE);
                        shooter.flywheelVoltage(-16.0);
                        shooter.helperVoltage(12.0);
                })).onFalse(new InstantCommand(() -> {
                        intake.speed(0);
                        shooter.flywheelVoltage(0);
                        shooter.helperVoltage(0);
                }));

                drive.leftTrigger().onTrue(new AmpShooting(this));

                drive.rightBumper().onTrue(new ToggleIntake(this));

                drive.leftBumper().onTrue(new InstantCommand(() -> intake.speed(.5)))
                                .onFalse(new InstantCommand(() -> {
                                        intake.speed(0);
                                        profiledShoot.stop();
                                }));

                drive.povUp().onTrue(new InstantCommand(() -> {
                        profiledShoot.setAngle(115.0);
                        climber.setHangPos(Climber.HANG_UP_POS);
                }));

                drive.povDown().onTrue(new InstantCommand(() -> climber.setHangPos(Climber.HANG_DOWN_POS)));

                drive.povRight().onTrue(new InstantCommand(() -> {
                        if (climber.elevatorPos() > 1)
                                climber.setElevatorVolts(-4);
                })).onFalse(new InstantCommand(() -> climber.setElevatorVolts(0)));

                drive.povLeft().onTrue(new InstantCommand(() -> climber.setElevatorPos(25)));
        }

        public void config2Player() {
                drive.leftBumper().onTrue(new InstantCommand(() -> chassis.field_oritented = !chassis.field_oritented));
                // drive.rightBumper().onTrue(new InstantCommand(() -> chassis.slow_mode = !chassis.slow_mode));

                // drive.a().onTrue(new Aimbot(this, false));
                // drive.b().onTrue(new Aimbot(this, true));
                drive.x().onTrue(new InstantCommand(profiledShoot::stop));

                drive.y().onTrue(new InstantCommand(() -> {
                        profiledShoot.setAngle(115.0);
                        climber.setHangPos(Climber.HANG_UP_POS);
                }));
                ;
                drive.a().onTrue(new InstantCommand(() -> {
                        // profiledShoot.setAngle(Flywheel.PASS_OFF_ANGLE);
                        climber.setHangPos(Climber.HANG_DOWN_POS);
                }));
                drive.b().onTrue(new Trap(this));

                drive.start().onTrue(new InstantCommand(climber::resetEncoders));
                drive.back().onTrue(new InstantCommand(chassis::resetOdometry));

                drive.povRight().onTrue(new InstantCommand(() -> {
                        if (climber.elevatorPos() > 1)
                                climber.setElevatorVolts(-4);
                })).onFalse(new InstantCommand(() -> climber.setElevatorVolts(0)));

                drive.povLeft().onTrue(new InstantCommand(() -> climber.setElevatorPos(25)));

                drive.povUp().onTrue(new InstantCommand(() -> climber.setHangVolts(6)))
                                .onFalse(new InstantCommand(() -> climber.setHangVolts(0)));

                drive.povDown().onTrue(new InstantCommand(() -> climber.setHangVolts(-6)))
                                .onFalse(new InstantCommand(() -> climber.setHangVolts(0)));

                mech.leftBumper().onTrue(new InstantCommand(() -> intake.speed(-.5)))
                                .onFalse(new InstantCommand(() -> {
                                        intake.speed(0);
                                        profiledShoot.stop();
                                }));
                mech.rightBumper().onTrue(new InstantCommand(() -> intake.speed(0.3)))
                                .onFalse(new InstantCommand(() -> intake.speed(0)));

                mech.rightTrigger().onTrue(new AmpShooting(this));
                mech.leftTrigger().onTrue(new ToggleIntake(this));

                mech.back().onTrue(new InstantCommand(scheduler::cancelAll));
                mech.start().onTrue(new InstantCommand(climber::resetEncoders));

                mech.y().onTrue(new InstantCommand(() -> profiledShoot.setAngle(Flywheel.PASS_OFF_ANGLE)));

                mech.x().onTrue(new InstantCommand(() -> { // passing
                        profiledShoot.setAngle(Flywheel.PASS_OFF_ANGLE);
                        shooter.flywheelVoltage(-16.0);
                        shooter.helperVoltage(12.0);
                })).onFalse(new InstantCommand(() -> {
                        intake.speed(0);
                        shooter.flywheelVoltage(0);
                        shooter.helperVoltage(0);
                }));

                mech.a().onTrue(new PassOff(this, false));

                mech.b().onTrue(new InstantCommand(() -> { // shooting
                        profiledShoot.setAngle(Flywheel.PASS_OFF_ANGLE);
                        shooter.flywheelVoltage(-14.0);
                        shooter.helperVoltage(4.0); // TESTING using smaller voltages for the helper
                })).onFalse(new InstantCommand(() -> {
                        intake.speed(0);
                        shooter.flywheelVoltage(0);
                        shooter.helperVoltage(0);
                }));

                mech.povDown().onTrue(new ToggleIntake(this));
                mech.povUp().onTrue(new InstantCommand(profiledShoot::stop));
                mech.povLeft().onTrue(new InstantCommand(() -> intake.speed(-0.5)))
                                .onFalse(new InstantCommand(() -> {
                                        intake.speed(0);
                                        profiledShoot.stop();
                                }));
                mech.povRight().onTrue(new InstantCommand(
                                () -> profiledShoot.setAngle((double) DebugTable.get("Test Angle", 75.0))));
        }

        public void configTesting() {
                mech.leftTrigger().onTrue(
                                new InstantCommand(() -> chassis.setOdometry(new Pose2d(1.2, 5.53, new Rotation2d()))));
                mech.rightTrigger().onTrue(new InstantCommand(scheduler::cancelAll));

                mech.leftBumper().onTrue(new InstantCommand(() -> intake.pivotVoltage(2)))
                                .onFalse(new InstantCommand(() -> intake.pivotVoltage(0)));
                mech.rightBumper().onTrue(new InstantCommand(() -> intake.pivotVoltage(-2)))
                                .onFalse(new InstantCommand(() -> intake.pivotVoltage(0)));

                mech.b().onTrue(new InstantCommand(() -> shooter.helperVoltage(4)))
                                .onFalse(new InstantCommand(() -> shooter.helperVoltage(0)));
                mech.a().onTrue(new InstantCommand(() -> profiledShoot
                                .setAngle((double) DebugTable.get("Test Angle", Flywheel.PASS_OFF_ANGLE))));
                mech.x().onTrue(new AutoFire(this, false));
                mech.y().onTrue(new AutoFire(this, true));

                drive.povDownLeft().onTrue(new InstantCommand(chassis::resetAbsolute));
                drive.povUpLeft().onTrue(new InstantCommand(chassis::disable));
                drive.povDownRight().onTrue(new InstantCommand(chassis::enable));

        }

        StructPublisher<Pose2d> estimated_pose = NetworkTableInstance.getDefault().getTable("Debug")
                        .getStructTopic("Estimated Pose", Pose2d.struct).publish();

        @Override
        public void periodic() {
                estimated_pose.set(shooter_light.poseEstimation());
                SmartDashboard.putNumber("Odometry Distance",
                                chassis.distance(new Pose2d(shooter_light.tagPose()[0], shooter_light.tagPose()[2],
                                                new Rotation2d())));
        }
}