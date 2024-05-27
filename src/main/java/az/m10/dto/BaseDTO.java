package az.m10.dto;

import az.m10.domain.BaseEntity;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@EqualsAndHashCode
public abstract class BaseDTO<T extends BaseEntity> {
    public abstract T toEntity(Optional<T> existingEntity);
}
