package se.irori.bank.ports.Account;

public class AccountPortsFactory {

  DataAccessLayer accountAccess;

  public AccountPort createAccountPort(String accountIdentifier) {
	
	if (accountIdentifier.startsWith("BA")) {
	  return new BusinessAccountPort();
	}
	
	return new IndividualAccountPort();
  }
}
