package com.myorg.cluster.admin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.cluster.admin.model.ClusterDetails;
import com.myorg.cluster.admin.model.ClusterStats;
import com.myorg.cluster.admin.model.FileInformation;

@Service
public class FileAdminService {

	private static final String DATA_FILE_NAME = "src/main/resources/input.json";
	private ArrayList<ClusterStats> clusterStats = new ArrayList<ClusterStats>();

	@Autowired
	ObjectMapper objectMapper;

	@PostConstruct
	private void readClusterStatsToMemory() {
		JsonNode jsonTree;
		try {
			jsonTree = objectMapper.readTree(new File(DATA_FILE_NAME));

			// Load JSON Tree into an arrayList
			// Ideally, this data-structure should be continuously updated by a service
			//	after data file is written to the cluster, with updated availableDiskSpace 
			jsonTree.elements().forEachRemaining(entry -> {
				System.out.println(entry);

				ClusterStats stat;
				try {
					stat = objectMapper.readValue(entry.traverse(), ClusterStats.class);
					clusterStats.add(stat);

				} catch (IOException e) {
					e.printStackTrace();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public List<ClusterDetails> getTargetClusterToSaveFile(FileInformation fileInfo) {
		System.out.println(fileInfo);

		int fileSizeInGB = Integer.parseInt(fileInfo.getFileSize().toLowerCase().replaceAll("gb", ""));

		// In cluster is not specified in input, get suitable hosts across all clusters
		if (fileInfo.getClusterName() == null) {
			return clusterStats.parallelStream().filter(host -> {
				return host.getAvailable_disk_space() > fileSizeInGB;
			}).map(host -> new ClusterDetails(host.getCluster(), host.getHostname())).collect(Collectors.toList());
		} else { // get suitable hosts in specified cluster
			return clusterStats.parallelStream().filter(host -> {
				return (host.getCluster().equals(fileInfo.getClusterName())
						&& (host.getAvailable_disk_space() > fileSizeInGB));
			}).map(host -> new ClusterDetails(host.getCluster(), host.getHostname())).collect(Collectors.toList());
		}
	}

}
