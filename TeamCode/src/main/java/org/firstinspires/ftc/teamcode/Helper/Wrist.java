package org.firstinspires.ftc.teamcode.Helper;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Wrist {
    public Servo servo;
    public double TARGET_POSITION;

    public double WRIST_DELIVERY_POSITION_HIGH = 0.2;
    public double WRIST_DELIVERY_POSITION_LOW = 0.25;
    public double WRIST_PICKUP_POSITION = 0.78;


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

    public void gotoPickupPosition(){
        servo.setPosition(WRIST_PICKUP_POSITION);
    }
    public void gotoLowPosition(){
        servo.setPosition(WRIST_DELIVERY_POSITION_LOW);
    }
    public void gotoHighPosition(){
        servo.setPosition(WRIST_DELIVERY_POSITION_HIGH);
    }


}