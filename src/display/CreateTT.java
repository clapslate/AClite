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
import java.util.Vector;
import javax.microedition.lcdui.*;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/*Steps -
 * 1. Add Start,End date and target Attendance
 * 2. Select Rows - Days
 * 3. Insert Columns - Time Slots
 * 4. Add Subjects
 * 5. Enter TimeTable
 */
public class CreateTT extends Form implements CommandListener
{
    TimeTable tt;
    DateField startDate;
    TextField noOfWeeks;
    TextField targetAttendance;

    Command add = new Command("Add",Command.OK,0);;
    Command ok = new Command("OK", Command.OK, 0);
    Command yes = new Command("Yes", Command.OK, 0);
    Command no = new Command("No", Command.BACK, 1);
    Command yup = new Command("Yup", Command.OK, 0);
    Command nope = new Command("Nope", Command.BACK, 1);

 	String subjects[];

    int currentDay;
    String currentStartTime= null;
    int dayCounter=-1;
    int weeks;

    AC ac;
    Displayable d;

    Thread thread;

    boolean[] whichDays = new boolean[7];
    Day days[] = new Day[7];
    Subject subject[];


    public CreateTT(String label, AC ac, Displayable d) throws IOException
    {
        super(label);
        System.out.println("here3");
        setCommandListener(this);
        this.ac = ac;
        this.d = d;
        //setItemStateListener(this);
        getSEDates();   // Start and no. of weeks


    }

    void getSEDates()
    {
        deleteAll();
        setTitle("Duration");
        startDate = new DateField("Start Date",DateField.DATE);
        noOfWeeks = new TextField("No. of weeks","14",3,TextField.NUMERIC);
        targetAttendance = new TextField("Target Attendance in %", "75", 3, TextField.NUMERIC);
        append(startDate);
        append(new Spacer(1, 5));
        append(noOfWeeks);
        append(new Spacer(1, 5));
        append(targetAttendance);
        addCommand(new Command("Next",Command.OK,0));
    }


    void addSubject()
    {
        deleteAll();
        append(new TextField("How many subjects do you have?",null,2,TextField.NUMERIC));
        addCommand(ok);
        setTitle("Subjects");
    }

    void addSlot()
    {
		deleteAll();
		removeCommand(add);
		ChoiceGroup slot = new ChoiceGroup("Select a subject: ",Choice.POPUP,subjects,null);
		addCommand(add);
		append(slot);
		Display.getDisplay(ac).setCurrent(this);
	}

    void constructTT()
    {
		deleteAll();
		Alert a = new Alert("Construct TT"," ",null,AlertType.CONFIRMATION);
		a.setTimeout(Alert.FOREVER);
		a.setCommandListener(this);
		a.addCommand(yes);
		a.addCommand(no);

		String dayNames[] = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
		//for(; dayCounter<7; dayCounter++)
		//{
			dayCounter++;
			if(dayCounter!=7)
			{
				a.setString("Do you have lectures/practicals on "+dayNames[dayCounter]);
				Day day = new Day();//dayCounter
				days[dayCounter]=day;
				//thread = Thread.currentThread();

				Display.getDisplay(ac).setCurrent(a,this);
			}

			/*try
			{
				Thread.sleep(99999999);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}*/
		//}
		else
		{

			deleteAll();
			append("Please wait...");

			Day.openDB();
			for(int i=0; i<7; i++)
				days[i].commit(-1);
			Day.closeDB();

			Subject.openDB();
			for(int i=0; i<subject.length; i++)
				subject[i].commit(-1);
			Subject.closeDB();

			tt.setWhichDays(whichDays);
			TimeTable.openDB();
			tt.commit(-1);
			TimeTable.closeDB();

			Display.getDisplay(ac).setCurrent(d);
		}
	}

	void appropriateSubjectTotal(String name)
	{
		for(int i=0; i<subject.length;i++)
			if(subject[i].getName().equals(name))
			{
				subject[i].setTotal(subject[i].getTotal()+weeks);
				break;
			}
	}

    public void commandAction(Command c, Displayable d)
    {

        if(c.getLabel().equals("Next"))
        {
            removeCommand(c);

            java.util.Date sd = startDate.getDate();
            String now = noOfWeeks.getString();
            String tA = targetAttendance.getString();

            int target = 75;
            weeks = 14;

            if(tA!=null)
                target = Integer.parseInt(tA);

            if(now!=null)
                weeks = Integer.parseInt(now);

            if(sd==null)
                getSEDates();
            else
            {
                data.Date std = new data.Date(sd);

                try
                {
					int noOfDays = weeks * 7;
					boolean daysStatus[] = new boolean[noOfDays];
					tt = new TimeTable(daysStatus,target, std);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

                addSubject();
            }
        }
        else if(c.getLabel().equals("OK"))
        {
			removeCommand(c);
			Form nos = (Form)d;
			TextField num = (TextField)nos.get(0);
			int n = Integer.parseInt(num.getString());

			deleteAll();

			for(int i=0; i<n; i++)
				append(new TextField("Subject name "+(i+1)+": ", null, 33, TextField.ANY));

			addCommand(new Command("Done",Command.OK,0));

        }
        else if(c.getLabel().equals("Add"))
        {
			Form f = (Form)d;
			ChoiceGroup cg = (ChoiceGroup)f.get(0);
            days[dayCounter].addSlot(cg.getString(cg.getSelectedIndex()));
            appropriateSubjectTotal(cg.getString(cg.getSelectedIndex()));

			Alert a = new Alert("One more?","Do you want to add 1 more slot?",null,AlertType.CONFIRMATION);
			a.setTimeout(Alert.FOREVER);
			a.setCommandListener(this);
			a.addCommand(yup);
			a.addCommand(nope);
			Display.getDisplay(ac).setCurrent(a);

        }
        else if(c.getLabel().equals("Yup"))
        {
			addSlot();
		}
		else if(c.getLabel().equals("Nope"))
		{
			//thread.start();
			constructTT();
		}
        else if(c.getLabel().equals("Done"))
        {
 			removeCommand(c);
 			Form subs = (Form)d;
 			int n = subs.size();
 			Vector su = new Vector();
 			subject = new Subject[n];

 			for(int i=0; i<n; i++)
 			{
 				TextField temp = (TextField)subs.get(i);
 				su.addElement(temp.getString());
 				Subject s = new Subject(temp.getString());
 				subject[i] = s;
			}

			subjects = new String[su.size()];
			su.copyInto(subjects);

            constructTT();

        }
        else if(c.getLabel().equals("Yes"))
        {
            whichDays[dayCounter] = true;
            addSlot();
		}
        else if(c.getLabel().equals("No"))
        {
			//thread.start();
			constructTT();
		}
    }

}
