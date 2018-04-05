package com.acme.repository;

import com.acme.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SettingsRepository extends JpaRepository<Settings, String>, JpaSpecificationExecutor<Settings> {



}
