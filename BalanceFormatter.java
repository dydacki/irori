package se.irori.bank.formatters;

import static se.irori.bank.model.Balance;

import java.text.SimpleDateFormat;

public class BalanceFormatter {

  @Autowired
  private ConverterService converterService;
  
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  public Balance formatBalance(Account account, Double balance) {
  
	double currentBalanceInSek = account.getCurrentBalanceInSek();
    Double balance = converterService.convertToCurrency(account.getCurrentBalanceInSek(), currency);
    String dateString = DATE_FORMAT.format(account.getLastTransaction());
    return new Balance(account.getId(), balance, account.getAccountHolder(), dateString);
  }
}
