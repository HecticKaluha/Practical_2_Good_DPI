package forms.loanbroker;

import mix.messaging.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;

import java.util.HashMap;
import java.util.Map;

public class AggregationProcessor {

    private LoanBrokerFrame frame;
    private Map<RequestReply<BankInterestRequest, BankInterestReply>, String> received;

    public AggregationProcessor(LoanBrokerFrame frame) {
        this.frame = frame;
        this.received = new HashMap<>();
    }

    public void setMessage(RequestReply<BankInterestRequest, BankInterestReply> rr)
    {
        boolean allReplies = true;
        for(Map.Entry<RequestReply<BankInterestRequest, BankInterestReply>, String> entry: received.entrySet() )
        {
            if(rr.getReply().getId() == entry.getKey().getRequest().getId())
            {
                if(rr.getReply().getQuoteId().equals(entry.getValue()))
                {
                    entry.getKey().setReply(rr.getReply());
                }
                else if (entry.getKey().getReply() == null) allReplies = false;
            }
        }
        if(allReplies)
        {
            updateList(rr);
        }
    }

    public void setRecipients(BankInterestRequest bankInterestRequest, String sentTo)
    {
        received.put(new RequestReply<BankInterestRequest, BankInterestReply>(bankInterestRequest, null), sentTo);
    }

    public void updateList(RequestReply<BankInterestRequest, BankInterestReply> rr)
    {
        RequestReply<BankInterestRequest, BankInterestReply> bestrr = rr;
        for(Map.Entry<RequestReply<BankInterestRequest, BankInterestReply>, String> entry: received.entrySet())
        {
            if(entry.getKey().getReply().getInterest() < bestrr.getReply().getInterest() && entry.getKey().getReply().getId() == rr.getReply().getId())
            {
                bestrr = entry.getKey();
            }
        }

        LoanRequest lr = new LoanRequest();
        lr.setAmount(bestrr.getRequest().getAmount());
        lr.setTime(bestrr.getRequest().getTime());

        frame.add(lr, bestrr.getReply());
        frame.sendToClient(bestrr);
        //send to client
    }



}
