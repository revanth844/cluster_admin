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
public class FileInformation {

	String fileName;
	String fileSize;
	String clusterName;
}
