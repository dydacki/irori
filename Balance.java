package se.irori.bank.model;

  public class Balance {
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
}
