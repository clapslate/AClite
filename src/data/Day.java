/*
*Copyright 2010, Mohit Gvalani

*This file is part of AC lite.
*AC lite is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*AC lite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*You should have received a copy of the GNU General Public License along with AC lite.  If not, see <http://www.gnu.org/licenses/>.
*/


package data;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class Day
{
    Vector subName = new Vector();	// or String subName[];

    int id;
    static RecordStore days;

    public Day()
    {
        //System.out.println("Day Created here");
    }


    static public void openDB()
    {
        if(days==null)
        {
            try
            {
                days = RecordStore.openRecordStore("Day", true);
                days.setMode(RecordStore.AUTHMODE_PRIVATE, true);
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
			days.closeRecordStore();
			days = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

    public void addSlot(String sub)
    {
		subName.addElement(sub);
	}

    public static Day[] getAllDays()
    {
		try
		{
			openDB();
			Day[] d = new Day[7];
			for(int i=0; i<7; i++)
				d[i] = new Day();
			byte[] byteInputData = new byte[300];
			ByteArrayInputStream inputStream = new ByteArrayInputStream(byteInputData);
			DataInputStream inputDataStream = new DataInputStream(inputStream);

			//System.out.println(days.getNumRecords());

			for (int x = 1; x <= days.getNumRecords(); x++)
			{
				days.getRecord(x, byteInputData, 0);
				int size = inputDataStream.readInt();
				for(int i=0; i<size; i++)
				{
					d[x-1].subName.addElement(inputDataStream.readUTF());
				}
				inputStream.reset();
			}
			inputStream.close();
			inputDataStream.close();
			//closeDB();

			return d;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}

    public boolean commit(int id)
    {
        try
        {
			byte[] newData = null;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DataOutputStream outputDataStream =new DataOutputStream(outputStream);

			outputDataStream.writeInt(subName.size());
			for(int i=0;i<subName.size();i++)
				outputDataStream.writeUTF((String)subName.elementAt(i));

			outputDataStream.flush();
			newData = outputStream.toByteArray();
			try
			{
				if(id!=-1)
				{
					days.setRecord(id, newData, 0, newData.length);
				}
				else
					days.addRecord(newData, 0, newData.length);
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

    public Vector getSubName()
    {
		return subName;
	}
}

