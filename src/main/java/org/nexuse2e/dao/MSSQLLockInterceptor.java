package org.nexuse2e.dao;



import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.HibernateException;
import org.hibernate.exception.LockAcquisitionException;




public class MSSQLLockInterceptor implements MethodInterceptor { //, ThrowsAdvice

    private int timeout = 3000;
    private int retries = 3; 
    
    public Object invoke( MethodInvocation invocation ) throws Throwable {
        
        Object rval = null;
        Exception ex = null;
        for(int i = 0; i<getRetries(); i++) {
            try {
//                System.out.println("invocation: "+invocation);
                rval = invocation.proceed();
                return rval;
            } catch ( Exception e ) {
                if(e.getCause() instanceof LockAcquisitionException) { // org.hibernate.exception.LockAcquisitionException
                    
                    Thread.sleep( getTimeout() );
                    System.out.println("retrying");
                    continue;
                    
                }
                ex = e;
                throw e;
            }
        }
        throw new HibernateException("Lock Exception retries exceeded",ex);
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
     */
    public void setRetries( int retries ) {
    
        this.retries = retries;
    }
}
