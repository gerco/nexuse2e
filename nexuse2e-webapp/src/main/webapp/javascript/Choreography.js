/*
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
<!-- *************************************************************************  -->
<!-- ***************************** Choreography ******************************  -->
<!-- *************************************************************************  -->
function choreographyCheckFields() {
  if (document.form1.URI.value ==  "") {
    missinginfo ="The TPA URI must be entered.";
    alert(missinginfo);
    document.form1.URI.focus();
    return false;
  } else {  
  if (document.form1.Interval.value ==  "" || isNaN(document.form1.Interval.value)) {
    missinginfo ="The interval time for message retries must be entered and numeric (seconds).";
    alert(missinginfo);
    document.form1.Interval.focus();
    return false;
  } else {      
  if (document.form1.Retries.value ==  "" || isNaN(document.form1.Retries.value)) {
    missinginfo = "The number of retries must be entered and numeric.";
    alert(missinginfo);
    document.form1.Retries.focus();
    return false;
  } else {
  if (document.form1.Timeout.value ==  "" || isNaN(document.form1.Timeout.value)) {
    missinginfo = "The message timeout time must be entered and numeric (seconds).";
    alert(missinginfo);
    document.form1.Timeout.focus();
    return false;
  } else {    
  return true;
  }}}}
}
<!-- *************************************************************************  -->
  function checkChoreographyID() {
    if (document.choreographyForm.choreographyName.value ==  "" || embeddedSpaces(document.choreographyForm.choreographyName)) {
      missinginfo = "A Choreography name must be entered, without embedded spaces.";
      alert(missinginfo);
      document.choreographyForm.choreographyName.focus();
      return false;
    } else {    
      return true;
    }
  }
<!-- *************************************************************************  -->
