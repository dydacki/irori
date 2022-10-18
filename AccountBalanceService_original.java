package se.irori.bank.rest.jobs;

import static se.irori.bank.rest.jobs.AccountBalanceService.Currency.GBP;
import static se.irori.bank.rest.jobs.AccountBalanceService.Currency.SEK;
import static se.irori.bank.rest.jobs.AccountBalanceService.Currency.USD;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * Returns the balance of the customer's account.
 *
 * @author Trazan Apansson
 */
@Path("/account/balance")
@Service
public class AccountBalanceService {

  @Autowired
  AccountServiceFactory accountServiceFactory;

  @Autowired
  AlertServiceImpl alertService;

  @Autowired
  private ConverterService converterService;

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


  /**
   * Returns the balance of the customer's account in the given currency.
   *
   * @param accountIdentifier The account id.
   * @param selectedCurrency Can be either USD, EURO or SEK
   * @return the current balance for in the specified currency
   */
   
  // Email unneeded
  @Path("/{accountIdentifier}/{selectedCurrency}/{email}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Balance getBalance(
      @PathParam("accountIdentifier") String accountIdentifier,
      @PathParam("selectedCurrency") String selectedCurrency,
      @PathParam("email") String email) {

	// variables should be defined where they are used
    Integer businessCustomerId = null;
    Integer businessAccountNumber = null;
    Integer accountId = null;
    if (accountIdentifier.startsWith("BA")) {
      // business account, format 1234567890-4
      String[] parts = accountIdentifier.split("-");
	  
	  // no try-catch: we have no guarantee that there is a dash ("-") in the account number
      String businessCustomerIdStr = parts[0].substring(2);
	  // nor we there is certainty the parsing will succeed
      businessCustomerId = Integer.parseInt(businessCustomerIdStr);
      String businessAccountNumberStr = parts[1];
      businessAccountNumber = Integer.parseInt(businessAccountNumberStr);
    } else {
      accountId = Integer.parseInt(accountIdentifier);
    }

    Currency currency;
    try {
      currency = Currency.valueOf(selectedCurrency);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
	  // return bad request
      return null;
    }

    Account account = null;
    if (accountId != null) {
      account = accountService.findAccountById(accountId);
    } else {
      List<Account> businessAccounts = accountService.findAccountsByBusinessCustomerId(businessCustomerId);
      final Integer finalBusinessAccountNumber = businessAccountNumber;
      account = businessAccounts.stream()
          .filter(a -> a.getAccountNumber().equals(finalBusinessAccountNumber)).findFirst().get();
    }
	
	// shouldn't make save on gets, should be a different endpoint
    if (email != null && email.length() > 0) {
      account.setContactInformation(email);
      accountService.save(account);
    }

    double currentBalanceInSek = account.getCurrentBalanceInSek();
    Double balance = null;

	// this is a responsibility of a converter service which should have
	// convertToCurrency method. This would eliminate the whole switch statement
    switch (currency) {
      case USD:
        balance = converterService.convert(currentBalanceInSek, SEK, USD);
        break;
      case EURO:
        balance = converterService.convert(currentBalanceInSek, SEK, GBP);
      case GBP:
        throw new NotImplementedException("GBP format not implemented");
      case SEK:
        balance = currentBalanceInSek;
        break;
    }
	
	// None of the alerts should be handled in a GET method.
	// Handling notifications i a job of PUT requests which change the account balance
    // if ((currency.equals(USD) && balance > 10000) || (currency.equals(Currency.EURO) && balance > 8400)
        // || (currency.equals(SEK) && balance > 86000)) {

      // alertService.triggerAlert(accountId, "investment_opportunity");
    // }
	
	// Unused code should be taken away
    /*
        if ((currency.equals(USD) && balance < 10) || (currency.equals(Currency.EURO) && balance < 84)
            || (currency.equals(SEK) && balance < 86)) {
          alertService.triggerAlert(accountId, "low_balance");
        }
    */

    Date lastTransaction = account.getLastTransaction();
    String dateString = DATE_FORMAT.format(lastTransaction);
    return new Balance(accountId, balance, account.getAccountHolder(), dateString);
  }

  // should be implemented in a domain model directory
  public static class Balance {
    public final Integer accountId;
    public final double balance;
    public final String accountHolder;
    public final String lastTransaction;

    public Balance(Integer accountId, double balance, String accountHolder, String lastTransaction) {
      this.accountId = accountId;
      this.balance = balance;
      this.accountHolder = accountHolder;
      this.lastTransaction = lastTransaction;
    }
  }

  // should be implemented in a domain model directory
  public static enum Currency {
    USD,
    GBP,
    EURO,
    SEK
  }

}
