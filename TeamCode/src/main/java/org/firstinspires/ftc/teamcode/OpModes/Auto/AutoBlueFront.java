// AutoBlueFront.java
package org.firstinspires.ftc.teamcode.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoBlueFront", group = "Auto")
public class AutoBlueFront extends AutoCommon {
    @Override
    public void setUniqueParameters() {
        // Specific values for AutoBlueFront
        targetAprilTagOffset = 0;
        strafeDirAfterPurPix = -1;
        turnAngleNearBackstage = -95;
        strafeDistAfterPurPix = 98;
        strafeDistAtBackboard = -29;
        strafeDirForParking = -1;
        //-1 for blue, 1 for Red
        redOrBlueSide = -1;
        DELIVERY_DISTANCE = 19;
        super.centerTagID = 2;
    }

}
