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
package org.nexuse2e.service;

import java.util.Date;
import java.util.List;

import org.nexuse2e.Constants.BeanStatus;

/**
 * Interface for a service that can schedule tasks at a fixed rate.
 * 
 * @author jonas.reese
 */
public interface SchedulingService extends Service {

    /**
     * Registers a <code>SchedulerClient</code> for this <code>SchedulingService</code>.
     * It will be invoked once per given interval when the <code>SchedulingService</code>
     * is in {@link BeanStatus#STARTED} state.
     * @param client The client to register. Must not be <code>null</code>.
     * @param millseconds The interval the client will be invoked in.
     * @throws IllegalArgumentException if <code>client</code> is <code>null</code> or
     * an illegal interval was specified.
     */
    public abstract void registerClient( SchedulerClient client, long millseconds ) throws IllegalArgumentException;

    
    /**
     * Registers a <code>SchedulerClient</code> for this <code>SchedulingService</code>.
     * It will be invoked based on the given cron pattern when the <code>SchedulingService</code>
     * is in {@link BeanStatus#STARTED} state.
     * @param client The client to register. Must not be <code>null</code>.
     * @param pattern The cron based pattern.
     * @throws IllegalArgumentException if <code>client</code> is <code>null</code> or
     * an illegal interval was specified.
     */
    public abstract void registerClient( SchedulerClient client, String pattern ) throws IllegalArgumentException;

    /**
     * Registers a <code>SchedulerClient</code> for this <code>SchedulingService</code>.
     * It will be invoked once per day at the specified time when the <code>SchedulingService</code>
     * is in {@link BeanStatus#STARTED} state.
     * @param client The client to register. Must not be <code>null</code>.
     * @param time The time (date part must be today's date).
     * @throws IllegalArgumentException if <code>client</code> is <code>null</code> or
     * an illegal interval was specified.
     */
    public abstract void registerClient( SchedulerClient client, List<Date> times ) throws IllegalArgumentException;

    /**
     * Deregisters a <code>SchedulerClient</code> from this <code>SchedulingService</code>.
     * @param client The client that shall not be invoked any more. Must not be <code>null</code>.
     * @throws IllegalArgumentException if <code>client</code> is <code>null</code>.
     */
    public abstract void deregisterClient( SchedulerClient client) throws IllegalArgumentException;
}