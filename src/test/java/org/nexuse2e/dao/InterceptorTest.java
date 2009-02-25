package org.nexuse2e.dao;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.exception.LockAcquisitionException;
import org.junit.Test;
import org.nexuse2e.NexusException;

import static org.mockito.Mockito.*;


public class InterceptorTest {
    @Test 
    public void invocationThrowsException() throws Throwable {
        
        MethodInvocation invocationMock = mock( MethodInvocation.class );
        when( invocationMock.proceed() ).thenThrow( new RuntimeException( "Message", new LockAcquisitionException("bla",new SQLException()) ) );
        MSSQLLockInterceptor interceptor = new MSSQLLockInterceptor();
        interceptor.invoke( invocationMock );
        verify(invocationMock, times(2)).proceed();

    }
}
