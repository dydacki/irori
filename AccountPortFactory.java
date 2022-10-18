package se.irori.bank.ports.Account;

public class AccountPortsFactory {

  DataAccessLayer accountAccess;

  public AccountPort createPort(String accountIdentifier) {
	
	if (accountIdentifier.startsWith("BA")) {
	  return new BusinessAccountPort();
	}
	
	return new IndividualAccountPort();
  }
}
