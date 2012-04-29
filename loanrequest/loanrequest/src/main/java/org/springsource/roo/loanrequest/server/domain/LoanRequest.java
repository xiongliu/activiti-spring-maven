package org.springsource.roo.loanrequest.server.domain;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class LoanRequest {

    private String processId;

    private String customerName;

    private Double amount;
}
