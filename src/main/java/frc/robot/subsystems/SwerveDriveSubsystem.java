package frc.robot.subsystems;

import com.ctre.phoenix.sensors.Pigeon2;
import frc.robot.SwerveLib.SwerveModule;
import frc.robot.SwerveLib.Mk4SwerveModuleHelper;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SwerveDriveSubsystem extends SubsystemBase {
    public final SwerveModule frontLeftModule;
    public final SwerveModule frontRightModule;
    public final SwerveModule backLeftModule;
    public final SwerveModule backRightModule;
    public ChassisSpeeds previousSpeeds = new ChassisSpeeds(0,0,0);
    public String CommandVariable = "None";
    public SwerveModuleState[] states = {new SwerveModuleState(), new SwerveModuleState(), new SwerveModuleState(), new SwerveModuleState()};

    public final Pigeon2 gyroscope = new Pigeon2(Constants.DRIVETRAIN_PIGEON_ID);
    public final SimpleMotorFeedforward feedForward = new SimpleMotorFeedforward(0.45, 1.85, 0.000037994);
    public final PIDController pid = new PIDController(0.25, 0.0, 0.0);
    public final HolonomicDriveController driveController = new HolonomicDriveController(new PIDController(0.25, 0.0, 0.0), new PIDController(0.25, 0.0, 0.0), new ProfiledPIDController(0.2, 0.0, 0.1, new TrapezoidProfile.Constraints(5.0,10.0)));
    public final ProfiledPIDController rotationPID = new ProfiledPIDController(9.25, 5.0, 0, new TrapezoidProfile.Constraints(100, 5));

    public final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
            new Translation2d(Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0, Constants.DRIVETRAIN_WHEELBASE_METERS / 2.0),
            new Translation2d(Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0, -Constants.DRIVETRAIN_WHEELBASE_METERS / 2.0),
            new Translation2d(-Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0, Constants.DRIVETRAIN_WHEELBASE_METERS / 2.0),
            new Translation2d(-Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0, -Constants.DRIVETRAIN_WHEELBASE_METERS / 2.0)
    );
    public final SwerveDriveOdometry odometry = new SwerveDriveOdometry(kinematics, Rotation2d.fromDegrees(gyroscope.getCompassHeading()));

    private ChassisSpeeds chassisSpeeds = new ChassisSpeeds(0.0, 0.0, 0.0);
    public boolean RobotCentric = false;

    public SwerveDriveSubsystem() {
        rotationPID.enableContinuousInput(0, 2*Math.PI);
        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Drivetrain");

        frontLeftModule = Mk4SwerveModuleHelper.createFalcon500(
                shuffleboardTab.getLayout("Front Left Module", BuiltInLayouts.kList)
                        .withSize(1, 4)
                        .withPosition(0, 0),
                Mk4SwerveModuleHelper.GearRatio.L2,
                Constants.FRONT_LEFT_MODULE_DRIVE_MOTOR,
                Constants.FRONT_LEFT_MODULE_STEER_MOTOR,
                Constants.FRONT_LEFT_MODULE_STEER_ENCODER,
                Constants.FRONT_LEFT_MODULE_STEER_OFFSET
        );

        frontRightModule = Mk4SwerveModuleHelper.createFalcon500(
                shuffleboardTab.getLayout("Front Right Module", BuiltInLayouts.kList)
                        .withSize(1, 4)
                        .withPosition(1, 0),
                Mk4SwerveModuleHelper.GearRatio.L2,
                Constants.FRONT_RIGHT_MODULE_DRIVE_MOTOR,
                Constants.FRONT_RIGHT_MODULE_STEER_MOTOR,
                Constants.FRONT_RIGHT_MODULE_STEER_ENCODER,
                Constants.FRONT_RIGHT_MODULE_STEER_OFFSET
        );

        backLeftModule = Mk4SwerveModuleHelper.createFalcon500(
                shuffleboardTab.getLayout("Back Left Module", BuiltInLayouts.kList)
                        .withSize(1, 4)
                        .withPosition(2, 0),
                        Mk4SwerveModuleHelper.GearRatio.L2,
                Constants.BACK_LEFT_MODULE_DRIVE_MOTOR,
                Constants.BACK_LEFT_MODULE_STEER_MOTOR,
                Constants.BACK_LEFT_MODULE_STEER_ENCODER,
                Constants.BACK_LEFT_MODULE_STEER_OFFSET
        );

        backRightModule = Mk4SwerveModuleHelper.createFalcon500(
                shuffleboardTab.getLayout("Back Right Module", BuiltInLayouts.kList)
                        .withSize(1, 4)
                        .withPosition(3, 0),
                        Mk4SwerveModuleHelper.GearRatio.L2,
                Constants.BACK_RIGHT_MODULE_DRIVE_MOTOR,
                Constants.BACK_RIGHT_MODULE_STEER_MOTOR,
                Constants.BACK_RIGHT_MODULE_STEER_ENCODER,
                Constants.BACK_RIGHT_MODULE_STEER_OFFSET
        );
        ShuffleboardLayout orientationLayout = shuffleboardTab.getLayout("Orientation", BuiltInLayouts.kList).withSize(1, 3).withPosition(4,0);
        orientationLayout.addNumber("Pose X", () -> getRealOdometry().getX());
        orientationLayout.addNumber("Pose Y", () -> getRealOdometry().getY());
        orientationLayout.addNumber("Rotation", () -> getRotation().getDegrees());

        
        ShuffleboardLayout PigeonInfo = shuffleboardTab.getLayout("Pigeon Gyroscope", BuiltInLayouts.kList).withPosition(5, 0).withSize(1, 3);
        PigeonInfo.addNumber("Yaw", () -> gyroscope.getYaw());
        PigeonInfo.addNumber("Pitch", () -> gyroscope.getPitch());
        PigeonInfo.addNumber("Roll", () -> gyroscope.getRoll());
        shuffleboardTab.addBoolean("Robot Centric", () -> RobotCentric);
    }
    public void zeroGyroscope() {
        odometry.resetPosition(
                new Pose2d(odometry.getPoseMeters().getTranslation(), Rotation2d.fromDegrees(0.0)),
                Rotation2d.fromDegrees(gyroscope.getYaw())
        );
    }

    public void Gyroscope180() {
        odometry.resetPosition(
                new Pose2d(odometry.getPoseMeters().getTranslation(), Rotation2d.fromDegrees(150)),
                Rotation2d.fromDegrees(gyroscope.getYaw())
        );
    }

    public Rotation2d getRotation() {
        return odometry.getPoseMeters().getRotation();
    }

    public void drive(ChassisSpeeds chassisSpeeds) {
        this.previousSpeeds = this.chassisSpeeds;
        this.chassisSpeeds = chassisSpeeds;
        states = kinematics.toSwerveModuleStates(chassisSpeeds);
    }
    public void updateOdometry() {
        odometry.update(Rotation2d.fromDegrees(gyroscope.getYaw()),
                new SwerveModuleState(frontLeftModule.getDriveVelocity()*1.05, new Rotation2d(frontLeftModule.getSteerAngle())),
                new SwerveModuleState(frontRightModule.getDriveVelocity()*1.05, new Rotation2d(frontRightModule.getSteerAngle())),
                new SwerveModuleState(backLeftModule.getDriveVelocity()*1.05, new Rotation2d(backLeftModule.getSteerAngle())),
                new SwerveModuleState(backRightModule.getDriveVelocity()*1.05, new Rotation2d(backRightModule.getSteerAngle()))
        );
    }

        public Pose2d getRealOdometry() {
                double centerToWheel = Math.hypot(Constants.DRIVETRAIN_WHEELBASE_METERS / 2, Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2);
                return new Pose2d(
                                odometry.getPoseMeters().getTranslation().plus(new Translation2d(
                                        0/*Constants.DRIVETRAIN_WHEELBASE_METERS / 2*/,
                                        0/*Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2*/
                                ).rotateBy(Rotation2d.fromDegrees(135 + getRotation().getDegrees())))/*odometry.getPoseMeters().getX() + centerToWheel*Math.sin(Math.toRadians(135) + getRotation().getRadians()),
                                odometry.getPoseMeters().getY() + centerToWheel*Math.cos(Math.toRadians(135) + getRotation().getRadians())*/,
                                getRotation()
                );
        }

    @Override
    public void periodic() {
        //SmartDashboard.putString("Drivetrain command", CommandVariable);
        updateOdometry();
        if (!DriverStation.isAutonomousEnabled()) {
                updateOdometry();
                frontLeftModule.set((feedForward.calculate(states[0].speedMetersPerSecond) + pid.calculate(states[0].speedMetersPerSecond)) * Math.min(Constants.SWERVE_SPEED_MULTIPLIER, 1), states[0].angle.getRadians());
                frontRightModule.set((feedForward.calculate(states[1].speedMetersPerSecond) + pid.calculate(states[1].speedMetersPerSecond)) * Math.min(Constants.SWERVE_SPEED_MULTIPLIER, 1), states[1].angle.getRadians());
                backLeftModule.set(-(feedForward.calculate(states[2].speedMetersPerSecond) + pid.calculate(states[2].speedMetersPerSecond)) * Math.min(Constants.SWERVE_SPEED_MULTIPLIER, 1), states[2].angle.getRadians()); 
                backRightModule.set((feedForward.calculate(states[3].speedMetersPerSecond) + pid.calculate(states[3].speedMetersPerSecond)) * Math.min(Constants.SWERVE_SPEED_MULTIPLIER, 1), states[3].angle.getRadians()); 
        }
    }
//Eric Koenemann was here!
}