package se.irori.bank.ports.Account;

import static se.irori.bank.model.Account;

import org.springframework.beans.factory.annotation.Autowired;

public interface AccountPort {

  Account getAccountById(String accountIdentifier);
}

public class IndividualAccountPort implements AccountPort {

  //Ignore the class name, it's just for the sake of presentation
  @Autowired
  DataAccessLayer accountAccess;

  public Account getAccountById(String accountIdentifier) {
	
	Integer accountId = Integer.parseInt(accountIdentifier);
	return accountAccess.getById(accountId);
  }
}

public class BusinessAccountPort implements AccountPort {

  //Ignore the class name, it's just for the sake of presentation
  @Autowired
  DataAccessLayer accountAccess;

  public Account getAccountById(String accountIdentifier) {
	
	// business account, format 1234567890-4
    String[] parts = accountIdentifier.split("-");
	Integer businessCustomerId = this.getBusinessCustomerId(parts[0]);
    String businessAccountNumberStr = parts[1];
    int businessAccountNumber = this.getAccountNumber(parts);
	
	return this.getAccountByNumber(businessCustomerId, businessAccountNumber);
  }
  
  private Integer getBusinessCustomerId(String businessCustomerId) {
	
	if (businessCustomerId.substring(2).Length == 0) {
		throw new IllegalArgumentException("No account identifier provided for business customer.");
	}
	
    return Integer.parseInt(businessCustomerId.substring(2));
  }
  
  private Integer getAccountNumber(String[] accountParts) {
	
	if (accountParts.Length == 1) {
		return 1;
	}
	
    return Integer.parseInt(accountParts[1]);
  }
  
  private getAccountByNumber(Integer customerId, Integer accountNumber) {
	
	List<Account> businessAccounts = accountAccess.findAccountsByBusinessCustomerId(customerId);
    final Integer finalAccountNumber = accountNumber;
    return businessAccounts
		    .stream()
            .filter(a -> a.getAccountNumber().equals(finalAccountNumber))
			.findFirst()
			.get();
  }
}
