package forms.loanbroker;

import connection.MessageReceiverGateway;
import connection.MessageSenderGateway;
import forms.bank.IBank;
import forms.bank.LoanBrokerFromBankAppGateway;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;

import java.util.ArrayList;
import java.util.List;

public class RecipientProcessor {

    private BankAppGateway bankAppGateway;
    private AggregationProcessor aggrogationProcessor;

    public RecipientProcessor(LoanBrokerFrame frame) {

        aggrogationProcessor = new AggregationProcessor(frame);
        bankAppGateway = new BankAppGateway(aggrogationProcessor);
    }

    public void sendToBank(BankInterestRequest bir)
    {
        //BankInterestRequest bir = new BankInterestRequest(request.getAmount(), request.getTime());
        List<String> availableBanks = checkBank(bir);

        for(String availableBank: availableBanks)
        {
            bankAppGateway.setSender(new MessageSenderGateway(availableBank));
            bankAppGateway.sendBankRequest(bir);
            aggrogationProcessor.setRecipients(bir, availableBank);
        }
    }

    public List<String> checkBank(BankInterestRequest bir)
    {
        List<String> availableBanks = new ArrayList<>();

        if(bir.getAmount() <= 100000 && bir.getTime() <=10)
        {
            availableBanks.add("BrokerToING");
        }
        if(bir.getAmount() >= 200000 && bir.getAmount() <= 300000 && bir.getTime() <= 20)
        {
            availableBanks.add("BrokerToABN");
        }
        if(bir.getAmount() <= 250000 && bir.getTime() <= 15)
        {
            availableBanks.add("BrokerToRabo");
        }

        return availableBanks;
    }
}
