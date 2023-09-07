package ank.featurefinder.impl.communicator;

// Used for reading from RealTime Client
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ScriptSender {

  // IP of the robot
  private final String TCP_IP;
  // Port for secondary client
  private final int TCP_port = 30002;
  private final int DASHBOARD_PORT = 29999;

  /**
   * Default constructor, using localhost IP (127.0.0.1)
   */
  public ScriptSender() {
    this.TCP_IP = "127.0.0.1";
  }

  /**
   * Constructor for IP different from localhost
   * @param IP the IP address of the robot
   */
  public ScriptSender(String IP) {
    this.TCP_IP = IP;
  }

  /**
   * Method used to send a ScriptCommand as a primary program to the Secondary Client Interface.
   * If called while a program is already running, the existing program will halt.
   * @param command the ScriptCommand object to send.
   */
  public void sendScriptCommand(ScriptCommand command) {
    sendToSecondary(command.toString());
  }

  // Internal method that sends script to client
  private void sendToSecondary(String command) {
    try {
      // Create a new Socket Client
      Socket sc = new Socket(TCP_IP, TCP_port);
      if (sc.isConnected()) {
        // Create stream for data
        DataOutputStream out;
        out = new DataOutputStream(sc.getOutputStream());

        // Send command
        out.write(command.getBytes("US-ASCII"));
        out.flush();

        // Perform housekeeping
        out.close();
      }
      sc.close();
    } catch (IOException e) {
      System.out.println(e);
      System.out.println("Could not send command to robot.");
    }
  }

  private void sendToDashboard(String command) {
    try {
      Socket sc = new Socket(TCP_IP, DASHBOARD_PORT);
      if (sc.isConnected()) {
        // Create stream for data
        DataOutputStream out;
        out = new DataOutputStream(sc.getOutputStream());

        // Send command
        out.write(command.getBytes("US-ASCII"));
        out.flush();

        // Perform housekeeping
        out.close();
      }
      sc.close();
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public void sendPopup(String message) {
    ScriptCommand command = new ScriptCommand();
    command.appendLine("popup(\"" + message + "\")");
    sendToSecondary(command.toString());
  }

  public void sendDashboardPopup(String message) {
    String command = "popup " + message + "\r\n";
    sendToDashboard(command.toString());
  }

  public void sendLogMsg(String message) {
    ScriptCommand command = new ScriptCommand();
    command.appendLine("textmsg(\"" + message + "\")");
    sendToSecondary(command.toString());
  }
}
