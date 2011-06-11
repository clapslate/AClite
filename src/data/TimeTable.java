/*
*Copyright 2010, Mohit Gvalani

*This file is part of AC lite.
*AC lite is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*AC lite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*You should have received a copy of the GNU General Public License along with AC lite.  If not, see <http://www.gnu.org/licenses/>.
*/


package data;

import java.io.*;
//import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class TimeTable
{
    int targetAttendance;
    boolean[] whichDays = new boolean[7];
    boolean[] days;

    Date startDate;

    int id;
    static RecordStore timetable;

    public TimeTable()
    {
		startDate = new Date();
    }

    public TimeTable(boolean[] d, int t, Date sd)
    {
		days = d;
		targetAttendance = t;
		startDate = sd;
	}


    public static void openDB()
    {
        if(timetable==null)
        {
            try
            {
                timetable = RecordStore.openRecordStore("TimeTable", true);
                timetable.setMode(RecordStore.AUTHMODE_PRIVATE, true);
            }
            catch (RecordStoreException ex)
            {
                ex.printStackTrace();
            }
        }
    }

	public static void closeDB()
	{
		try
		{
			timetable.closeRecordStore();
			timetable = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


    public static TimeTable getTimeTable()
    {
        TimeTable t = new TimeTable();
        byte[] byteInputData /*= new byte[timetable.getSize()]*/;
        try
        {
            RecordEnumeration re = timetable.enumerateRecords(null,null,false);
            t.id = re.nextRecordId();
            byteInputData = timetable.getRecord(t.id);
        }
        catch (InvalidRecordIDException ex)
        {
            ex.printStackTrace();
            return null;
        }
        catch (RecordStoreNotOpenException ex)
        {
            ex.printStackTrace();
            return null;
        }
        catch (RecordStoreException ex)
        {
            ex.printStackTrace();
            return null;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteInputData);
		DataInputStream inputDataStream = new DataInputStream(inputStream);

        try
        {
			//t.no = inputDataStream.readInt();
			t.targetAttendance = inputDataStream.readInt();

			for(int i=0; i<7; i++)
			{
				t.whichDays[i] = inputDataStream.readBoolean();
				//System.out.println("HERE "+i);
			}

			t.startDate.date = inputDataStream.readInt();
			t.startDate.month = inputDataStream.readInt();
			t.startDate.year = inputDataStream.readInt();
			t.startDate.day = inputDataStream.readInt();

			int size = inputDataStream.readInt();
			t.days = new boolean[size];
			//System.out.println(size);

			for (int i = 0; i < size; i++)
			{
				t.days[i] = inputDataStream.readBoolean();
				//System.out.println("here "+i);
			}

			inputStream.reset();
			inputStream.close();
			inputDataStream.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return t;
    }

    public boolean commit(int id)
    {
        try
        {
			byte[] newData = null;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DataOutputStream outputDataStream =new DataOutputStream(outputStream);

			//outputDataStream.writeInt(no);
			outputDataStream.writeInt(targetAttendance);

			for(int i=0; i<7; i++)
				outputDataStream.writeBoolean(whichDays[i]);

			outputDataStream.writeInt(startDate.date);
			outputDataStream.writeInt(startDate.month);
			outputDataStream.writeInt(startDate.year);
			outputDataStream.writeInt(startDate.day);

			outputDataStream.writeInt(days.length);

			 for (int i = 0; i < days.length; i++)
			 {
				outputDataStream.writeBoolean(days[i]);
			 }

			outputDataStream.flush();
			newData = outputStream.toByteArray();
			try
			{
				if(id!=-1)
				{
					timetable.setRecord(id, newData, 0, newData.length);
				}
				else
					timetable.addRecord(newData, 0, newData.length);
			}
			catch (RecordStoreNotOpenException ex)
			{
				ex.printStackTrace();
				return false;
			}
			catch (InvalidRecordIDException ex)
			{
				ex.printStackTrace();
				return false;
			}
			catch (RecordStoreException ex)
			{
				ex.printStackTrace();
				return false;
			}
			outputStream.reset();
			outputStream.close();
			outputDataStream.close();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getId() {
        return id;
    }

    public int getTargetAttendance() {
        return targetAttendance;
    }

    public void setTargetAttendance(int targetAttendance) {
        this.targetAttendance = targetAttendance;
    }

    public boolean[] getWhichDays() {
        return whichDays;
    }

    public void setWhichDays(boolean[] whichDays) {
        this.whichDays = whichDays;
    }

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }
}
