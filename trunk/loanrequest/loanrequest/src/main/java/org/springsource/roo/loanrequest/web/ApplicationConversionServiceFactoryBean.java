package org.springsource.roo.loanrequest.web;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;
import org.springsource.roo.loanrequest.server.domain.LoanRequest;

@Configurable
/**
 * A central place to register application converters and formatters. 
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register application converters and formatters
	}

	public Converter<LoanRequest, String> getLoanRequestToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<org.springsource.roo.loanrequest.server.domain.LoanRequest, java.lang.String>() {
            public String convert(LoanRequest loanRequest) {
                return new StringBuilder().append(loanRequest.getProcessId()).append(" ").append(loanRequest.getCustomerName()).append(" ").append(loanRequest.getAmount()).toString();
            }
        };
    }

	public Converter<Long, LoanRequest> getIdToLoanRequestConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, org.springsource.roo.loanrequest.server.domain.LoanRequest>() {
            public org.springsource.roo.loanrequest.server.domain.LoanRequest convert(java.lang.Long id) {
                return LoanRequest.findLoanRequest(id);
            }
        };
    }

	public Converter<String, LoanRequest> getStringToLoanRequestConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, org.springsource.roo.loanrequest.server.domain.LoanRequest>() {
            public org.springsource.roo.loanrequest.server.domain.LoanRequest convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), LoanRequest.class);
            }
        };
    }

	public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getLoanRequestToStringConverter());
        registry.addConverter(getIdToLoanRequestConverter());
        registry.addConverter(getStringToLoanRequestConverter());
    }

	public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
}
