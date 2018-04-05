package forms.bank;

import connection.ConnectionManager;
import connection.MessageSenderGateway;
import exception.CouldNotCreateConnectionException;
import mix.messaging.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class BankLoanRequestListener implements MessageListener {
    private Session session;
    private boolean transacted = false;
    private MessageSenderGateway messageSenderGateway;
    private MessageProducer replyProducer;

    private ObjectMessage response =null;

    private JMSBankFrame bf;

    public BankLoanRequestListener() {

    }

    /*public void setupMessageQueueConsumer() {

        try {
            Connection connection = ConnectionManager.getNewConnection();
            connection.start();
            this.session = connection.createSession(this.transacted, ackMode);
            Destination adminQueue = this.session.createQueue(messageQueueName);


            this.replyProducer = this.session.createProducer(null);
            this.replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);


            MessageConsumer consumer = this.session.createConsumer(adminQueue);
            consumer.setMessageListener(this);
        } catch (JMSException | CouldNotCreateConnectionException e) {
            System.out.print("\n Something went wrong while setting up a connection: " + e.getMessage());
        }
    }*/

    @Override
    public void onMessage(Message message) {
            try {
                if (message instanceof ObjectMessage) {
                    System.out.print("\n I got your BrokerLoanRequest! The BrokerLoanRequest was: " + message.toString());
                    //TODO: set message to a line and add to scrollpane.

                    BankInterestRequest bir = (BankInterestRequest)((ObjectMessage) message).getObject();
                    bf.add(bir);
                }
                else{
                    System.out.print("\n Something went wrong while de-enqueueing the message");
                }
            }
            catch (JMSException e) {
                e.printStackTrace();
            }

    }


    public static void main(String[] args) {
        new BankLoanRequestListener();
    }

    public void sendResponse(RequestReply<BankInterestRequest, BankInterestReply> bankreply) {

        try {
            response = this.session.createObjectMessage(bankreply);
            //TODO: get correlationID from message from selectedline in scrollpane
            response.setJMSCorrelationID("BankReplyListener");
            //TODO: get replydestination from message from selected line in scrollpane
            System.out.print("\n Sending Bankinterestreply to broker: " + response.toString());
            Destination replyDestination = session.createQueue("BankLoanRequestReplyQueue");
            this.replyProducer.send(replyDestination, response);
        } catch (JMSException e) {
            System.out.print("\n" + e.getMessage());
        }
    }

    public JMSBankFrame getBf() {
        return bf;
    }

    public void setBf(JMSBankFrame bf) {
        this.bf = bf;
    }
}