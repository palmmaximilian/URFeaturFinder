package ank.featurefinder.impl;

import com.ur.urcap.api.domain.value.Pose;

public class ProbeFeatureClass {

  private String ZUpPose;
  private String XUpPose;
  private String XDownPose;
  private String Y1UpPose;
  private String Y1DownPose;
  private String Y2UpPose;
  private String Y2DownPose;

  private String ZDirection;
  private String XDirection;
  private String YDirection;


  private int ZDirectionIndex;
  private int XDirectionIndex;
  private int YDirectionIndex;


  private String ZWrench;
  private String XWrench;
  private String YWrench;

  private int ProbeForce;

  private double ProbeSpeed;

  private double RapidSpeed;
  private double RapidAcceleration;

  private boolean doubleProbe;

  public ProbeFeatureClass() {
    ZUpPose = "p[0,0,0,0,0,0]";
    XUpPose = "p[0,0,0,0,0,0]";
    XDownPose = "p[0,0,0,0,0,0]";
    Y1UpPose = "p[0,0,0,0,0,0]";
    Y1DownPose = "p[0,0,0,0,0,0]";
    Y2UpPose = "p[0,0,0,0,0,0]";
    Y2DownPose = "p[0,0,0,0,0,0]";

    ZDirection = "[0,0,1,0,0,0]";
    XDirection = "[0,1,0,0,0,0]";
    YDirection = "[1,0,0,0,0,0]";

    ZDirectionIndex = 5;
    XDirectionIndex = 3;
    YDirectionIndex = 0;


    ProbeForce = 8;

    ProbeSpeed = 20;

    RapidSpeed = 1000;
    RapidAcceleration = 800;

    doubleProbe = false;

    ZWrench = generateWrenchString(ZDirectionIndex, ProbeForce);
    XWrench = generateWrenchString(XDirectionIndex, ProbeForce);
    YWrench = generateWrenchString(YDirectionIndex, ProbeForce);
  }

  public ProbeFeatureClass(String initString) {
    // initString looks like[ZupPose/XupPose/XdownPose/Y1upPose/Y1downPose/Y2upPose/Y2downPose/ZDirection/XDirection/YDirection/ProbeForce/ProbeSpeed/RapidSpeed/RapidAcceleration/DoubleProbe]
    String[] initArray = initString.split("/");
    ZUpPose = initArray[0];
    XUpPose = initArray[1];
    XDownPose = initArray[2];
    Y1UpPose = initArray[3];
    Y1DownPose = initArray[4];
    Y2UpPose = initArray[5];
    Y2DownPose = initArray[6];

    ZDirectionIndex = Integer.parseInt(initArray[7]);
    XDirectionIndex = Integer.parseInt(initArray[8]);
    YDirectionIndex = Integer.parseInt(initArray[9]);

    ZDirection = generateDirectionString(ZDirectionIndex);
    XDirection = generateDirectionString(XDirectionIndex);
    YDirection = generateDirectionString(YDirectionIndex);

    ZWrench = generateWrenchString(ZDirectionIndex, ProbeForce);
    XWrench = generateWrenchString(XDirectionIndex, ProbeForce);
    YWrench = generateWrenchString(YDirectionIndex, ProbeForce);

    ProbeForce = Integer.parseInt(initArray[10]);
    ProbeSpeed = Double.parseDouble(initArray[11]);

    RapidSpeed = Double.parseDouble(initArray[12]);
    RapidAcceleration = Double.parseDouble(initArray[13]);
  
    doubleProbe = Boolean.parseBoolean(initArray[14]);

  }

  public void setZUpPose(Pose pose) {
    ZUpPose = pose.toString();
  }

  public void setXUpPose(Pose pose) {
    XUpPose = pose.toString();
  }

  public void setXDownPose(Pose pose) {
    XDownPose = pose.toString();
  }

  public void setY1UpPose(Pose pose) {
    Y1UpPose = pose.toString();
  }

  public void setY1DownPose(Pose pose) {
    Y1DownPose = pose.toString();
  }

  public void setY2UpPose(Pose pose) {
    Y2UpPose = pose.toString();
  }

  public void setY2DownPose(Pose pose) {
    Y2DownPose = pose.toString();
  }

  public String getZUpPoseString() {
    return ZUpPose;
  }

  public String getXUpPoseString() {
    return XUpPose;
  }

  public String getXDownPoseString() {
    return XDownPose;
  }

  public String getY1UpPoseString() {
    return Y1UpPose;
  }

  public String getY1DownPoseString() {
    return Y1DownPose;
  }

  public String getY2UpPoseString() {
    return Y2UpPose;
  }

  public String getY2DownPoseString() {
    return Y2DownPose;
  }

  private String generateWrenchString(int index, int force) {
    String[] directionArray = { "0", "0", "0", "0", "0", "0" };
    if (index % 2 == 0) {
      directionArray[(int) Math.floor(index / 2)] = Integer.toString(force);
    } else {
      directionArray[(int) Math.floor(index / 2)] = "-" + Integer.toString(force);
    }
    String directionString = "[" + String.join(",", directionArray) + "]";
    return directionString;
  }

