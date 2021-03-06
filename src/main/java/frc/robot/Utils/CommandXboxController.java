package frc.robot.Utils;

import java.util.EnumMap;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController.Axis;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;

/**
 * An alternative XboxController class used in 
 * command-based robot code. Provides JoystickButtons instead of Button values. 
 * Using this class to bind a command to a trigger is very simple:
 * i.e. xbox.a().whenPressed(.....
 * Provides JoystickButtons for binding commands to an XboxController's buttons. Additionally offers
 * getters for retrieving axis values.
 */

public class CommandXboxController extends GenericHID {
    private enum XboxPOV {
        kUpper(0),
        kUpperRight(45),
        kRight(90),
        kLowerRight(135),
        kLower(180),
        kLowerLeft(225),
        kLeft(270),
        kUpperLeft(315);

        public final int direction;

        XboxPOV(int direction) {
            this.direction = direction;
        }
    }

    private final EnumMap<Button, JoystickButton> m_buttons = new EnumMap<Button, JoystickButton>(Button.class);

    private final EnumMap<XboxPOV, POVButton> m_povButtons = new EnumMap<XboxPOV, POVButton>(XboxPOV.class);


    public CommandXboxController(final int port) {
        super(port);
    
        HAL.report(tResourceType.kResourceType_XboxController, port + 1);
    }

    private JoystickButton build(Button button) {
		return new JoystickButton(this, button.value);
	}

    private POVButton build(XboxPOV button) {
        return new POVButton(this, button.direction);
    }

    public JoystickButton leftBumper() {
		return m_buttons.computeIfAbsent(Button.kLeftBumper, this::build);
	}

    public JoystickButton rightBumper() {
		return m_buttons.computeIfAbsent(Button.kRightBumper, this::build);
	}

    public JoystickButton leftStick() {
		return m_buttons.computeIfAbsent(Button.kLeftStick, this::build);
	}

    public JoystickButton rightStick() {
		return m_buttons.computeIfAbsent(Button.kRightStick, this::build);
	}

    public JoystickButton a() {
		return m_buttons.computeIfAbsent(Button.kA, this::build);
	}

    public JoystickButton b() {
		return m_buttons.computeIfAbsent(Button.kB, this::build);
	}

    public JoystickButton x() {
		return m_buttons.computeIfAbsent(Button.kX, this::build);
	}

    public JoystickButton y() {
		return m_buttons.computeIfAbsent(Button.kY, this::build);
	}

    public JoystickButton back() {
		return m_buttons.computeIfAbsent(Button.kBack, this::build);
	}

    public JoystickButton start() {
		return m_buttons.computeIfAbsent(Button.kStart, this::build);
	}

    public POVButton upperPOV() {
        return m_povButtons.computeIfAbsent(XboxPOV.kUpper, this::build);
    }

    public POVButton upperRightPOV() {
        return m_povButtons.computeIfAbsent(XboxPOV.kUpperRight, this::build);
    }
    
    public POVButton rightPOV() {
        return m_povButtons.computeIfAbsent(XboxPOV.kRight, this::build);
    }

    public POVButton lowerRightPOV() {
        return m_povButtons.computeIfAbsent(XboxPOV.kLowerRight, this::build);
    }

    public POVButton lowerPOV() {
        return m_povButtons.computeIfAbsent(XboxPOV.kLower, this::build);
    }

    public POVButton lowerLeftPOV() {
        return m_povButtons.computeIfAbsent(XboxPOV.kLowerLeft, this::build);
    }

    public POVButton leftPOV() {
        return m_povButtons.computeIfAbsent(XboxPOV.kLeft, this::build);
    }

    public POVButton upperLeftPOV() {
        return m_povButtons.computeIfAbsent(XboxPOV.kUpperLeft, this::build);
    }
  /**
   * Get the X axis value of left side of the controller.
   *
   * @return The axis value.
   */
  public double getLeftX() {
    return getRawAxis(Axis.kLeftX.value);
  }

  /**
   * Get the X axis value of right side of the controller.
   *
   * @return The axis value.
   */
  public double getRightX() {
    return getRawAxis(Axis.kRightX.value);
  }

  /**
   * Get the Y axis value of left side of the controller.
   *
   * @return The axis value.
   */
  public double getLeftY() {
    return getRawAxis(Axis.kLeftY.value);
  }

  /**
   * Get the Y axis value of right side of the controller.
   *
   * @return The axis value.
   */
  public double getRightY() {
    return getRawAxis(Axis.kRightY.value);
  }

  /**
   * Get the left trigger (LT) axis value of the controller. Note that this axis is bound to the
   * range of [0, 1] as opposed to the usual [-1, 1].
   *
   * @return The axis value.
   */
  public double getLeftTriggerAxis() {
    return getRawAxis(Axis.kLeftTrigger.value);
  }

  /**
   * Get the right trigger (RT) axis value of the controller. Note that this axis is bound to the
   * range of [0, 1] as opposed to the usual [-1, 1].
   *
   * @return The axis value.
   */
  public double getRightTriggerAxis() {
    return getRawAxis(Axis.kRightTrigger.value);
  }

   /**
     * Returns AxisButton
     * @param port port number of axis/joystick
     * @param threshold threshold of JoystickAxis
     * @return AxisButton
     */
    public AxisButton getJoystickAxisButton(int port, double threshold) {
        return new AxisButton(this, port, threshold);
    }

    /**
     * Returns AxisButton
     * @param axis XboxJoystick joystick axis
     * @param threshold threshold of JoystickAxis
     * @return AxisButton
     */
    public AxisButton getJoystickAxisButton(Axis axis, double threshold) {
        return getJoystickAxisButton(axis.value, threshold);
    }

    
}
