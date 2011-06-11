

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
