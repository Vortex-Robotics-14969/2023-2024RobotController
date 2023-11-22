package org.firstinspires.ftc.teamcode.Helper;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Wrist {
    public Servo servo;
    public double TARGET_POSITION;


    HardwareMap hwMap = null;

    public void init(HardwareMap ahwMap) throws InterruptedException {

        hwMap = ahwMap;
        //Init motors and servos
        servo = hwMap.get(Servo.class, "Wrist");
        servo.setDirection(Servo.Direction.FORWARD);
    }

    public void servoPosition(double TARGET_POSITION) {
        servo.setPosition(TARGET_POSITION);

    }


}