package az.m10.domain.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum ReservationStatus {
    WAITING_FOR_APPROVE,
    APPROVED,
    DECLINED
}
