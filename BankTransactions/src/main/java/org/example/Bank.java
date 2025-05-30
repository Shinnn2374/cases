package org.example;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Bank {
    private final Map<String, Account> accounts;
    private final Random random = new Random();
    private final Object fraudCheckLock = new Object();

    public Bank() {
        this.accounts = new ConcurrentHashMap<>();
    }

    public void addAccount(Account account) {
        accounts.put(account.getAccNumber(), account);
    }

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public void transfer(String fromAccountNum, String toAccountNum, long amount) {
        if (fromAccountNum.equals(toAccountNum)) {
            return; // нельзя переводить на тот же счет
        }

        Account fromAccount = accounts.get(fromAccountNum);
        Account toAccount = accounts.get(toAccountNum);

        if (fromAccount == null || toAccount == null) {
            return; // один из счетов не существует
        }

        // Упорядочиваем блокировки, чтобы избежать deadlock
        Account firstLock = fromAccountNum.compareTo(toAccountNum) < 0 ? fromAccount : toAccount;
        Account secondLock = fromAccountNum.compareTo(toAccountNum) < 0 ? toAccount : fromAccount;

        try {
            firstLock.lock();
            try {
                secondLock.lock();

                if (fromAccount.isBlocked() || toAccount.isBlocked()) {
                    return; // один из счетов заблокирован
                }

                if (fromAccount.getMoney() < amount) {
                    return; // недостаточно средств
                }

                // Выполняем перевод
                fromAccount.setMoney(fromAccount.getMoney() - amount);
                toAccount.setMoney(toAccount.getMoney() + amount);

                // Проверка на крупную транзакцию
                if (amount > 50_000) {
                    checkForFraud(fromAccountNum, toAccountNum, amount);
                }
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    private void checkForFraud(String fromAccountNum, String toAccountNum, long amount) {
        synchronized (fraudCheckLock) {
            try {
                if (isFraud(fromAccountNum, toAccountNum, amount)) {
                    Account fromAccount = accounts.get(fromAccountNum);
                    Account toAccount = accounts.get(toAccountNum);

                    // Блокируем счета
                    fromAccount.lock();
                    try {
                        toAccount.lock();
                        try {
                            fromAccount.block();
                            toAccount.block();
                        } finally {
                            toAccount.unlock();
                        }
                    } finally {
                        fromAccount.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public long getBalance(String accountNum) {
        Account account = accounts.get(accountNum);
        if (account == null) {
            return -1; // или можно выбросить исключение
        }

        account.lock();
        try {
            return account.getMoney();
        } finally {
            account.unlock();
        }
    }
}