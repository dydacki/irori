package se.irori.bank.formatters;

import static se.irori.bank.model.Balance;

import java.text.SimpleDateFormat;

public class BalanceFormatter {

  @Autowired
  private ConverterService converterService;
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  public Balance formatAccountBalance(Account account, Currency currency) {
	  BigDecimal currentBalanceInSek = account.getCurrentBalanceInSek();
    Currency balance = converterService.convertToCurrency(account.getCurrentBalanceInSek(), currency);
    String dateString = DATE_FORMAT.format(account.getLastTransaction());
    return new Balance(account.getId(), balance, account.getAccountHolder(), dateString);
  }
}
