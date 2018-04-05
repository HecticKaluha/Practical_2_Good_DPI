package forms.loanbroker;

import connection.MessageReceiverGateway;
import connection.MessageSenderGateway;
import mix.messaging.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;

import javax.jms.*;

public class ClientAppGateway implements MessageListener{

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private String channelName;
    private LoanBrokerFrame loanBrokerFrame;

    public ClientAppGateway(String channelName) {
        this.channelName = channelName;
        sender = new MessageSenderGateway(channelName);
        receiver = new MessageReceiverGateway("ClientToBroker");
        receiver.setListerner(this);
    }
    public void sendLoanReply(RequestReply<BankInterestRequest, BankInterestReply> bankreply)
    {
        sender.send(sender.createObjectMessage(bankreply));
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                System.out.print("\n I got your Loanrequest! The Request was: " + message.toString());

                ObjectMessage om = (ObjectMessage)(message);
                LoanRequest lr = (LoanRequest)(om.getObject());
                loanBrokerFrame.add(lr);

                loanBrokerFrame.senToBank(lr);
            }
            else{
                System.out.print("\n Something went wrong while de-enqueueing the message");
            }
        } catch (JMSException e) {
            System.out.print("\n Something went wrong when trying to serialize : " + e.getMessage());
        }
    }

    public void setLoanBrokerFrame(LoanBrokerFrame lbf) {
        this.loanBrokerFrame = lbf;
    }






}
