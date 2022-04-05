// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class ShootAuto extends CommandBase {
  HoodSubsystem m_hood;
  ShooterSubsystem m_shooter;
  /** Creates a new ShootAuto. */
  public ShootAuto(HoodSubsystem hoodSubsystem, ShooterSubsystem shooterSubsystem) {
    m_hood = hoodSubsystem;
    m_shooter = shooterSubsystem;
    addRequirements(m_hood, m_shooter);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_hood.setHoodPositionAuto();
    m_shooter.setShooterSpeedsAuto();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_shooter.shootStop();
    m_hood.hoodReturnToZero();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
