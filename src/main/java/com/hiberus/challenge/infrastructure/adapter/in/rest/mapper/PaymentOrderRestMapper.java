package com.hiberus.challenge.infrastructure.adapter.in.rest.mapper;

import com.hiberus.challenge.domain.model.AccountInfo;
import com.hiberus.challenge.domain.model.MonetaryAmount;
import com.hiberus.challenge.domain.model.PaymentOrder;
import com.hiberus.challenge.domain.model.PaymentOrderStatus;
import com.hiberus.challenge.domain.model.PaymentPriority;
import com.hiberus.challenge.domain.port.in.InitiatePaymentOrderCommand;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.InitiatePaymentOrderRequest;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.PaymentOrderResponse;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.PaymentOrderStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.OffsetDateTime;

/**
 * MapStruct mapper for converting between REST DTOs and domain models.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentOrderRestMapper {

    /**
     * Converts REST request to domain command.
     */
    InitiatePaymentOrderCommand toDomainCommand(InitiatePaymentOrderRequest request);

    /**
     * Converts domain model to REST response.
     */
    PaymentOrderResponse toResponse(PaymentOrder paymentOrder);

    /**
     * Converts domain model to status response.
     */
    @Mapping(target = "paymentOrderId", source = "paymentOrderId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "statusReason", source = "statusReason")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PaymentOrderStatusResponse toStatusResponse(PaymentOrder paymentOrder);

    /**
     * Converts domain AccountInfo to REST model.
     */
    com.hiberus.challenge.infrastructure.adapter.in.rest.model.AccountInfo toRestAccountInfo(AccountInfo accountInfo);

    /**
     * Converts REST AccountInfo to domain model.
     */
    AccountInfo toDomainAccountInfo(com.hiberus.challenge.infrastructure.adapter.in.rest.model.AccountInfo accountInfo);

    /**
     * Converts domain MonetaryAmount to REST model.
     */
    com.hiberus.challenge.infrastructure.adapter.in.rest.model.MonetaryAmount toRestMonetaryAmount(
            MonetaryAmount monetaryAmount);

    /**
     * Converts REST MonetaryAmount to domain model.
     */
    MonetaryAmount toDomainMonetaryAmount(
            com.hiberus.challenge.infrastructure.adapter.in.rest.model.MonetaryAmount monetaryAmount);

    /**
     * Converts enum from domain to REST.
     */
    default PaymentOrderResponse.StatusEnum toRestStatus(PaymentOrderStatus status) {
        return status != null ? PaymentOrderResponse.StatusEnum.fromValue(status.name()) : null;
    }

    /**
     * Converts enum from domain to REST for status response.
     */
    default PaymentOrderStatusResponse.StatusEnum toRestStatusEnum(PaymentOrderStatus status) {
        return status != null ? PaymentOrderStatusResponse.StatusEnum.fromValue(status.name()) : null;
    }

    /**
     * Converts priority enum from REST to domain.
     */
    default PaymentPriority toDomainPriority(InitiatePaymentOrderRequest.PriorityEnum priority) {
        if (priority == null) {
            return PaymentPriority.NORMAL;
        }
        return PaymentPriority.valueOf(priority.name());
    }

    /**
     * Converts priority enum from domain to REST.
     */
    default PaymentOrderResponse.PriorityEnum toRestPriority(PaymentPriority priority) {
        return priority != null ? PaymentOrderResponse.PriorityEnum.fromValue(priority.name()) : null;
    }

    /**
     * Converts OffsetDateTime to String for REST API.
     */
    default String map(OffsetDateTime value) {
        return value != null ? value.toString() : null;
    }
}
