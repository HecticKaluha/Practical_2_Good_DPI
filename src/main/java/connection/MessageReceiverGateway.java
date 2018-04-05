package connection;

import exception.CouldNotCreateConnectionException;

import javax.jms.*;

public class MessageReceiverGateway {
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageConsumer consumer;

    public MessageReceiverGateway(String channelName) {
        try
        {
            this.connection = ConnectionManager.getNewConnection();
            connection.start();
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.destination = session.createQueue(channelName);
            this.consumer = session.createConsumer(destination);
        }
        catch(CouldNotCreateConnectionException | JMSException e)
        {
            System.out.print("Something went wrong while creating the MessageReceiverGateway because of " + e.getMessage());
        }
    }

    public void setListerner(MessageListener ml)
    {
        try
        {
            consumer.setMessageListener(ml);
        }
        catch(JMSException e)
        {
            System.out.print("Something went wrong while setting the messagelistener because of " + e.getMessage());
        }

    }
}
