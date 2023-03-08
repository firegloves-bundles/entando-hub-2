package com.entando.hub.catalog.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.testhelper.AssertionHelper;
import com.entando.hub.catalog.testhelper.TestHelper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
class BundleGroupVersionFlowIT extends BaseFlowIT {

    private static final String BASE_URL = "/api/bundlegroupversions";

    @Test
    void shouldGetTheExpectedFilteredData() throws Exception {

        // prepare expected
        BundleGroupVersionFilteredResponseView expected1 = TestHelper.stubBundleGroupVersionFilteredResponseView(
                        bundleGroup1.getId(), bundleGroupVersion1.getId());
        BundleGroupVersionFilteredResponseView expected2 = TestHelper.stubSecondBundleGroupVersionFilteredResponseView(
                bundleGroup2.getId(), bundleGroupVersion2.getId());
        final List<BundleGroupVersionFilteredResponseView> expectedList = Arrays.asList(expected2, expected1);

        // filter by organisationId
        ResultActions resultActions = executeFilteredRequest("&organisationId=" + organisation.getId());
        AssertionHelper.assertOnBundleGroupVersions(resultActions, expectedList);

        // filter by categoryIds
        resultActions = executeFilteredRequest("&categoryIds=" + categorySet.iterator().next().getId());
        AssertionHelper.assertOnBundleGroupVersions(resultActions, expectedList);

        // filter by searchText
        resultActions = executeFilteredRequest("&searchText=" + "Group Name");
        AssertionHelper.assertOnBundleGroupVersions(resultActions, expectedList);

        // no result expected
        resultActions = executeFilteredRequest("&searchText=" + "not existing");
        AssertionHelper.assertOnBundleGroupVersions(resultActions, Collections.emptyList());
    }

    @Test
    void shouldGetTheExpectedBundleGroupVersions() throws Exception {

        // prepare expected
        BundleGroupVersionFilteredResponseView expected = TestHelper.stubSecondBundleGroupVersionFilteredResponseView(
                bundleGroup2.getId(), bundleGroupVersion2.getId());

        // filter by existing bundle group id
        ResultActions resultActions = executeGetRequest(bundleGroup2.getId(), "");
        AssertionHelper.assertOnBundleGroupVersions(resultActions, Collections.singletonList(expected));

        // filter by not existing bundle group id
        resultActions = executeGetRequest(10L, "");
        resultActions.andExpect(jsonPath("$").doesNotExist());
    }

    private ResultActions executeGetRequest(Long bundleGroupId, String url) throws Exception {
        return executeRequest("/versions/" + bundleGroupId + "?page=0&pageSize=10" + url);
    }

    private ResultActions executeFilteredRequest(String url) throws Exception {
        return executeRequest("/filtered?page=0&pageSize=10" + url);
    }

    private ResultActions executeRequest(String url) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.get(
                                BASE_URL + url)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
