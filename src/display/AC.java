/*
*Copyright 2010, Mohit Gvalani

*This file is part of AC lite.
*AC lite is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*AC lite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*You should have received a copy of the GNU General Public License along with AC lite.  If not, see <http://www.gnu.org/licenses/>.
*/


package display;

import data.*;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

import java.io.IOException;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;


public class AC extends MIDlet implements CommandListener
{

    private boolean midletPaused = false;
    List welcome;
    TimeTable tt;
    Command exitCommand = new Command("Exit", Command.EXIT, 0);
    Command yes = new Command("Yes", Command.OK, 0);
    Command no = new Command("No", Command.BACK, 1);

    public AC() throws RecordStoreException
    {
       welcome = new List("AC lite", List.IMPLICIT);
       String[] rs = RecordStore.listRecordStores();
       boolean exists = false;
       if(rs!=null)
       {
          for(int i=0;i<rs.length;i++)
           if(rs[i].equals("TimeTable"))
           {
               exists = true;
               break;
           }
       }
       if(exists)
       {
	       welcome.append("View Attendance", null);
		   welcome.append("Enter Attendance", null);
		   //welcome.append("Modify attendance", null);
		   welcome.append("Extra Attendance", null);
		   welcome.append("Advanced Edit", null);
		   welcome.append("Reset", null);
	   }//welcome.append("View/Modify TimeTable", null);
       else
           welcome.append("Create TimeTable", null);


       welcome.append("Instructions", null);
       welcome.append("Exit", null);

       welcome.setSelectCommand(new Command("OK", Command.OK, 0));
       welcome.setCommandListener(this);
       switchDisplayable(null, welcome);

    }

    Display getDisplay()
    {
        return Display.getDisplay(this);
    }


    private void initialize() {}
    public void startMIDlet() {}
    public void resumeMIDlet() {}

    public void switchDisplayable(Alert alert, Displayable nextDisplayable)
    {
        Display display = getDisplay();
        if (alert == null)
            display.setCurrent(nextDisplayable);
        else
            display.setCurrent(alert, nextDisplayable);
    }

    public Command getExitCommand()
    {
        if (exitCommand == null)
            exitCommand = new Command("Exit", Command.EXIT, 0);
        return exitCommand;
    }

    public void exitMIDlet()
    {
        switchDisplayable (null, null);
        destroyApp(true);
        notifyDestroyed();
    }

    public void startApp()
    {
        if (midletPaused)
        {
            resumeMIDlet ();
        }
        else
        {
            initialize ();
            startMIDlet ();
        }
        midletPaused = false;
    }

    public void pauseApp()
    {
        midletPaused = true;
    }

    public void destroyApp(boolean unconditional)
    {
    }

    public void commandAction(Command c, Displayable d)
    {
       if(c.getLabel().equals("OK"))
       {
           if(welcome.getString(welcome.getSelectedIndex()).equals("Create TimeTable"))
           {

               createTimeTable(d);
               System.out.println("here1");
               welcome.deleteAll();
			   welcome.append("View Attendance", null);
			   welcome.append("Enter Attendance", null);
			   //welcome.append("Modify attendance", null);
			   welcome.append("Extra Attendance", null);
			   welcome.append("Advanced Edit", null);
			   welcome.append("Reset", null);
       		   welcome.append("Instructions", null);
       		   welcome.append("Exit", null);

               //Display.getDisplay(this).setCurrent(d);
               //System.out.println("here2");
           }
           else if(welcome.getString(welcome.getSelectedIndex()).equals("View Attendance"))
               viewAttendance(d);
           else if(welcome.getString(welcome.getSelectedIndex()).equals("Enter Attendance"))
               enterAttendance(d);
           else if(welcome.getString(welcome.getSelectedIndex()).equals("Extra Attendance"))
               extraAttendance(d);
           else if(welcome.getString(welcome.getSelectedIndex()).equals("Advanced Edit"))
               advancedEdit(d);
           else if(welcome.getString(welcome.getSelectedIndex()).equals("Instructions"))
               viewInstructions(d);
           else if(welcome.getString(welcome.getSelectedIndex()).equals("Reset"))
           {
               Alert a = new Alert("Delete Everything?", "Are you sure you want to delete your time-table and attendance record?",null, AlertType.CONFIRMATION);
               a.setCommandListener(this);
               a.setTimeout(Alert.FOREVER);
               a.addCommand(yes);
               a.addCommand(no);
               switchDisplayable(null,a);
           }
           else if(welcome.getString(welcome.getSelectedIndex()).equals("Exit"))
           {
			   notifyDestroyed();
		   }

       }
       else if(c.getLabel().equals("Yes"))
       {
              String[] recordStores = RecordStore.listRecordStores();
              for(int i=0;i<recordStores.length; i++)
              {
                  try
                  {
                      RecordStore r = RecordStore.openRecordStore(recordStores[i],false);
                      int ij=0;
                      while(true)
                      {
                          try
                          {
                              ij++;
                              r.closeRecordStore();
                          }
                          catch(RecordStoreNotOpenException re)
                          {
                              System.out.println(recordStores[i]+"Not Open Exception\nclose() count:"+ij);
                              break;
                          }
                      }
                      RecordStore.deleteRecordStore(recordStores[i]);
                  }
                  catch (RecordStoreException ex)
                  {
                        ex.printStackTrace();
                  }
              }
              //switchDisplayable(null, welcome);
			  System.out.println("Here 1");
              Alert a = (Alert)d;

              a.removeCommand(yes);
              a.removeCommand(no);
              a.addCommand(exitCommand);
              System.out.println("Here 2");
              a.setString("Restart Application.");

              //System.out.println("Exiting Yes");
       }
       else if(c.getLabel().equals("No"))
       {
           System.out.println("In No");
           Display.getDisplay(this).setCurrent(welcome);
       }
       else if(c==exitCommand)
       	notifyDestroyed();
    }

    public void createTimeTable(Displayable d)
    {
        CreateTT tt=null;
        try
        {
            tt = new CreateTT("Subject Info",this,d);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        Display.getDisplay(this).setCurrent(tt);

    }

    void viewAttendance(Displayable d)
    {
        ViewAttendance va = new ViewAttendance("Attendance Score",this,d);
        Display.getDisplay(this).setCurrent(va);
    }

    void advancedEdit(Displayable d)
    {
        AdvancedEdit ae = new AdvancedEdit("Advanced Edit",this,d);
        //Display.getDisplay(this).setCurrent(ae);
    }

    void extraAttendance(Displayable d)
    {
        ExtraAttendance ea = new ExtraAttendance("Extra Attendance",this,d);
        Display.getDisplay(this).setCurrent(ea);
    }

    void enterAttendance(Displayable d)
    {
        EnterAttendance ea = new EnterAttendance("Please wait...",this,d);
        //System.out.println("EA created");
        //Display.getDisplay(this).setCurrent(ea);
    }

    void viewInstructions(Displayable d)
    {
        Instructions i = new Instructions("AC 0.1",this,d);
        Display.getDisplay(this).setCurrent(i);
    }

}

