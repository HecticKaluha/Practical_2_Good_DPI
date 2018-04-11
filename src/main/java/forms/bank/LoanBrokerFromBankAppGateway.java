package forms.bank;

import connection.MessageReceiverGateway;
import connection.MessageSenderGateway;
import forms.loanbroker.LoanBrokerFrame;
import mix.messaging.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;

import javax.jms.*;

public class LoanBrokerFromBankAppGateway implements MessageListener {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private String channelName;
    private IBank bankFrame;

    public LoanBrokerFromBankAppGateway(String BrokerToBank) {
        this.channelName = channelName;
        sender = new MessageSenderGateway("BankToBroker");
        receiver = new MessageReceiverGateway(BrokerToBank);
        receiver.setListerner(this);
    }
    public void sendBankReply(RequestReply<BankInterestRequest, BankInterestReply> bankreply)
    {
        sender.send(sender.createObjectMessage(bankreply));
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                System.out.print("\n I got your BankInterestRequest! The BankInterestRequest was: " + message.toString());

                BankInterestRequest bir = (BankInterestRequest)((ObjectMessage) message).getObject();
                bankFrame.add(bir);
            }
            else{
                System.out.print("\n Something went wrong while de-enqueueing the message");
            }
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setBankFrame(IBank bf) {
        this.bankFrame = bf;
    }
}
