// AutoBlueFront.java
package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoRedBack", group = "Auto")
public class AutoRedBack extends AutoCommon {

    @Override
    public void setUniqueParameters() {
        // Specific values for AutoBlueFront
        targetAprilTagOffset = 3;
        strafeDirAfterPurPix = "Left";
        turnDirNearBackstage = "CW";
        strafeDistAfterPurPix = 24;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // Call the initialization method
        setUniqueParameters();
        // Now call the parent class's runOpMode method
        super.runOpMode();
    }
}
