package com.myorg.cluster.admin.model;

//import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@NoArgsConstructor
@ToString
public class ClusterStats {

	String cluster;
	String hostname;
	long max_disk_space;
	long available_disk_space;
	long total_no_of_files;
}
