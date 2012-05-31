import org.nexuse2e.Engine;
import org.nexuse2e.controller.TransactionService;
import org.nexuse2e.patch.Patch;
import org.nexuse2e.patch.PatchException;
import org.nexuse2e.patch.PatchReporter;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;


/**
 * Created: 02.01.2012
 * TODO Class documentation
 *
 * @author jr
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class CreateTestConvPatch implements Patch {

    private PatchReporter patchReporter;
    
    /* (non-Javadoc)
     * @see org.nexuse2e.patch.Patch#executePatch()
     */
    public void executePatch() throws PatchException {
        
        try {
            String choreography = "GenericFileSoap";
            String action = "SendFile";
            String partner = "0612253000005"; // IRM
            String xml = "<test/>";
            TransactionService ts = Engine.getInstance().getTransactionService();
            String messageId = Engine.getInstance().getIdGenerator("messageId").getId();
            String conversationId = Engine.getInstance().getIdGenerator("conversationId").getId();
            
            MessagePojo message = ts.createMessage(
                  messageId,
                  conversationId,
                  action,
                  partner,
                  choreography,
                  1 // message type (NORMAL)
                );
            message.setOutbound(false);
            MessagePayloadPojo payload = new MessagePayloadPojo();
            payload.setPayloadData(xml.getBytes());
            payload.setMessage(message);
            payload.setContentId(message.getMessageId() + "__body1");
            payload.setCreatedDate(message.getCreatedDate());
            payload.setModifiedDate(message.getModifiedDate());
            payload.setMimeType("text/xml");

            message.getMessagePayloads().add(payload);
            message.getConversation().setCurrentAction(message.getAction());
            
            ts.updateTransaction(message, true);
            patchReporter.info("Successfully created new conversation with ID " + conversationId + ", message ID is " + messageId);
        } catch (Exception ex) {
            patchReporter.error(ex.getMessage());
            throw new PatchException(ex);
        }
     // BEGIN GROOVY SCRIPT
     // You can change the following testing parameters
//     def choreography = "GenericFileSoap"
//     def action = "SendFile"
//     def partner = "0612253000005" // IRM
//     def xml = "<test/>"
//    
//    
//     def ts = org.nexuse2e.Engine.instance.transactionService
//     def messageId = org.nexuse2e.Engine.instance.getIdGenerator("messageId").id
//     def conversationId = org.nexuse2e.Engine.instance.getIdGenerator("conversationId").id
//    
//     def message = ts.createMessage(
//       messageId,
//       conversationId,
//       action,
//       partner,
//       choreography,
//       1 // message type (NORMAL)
//     )
//    
//     message.outbound = false
//    
//     def payload = new org.nexuse2e.pojo.MessagePayloadPojo()
//     payload.payloadData = xml.bytes
//     payload.message = message
//     payload.contentId = message.messageId + "__body1"
//     payload.createdDate = message.createdDate
//     payload.modifiedDate = message.modifiedDate
//     payload.mimeType = "text/xml"
//    
//     message.messagePayloads.add(payload)
//    
//     message.conversation.currentAction = message.action
//     ts.updateTransaction(message, true)
     // END GROOVY SCRIPT

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.patch.Patch#getVersionInformation()
     */
    public String getVersionInformation() {
        return "0.1";
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.patch.Patch#getPatchName()
     */
    public String getPatchName() {
        return "CreateTestConvPatch";
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.patch.Patch#getPatchDescription()
     */
    public String getPatchDescription() {
        return "Creates a test conversation";
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.patch.Patch#setPatchReporter(org.nexuse2e.patch.PatchReporter)
     */
    public void setPatchReporter(PatchReporter patchReporter) {
        this.patchReporter = patchReporter;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.patch.Patch#isExecutedSuccessfully()
     */
    public boolean isExecutedSuccessfully() {
        return true;
    }
}
