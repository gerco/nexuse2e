package org.nexuse2e.messaging.ebxml;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePojo;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


/**
 * Test class for {@link HTTPMessagePackager} frontend pipelet
 * <p/>
 * Created 23.02.2016.
 */
public class HTTPMessagePackagerTest {

    @Mock
    MessageContext      messageContext;
    @Mock
    MessagePojo         messagePojo;
    @Mock
    ListParameter       paramList;
    @Mock
    Map<String, Object> parameters;

    @InjectMocks
    HTTPMessagePackager classUnderTest = new HTTPMessagePackager();

    @Before
    public void setUp() throws Exception {
        when(parameters.get(anyString())).thenReturn(paramList);
        when(messagePojo.getType()).thenReturn(1); // 1 is the normal type for messages, TODO: Message type should really be an enum!
        when(messageContext.getMessagePojo()).thenReturn(messagePojo);
    }

    @Test
    public void testProcessMessage() throws Exception {
        MessageContext resultContext = classUnderTest.processMessage(messageContext);

        assertNotNull(resultContext);
    }

    @Test
    public void testGetContentId() throws Exception {

    }

    @Test
    public void testAfterPropertiesSet() throws Exception {

    }
}