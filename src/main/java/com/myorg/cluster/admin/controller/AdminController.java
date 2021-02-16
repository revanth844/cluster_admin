package com.myorg.cluster.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myorg.cluster.admin.model.ClusterDetails;
import com.myorg.cluster.admin.model.FileInformation;
import com.myorg.cluster.admin.service.FileAdminService;

@RestController
@RequestMapping("file")
public class AdminController {
	@Autowired
	private FileAdminService adminService;
	
	@PostMapping(value = "getTargetCluster", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ClusterDetails>> getTargetCluster(@RequestBody FileInformation fileInfo) {
		List<ClusterDetails> list = adminService.getTargetClusterToSaveFile(fileInfo);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

}