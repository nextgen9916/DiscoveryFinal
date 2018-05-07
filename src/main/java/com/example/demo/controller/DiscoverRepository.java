package com.example.demo.controller;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface DiscoverRepository extends JpaRepository <Discover, String>{
	
	@Query(value = "select * from device_info ip where ip.ip = :ipaddress", nativeQuery = true)
    public List<Discover> findByIp(@Param("ipaddress") String ipadd);

}


