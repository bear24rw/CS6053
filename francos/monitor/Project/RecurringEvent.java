// RecurringEvent.java                                           -*- Java -*-
//    An class for creating events that keep happening
//
// COPYRIGHT (C) 1998, Bradley M. Kuhn
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of
// the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
//
// Written   :   Bradley M. Kuhn         University of Cincinnati
//   By          
//
// Written   :   John Franco
//   For         Special Topics: Java Programming
//               15-625-595-001, Fall 1998
// RCS       :
//
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/RecurringEvent.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: RecurringEvent.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.4  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.3  1998/11/30 14:12:09  bkuhn
//   -- fixed error message in exception
//
// Revision 0.2  1998/11/30 10:13:54  bkuhn
//   -- made it serializable because of wars
//
// Revision 0.1  1998/11/13 09:13:36  bkuhn
//   # initial version
//
//
import java.util.*;
import java.io.Serializable;

/*****************************************************************************/
// Note that we Serialize this class....This is  for convience only!
// Make sure you make a new one of these instead when you bring this back in

class RecurringEvent extends Thread implements Serializable
{
      Class recurringClass;
      long  milliseconds;
      Object initializingObject;

      /**********************************************************************/
      /* Note that myClass *must* implement RecurringEvent
       */
      RecurringEvent(Class myClass, Object o, double seconds)
         throws ClassNotFoundException
      {
         milliseconds = (long) (seconds * 1000);

         initializingObject = o;

         recurringClass = myClass;

         Class[] interfaces = myClass.getInterfaces();
         boolean haveRecurredEvent = false;

         for(int ii = 0; ii < interfaces.length; ii++)
            if (interfaces[ii] == Class.forName("RecurredEvent"))
            {
               haveRecurredEvent = true;
               break;
            }

         if (! haveRecurredEvent)
            throw new ClassNotFoundException(
               "does not implement RecurredEvent!");
         
         setPriority(NORM_PRIORITY - 3);
      }
      /**********************************************************************/
      public void run()
      {
         while (true)
         {
            RecurredEvent r;
            try
            {
               r = (RecurredEvent) recurringClass.newInstance();
            }
            catch (Exception e)
            {
					Date date = new Date();
					String ss = date.toString();
               System.out.println(ss+": MONITOR: unable to instantiate, " +
                                  recurringClass.getName() + 
                                  " in RecurringEvent");
               break;
            }

            r.initialize(initializingObject);

            r.setPriority(this.getPriority());
            r.run();

            // wait for this fellow to terminate
            try { r.join(); }
            catch(InterruptedException ie) { }

            // sleep() for the required amount
            performSleep();
         }
      }
      /**********************************************************************/
      void performSleep()
      {
         try
         {
            sleep(milliseconds);
         }
         catch (InterruptedException  ie) { }
      }
}

/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
