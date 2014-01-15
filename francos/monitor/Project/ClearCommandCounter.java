// ClearCommandCounter.java                                        -*- Java -*-
//    Clear command counter
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/ClearCommandCounter.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: ClearCommandCounter.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.2  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.1  1998/12/08 22:47:30  bkuhn
//   -- minor fixes
//


import java.util.Enumeration;
import java.io.*;
/*****************************************************************************/
class ClearCommandCounter extends Thread implements RecurredEvent
{
      PlayerCommandCounter playerCommandCounter;
      static final String rcsid = "$Revision: 1.1 $";

      ClearCommandCounter()
      {
      }
      /**********************************************************************/
      public void initialize(Object o)
      {
         playerCommandCounter = (PlayerCommandCounter) o;
      }
      /**********************************************************************/
      public void run()
      {
         Player curPlayer;

         System.out.println("CLEAR_COMMAND_COUNTER: Beginning...");

         // First, if we have any players, update the economy

         playerCommandCounter.clear();

         System.out.println("CLEAR_COMMAND_COUNTER: Ending...");
      }
      /**********************************************************************/
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
