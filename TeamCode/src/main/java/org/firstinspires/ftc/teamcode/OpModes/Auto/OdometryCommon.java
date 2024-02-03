package org.firstinspires.ftc.teamcode.OpModes.Auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.teamcode.Helper.Robot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous(name = "OdometryCommon", group = "Auto")

public class OdometryCommon extends LinearOpMode{


    /*
    Vision Variables
     */

    //For centering on the AprilTag
    // X-coordinate of team element at the start of auto.

    public int RED_APRILTAG_OFFSET;

    // Set Unique Parameters

    /*
        Objects of classes
     */
    public Robot robot = new Robot();
    public AutoVision vision = new AutoVision();
    SampleMecanumDrive drive;


    enum AutoStages {DETECT_TE, GOTOOUTTAKE, OUTTAKE, GO_TO_BACKBOARD, DELIVER_BACKBOARD, INTAKE_STACK, RETURN_BACKBOARD, PARK, END_AUTO}
    AutoStages currentStage = AutoStages.DETECT_TE;

    /*
        Road Runner Variables; vectors, poses, etc.
     */
    boolean IS_AUTO_FRONT;

    //Trajectories
    Trajectory avoidPerimeter;
    Trajectory goToBackOutake;
    Trajectory goToFrontOutake;
    Trajectory outtake_1_6;
    Trajectory outtake_2_5;
    Trajectory outtake_3_4;
    Trajectory comeBack;
    Trajectory startBackboard;
    Trajectory FrontSideBackboard;
    Trajectory BackSideBackboard;
    Trajectory backintoBoard;
    Trajectory park;
    Trajectory moveToDeliveryTag;

    //Positions and Vectors
    Pose2d startPose;
    Vector2d avoidPerimeterPosition;
    Vector2d outtakeCommonPose;
    Vector2d outtake16pose;
    Vector2d outtake25Pose;
    Vector2d outtake34Pose;
    Vector2d comeBack2_5Pose;
    Vector2d comeBack3_4Pose;
    Vector2d startBackboardPose;
    Vector2d backboardPose;
    Vector2d parkPose;
    Vector2d robotLocalOffset = new Vector2d(0,-6.5);

    public void setUniqueParameters() {
        IS_AUTO_FRONT = false;
        vision.RED_APRILTAG_OFFSET = 0;

        //Center Tag is 5 for Red, 2 for Blue
        vision.CENTER_TAG_ID = 5;

        //Coordinates for where the robot is initialized
        startPose = new Pose2d(12, 72, Math.toRadians(90));
        avoidPerimeterPosition = new Vector2d(12, 62);
        outtakeCommonPose = new Vector2d(37, 45);
        outtake16pose = new Vector2d(-55, 45);
        outtake25Pose = new Vector2d(20, 36);
        outtake34Pose = new Vector2d(12,45);
        startBackboardPose = new Vector2d(-12,12);
        backboardPose = new Vector2d(45, 48);
        parkPose = new Vector2d(48, 72);
    }
    
