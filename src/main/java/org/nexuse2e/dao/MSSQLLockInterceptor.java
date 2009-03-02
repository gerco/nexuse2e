/**
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
package org.nexuse2e.dao;



import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.hibernate.exception.LockAcquisitionException;


/**
 * Implements retry strategy for MS SQLServer deadlocks. 
 * @author Guido Esch, Sebastian Schulze
 * @date 25.02.2009
 */
public class MSSQLLockInterceptor implements MethodInterceptor { //, ThrowsAdvice

    Logger LOG = Logger.getLogger( MSSQLLockInterceptor.class );
    private int timeout = 3000;
    private int retries = 3; 
    
    public Object invoke( MethodInvocation invocation ) throws Throwable {
        
        Object rval = null;
        Exception ex = null;
        
        for(int i = 0; i <= getRetries(); i++) {
            boolean lockFound = false;
            try {
                rval = invocation.proceed();
                return rval;
            } catch ( Exception e ) {
                ex = e;
                Throwable cause = e;
                while(cause != null && !lockFound) {
                    if(cause instanceof LockAcquisitionException) { // org.hibernate.exception.LockAcquisitionException
                        lockFound = true;
                        Thread.sleep( getTimeout() );
                        LOG.trace( "LockAcquisitionException occured, retrying" );
                    }
                    cause = cause.getCause();
                }
                if(!lockFound) {
                    throw e;
                }
            }
        }
        //  ex will not be null, because of the return statement above
        throw ex;
    }
//    public void afterThrowing(Method method, Object[] args, Object target, Exception ex) {
//        
//        for ( int i = 0; i < 20; i++ ) {
//            try {
//                method.invoke( target, args );
//                System.out.println("retrying");
//                Thread.sleep( 2000 );
//            } catch ( IllegalArgumentException e ) {
//                throw e;
//            } catch ( IllegalAccessException e ) {
//                throw new RuntimeException("Illegal Access",e);
//            } catch ( InvocationTargetException e ) {
//                throw new RuntimeException("Invocation Target",e);
//            } catch (Exception e) {
//                continue;
//            }
//        }
//        throw new RuntimeException("retry failed");
//    }

    
    /**
     * @return the timeout
     */
    public int getTimeout() {
    
        return timeout;
    }

    
    /**
     * @param timeout the timeout to set
     */
    public void setTimeout( int timeout ) {
    
        this.timeout = timeout;
    }

    
    /**
     * @return the retries
     */
    public int getRetries() {
    
        return retries;
    }

    
    /**
     * @param retries the retries to set
     * @throw IllegalArgumentException, if <code>retries</code> &lt; 0.
     */
    public void setRetries( int retries ) {
    
    	if ( retries < 0 ) {
    		throw new IllegalArgumentException( "retries must not be negative" );
    	}
        this.retries = retries;
    }
}
