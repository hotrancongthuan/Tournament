package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Account;
import com.duan2.tournamentTDTU.repositorys.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(String accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    public Account createAccount(Account account) {
        Account account1 = getAccountById(account.getID());
        if(account1 != null){
            return account1;
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String ID) throws UsernameNotFoundException {
        Optional<Account> optionalAcount = accountRepository.findById(ID);
        if(!optionalAcount.isPresent()){
            throw new UsernameNotFoundException("Account not found");
        }
        Account account = optionalAcount.get();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(account.getRole()));
        System.out.println("login");
        System.out.println(account.getRole());
        return new User(account.getID(),account.getPassword(),grantedAuthorities);
    }
}