package se.irori.bank.rest.jobs;

import static se.irori.bank.formatters.BalanceFormatter;

import static se.irori.bank.model.Balance;
import static se.irori.bank.model.Currency;

import static se.irori.bank.rest.jobs.AccountBalanceService.Currency.GBP;
import static se.irori.bank.rest.jobs.AccountBalanceService.Currency.SEK;
import static se.irori.bank.rest.jobs.AccountBalanceService.Currency.USD;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  AccountService accountService;

  @Autowired
  private ConverterService converterService;

  /**
   * Returns the balance of the customer's account in the given currency.
   *
   * @param accountIdentifier The account id.
   * @param selectedCurrency Can be either USD, EURO or SEK
   * @return the current balance for in the specified currency
   */
   
  // Email unneeded
  // @Path("/{accountIdentifier}/{selectedCurrency}/{email}")
  @Path("/{accountIdentifier}/{selectedCurrency}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Balance getBalance(
      @PathParam("accountIdentifier") String accountIdentifier,
      @PathParam("selectedCurrency") String selectedCurrency) {

    Currency currency;
    try {
      currency = Currency.valueOf(selectedCurrency);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

	Account account; 
	try {
      account = accountService.findAccountById(accountIdentifier);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
	
	if (account == null) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
    return new BalanceFormatter().formatBalance(account, balance);
  }
}
