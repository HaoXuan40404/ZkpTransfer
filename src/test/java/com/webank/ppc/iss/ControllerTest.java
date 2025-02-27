//package com.webank.ppc.iss;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.webank.ppc.iss.message.*;
//import java.util.List;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class ControllerTest {
//
//    @Autowired private MockMvc mockMvc;
//
//    @Autowired private ObjectMapper objectMapper;
//
//    private String URI_PREFIX = "/api/v3/ppc-management/iss/";
//    private String okResponse;
//
//    @BeforeEach
//    public void setup() throws JsonProcessingException {
//        BaseResponse baseResponse = new BaseResponse();
//        baseResponse.setErrorCode(0);
//        baseResponse.setMessage("success");
//        okResponse = makeContent(baseResponse);
//    }
//
//    @Test
//    public void testDataset() throws Exception {
//        testUploadDataset();
//        testUpdateDataset();
//        testGetDataset();
//        testDeleteDataset();
//    }
//
//    private void testDeleteDataset() throws Exception {
//        MultiValueMap<String, String> deleteParams = new LinkedMultiValueMap();
//        deleteParams.add("ownerAgencyId", "1001");
//        deleteParams.add("datasetId", "d1");
//        mockMvc.perform(
//                        MockMvcRequestBuilders.delete(URI_PREFIX + "/data")
//                                .accept(MediaType.APPLICATION_JSON)
//                                .queryParams(deleteParams))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string(okResponse))
//                .andDo(MockMvcResultHandlers.print());
//    }
//
//    private void testGetDataset() throws Exception {
//        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap();
//        queryParams.add("ownerAgencyId", "1001");
//        queryParams.add("datasetId", "d1");
//        MvcResult mvcResult =
//                mockMvc.perform(
//                                MockMvcRequestBuilders.get(URI_PREFIX + "/data")
//                                        .accept(MediaType.APPLICATION_JSON)
//                                        .queryParams(queryParams))
//                        .andExpect(MockMvcResultMatchers.status().isOk())
//                        .andDo(MockMvcResultHandlers.print())
//                        .andReturn();
//        String resultStr = mvcResult.getResponse().getContentAsString();
//        GetDatasetResponse getDatasetResponse =
//                objectMapper.readValue(resultStr, GetDatasetResponse.class);
//        Assertions.assertEquals(0, getDatasetResponse.getErrorCode());
//        Assertions.assertEquals("success", getDatasetResponse.getMessage());
//        DatasetMessage data = getDatasetResponse.getData();
//        Assertions.assertEquals(1L, data.getTotal());
//        Assertions.assertEquals(true, data.isLastPage());
//        List<DatasetContent> content = data.getContent();
//        DatasetContent datasetContent = content.get(0);
//        Assertions.assertEquals("1001", datasetContent.getOwnerAgencyId());
//        Assertions.assertEquals("d1", datasetContent.getDatasetId());
//        Assertions.assertEquals("dt2", datasetContent.getDatasetTitle());
//        Assertions.assertEquals("c1", datasetContent.getOwnerAgencyName());
//        Assertions.assertEquals("dd2", datasetContent.getDataDetail());
//    }
//
//    private void testUpdateDataset() throws Exception {
//        UpdateDatasetRequest updateDatasetRequest = new UpdateDatasetRequest();
//        updateDatasetRequest.setOwnerAgencyId("1001");
//        updateDatasetRequest.setDatasetId("d1");
//        updateDatasetRequest.setDatasetTitle("dt2");
//        updateDatasetRequest.setDataDetail("dd2");
//        mockMvc.perform(
//                        MockMvcRequestBuilders.patch(URI_PREFIX + "/data")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .accept(MediaType.APPLICATION_JSON)
//                                .content(makeContent(updateDatasetRequest)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string(okResponse))
//                .andDo(MockMvcResultHandlers.print());
//    }
//
//    private void testUploadDataset() throws Exception {
//        UploadDatasetRequest uploadDatasetRequest = new UploadDatasetRequest();
//        uploadDatasetRequest.setOwnerAgencyId("1001");
//        uploadDatasetRequest.setDatasetId("d1");
//        uploadDatasetRequest.setDatasetTitle("dt");
//        uploadDatasetRequest.setOwnerAgencyName("c1");
//        uploadDatasetRequest.setDataDetail("dd");
//        mockMvc.perform(
//                        MockMvcRequestBuilders.post(URI_PREFIX + "/data")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .accept(MediaType.APPLICATION_JSON)
//                                .content(makeContent(uploadDatasetRequest)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string(okResponse))
//                .andDo(MockMvcResultHandlers.print());
//    }
//
//    private <T> String makeContent(T object) throws JsonProcessingException {
//        return objectMapper.writeValueAsString(object);
//    }
//}
