
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
import javax.microedition.rms.*;

public class Subject
{
    String name;

    int attended;
    int conducted;
    int total;

    int id;

    static RecordStore sub;

    static Subject[] subjects;

    public Subject()
    {
		name = " ";
	}

    public Subject(String n)
    {
        name = n;
    }

    public static void openDB()
    {
        if(sub==null)
        {
            try
            {
                sub = RecordStore.openRecordStore("Subject", true);
                sub.setMode(RecordStore.AUTHMODE_PRIVATE, true);
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
			sub.closeRecordStore();
			sub = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

    public int getAttended() {
           return attended;
    }

    public void setAttended(int attended) {
          this.attended = attended;
    }

    public int getConducted() {
        return conducted;
    }

    public void setConducted(int conducted) {
        this.conducted = conducted;
    }

    public int getTotal() {
          return total;
    }

    public void setTotal(int total) {
           this.total = total;
    }


    public static Subject getSubject(String n)
	{
		if(subjects==null)
			loadSubjects();

		for(int i=0; i<subjects.length; i++)
			if(subjects[i].getName().equals(n))
				return subjects[i];

		return null;
    }

    public static Subject[] getAllSubjects()
	{
		if(subjects==null)
			loadSubjects();

		return subjects;
	}

    public static void loadSubjects()
    {
        openDB();
        try
        {
			byte[] byteInputData = new byte[100];
			ByteArrayInputStream inputStream = new ByteArrayInputStream(byteInputData);
			DataInputStream inputDataStream = new DataInputStream(inputStream);
			int x;
			//System.out.println("HERE1");
			int size = sub.getNumRecords();
			subjects = new Subject[size];
			for (x = 1; x <= size; x++)
			{
				//System.out.println("HERE2");
				Subject s = new Subject();
				sub.getRecord(x, byteInputData, 0);
				//System.out.println("HERE3");
				s.name = inputDataStream.readUTF();
				//System.out.println("HERE4");
				s.attended = inputDataStream.readInt();
				//System.out.println("HERE5");
				s.conducted = inputDataStream.readInt();
				//System.out.println("HERE6");
				s.total= inputDataStream.readInt();
				s.id = x;
				subjects[x-1] = s;
				//System.out.println("HERE7");
				inputStream.reset();
			}

			inputStream.close();
			inputDataStream.close();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }

    public boolean commit(int id)
    {
        try
        {
			byte[] newData = null;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DataOutputStream outputDataStream =new DataOutputStream(outputStream);
			outputDataStream.writeUTF(name);
			outputDataStream.writeInt(attended);
			outputDataStream.writeInt(conducted);
			outputDataStream.writeInt(total);

			outputDataStream.flush();
			newData = outputStream.toByteArray();
			try
			{
				if(id!=-1)
				{
					sub.setRecord(id, newData, 0, newData.length);
				}
				else
					sub.addRecord(newData, 0, newData.length);
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


    public String getName()
    {
        return name;
    }

    public int getId()
    {
		return id;
	}

}
