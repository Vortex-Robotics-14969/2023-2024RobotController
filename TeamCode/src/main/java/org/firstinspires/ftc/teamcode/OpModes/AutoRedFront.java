// AutoBlueFront.java
package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoRedFront", group = "Auto")
public class AutoRedFront extends AutoCommon {

    @Override
    public void setUniqueParameters() {
        // Specific values for AutoBlueFront
        targetAprilTagOffset = 3;
        strafeDirAfterPurPix = "Left";
        turnDirNearBackstage = "CW";
        strafeDistAfterPurPix = 96;
    }

    @Override
    public void runOpMode() {
        // Call the initialization method
        setUniqueParameters();
        // Now call the parent class's runOpMode method
        super.runOpMode();
    }
}