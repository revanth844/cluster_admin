package com.myorg.cluster.admin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.cluster.admin.model.ClusterDetails;
import com.myorg.cluster.admin.model.ClusterHostStats;
import com.myorg.cluster.admin.model.FileInformation;

@Service
public class FileAdminService {

	private static final String DATA_FILE_NAME = "src/main/resources/input.json";

	// Map<clusterName, List<hostStats>>
	private HashMap<String, ArrayList<ClusterHostStats>> clusterStatsMap = new HashMap<String, ArrayList<ClusterHostStats>>();

	@Autowired
	ObjectMapper objectMapper;

	@PostConstruct
	private void readClusterStatsToMemory() throws JsonProcessingException, IOException {
		JsonNode jsonTree = objectMapper.readTree(new File(DATA_FILE_NAME));

		// Load JSON Tree into Primary memory, as Map of arrayList for each cluster
		// This data-structure should be continuously updated with availableDiskSpace
		// after data file is written to the cluster, with updated
		jsonTree.elements().forEachRemaining(entry -> {
			System.out.println(entry);

			try {
				ClusterHostStats stat = objectMapper.readValue(entry.traverse(), ClusterHostStats.class);
				String clusterName = stat.getCluster();

				ArrayList<ClusterHostStats> clusterStats = (clusterStatsMap.containsKey(clusterName))
						? (clusterStatsMap.get(clusterName))
						: (new ArrayList<ClusterHostStats>());
				clusterStats.add(stat);
				clusterStatsMap.put(clusterName, clusterStats);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public List<ClusterDetails> getTargetClusterToSaveFile(FileInformation fileInfo) {
		List<ClusterDetails> eligibleHosts = new ArrayList<>();

		System.out.println(fileInfo);

		int fileSizeInGB = Integer.parseInt(fileInfo.getFileSize().toLowerCase().replaceAll("gb", ""));

		// In cluster is not specified in input, get suitable hosts across all clusters
		if (fileInfo.getClusterName() == null) {
			clusterStatsMap.forEach((cluster, hosts) -> {
				eligibleHosts.addAll(hosts.parallelStream().filter(host -> {
					return host.getAvailable_disk_space() > fileSizeInGB;
				}).map(host -> new ClusterDetails(host.getCluster(), host.getHostname())).collect(Collectors.toList()));
			});

		} else { // get suitable hosts in specified cluster
			eligibleHosts.addAll(clusterStatsMap.get(fileInfo.getClusterName()).parallelStream().filter(host -> {
				return host.getAvailable_disk_space() > fileSizeInGB;
			}).map(host -> new ClusterDetails(host.getCluster(), host.getHostname())).collect(Collectors.toList()));
		}
		return eligibleHosts;
	}

}
