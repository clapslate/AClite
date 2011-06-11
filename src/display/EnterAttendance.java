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
import java.util.Vector;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.lcdui.Display;
import javax.microedition.rms.RecordStoreNotOpenException;

public class EnterAttendance extends Form implements CommandListener
{
    AC ac;
    Displayable dis;
    ChoiceGroup[] displaySlots = null;

    Day day;
    data.Date today;
    List unmarked;
    TimeTable tt;
    int dayCounter = 0;
    Vector tracker = new Vector();
    Vector counterTracker = new Vector();
    Day days[];
    boolean[] daysStatus;
    int index;

    //Command enter = new Command("Enter", Command.OK, 0);
    Command back = new Command("Back", Command.BACK, 0);
    //Command goToDate = new Command("Go to Date", Command.SCREEN, 0);
    Command save = new Command("Save",Command.OK,0);

    public EnterAttendance(String label, AC ac, Displayable d)
    {
        super(label);
        this.ac = ac;
        dis = d;
        //addCommand(enter);
        addCommand(back);
        //addCommand(goToDate);
        setCommandListener(this);

        java.util.Date todaysDate = new java.util.Date();
        today = new Date(todaysDate);
        unmarked = new List("Enter attendance for - ",Choice.IMPLICIT);
        unmarked.setCommandListener(this);
        unmarked.addCommand(back);

        Day.openDB();
        days = Day.getAllDays();
        Day.closeDB();

        displayUnmarked();

    }

    void displayUnmarked()
    {
		TimeTable.openDB();
		tt = TimeTable.getTimeTable();
		daysStatus = tt.getDays();
		boolean[] whichDays = tt.getWhichDays();
		Date startDate = tt.getStartDate();

		int result = today.compareTo(startDate);
		//System.out.println("result = "+result);
		if(result==-1)
		{
			Alert a = new Alert("Not Started","Your course is yet to start",null,AlertType.CONFIRMATION);
			a.setTimeout(Alert.FOREVER);
			Display.getDisplay(ac).setCurrent(a,dis);
		}
		else
		{
			Date onDate = startDate.copy();

			while(true)
			{
				try
				{
					if((daysStatus[dayCounter] == false) && (whichDays[(dayCounter+startDate.getDay())%7] == true))
					{
						unmarked.append(onDate.toString(),null);	// Today, yesterday
						tracker.addElement(new Integer(onDate.getDay()));
						counterTracker.addElement(new Integer(dayCounter));
					}
				}
				catch(Exception e)
				{
					break;
				}
				dayCounter++;

				if(today.compareTo(onDate)==0)
					break;
				else
					onDate.nextDate();
			}
			//System.out.println("HERE2");
			Display.getDisplay(ac).setCurrent(unmarked);
			//System.out.println("HERE3");
		}
	}


    public void commandAction(Command c, Displayable di)
    {
        //System.out.println("in commandAction");
        if(c==back)
        {
            //System.out.println("In back");
            TimeTable.closeDB();
            Display.getDisplay(ac).setCurrent(dis);
        }
        else if(c==List.SELECT_COMMAND)
        {
			index = unmarked.getSelectedIndex();
			Integer inte = (Integer)tracker.elementAt(index);
			int d = inte.intValue();
			//System.out.println("Day "+d);
			Vector subs = days[d].getSubName();
			//String sub[] = new String[subs.size()];
			//subs.copyInto(sub);
			Form f = new Form("Mark Attendance");
			// Each subject having a choicegroup - attended, bunked, cancelled

			String status[] = {"Attended","Bunked","Cancelled"};

			for(int i=0; i<subs.size(); i++)
			{
				ChoiceGroup cg = new ChoiceGroup((String)subs.elementAt(i), Choice.POPUP,status,null);
				f.append(cg);
			}
			Display.getDisplay(ac).setCurrent(f);
			f.addCommand(save);
			f.addCommand(back);
			f.setCommandListener(this);
		}

		else if(c==save)
		{
			Subject.openDB();

			Form f = (Form)di;
			for(int i=0; i<f.size(); i++)
			{
				ChoiceGroup cg = (ChoiceGroup)f.get(i);
				String subName = cg.getLabel();
				Subject s = Subject.getSubject(subName);
				int j = cg.getSelectedIndex();
				if(j==0) // Attended
				{
					s.setConducted(s.getConducted()+1);
					s.setAttended(s.getAttended()+1);
				}
				else if(j==1) //Bunked
				{
					s.setConducted(s.getConducted()+1);
				}
				else if(j==2) //Cancelled
				{
					s.setTotal(s.getTotal()-1);
				}
				s.commit(s.getId());
			}

			Subject.closeDB();

			Integer dc = (Integer)counterTracker.elementAt(index);
			int counter = dc.intValue();
			daysStatus[counter] = true;
			tt.commit(tt.getId());

			unmarked.delete(index);
			tracker.removeElementAt(index);
			counterTracker.removeElementAt(index);

			Display.getDisplay(ac).setCurrent(unmarked);

		}
    }

}
