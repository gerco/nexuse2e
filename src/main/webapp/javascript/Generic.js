<!-- *************************************************************************  -->
<!-- ***************************** Generic JavaScript ************************  -->
<!-- *************************************************************************  -->
function checkKey(e) {
  if (!e) {
    e = window.event;
  }
  if (e.which) {
    code = e.which;
  } else if (e.keyCode) {
    code = e.keyCode;
  }
  if (code == 13) {
    return false;
  }
  return true;
}
<!-- *************************************************************************  -->
function confirmDelete(deleteMsg) {
  var agree=confirm(deleteMsg);
  if (agree)
    return true;
  else
    return false;
}
<!-- *************************************************************************  -->
function displayErrors(missinginfo) {
  if (missinginfo !=  "" ) {
    missinginfo = "Please correct the following error(s):\n\n" + missinginfo;
    alert(missinginfo);
    return false;
  } else {
    return true;
  }
}
<!-- *************************************************************************  -->
function passwordsMatch(pwd1, pwd2) {
  if (pwd1 !=  pwd2 ) {
    return false;
  } else {
    return true;
  }
}
<!-- *************************************************************************  -->
function updatedAlert(msg) {
  alert(msg);
}
<!-- *************************************************************************  -->
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}
<!-- *************************************************************************  -->
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}
<!-- *************************************************************************  -->
function MM_findObj(n, d) { //v3.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document); return x;
}
<!-- *************************************************************************  -->
function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
<!-- *************************************************************************  -->
function isNumeric(inputField) {
  var numbers = "0123456789";
  for (var i = 0; i < inputField.length; i++) {
    if (numbers.indexOf(inputField.charAt(i)) == -1) {
      return false;
    }
  }
  return true;
}
<!-- *************************************************************************  -->
function embeddedSpaces(inputField) {
  while(''+inputField.value.charAt(0)==' ')
    inputField.value=inputField.value.substring(1,inputField.value.length);           
  while(''+inputField.value.charAt(inputField.value.length-1)==' ')
    inputField.value=inputField.value.substring(0,inputField.value.length-1);           
  if (inputField.value.indexOf(' ') == -1) 
    return false;
  else
    return true;
}
<!-- *************************************************************************  -->
function emailCheck (emailStr) {
  /* The following pattern is used to check if the entered e-mail address
     fits the user@domain format.  It also is used to separate the username
     from the domain. */
  var emailPat=/^(.+)@(.+)$/
  /* The following string represents the pattern for matching all special
     characters.  We don't want to allow special characters in the address. 
     These characters include ( ) < > @ , ; : \ " . [ ]    */
  var specialChars="\\(\\)<>@,;:\\\\\\\"\\.\\[\\]"
  /* The following string represents the range of characters allowed in a 
     username or domainname.  It really states which chars aren't allowed. */
  var validChars="\[^\\s" + specialChars + "\]"
  /* The following pattern applies if the "user" is a quoted string (in
     which case, there are no rules about which characters are allowed
     and which aren't; anything goes).  E.g. "jiminy cricket"@disney.com
     is a legal e-mail address. */
  var quotedUser="(\"[^\"]*\")"
  /* The following pattern applies for domains that are IP addresses,
     rather than symbolic names.  E.g. joe@[123.124.233.4] is a legal
     e-mail address. NOTE: The square brackets are required. */
  var ipDomainPat=/^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/
  /* The following string represents an atom (basically a series of
     non-special characters.) */
  var atom=validChars + '+'
  /* The following string represents one word in the typical username.
     For example, in john.doe@somewhere.com, john and doe are words.
     Basically, a word is either an atom or quoted string. */
  var word="(" + atom + "|" + quotedUser + ")"
  // The following pattern describes the structure of the user
  var userPat=new RegExp("^" + word + "(\\." + word + ")*$")
  /* The following pattern describes the structure of a normal symbolic
     domain, as opposed to ipDomainPat, shown above. */
  var domainPat=new RegExp("^" + atom + "(\\." + atom +")*$")


  /* Finally, let's start trying to figure out if the supplied address is
     valid. */

  /* If it is blank, reject is straight away */
  if (emailStr == null) {
    return "Email address must be entered.";
  }

  /* Begin with the coarse pattern to simply break up user@domain into
     different pieces that are easy to analyze. */
  var matchArray=emailStr.match(emailPat)
  if (matchArray==null) {
    /* Too many/few @'s or something; basically, this address doesn't
       even fit the general mould of a valid e-mail address. */
    return "Email address must be entered.  Also, verify @ and '.' symbols are correct.";
  }
  var user=matchArray[1]
  var domain=matchArray[2]

  // See if "user" is valid 
  if (user.match(userPat)==null) {
      // user is not valid
      return "EMail username doesn't seem to be valid.";
  }

  /* if the e-mail address is at an IP address (as opposed to a symbolic
     host name) make sure the IP address is valid. */
  var IPArray=domain.match(ipDomainPat)
  if (IPArray!=null) {
      // this is an IP address
      for (var i=1;i<=4;i++) {
        if (IPArray[i]>255) {
      return "EMail Destination IP address is invalid!";
        }
      }
      return "";
  }

  // Domain is symbolic name
  var domainArray=domain.match(domainPat)
  if (domainArray==null) {
    return "EMail domain name doesn't seem to be valid.";
  }

  /* domain name seems valid, but now make sure that it ends in a
     three-letter word (like com, edu, gov) or a two-letter word,
     representing country (uk, nl), and that there's a hostname preceding 
     the domain or country. */

  /* Now we need to break up the domain to get a count of how many atoms
     it consists of. */
  var atomPat=new RegExp(atom,"g")
  var domArr=domain.match(atomPat)
  var len=domArr.length
  if (domArr[domArr.length-1].length<2 || 
      domArr[domArr.length-1].length>3) {
     // the address must end in a two letter or three letter word.
     return "EMail address must end in a three-letter domain, or two letter country.";
  }

  // Make sure there's a host name preceding the domain.
  if (len<2) {
     return "EMail address is missing a hostname!";
  }

  // If we've gotten this far, everything's valid!
  return "";
}
