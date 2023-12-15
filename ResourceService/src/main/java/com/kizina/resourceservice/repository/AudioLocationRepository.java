package com.kizina.resourceservice.repository;

import com.kizina.resourceservice.entity.AudioLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioLocationRepository extends JpaRepository<AudioLocation, Long> {
}
