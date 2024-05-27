package az.m10.service;

import az.m10.domain.BaseEntity;
import az.m10.dto.BaseDTO;
import az.m10.exception.CustomNotFoundException;
import az.m10.repository.BaseJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public abstract class GenericService<T extends BaseEntity, E extends BaseDTO> {
    private final BaseJpaRepository<T, Long> baseJpaRepository;

    public GenericService(BaseJpaRepository<T, Long> repository) {
        this.baseJpaRepository = repository;
    }

    @Transactional
    public abstract T add(E dto);

    public T update(Long id, E dto) {
        T t = baseJpaRepository.findById(id).orElseThrow(
                () -> new CustomNotFoundException("Entity not found.")
        );
        t = (T) dto.toEntity(Optional.of(t));
        baseJpaRepository.save(t);
        return t;
    }

    public T findById(Long id) {
        return baseJpaRepository.findById(id).orElseThrow(
                () -> new CustomNotFoundException("Entity not found")
        );
    }

    public void delete(Long id) {
        baseJpaRepository.deleteById(id);
    }

    public List<T> findAll() {
        return baseJpaRepository.findAll();
    }
}
