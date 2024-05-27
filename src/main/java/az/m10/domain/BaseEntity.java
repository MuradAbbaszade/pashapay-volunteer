package az.m10.domain;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public abstract Long getId();
}

