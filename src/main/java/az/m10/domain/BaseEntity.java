package az.m10.domain;

import az.m10.dto.BaseDTO;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public abstract class BaseEntity<E extends BaseDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    public abstract Long getId();
    public abstract E toDto();
}

