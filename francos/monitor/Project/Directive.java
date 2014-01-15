// Directive.java                                                -*- Java -*-
//   Directives avaiable to the monitor
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/Directive.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: Directive.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.7  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.6  1998/11/29 12:01:48  bkuhn
//   -- added WAR_TRUCE_OFFER_DIRECTIVE
//
// Revision 0.5  1998/11/24 03:25:47  bkuhn
//   -- added WAR_DECLARATION_DIRECTIVE
//
// Revision 0.4  1998/11/18 06:44:55  jvf
//   -- added TRANSFER: Directive
//
// Revision 0.3  1998/11/09 04:52:49  bkuhn
//   -- added PARTICIPANT_PASSWORD_CHECKSUM, MONITOR_PASSWORD, WAITING,
//      and COMMAND_ERROR
//   -- removed ARE_YOU_ALIVE, that's now an exchange, not a
//      directive
//
// Revision 0.2  1998/11/03 06:15:53  bkuhn
//    # cosmetic changes
//

import java.util.*;
import java.io.*;

/*****************************************************************************/
class Directive {
   static final String rcsid = "$Revision: 1.1 $";

   public static final String COMMENT_DIRECTIVE       = "COMMENT: ";
   public static final String COMMAND_ERROR_DIRECTIVE = "COMMAND_ERROR: ";
   public static final String REQUIRE_DIRECTIVE       = "REQUIRE: ";
   public static final String RESULT_DIRECTIVE        = "RESULT: ";
   public static final String WAITING_DIRECTIVE       = "WAITING: ";
   public static final String MONITOR_PASSWORD_DIRECTIVE 
      = "MONITOR_PASSWORD: ";
   public static final String PARTICIPANT_PASSWORD_CHECKSUM_DIRECTIVE 
      = "PARTICIPANT_PASSWORD_CHECKSUM: ";
   public static final String TRANSFER_DIRECTIVE
      = "TRANSFER: ";
   public static final String WAR_DECLARATION_DIRECTIVE
      = "WAR_DECLARATION: ";
   public static final String WAR_TRUCE_OFFER_DIRECTIVE
      = "WAR_TRUCE_OFFERED: ";

}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/

