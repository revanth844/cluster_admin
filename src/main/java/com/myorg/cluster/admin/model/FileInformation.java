package com.myorg.cluster.admin.model;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class FileInformation {

	@NotBlank(message = "fileName is mandatory")
	String fileName;
	
	@NotBlank(message = "fileSize is mandatory")
	String fileSize;
	
	String clusterName;
}
