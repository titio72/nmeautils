package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.sentence.Sentence;

public interface XXXPSentence extends Sentence {

    double getMagX();
    void setMagX(double x);
    
    double getMagY();
    void setMagY(double y);

    double getMagZ();
    void setMagZ(double z);

    double getHeading();
    void setHeading(double h);

    double getRotationX();
    void setRotationX(double x);

    double getRotationY();
    void setRotationY(double y);

    double getRotationZ();
    void setRotationZ(double z);

    double getPressure();
    void setPressure(double p);

    double getTemperature();
    void setTemperature(double t);

    double getVoltage();
    void setVoltage(double v);
    
    double getVoltage1();
    void setVoltage1(double v);
    
    double getRPM();
    void setRPM(double rpm);
    
}