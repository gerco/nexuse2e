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
