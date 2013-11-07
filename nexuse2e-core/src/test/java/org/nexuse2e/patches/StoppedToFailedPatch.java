package org.nexuse2e.patches;

import java.util.Date;
import java.util.List;

import org.nexuse2e.Engine;
import org.nexuse2e.MessageStatus;
import org.nexuse2e.NexusException;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.patch.Patch;
import org.nexuse2e.patch.PatchException;
import org.nexuse2e.patch.PatchReporter;
import org.nexuse2e.pojo.MessagePojo;

/**
 * Path that sets STOPPED messages for the last 72 hours to FAILED (so they can be re-queued although there is a bug in NEXUS).
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class StoppedToFailedPatch implements Patch {

    private PatchReporter patchReporter;
    private boolean success = false;
    
    public void executePatch() throws PatchException {
        try {
            Date start = new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000 * 3));
            patchReporter.info("Fetching STOPPED messages after " + start);
            List<MessagePojo> messages = Engine.getInstance().getTransactionService().getMessagesForReport(
                Integer.toString(MessageStatus.STOPPED.getOrdinal()), 0, 0, null, null, null, start, null, 100000, 0, 1, true);
            patchReporter.info("Found " + messages.size() + " messages, setting them to from STOPPED to FAILED");
            for (MessagePojo mp : messages) {
                patchReporter.info("    Setting conversation " + mp.getConversation().getConversationId() + ", message " + mp.getMessageId() + " to FAILED");
                mp.setStatus(MessageStatus.FAILED.getOrdinal());
                Engine.getInstance().getTransactionService().updateTransaction(mp, true);
            }
            patchReporter.info("Done. Please Re-queue messages listed above.");
        } catch (NexusException nex) {
            patchReporter.error(nex.getMessage());
            throw new PatchException(nex);
        } catch (StateTransitionException stex) {
            patchReporter.error(stex.getMessage());
            throw new PatchException(stex);
        }
    }

    public String getPatchDescription() {
        return "Sets all messages that have been set to STOPPED state within the<br>last 72 hours to FAILED so that they can be re-queued";
    }

    public String getPatchName() {
        return "Set messages STOPPED within the last 72 hours to FAILED";
    }

    public String getVersionInformation() {
        return "1.0";
    }

    public boolean isExecutedSuccessfully() {
        return success;
    }

    public void setPatchReporter(PatchReporter patchReporter) {
        this.patchReporter = patchReporter;
    }

}
