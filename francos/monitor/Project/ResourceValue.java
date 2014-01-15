// ResourceValue.java                                             -*- Java -*-
//     The overall values of various resources
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/ResourceValue.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: ResourceValue.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.5  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.4  1998/11/30 06:50:39  bkuhn
//   -- made it serializable
//
// Revision 0.3  1998/11/29 12:02:04  bkuhn
//    # changed some types from int to long
//    -- added getMonitorAmount()
//
// Revision 0.2  1998/11/16 07:25:43  bkuhn
//   -- added changeMonitorAmount() so we could give the resources
//      for those who weren't awarded to the Monitor
//
// Revision 0.1  1998/11/13 10:56:33  bkuhn
//   # initial version
//

import java.util.*;
import java.io.Serializable;
/*****************************************************************************/
class ResourceValue implements Serializable {
   String name;
   long totalInSystem;
   long totalHeldByMonitor;
   double marketValue;
   /*********************************************************************/
   ResourceValue(String n) {
      name = n;
      marketValue = 1;
      totalHeldByMonitor = totalInSystem = 0;
   }
   ResourceValue(String n, double r) {
      name = n;
      marketValue = r;
      totalHeldByMonitor = totalInSystem = 0;
   }
   /*********************************************************************/
   synchronized double  getMarketValue() {
      return marketValue;
   }
   /*********************************************************************/
   synchronized long getAmount() {
      return totalInSystem;
   }
   /*********************************************************************/
   synchronized void  setMarketValue(double m) {
      marketValue = m;
   }
   /*********************************************************************/
   synchronized long  changeAmount(long t) {
      totalInSystem += t;
      if (totalInSystem < 0)
         totalInSystem = 0;
      
      return totalInSystem;
   }
   synchronized long  decrementAmount() {
      totalInSystem *= 0.99;
      return totalInSystem;
   }
   /*********************************************************************/
   synchronized public long getMonitorAmount() {
      return totalHeldByMonitor;
   }
   /*********************************************************************/
   synchronized public long changeMonitorAmount(long t) {
      totalHeldByMonitor += t;
      if (totalHeldByMonitor < 0)
         totalHeldByMonitor = 0;
      
      changeAmount(t);
      
      return totalHeldByMonitor;
   }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
