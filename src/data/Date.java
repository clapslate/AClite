/*
*Copyright 2010, Mohit Gvalani

*This file is part of AC lite.
*AC lite is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*AC lite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*You should have received a copy of the GNU General Public License along with AC lite.  If not, see <http://www.gnu.org/licenses/>.
*/


package data;

public class Date
{
    int date;
    int month;
    int year;
    int day;

    public Date(int dd,int mm,int yy,int day)
    {
        date = dd;
        month = mm;
        year = yy;
        this.day = day;
    }

    public Date()
    {}

    public Date(java.util.Date d)
    {
        String sd = d.toString();
        year = Integer.parseInt(sd.substring(sd.length()-4, sd.length()));
        month = getIntMonth(sd.substring(4, 7));
        date = Integer.parseInt(sd.substring(8,10));
        day = getIntDay(sd.substring(0,3));
        System.out.println("Date:"+date+" Month:"+month+" Year:"+year+" Day:"+day);

    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getIntMonth(String mon)
    {
        String[] months={"Jan","Feb","Mar","Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for(int i=0; i<=12; i++)
            if(mon.equals(months[i]))
                return i;

        return -1;

    }

    public int getIntDay(String day)
    {
        String[] days={"Mon", "Tue", "Wed", "Thu", "Fri", "Sat","Sun"};
        for(int i=0; i<7; i++)
            if(day.equals(days[i]))
                return (i);
        return -1;

    }

    public String toString()
    {
		String[] months={"Jan","Feb","Mar","Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		String s = date+" "+months[month]+" "+year;
		return s;
	}

    public void nextDate()
    {
        int mm = month;
        int dd = date;
        int yy = year;

        if((mm==0)||(mm==2)||(mm==4)||(mm==6)||(mm==7)||(mm==9)||(mm==11))
        {
			if(dd==31)
            {
                if(mm==11)
                    yy++;
                dd=1;
                mm++;
                if(mm==12)
                    mm=0;
            }
            else
                dd++;
		}
        else if((mm==3)||(mm==5)||(mm==8)||(mm==10))
        {
			if(dd==30)
            {
                dd=1;
                mm++;
            }
            else
                dd++;
		}
        else if(mm==1)
        {
			if((yy%4==0)&&(yy%100!=0)&&(yy%400==0))     // Leap Year
            {
                if(dd==29)
                {
                    mm++;
                    dd = 1;
                }
                else dd++;
            }
            else
            {
                if(dd==28)
                {
                    mm++;
                    dd = 1;
                }
                else dd++;
            }
		}
		month = mm;
		year = yy;
		date = dd;

        if(day==6)
            day = 0;
        else
            day++;
    }

    public int compareTo(Date d)
    {
        if((date==d.date)&&(month==d.month)&&(year==d.year))
            return 0;

        if(year>d.year) return 1;
        else if(year<d.year) return -1;
        else if(year==d.year)
        {
			if(month>d.month)return 1;
			else if(month<d.month)return -1;
			else if(month==d.month)
			{
				if(date>d.date) return 1;
				else if(date<d.date) return -1;
				if(date==d.date) return 0;
			}
       	}
       	return 2;
    }

    public Date copy()
    {
		Date d = new Date();
		d.date = date;
		d.month = month;
		d.year = year;
		d.day = day;
		return d;
	}



}