    @Override
    public void runOpMode() throws InterruptedException {

        setUniqueParameters();

        robot.intake.init(hardwareMap);
        robot.arm.init(hardwareMap);
        robot.wrist.init(hardwareMap);
        robot.gate.init(hardwareMap);

        //Finish creating drive object
        drive = new SampleMecanumDrive(hardwareMap);

        vision.initDoubleVision(hardwareMap);

        RED_APRILTAG_OFFSET = 0;
        vision.CENTER_TAG_ID = 2;


        while (!isStarted()) {
            if (opModeInInit()) {
                vision.detectTeamElement(); // run detections continuously.
                telemetry.addData("Target Tag ID", vision.TARGET_TAG_ID);
                telemetry.update();
                robot.gate.close();
            }
        }

        waitForStart();



        while (opModeIsActive()) {
            switch (currentStage) {
                case DETECT_TE:
                    vision.detectTeamElement();
                    telemetry.addData("Target Tag ID", vision.TARGET_TAG_ID);
                    telemetry.update();
                    currentStage = AutoStages.GOTOOUTTAKE;
                    break;
                case GOTOOUTTAKE:
                    if (!IS_AUTO_FRONT) {
                        outakeBackCommon();
                    }
                    else {
                        outakeFrontCommon();
                    }

                    currentStage = AutoStages.OUTTAKE;
                    break;
                case OUTTAKE:
                    /**
                     * Step 2: Deliver purple pixel to the detected Position.
                     * (common to all auto)
                     */

                    switch (vision.TARGET_SPIKE_MARK) {
                        case 1:
                            outtake_1_6();
                            telemetry.addLine("Case 1");
                            break;
                        case 2:
                            outtake_2_5();
                            telemetry.addLine("Case 2");
                            break;
                        case 3:
                            outtake_3_4();
                            telemetry.addLine("Case 3");
                            break;
                    }
                    currentStage = AutoStages.GO_TO_BACKBOARD;
                    break;
                case GO_TO_BACKBOARD:
                    if (!IS_AUTO_FRONT) {
                        BackboardBack();
                    }
                    else {
                        BackboardFront();
                    }
                    currentStage = AutoStages.DELIVER_BACKBOARD;
                    break;
                case DELIVER_BACKBOARD:
                    if (!IS_AUTO_FRONT) {
                        deliverBack();
                    } else {
                        deliverFront();
                    }
                    currentStage = AutoStages.PARK;
                    break;
                case PARK:
                    park();
                    currentStage = AutoStages.END_AUTO;
                    break;
                case END_AUTO:
                    // End Auto keeps printing debug information via telemetry.
                    telemetry.update();
                    sleep(5000); //5 sec delay between telemetry.
            }
        }
    }

    public void outakeBackCommon() throws InterruptedException {

        drive.setPoseEstimate(startPose);

        avoidPerimeter = drive.trajectoryBuilder(startPose)
                .lineTo(avoidPerimeterPosition)
                .build();

        goToBackOutake = drive.trajectoryBuilder(avoidPerimeter.end())
                .lineToLinearHeading(new Pose2d(outtakeCommonPose.getX(),outtakeCommonPose.getY(), Math.toRadians(180)))
                .build();

        //Move away so we don't hit the perimeter.
        drive.followTrajectory(avoidPerimeter);

        //Drive to spikemark
        drive.followTrajectory(goToBackOutake);

    }
    public void outakeFrontCommon() throws InterruptedException {

        drive.setPoseEstimate(startPose);

        avoidPerimeter = drive.trajectoryBuilder(startPose)
                .lineTo(avoidPerimeterPosition)
                .build();

        goToFrontOutake = drive.trajectoryBuilder(avoidPerimeter.end())
                .lineToLinearHeading(new Pose2d(outtakeCommonPose.getX(),outtakeCommonPose.getY(), Math.toRadians(0))).build();

        //Move away so we don't hit the perimeter.
        drive.followTrajectory(avoidPerimeter);

        //Drive to spikemark
        drive.followTrajectory(goToFrontOutake);

    }
    public void outtake_1_6() {
        if (!IS_AUTO_FRONT) {
            //Outtake at spike mark
            robot.intake.motor.setPower(0.5);
            robot.intake.MoveIntake(0.6, true);
            sleep(2000);
            robot.intake.MoveIntake(0, false);
        } else {
            outtake_1_6 = drive.trajectoryBuilder(goToFrontOutake.end())
                    .lineTo(
                            outtake16pose,
                            SampleMecanumDrive.getVelocityConstraint(15, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)
                    )
                    .build();
            comeBack = drive.trajectoryBuilder(outtake_1_6.end())
                    .lineTo(outtakeCommonPose)
                    .build();
            drive.followTrajectory(outtake_1_6);

            //Outtake at spike mark
            robot.intake.motor.setPower(0.5);
            robot.intake.MoveIntake(0.5, true);
            sleep(2000);
            robot.intake.MoveIntake(0, false);

            //Return to Outtake common position
            drive.followTrajectory(comeBack);
        }
    }
    public void outtake_2_5() {

        if (!IS_AUTO_FRONT){
            outtake_2_5 = drive.trajectoryBuilder(goToBackOutake.end())
                    .lineTo(outtake25Pose)
                    .build();

            comeBack = drive.trajectoryBuilder(outtake_2_5.end())
                    .lineTo(outtakeCommonPose)
                    .build();

            drive.followTrajectory(outtake_2_5);

            //Outtake at spike mark
            robot.intake.motor.setPower(0.5);
            robot.intake.MoveIntake(0.5, true);
            sleep(2000);
            robot.intake.MoveIntake(0, false);

            //Return to Outtake common position
            drive.followTrajectory(comeBack);
        } else {
            outtake_2_5 = drive.trajectoryBuilder(goToFrontOutake.end())
                    .lineTo(outtake25Pose)
                    .build();
            comeBack = drive.trajectoryBuilder(outtake_2_5.end())
                    .lineTo(comeBack2_5Pose)
                    .build();

            drive.followTrajectory(outtake_2_5);

            //Outtake at spike mark
            robot.intake.motor.setPower(0.5);
            robot.intake.MoveIntake(0.5, true);
            sleep(2000);
            robot.intake.MoveIntake(0, false);

            //Return to Outtake common position
            drive.followTrajectory(comeBack);
        }


    }


