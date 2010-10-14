package org.nexuse2e.logging;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;


public class ExtPatternLayout extends PatternLayout{
    public ExtPatternLayout() 

    {

       this(DEFAULT_CONVERSION_PATTERN);

    }



    public ExtPatternLayout(String pattern) 

    {

       super(pattern);

    }

     

    public PatternParser createPatternParser(String pattern) 

    {

       PatternParser result;

       if ( pattern == null )

          result = new ExtPatternParser( DEFAULT_CONVERSION_PATTERN );

       else

          result = new ExtPatternParser ( pattern );



       return result;

   }

}
