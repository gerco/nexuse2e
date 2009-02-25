package org.nexuse2e.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.exception.LockAcquisitionException;
import org.junit.Test;

public class InterceptorTest {
	@Test
	public void invocationThrowsLockAcquisitionException() throws Throwable {

		MethodInvocation invocationMock = mock(MethodInvocation.class);
		when(invocationMock.proceed()).thenThrow(
				new RuntimeException("Message", new LockAcquisitionException(
						"bla", new SQLException())));
		MSSQLLockInterceptor interceptor = new MSSQLLockInterceptor();
		interceptor.setTimeout(0);
		interceptor.setRetries(3);
		try {
			interceptor.invoke(invocationMock);
			fail( "Exception expected" );
		} catch (Exception e) {
			
		}
		verify(invocationMock, times(4)).proceed();

	}

	@Test
	public void invocationThrowsNoException() throws Throwable {

		MethodInvocation invocationMock = mock(MethodInvocation.class);
		when(invocationMock.proceed()).thenReturn(new Object());
		MSSQLLockInterceptor interceptor = new MSSQLLockInterceptor();
		interceptor.setTimeout(0);
		interceptor.setRetries(3);
		try {
			interceptor.invoke(invocationMock);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		verify(invocationMock, times(1)).proceed();
	}

	public void invocationThrowsNoLockAcquisitionException() throws Throwable {

		MethodInvocation invocationMock = mock(MethodInvocation.class);
		when(invocationMock.proceed()).thenThrow(
				new RuntimeException("Message", new SQLException() ) );
		MSSQLLockInterceptor interceptor = new MSSQLLockInterceptor();
		interceptor.setTimeout(0);
		interceptor.setRetries(3);
		try {
			interceptor.invoke(invocationMock);
			fail( "Exception expected" );
		} catch (Exception e) {
			assertTrue( e instanceof RuntimeException );
			assertEquals( e.getMessage(), "Message" );
			assertTrue( e.getCause() instanceof SQLException );
		}
		verify(invocationMock, times(1)).proceed();

	}
	
	@Test
	public void invocationThrowsExceptionNoRetries() throws Throwable {

		MethodInvocation invocationMock = mock(MethodInvocation.class);
		when(invocationMock.proceed()).thenThrow(
				new RuntimeException("Message", new LockAcquisitionException(
						"bla", new SQLException())));
		MSSQLLockInterceptor interceptor = new MSSQLLockInterceptor();
		interceptor.setTimeout(0);
		interceptor.setRetries(0);
		try {
			interceptor.invoke(invocationMock);
			fail( "Exception expected" );
		} catch (Exception e) {
			assertTrue( e instanceof RuntimeException );
			assertEquals( e.getMessage(), "Message" );
			assertTrue( e.getCause() instanceof LockAcquisitionException );
		}
		verify(invocationMock, times(1)).proceed();

	}
}
