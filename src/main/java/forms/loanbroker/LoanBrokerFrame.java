package forms.loanbroker;

import forms.bank.IBank;
import forms.loanclient.LoanBrokerAppGateway;
import mix.messaging.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;

import java.awt.*;
import java.io.Serializable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;



public class LoanBrokerFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
	private JList<JListLine> list;
	private static JScrollPane scrollPane;
	private static LoanRequestListener ml;
	private static BankReplyListener bl;
//	private BankAppGateway bankAppGateway;
	private ClientAppGateway clientAppGateway;
	private RecipientProcessor recipientProcessor;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoanBrokerFrame frame = new LoanBrokerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	public LoanBrokerFrame() {
		setTitle("Loan Broker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(800, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		list = new JList<JListLine>(listModel);
		scrollPane.setViewportView(list);

		//bankAppGateway = new BankAppGateway("BrokerToBank");
		clientAppGateway = new ClientAppGateway("BrokerToClient");
		recipientProcessor = new RecipientProcessor(this);

		//bankAppGateway.setLoanBrokerFrame(this);
		clientAppGateway.setLoanBrokerFrame(this);

		/*ml = new LoanRequestListener();
		ml.setupMessageQueueConsumer();
		bl = new BankReplyListener();
		bl.setupMessageQueueConsumer();
		ml.setBrokerFrame(this);
		bl.setBrokerFrame(this);*/
	}
	
	 private JListLine getRequestReply(LoanRequest request){
	     for (int i = 0; i < listModel.getSize(); i++){
	    	 JListLine rr =listModel.get(i);
	    	 if ((rr.getLoanRequest().getTime() == request.getTime() && (rr.getLoanRequest().getAmount() == request.getAmount()))){
	    		 return rr;
	    	 }
	     }
	     return null;
	   }
	
	public void add(LoanRequest loanRequest){
		listModel.addElement(new JListLine(loanRequest));		
	}
	

	public void add(LoanRequest loanRequest, BankInterestRequest bankRequest){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}		
	}
	
	public void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);
            list.repaint();
		}		
	}

	public void sendToClient(RequestReply<BankInterestRequest, BankInterestReply> bankreply)
	{
		clientAppGateway.sendLoanReply(bankreply);
	}

	public void senToBank(LoanRequest loanrequest)
	{
		BankInterestRequest bir = new BankInterestRequest(loanrequest.getAmount(), loanrequest.getTime());
		recipientProcessor.sendToBank(bir);
		//bankAppGateway.sendBankRequest(bir);
	}
}
