package org.firstinspires.ftc.teamcode.Helper;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

//Related to IMU

import java.util.*;

public class Robot {
    /*
    Properties that describe hardware.
     */
    private ElapsedTime runtime = new ElapsedTime();
    double timeout_ms = 0;

    public Chassis chassis = new Chassis();
    public Intake intake = new Intake();
    public double armHoldingPower = 1;

    private int robotInUse = 2022;

    /* local OpMode members. */
    //Init hardware map
    HardwareMap hwMap = null;


    public ElapsedTime period = new ElapsedTime();
    //tells you how long the robot has run for


    public void init(HardwareMap ahwMap) throws InterruptedException {
        hwMap = ahwMap;
        chassis.init(hwMap);
        intake.init(hwMap);
    }


    public float modAngle(float angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    //Turns the robot
    public void turnRobotToAngle(float endAngle) {
        org.firstinspires.ftc.robotcore.external.navigation.Orientation angle;
        angle = chassis.imu.getAngularOrientation();

        float angleStart = modAngle(angle.firstAngle);
        float angleEnd = modAngle(endAngle);
        float angleCurrent = angleStart;
        float direction = 0;

        if (modAngle((angleEnd - angleCurrent)) >= 180) {
            //Go Clockwise
            direction = -1;
        } else if (modAngle((angleEnd - angleCurrent)) <= 180) {
            //Go Counter Clockwise
            direction = 1;
        }

        double pwr = -0.6;


        while (Math.abs(angleCurrent - angleEnd) > 2) {
            chassis.FLMotor.setPower(-pwr * direction);
            chassis.FRMotor.setPower(pwr * direction);
            chassis.BLMotor.setPower(-pwr * direction);
            chassis.BRMotor.setPower(pwr * direction);
            angle = chassis.imu.getAngularOrientation();
            angleCurrent = modAngle(angle.firstAngle);

        }
        chassis.stopDriveMotors();
    }
}
