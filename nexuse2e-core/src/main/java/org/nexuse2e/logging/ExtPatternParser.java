package org.nexuse2e.logging;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

public class ExtPatternParser extends PatternParser {

    private static final char CONVERSATION_CHAR = 'u';
    private static final char MESSAGE_CHAR      = 'v';

    public ExtPatternParser( String pattern ) {
        super( pattern );
    }

    public void finalizeConverter( char formatChar )
    {
        PatternConverter pc = null;
        switch ( formatChar )
        {
            case CONVERSATION_CHAR:
                pc = new ConversationPatternConverter( formattingInfo );
                currentLiteral.setLength( 0 );
                addConverter( pc );
                break;
            case MESSAGE_CHAR:
                pc = new MessagePatternConverter( formattingInfo );
                currentLiteral.setLength( 0 );
                addConverter( pc );
                break;
            default:
                super.finalizeConverter( formatChar );

        }

    }

    private static abstract class ExtPatternConverter extends PatternConverter
    {
        ExtPatternConverter( FormattingInfo formattingInfo )
        {
            super( formattingInfo );
        }

        public String convert( LoggingEvent event )
        {
            String result = null;
            if ( event.getMessage() instanceof LogMessage )
            {
                result = convert( event );
            }
            return result;
        }
    }

    private static class ConversationPatternConverter extends ExtPatternConverter
    {
        ConversationPatternConverter( FormattingInfo formatInfo )
        {
            super( formatInfo );
        }

        public String convert( LoggingEvent event )
        {
            if(event.getMessage() instanceof LogMessage) {
                return ((LogMessage)event.getMessage()).getConversationId();
            }
            return "";
        }
    }
    
    private static class MessagePatternConverter extends ExtPatternConverter
    {
        MessagePatternConverter( FormattingInfo formatInfo )
        {
            super( formatInfo );
        }

        public String convert( LoggingEvent event )
        {
            if(event.getMessage() instanceof LogMessage) {
                return ((LogMessage)event.getMessage()).getMessageId();
            }
            return "";
        }
    }

}
