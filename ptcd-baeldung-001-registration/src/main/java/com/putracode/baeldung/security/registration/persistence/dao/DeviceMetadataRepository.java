package com.putracode.baeldung.security.registration.persistence.dao;

import com.putracode.baeldung.security.registration.persistence.model.DeviceMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceMetadataRepository extends JpaRepository<DeviceMetadata,Long> {
    List<DeviceMetadata> findByUserId(long userId);
}
