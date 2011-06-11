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


public class ViewAttendance extends Form implements CommandListener,ItemStateListener
{
    ChoiceGroup subject ;
    Command back = new Command("Back", Command.BACK, 0);
    AC ac;
    Displayable dis;
    Subject[] subjects;
    String[] names;
    TimeTable tt;

    public ViewAttendance(String label, AC ac, Displayable d)
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

        append(subject);
        addCommand(back);
        setCommandListener(this);
        setItemStateListener(this);


    }

    public void commandAction(Command c, Displayable d)
    {
        Display.getDisplay(ac).setCurrent(dis);
    }

    public void itemStateChanged(Item item)
    {
        if(item.getLabel().equals("Select a Subject:"))
        {
            deleteAll();
            append(subject);
            append(new Spacer(1, 5));
            String subName = subject.getString(subject.getSelectedIndex());

            try
            {
                Subject s = Subject.getSubject(subName);
                int attended = s.getAttended();
                int conducted = s.getConducted();

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
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
