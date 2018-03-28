package com.example.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

interface AccountRepository extends JpaRepository<Account, Long> {
}

@SpringBootApplication
public class JtaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JtaApplication.class, args);
    }
}

@Entity
class Account {
    @Id
    @GeneratedValue
    private
    Long id;

    private String username;

    @SuppressWarnings("unused")
    Account() {
    }

    Account(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

@Component
class Message {
    private Log log = LogFactory.getLog(getClass());

    @JmsListener(destination = "accounts")
    public void onMessage(String content) {
        log.info("----> " + content);
    }
}

@Service
@Transactional
class AccountService {
    private final JmsTemplate jmsTemplate;
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(JmsTemplate jmsTemplate, AccountRepository accountRepository) {
        this.jmsTemplate = jmsTemplate;
        this.accountRepository = accountRepository;
    }

    public void createAccountAndNotify(String username) {
        this.jmsTemplate.convertAndSend("accounts", username);
        this.accountRepository.save(new Account(username));
        if ("error".equals(username)) {
            throw new RuntimeException("Simulated Error");
        }
    }
}