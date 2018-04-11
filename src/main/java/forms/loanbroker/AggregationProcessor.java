package forms.loanbroker;

import mix.messaging.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;

import java.util.Map;

public class AggregationProcessor {

    private LoanBrokerFrame frame;
    private Map<RequestReply<BankInterestRequest, BankInterestReply>, String> received;

    public AggregationProcessor(LoanBrokerFrame frame) {
        this.frame = frame;
    }

    public void setMessage(RequestReply<BankInterestRequest, BankInterestReply> rr)
    {
        boolean allReplies = true;
        for(Map.Entry<RequestReply<BankInterestRequest, BankInterestReply>, String> entry: received.entrySet() )
        {
            if(rr.getReply().getId() == entry.getKey().getRequest().getId())
            {
                if(rr.getReply().getQuoteId().equals(entry.getKey().getReply().getQuoteId()))
                {
                    entry.setValue(rr.getReply().getQuoteId());
                }
                else if (entry.getKey().getReply() == null) allReplies = false;
            }

            if(allReplies)
            {
                setFrame(rr);
            }
        }
    }

    public void setRecipients(BankInterestRequest bankInterestRequest, String sentTo)
    {
        received.put(new RequestReply<BankInterestRequest, BankInterestReply>(bankInterestRequest, null), sentTo);
    }

    public void setFrame(RequestReply<BankInterestRequest, BankInterestReply> rr)
    {
        RequestReply<BankInterestRequest, BankInterestReply> bestbir = rr;
        for(Map.Entry<RequestReply<BankInterestRequest, BankInterestReply>, String> entry: received.entrySet())
        {
            if(bestbir.getReply().getInterest() < entry.getKey().getReply().getInterest())
            {
                bestbir = entry.getKey();
            }
        }

        LoanRequest lr = new LoanRequest();
        lr.setAmount(bestbir.getRequest().getAmount());
        lr.setTime(bestbir.getRequest().getTime());

        frame.add(lr, bestbir.getReply());
    }



}