    public void outtake_3_4() {
        if (!IS_AUTO_FRONT){
            outtake_3_4 = drive.trajectoryBuilder(goToBackOutake.end())
                    .lineTo(
                            outtake34Pose,
                            SampleMecanumDrive.getVelocityConstraint(15, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)
                            )
                    .build();

            comeBack = drive.trajectoryBuilder(outtake_3_4.end())
                    .lineTo(outtakeCommonPose)
                    .build();

            drive.followTrajectory(outtake_3_4);

            //Outtake at spike mark
            robot.intake.motor.setPower(0.5);
            robot.intake.MoveIntake(0.5, true);
            sleep(2000);
            robot.intake.MoveIntake(0, false);

            //Return to Outtake common position
            drive.followTrajectory(comeBack);
        } else {
            outtake_3_4 = drive.trajectoryBuilder(goToFrontOutake.end())
                    .lineTo(
                            outtake34Pose,
                            SampleMecanumDrive.getVelocityConstraint(15, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)
                    )
                    .build();

            comeBack = drive.trajectoryBuilder(outtake_3_4.end())
                    .lineTo(comeBack3_4Pose)
                    .build();

            drive.followTrajectory(outtake_3_4);

            //Outtake at spike mark
            robot.intake.motor.setPower(0.5);
            robot.intake.MoveIntake(0.5, true);
            sleep(2000);
            robot.intake.MoveIntake(0, false);

            //Return to Outtake common position
            drive.followTrajectory(comeBack);


        }
    }

    public void BackboardBack() {
        BackSideBackboard = drive.trajectoryBuilder(goToBackOutake.end())
                .lineTo(backboardPose)
                .build();
        drive.followTrajectory(BackSideBackboard);
    }
    public void BackboardFront() {
        startBackboard = drive.trajectoryBuilder(comeBack.end())
                .lineTo(startBackboardPose)
                .build();
        FrontSideBackboard = drive.trajectoryBuilder(startBackboard.end())
                .splineTo(backboardPose, Math.toRadians(180))
                .build();
        drive.followTrajectory(startBackboard);
        drive.followTrajectory(FrontSideBackboard);
    }

    public double getRangeError() {

        AprilTagDetection tempTag = null;
        double rangeError = 0;

        tempTag = vision.detect_apriltag(vision.CENTER_TAG_ID);
        if (tempTag != null) {
            rangeError = (tempTag.ftcPose.range / 2.54) - vision.DELIVERY_DISTANCE;
            telemetry.addData("range error", rangeError);

        }
        return rangeError;
    }

