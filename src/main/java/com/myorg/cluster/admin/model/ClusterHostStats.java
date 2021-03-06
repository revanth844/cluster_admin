package com.myorg.cluster.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClusterHostStats {

	String cluster;
	String hostname;
	long max_disk_space;
	long available_disk_space;
	long total_no_of_files;
}
