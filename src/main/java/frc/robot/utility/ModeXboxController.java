package frc.robot.utility;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class ModeXboxController {
    int states = 1;

    // when the trigger is called from the HID

    EventLoop loop;

    public ModeXboxController(int Max_States){
        loop = CommandScheduler.getInstance().getDefaultButtonLoop();
    }

    class Trigger{
        public Trigger(BooleanSupplier condition, EventLoop loop){
            
        }
    }
}