    public void deliverBack() {
        telemetry.addData("CentertagID", vision.CENTER_TAG_ID);
        telemetry.addData("TargetTagID", vision.TARGET_TAG_ID);
        telemetry.update();
        double rangeError = getRangeError();
        double adjustedRangeX = BackSideBackboard.end().getX() + rangeError;
        if(vision.TARGET_TAG_ID < vision.CENTER_TAG_ID) {
            moveToDeliveryTag = drive.trajectoryBuilder(BackSideBackboard.end())
                    .lineTo(new Vector2d(adjustedRangeX , (BackSideBackboard.end().getY() + 7)))
                    .build();

            drive.followTrajectory(moveToDeliveryTag);
        }
        else if(vision.TARGET_TAG_ID > vision.CENTER_TAG_ID) {
            moveToDeliveryTag = drive.trajectoryBuilder(BackSideBackboard.end())
                    .lineTo(new Vector2d(adjustedRangeX, (BackSideBackboard.end().getY() - 7)))
                    .build();

            drive.followTrajectory(moveToDeliveryTag);
        }
        else{
            if (rangeError != 0){
                moveToDeliveryTag = drive.trajectoryBuilder(BackSideBackboard.end())
                        .lineTo(new Vector2d(adjustedRangeX, (BackSideBackboard.end().getY())))
                        .build();

                drive.followTrajectory(moveToDeliveryTag);
            }
        }

        backintoBoard = drive.trajectoryBuilder(moveToDeliveryTag.end())
                .lineTo(
                        new Vector2d(moveToDeliveryTag.end().getX()+ 2, moveToDeliveryTag.end().getY()),
                        SampleMecanumDrive.getVelocityConstraint(15, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)
                )
                .build();


        robot.gate.close();
        sleep(300);
        robot.arm.gotoAutoPosition();
        sleep(600);
        robot.wrist.gotoAutoPosition();
        sleep(1000);
        drive.followTrajectory(backintoBoard);
        sleep(1100);
        robot.gate.open();
        sleep(700);
        robot.wrist.gotoPickupPosition();
        sleep(500);
        robot.arm.gotoPickupPosition();
        sleep(500);

    }
    public void deliverFront() {
        telemetry.addData("CentertagID", vision.CENTER_TAG_ID);
        telemetry.addData("TargetTagID", vision.TARGET_TAG_ID);
        telemetry.update();
        double rangeError = getRangeError();
        double adjustedRangeX = FrontSideBackboard.end().getX() + rangeError;
        if(vision.TARGET_TAG_ID < vision.CENTER_TAG_ID) {
            moveToDeliveryTag = drive.trajectoryBuilder(FrontSideBackboard.end())
                    .lineTo(new Vector2d(adjustedRangeX , (FrontSideBackboard.end().getY() + 7)))
                    .build();

            drive.followTrajectory(moveToDeliveryTag);
        }
        else if(vision.TARGET_TAG_ID > vision.CENTER_TAG_ID) {
            moveToDeliveryTag = drive.trajectoryBuilder(FrontSideBackboard.end())
                    .lineTo(new Vector2d(adjustedRangeX, (FrontSideBackboard.end().getY() - 7)))
                    .build();

            drive.followTrajectory(moveToDeliveryTag);
        }
        else{
            if (rangeError != 0){
                moveToDeliveryTag = drive.trajectoryBuilder(FrontSideBackboard.end())
                        .lineTo(new Vector2d(adjustedRangeX, (FrontSideBackboard.end().getY())))
                        .build();

                drive.followTrajectory(moveToDeliveryTag);
            }
        }
        backintoBoard = drive.trajectoryBuilder(moveToDeliveryTag.end())
                .lineTo(
                        new Vector2d(moveToDeliveryTag.end().getX()+3.75, moveToDeliveryTag.end().getY()),
                        SampleMecanumDrive.getVelocityConstraint(15, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)
                )
                .build();

        robot.gate.close();
        sleep(300);
        robot.arm.gotoAutoPosition();
        sleep(500);
        robot.wrist.gotoAutoPosition();
        sleep(1000);
        drive.followTrajectory(backintoBoard);
        sleep(1000);
        robot.gate.open();
        sleep(500);
        robot.wrist.gotoPickupPosition();
        sleep(500);
        robot.arm.gotoPickupPosition();

    }
    public void park() {
        if (!IS_AUTO_FRONT) {
            park = drive.trajectoryBuilder(backintoBoard.end())
                    .lineTo(parkPose)
                    .build();
            drive.followTrajectory(park);
        } else {
            park = drive.trajectoryBuilder(backintoBoard.end())
                    .lineTo(parkPose)
                    .build();
            drive.followTrajectory(park);
        }

    }

    // initializing Vision

}



