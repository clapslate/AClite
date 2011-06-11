/*
*Copyright 2010, Mohit Gvalani

*This file is part of AC lite.
*AC lite is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*AC lite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*You should have received a copy of the GNU General Public License along with AC lite.  If not, see <http://www.gnu.org/licenses/>.
*/




package display;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Display;

public class Instructions extends Form implements CommandListener
{
    AC ac;
    Displayable dis;
    Command back = new Command("Back", Command.BACK, 0);

    public Instructions(String label, AC ac, Displayable d)
    {
        super(label);
        this.ac = ac;
        dis = d;
        append(new StringItem("Attendance Calculator", "Instructions are too long to be included here. Visit http://mohit-gvalani.appspot.com/mohit/AClite.html to view them."));
        addCommand(back);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d)
    {
        Display.getDisplay(ac).setCurrent(dis);
    }

}
