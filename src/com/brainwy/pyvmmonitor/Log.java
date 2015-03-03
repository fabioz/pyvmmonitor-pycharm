package com.brainwy.pyvmmonitor;

import com.intellij.notification.EventLog;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * Simple API to log errors.
 */
public class Log {
    public static void log(Exception e) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(stream);
        e.printStackTrace(out);
        out.close();
        //System.err.print(stream.toString());
        Notification notification = new Notification("PyVmMonitor Error", "Error in PyVmMonitor", stream.toString(), NotificationType.ERROR);
        Notifications.Bus.notify(notification);
    }
}
