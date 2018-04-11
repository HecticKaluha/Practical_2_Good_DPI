package forms.loanbroker;

import connection.MessageReceiverGateway;
import connection.MessageSenderGateway;
import forms.loanclient.LoanClientFrame;
import mix.messaging.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;

import javax.jms.*;

public class BankAppGateway implements MessageListener{

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private String channelName;
    private LoanBrokerFrame laonBrokerFrame;
    private AggregationProcessor aggregationProcessor;

    public BankAppGateway(String channelName) {
        this.channelName = channelName;
        sender = new MessageSenderGateway(channelName);
        receiver = new MessageReceiverGateway("BankToBroker");
        receiver.setListerner(this);
    }
    public BankAppGateway(AggregationProcessor aggregationProcessor)
    {
        receiver = new MessageReceiverGateway("BankToBroker");
        receiver.setListerner(this);
        this.aggregationProcessor = aggregationProcessor;
    }

    public void sendBankRequest(BankInterestRequest request)
    {
        request.setId(request.getTime());
        ObjectMessage om = sender.createObjectMessage(request);
        sender.send(om);
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                System.out.print("\n I got your BankReply! The Reply was: " + message.toString());
                RequestReply<BankInterestRequest, BankInterestReply> rr = (RequestReply<BankInterestRequest, BankInterestReply>) ((ObjectMessage) message).getObject();
                aggregationProcessor.setMessage(rr);


                /*LoanRequest lr = new LoanRequest();
                lr.setAmount(rr.getRequest().getAmount());
                lr.setTime(rr.getRequest().getTime());

                laonBrokerFrame.add(lr, rr.getReply());
                //send reply from bank to client
                laonBrokerFrame.sendToClient(rr);*/



                //sendReplyToClient(rr);
            } else {
                System.out.print("\n Something went wrong while de-enqueueing the message");
            }
        } catch (JMSException e) {
            System.out.print("Something went wrong: " + e.getMessage());
        }
    }

    public void setLoanBrokerFrame(LoanBrokerFrame lbf) {
        this.laonBrokerFrame = lbf;
    }

    public MessageSenderGateway getSender() {
        return sender;
    }

    public void setSender(MessageSenderGateway sender) {
        this.sender = sender;
    }

    public MessageReceiverGateway getReceiver() {
        return receiver;
    }

    public void setReceiver(MessageReceiverGateway receiver) {
        this.receiver = receiver;
    }
}
