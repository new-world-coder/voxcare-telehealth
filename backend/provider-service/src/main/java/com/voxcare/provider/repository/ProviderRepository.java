package com.voxcare.provider.repository;

import com.voxcare.provider.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByUserId(Long userId);

    List<Provider> findBySpecialtyIgnoreCase(String specialty);
}
