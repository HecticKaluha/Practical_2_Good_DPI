package forms.bank;

import connection.ConnectionManager;
import exception.CouldNotCreateConnectionException;
import mix.messaging.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class BankLoanRequestListener implements MessageListener {
    private Session session;
    private boolean transacted = false;
    private MessageProducer replyProducer;

    private static final int ackMode;

    private static final String messageBrokerUrl;

    private static final String messageQueueName;

    private ObjectMessage response =null;

    private JMSBankFrame bf;

    static {
        messageBrokerUrl = "tcp://localhost:61616";
        messageQueueName = "BankLoanRequestQueue";
        ackMode = Session.AUTO_ACKNOWLEDGE;
    }

    private String correlationID = "BankLoanRequestListener";

    public BankLoanRequestListener() {
        /*try {
            //This message broker is embedded
            //BrokerService broker = new BrokerService();
            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.addConnector(messageBrokerUrl);
            broker.start();
        } catch (exception e) {
            //Handle the exception appropriately
        }*/
    }

    public void setupMessageQueueConsumer() {

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
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("\n JMS exception occured.  Shutting down client.");
    }

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