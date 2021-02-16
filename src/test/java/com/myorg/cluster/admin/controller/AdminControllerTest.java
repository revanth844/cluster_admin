package com.myorg.cluster.admin.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.cluster.admin.model.ClusterDetails;
import com.myorg.cluster.admin.model.FileInformation;
import com.myorg.cluster.admin.service.FileAdminService;

@SpringBootTest
@RunWith(SpringRunner.class)
@WebMvcTest(AdminController.class)
public class AdminControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private FileAdminService service;

	private static byte[] toJson(Object object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.writeValueAsBytes(object);
	}

	@Test
	public void givenClusterStatistics_whenClusterSpecified_thenReturnHostsFromThatCluster() throws Exception {

		FileInformation fileInfo = FileInformation.builder().fileName("gift_items_from_north_carolina.txt")
				.fileSize("25GB").clusterName("c1").build();

		List<ClusterDetails> clusterHosts = new ArrayList<ClusterDetails>();
		clusterHosts.add(new ClusterDetails("c1", "h1"));

		given(service.getTargetClusterToSaveFile(fileInfo)).willReturn(clusterHosts);

		mvc.perform(post("/file/getTargetCluster").contentType(MediaType.APPLICATION_JSON).content(toJson(fileInfo)))
				.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].cluster", is(fileInfo.getClusterName())));
	}

	@Test
	public void givenClusterStatistics_whenClusterSpecified_thenReturnHostsFromThatCluster_2() throws Exception {

		FileInformation fileInfo = FileInformation.builder().fileName("claim_records_UHC.txt").fileSize("2GB")
				.clusterName("c1").build();

		List<ClusterDetails> clusterHosts = new ArrayList<ClusterDetails>();
		clusterHosts.add(new ClusterDetails("c1", "h1"));
		clusterHosts.add(new ClusterDetails("c1", "h2"));

		given(service.getTargetClusterToSaveFile(fileInfo)).willReturn(clusterHosts);

		mvc.perform(post("/file/getTargetCluster").contentType(MediaType.APPLICATION_JSON).content(toJson(fileInfo)))
				.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].cluster", is(fileInfo.getClusterName())))
				.andExpect(jsonPath("$[1].cluster", is(fileInfo.getClusterName())));
	}

	@Test
	public void givenClusterStatistics_whenClusterNotSpecified_thenReturnHostsFromAllClusters() throws Exception {

		FileInformation fileInfo = FileInformation.builder().fileName("gift_items_from_north_carolina.txt")
				.fileSize("28GB").build();

		List<ClusterDetails> clusterHosts = new ArrayList<ClusterDetails>();
		clusterHosts.add(new ClusterDetails("c1", "h1"));
		clusterHosts.add(new ClusterDetails("c2", "h1"));

		given(service.getTargetClusterToSaveFile(fileInfo)).willReturn(clusterHosts);

		mvc.perform(post("/file/getTargetCluster").contentType(MediaType.APPLICATION_JSON).content(toJson(fileInfo)))
				.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].cluster", is("c1"))).andExpect(jsonPath("$[1].cluster", is("c2")));
	}

	@Test
	public void givenClusterStatistics_whenFileSizeTooBigThanAnyClusterAvailability_thenReturnNullArray()
			throws Exception {

		FileInformation fileInfo = FileInformation.builder().fileName("genome_sequence_data.txt").fileSize("2000GB")
				.build();

		List<ClusterDetails> clusterHosts = new ArrayList<ClusterDetails>();
		given(service.getTargetClusterToSaveFile(fileInfo)).willReturn(clusterHosts);

		mvc.perform(post("/file/getTargetCluster").contentType(MediaType.APPLICATION_JSON).content(toJson(fileInfo)))
				.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$", hasSize(0)));
	}
}