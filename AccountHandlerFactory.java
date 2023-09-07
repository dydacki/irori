package se.irori.bank.ports.Account;

public class AccountHandlerFactory {

  public AccountHandler createHandler(String accountIdentifier) {
	
	if (accountIdentifier.startsWith("BA")) {
	  return new BusinessAccountHandler();
	}
	
	return new IndividualAccountHandler();
  }
}
