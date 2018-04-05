package forms.loanclient;

import connection.MessageReceiverGateway;
import connection.MessageSenderGateway;
import mix.messaging.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;

import javax.jms.*;

public class LoanBrokerAppGateway implements MessageListener{

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private String channelName;
    private LoanClientFrame loanClientFrame;

    public LoanBrokerAppGateway(String channelName) {
        this.channelName = channelName;
        sender = new MessageSenderGateway(channelName);
        receiver = new MessageReceiverGateway("BrokerToClient");
        receiver.setListerner(this);
    }
    public void applyForLoan(LoanRequest loanrequest)
    {
        sender.send(sender.createObjectMessage(loanrequest));
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                System.out.print("\n I got your BrokerReply! The BrokerReply was: " + message.toString());

                RequestReply<BankInterestRequest, BankInterestReply> rr = (RequestReply<BankInterestRequest, BankInterestReply>)((ObjectMessage) message).getObject();

                LoanRequest lr = new LoanRequest();
                lr.setAmount(rr.getRequest().getAmount());
                lr.setTime(rr.getRequest().getTime());

                loanClientFrame.add(lr, rr.getReply());
            }
            else if (message instanceof TextMessage)
            {
                System.out.print("\n it was a textmessage : " + ((TextMessage) message).getText());
            }
            else{
                System.out.print("\n Something went wrong while de-enqueueing the message");
            }
        } catch (JMSException e) {
            System.out.print("\n Something went wrong: " + e.getMessage());
        }
    }

    public void setLoanClientFrame(LoanClientFrame lcf) {
        this.loanClientFrame = lcf;
    }
}
