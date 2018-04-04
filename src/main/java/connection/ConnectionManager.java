package connection;

import exception.CouldNotCreateConnectionException;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

public class ConnectionManager {

    private static ConnectionManager instance;


    private ConnectionManager() {

    }

    public static ConnectionManager getInstance()
    {
        if(instance != null)
        {
            instance = new ConnectionManager();
        }
        return instance;

    }
    public static Connection getNewConnection() throws CouldNotCreateConnectionException {
        Connection connection;
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createConnection();
        }
        catch (JMSException e){
            throw new CouldNotCreateConnectionException(e.getMessage());
        }
        return connection;
    }


}