  private String generateDirectionString(int index) {
    String[] directionArray = { "0", "0", "0", "0", "0", "0" };
    directionArray[(int) Math.floor(index / 2)] = "1";
    String directionString = "[" + String.join(",", directionArray) + "]";
    return directionString;
  }

  public void setZDirection(int direction) {
    ZDirection = generateDirectionString(direction);
    ZDirectionIndex = direction;
    ZWrench = generateWrenchString(direction, ProbeForce);
    // System.out.println("setZDirection: " + ZDirection + " index: " + ZDirectionIndex);
  }

  public void setXDirection(int direction) {
    XDirection = generateDirectionString(direction);
    XDirectionIndex = direction;
    XWrench = generateWrenchString(direction, ProbeForce);
    // System.out.println("setXDirection: " + XDirection + " index: " + XDirectionIndex);
  }

  public void setYDirection(int direction) {
    YDirection = generateDirectionString(direction);
    YDirectionIndex = direction;
    YWrench = generateWrenchString(direction, ProbeForce);
    // System.out.println("setYDirection: " + YDirection + " index: " + YDirectionIndex);
  }

  public String getZDirection() {
    return ZDirection;
  }

  public String getXDirection() {
    return XDirection;
  }

  public String getYDirection() {
    return YDirection;
  }

  public int getZDirectionIndex() {
    return ZDirectionIndex;
  }

  public int getXDirectionIndex() {
    return XDirectionIndex;
  }

  public int getYDirectionIndex() {
    return YDirectionIndex;
  }

  public String getZWrench() {
    return ZWrench;
  }

  public String getXWrench() {
    return XWrench;
  }

  public String getYWrench() {
    return YWrench;
  }

  public void setProbeForce(int force) {
    ProbeForce = force;
    ZWrench = generateWrenchString(ZDirectionIndex, ProbeForce);
    XWrench = generateWrenchString(XDirectionIndex, ProbeForce);
    YWrench = generateWrenchString(YDirectionIndex, ProbeForce);
  }

  public int getProbeForce() {
    return ProbeForce;
  }

  public void setProbeSpeed(double speed) {
    ProbeSpeed = speed;
  }

  public double getProbeSpeed() {
    return ProbeSpeed;
  }

  public void setRapidSpeed(double speed) {
    RapidSpeed = speed;
  }

  public double getRapidSpeed() {
    return RapidSpeed;
  }

  public void setRapidAcceleration(double acceleration) {
    RapidAcceleration = acceleration;
  }

  public double getRapidAcceleration() {
    return RapidAcceleration;
  }

  public void setDoubleProbe(boolean doubleProbe) {
    this.doubleProbe = doubleProbe;
  }

  public boolean getDoubleProbe() {
    return doubleProbe;
  }

  private String generateLimitString(double speed) {
    return "[" + String.valueOf(speed) + "," + String.valueOf(speed) + "," + String.valueOf(speed) + ",0.341,0.341,0.341]";
  }

  public String generateZForceCommand() {
    String command = "force_mode(p[0,0,0,0,0,0]," + ZDirection + "," + ZWrench + ",2," + generateLimitString(ProbeSpeed) + ")\n";
    return command;
  }

  public String generateXForceCommand() {
    String command = "force_mode(p[0,0,0,0,0,0]," + XDirection + "," + XWrench + ",2," + generateLimitString(ProbeSpeed) + ")\n";
    return command;
  }

  public String generateYForceCommand() {
    String command = "force_mode(p[0,0,0,0,0,0]," + YDirection + "," + YWrench + ",2," + generateLimitString(ProbeSpeed) + ")\n";
    return command;
  }

  public Boolean isDefined(){
    // checks if none of the points are p[0,0,0,0,0,0]
    // System.out.println(XUpPose + XDownPose + Y1UpPose + Y1DownPose + Y2UpPose + Y2DownPose);
    return !(ZUpPose.equals("p[0,0,0,0,0,0]") || XUpPose.equals("p[0,0,0,0,0,0]") || XDownPose.equals("p[0,0,0,0,0,0]") || Y1UpPose.equals("p[0,0,0,0,0,0]") || Y1DownPose.equals("p[0,0,0,0,0,0]") || Y2UpPose.equals("p[0,0,0,0,0,0]") || Y2DownPose.equals("p[0,0,0,0,0,0]"));
  }

  @Override
  public String toString() {
  
    String[] returnString = { ZUpPose, XUpPose, XDownPose, Y1UpPose, Y1DownPose, Y2UpPose, Y2DownPose, Integer.toString(ZDirectionIndex), Integer.toString(XDirectionIndex), Integer.toString(YDirectionIndex), Integer.toString(ProbeForce), Double.toString(ProbeSpeed), Double.toString(RapidSpeed), Double.toString(RapidAcceleration), Boolean.toString(doubleProbe) };

    return String.join("/", returnString);
  }
}
