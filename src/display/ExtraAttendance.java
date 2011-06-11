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
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Spacer;
import javax.microedition.rms.RecordStoreNotOpenException;


public class ExtraAttendance extends Form implements CommandListener
{
    ChoiceGroup subject ;
    TextField tf;
    Command back = new Command("Back", Command.BACK, 1);
    Command add = new Command("Add", Command.OK, 0);
    AC ac;
    Displayable dis;
    Subject[] subjects;
    String[] names;
    TimeTable tt;

    public ExtraAttendance(String label, AC ac, Displayable d)
    {
        super(label);
        this.ac = ac;
        dis = d;
        TimeTable.openDB();
        tt = TimeTable.getTimeTable();
        TimeTable.closeDB();

        Subject.openDB();
        subjects = Subject.getAllSubjects();
        Subject.closeDB();

		//System.out.println("HERE1");
		names = new String[subjects.length];
        for(int i=0; i<subjects.length; i++)
        	names[i] = subjects[i].getName();
        //System.out.println("HERE2");

        subject = new ChoiceGroup("Select a Subject:", ChoiceGroup.POPUP, names, null);
        tf = new TextField("How many?",null,3,TextField.NUMERIC);

        append(subject);
        append(tf);
        addCommand(back);
        addCommand(add);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d)
    {
        if(c==back)
        	Display.getDisplay(ac).setCurrent(dis);
        else if(c==add)
        {
			deleteAll();
			removeCommand(c);
			//append(subject);
			append(new Spacer(1, 5));
			String subName = subject.getString(subject.getSelectedIndex());

			try
			{
				Subject s = Subject.getSubject(subName);
				String howMany = tf.getString();
				try
				{
					int amount = Integer.parseInt(howMany);
					s.setAttended(s.getAttended()+amount);
					s.setConducted(s.getConducted()+amount);
					s.setTotal(s.getTotal()+amount);

					Subject.openDB();
					s.commit(s.getId());
					Subject.closeDB();

					int conducted = s.getConducted();
					int attended = s.getAttended();


					append(new String("Attended: "+attended));
					append(new String("\nConducted: "+conducted));

					if( conducted !=0 )
					{
						append(new String("\nPercentage: "+((attended*100)/conducted)));
					}

					append(new Spacer(1, 5));

					append(new String("\nTo achieve your target attendance of "+tt.getTargetAttendance()+"%,\nYou need to attend: "));

					int total = s.getTotal();

					int shouldAttend = (tt.getTargetAttendance()*total)/100;
					append(new String((shouldAttend-attended)+" slots out of "+(total-conducted)));
				}
				catch(Exception e)
				{
					Display.getDisplay(ac).setCurrent(dis);
				}

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
    }
}
