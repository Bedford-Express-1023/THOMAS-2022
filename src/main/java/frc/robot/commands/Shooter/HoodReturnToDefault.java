// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.HoodSubsystem;

public class HoodReturnToDefault extends CommandBase {
  HoodSubsystem m_HoodSubsystem;
  /** Creates a new HoodReturnToDefault. */
  public HoodReturnToDefault(HoodSubsystem hoodSubsystem) {
     m_HoodSubsystem = hoodSubsystem;
    addRequirements(m_HoodSubsystem);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_HoodSubsystem.hoodReturnToDefault();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
