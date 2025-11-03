package com.hiberus.challenge.infrastructure.adapter.out.persistence.mapper;

import com.hiberus.challenge.domain.model.AccountInfo;
import com.hiberus.challenge.domain.model.MonetaryAmount;
import com.hiberus.challenge.domain.model.PaymentOrder;
import com.hiberus.challenge.domain.model.PaymentOrderStatus;
import com.hiberus.challenge.domain.model.PaymentPriority;
import com.hiberus.challenge.infrastructure.adapter.out.persistence.entity.PaymentOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain models and persistence entities.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentOrderPersistenceMapper {

    @Mapping(target = "debtorIban", source = "debtorAccount.identification")
    @Mapping(target = "debtorName", source = "debtorAccount.name")
    @Mapping(target = "debtorBic", source = "debtorAccount.bankIdentifier")
    @Mapping(target = "creditorIban", source = "creditorAccount.identification")
    @Mapping(target = "creditorName", source = "creditorAccount.name")
    @Mapping(target = "creditorBic", source = "creditorAccount.bankIdentifier")
    @Mapping(target = "amountValue", source = "amount.value")
    @Mapping(target = "amountCurrency", source = "amount.currency")
    @Mapping(target = "priority", source = "priority", qualifiedByName = "priorityToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    PaymentOrderEntity toEntity(PaymentOrder paymentOrder);

    @Mapping(target = "debtorAccount", expression = "java(toDebtorAccount(entity))")
    @Mapping(target = "creditorAccount", expression = "java(toCreditorAccount(entity))")
    @Mapping(target = "amount", expression = "java(toAmount(entity))")
    @Mapping(target = "priority", source = "priority", qualifiedByName = "stringToPriority")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    PaymentOrder toDomain(PaymentOrderEntity entity);

    @Named("priorityToString")
    default String priorityToString(PaymentPriority priority) {
        return priority != null ? priority.name() : null;
    }

    @Named("stringToPriority")
    default PaymentPriority stringToPriority(String priority) {
        return priority != null ? PaymentPriority.valueOf(priority) : null;
    }

    @Named("statusToString")
    default String statusToString(PaymentOrderStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    default PaymentOrderStatus stringToStatus(String status) {
        return status != null ? PaymentOrderStatus.valueOf(status) : null;
    }

    default AccountInfo toDebtorAccount(PaymentOrderEntity entity) {
        return AccountInfo.builder()
                .identification(entity.getDebtorIban())
                .name(entity.getDebtorName())
                .bankIdentifier(entity.getDebtorBic())
                .build();
    }

    default AccountInfo toCreditorAccount(PaymentOrderEntity entity) {
        return AccountInfo.builder()
                .identification(entity.getCreditorIban())
                .name(entity.getCreditorName())
                .bankIdentifier(entity.getCreditorBic())
                .build();
    }

    default MonetaryAmount toAmount(PaymentOrderEntity entity) {
        return MonetaryAmount.builder()
                .value(entity.getAmountValue())
                .currency(entity.getAmountCurrency())
                .build();
    }
}
