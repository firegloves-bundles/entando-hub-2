package com.entando.hub.catalog.testhelper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.BundleController;
import com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.hamcrest.Matchers;
import org.springframework.test.web.servlet.ResultActions;

@UtilityClass
public class AssertionHelper {

    public static void assertOnBundleGroupVersionFilteredResponseViews(
            ResultActions resultActions, List<BundleGroupVersionFilteredResponseView> expectedList)
            throws Exception {

        int bound = expectedList.size();

        resultActions.andExpect(jsonPath("$.payload", hasSize(bound)));

        for (int i = 0; i < bound; i++) {
            BundleGroupVersionFilteredResponseView expected = expectedList.get(i);
            resultActions
                    .andExpect(jsonPath("$.payload[" + i + "].bundleGroupId").value(expected.getBundleGroupId()))
                    .andExpect(jsonPath("$.payload[" + i + "].bundleGroupVersionId").value(
                            expected.getBundleGroupVersionId()))
                    .andExpect(jsonPath("$.payload[" + i + "].name").value(expected.getName()))
                    .andExpect(jsonPath("$.payload[" + i + "].description").value(expected.getDescription()))
                    .andExpect(jsonPath("$.payload[" + i + "].descriptionImage").value(expected.getDescriptionImage()))
                    .andExpect(jsonPath("$.payload[" + i + "].documentationUrl").value(expected.getDocumentationUrl()))
                    .andExpect(jsonPath("$.payload[" + i + "].version").value(expected.getVersion()))
                    .andExpect(jsonPath("$.payload[" + i + "].status").value(expected.getStatus().toString()))
                    .andExpect(jsonPath("$.payload[" + i + "].organisationId").value(expected.getOrganisationId()))
                    .andExpect(jsonPath("$.payload[" + i + "].organisationName").value(expected.getOrganisationName()))
                    .andExpect(jsonPath("$.payload[" + i + "].publicCatalog").value(expected.isPublicCatalog()))
                    .andExpect(jsonPath("$.payload[" + i + "].categories").value(
                            Matchers.containsInAnyOrder(expected.getCategories().toArray())))
                    .andExpect(jsonPath("$.payload[" + i + "].children").value(
                            Matchers.containsInAnyOrder(expected.getChildren().toArray())))
                    .andExpect(jsonPath("$.payload[" + i + "].allVersions").value(
                            Matchers.containsInAnyOrder(expected.getAllVersions().toArray())))
                    .andExpect(jsonPath("$.payload[" + i + "].bundleGroupUrl").value(expected.getBundleGroupUrl()))
                    .andExpect(jsonPath("$.payload[" + i + "].isEditable").value(expected.getIsEditable()))
                    .andExpect(jsonPath("$.payload[" + i + "].canAddNewVersion").value(expected.isCanAddNewVersion()))
                    .andExpect(
                            jsonPath("$.payload[" + i + "].displayContactUrl").value(expected.getDisplayContactUrl()))
                    .andExpect(jsonPath("$.payload[" + i + "].contactUrl").value(expected.getContactUrl()));
        }
    }


    public static void assertOnBundleGroupVersion(ResultActions resultActions, BundleGroupVersionView expected)
            throws Exception {

        resultActions
                .andExpect(jsonPath("$.bundleGroupId").value(expected.getBundleGroupId()))
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.description").value(expected.getDescription()))
                .andExpect(jsonPath("$.descriptionImage").value(expected.getDescriptionImage()))
                .andExpect(jsonPath("$.documentationUrl").value(expected.getDocumentationUrl()))
                .andExpect(jsonPath("$.version").value(expected.getVersion()))
                .andExpect(jsonPath("$.status").value(expected.getStatus().toString()))
                .andExpect(jsonPath("$.organisationId").value(expected.getOrganisationId()))
                .andExpect(jsonPath("$.organisationName").value(expected.getOrganisationName()))
                .andExpect(jsonPath("$.children").value(Matchers.containsInAnyOrder(
                        expected.getChildren().stream().map(Long::intValue).toArray())));
    }


    public static void assertOnBundles(ResultActions resultActions, List<BundleController.Bundle> expectedList)
            throws Exception {

        int bound = expectedList.size();

        resultActions.andExpect(jsonPath("$", hasSize(bound)));

        for (int i = 0; i < bound; i++) {
            BundleController.Bundle expected = expectedList.get(i);
            resultActions
                    .andExpect(jsonPath("$.[" + i + "].bundleId").value(expected.getBundleId()))
                    .andExpect(jsonPath("$.[" + i + "].name").value(expected.getName()))
                    .andExpect(jsonPath("$.[" + i + "].description").value(expected.getDescription()))
                    .andExpect(jsonPath("$.[" + i + "].descriptionImage").value(expected.getDescriptionImage()))
                    .andExpect(jsonPath("$.[" + i + "].descriptorVersion").value(expected.getDescriptorVersion()))
                    .andExpect(jsonPath("$.[" + i + "].gitRepoAddress").value(expected.getGitRepoAddress()))
                    .andExpect(jsonPath("$.[" + i + "].gitSrcRepoAddress").value(expected.getGitSrcRepoAddress()))
                    .andExpect(jsonPath("$.[" + i + "].dependencies").value(
                            Matchers.containsInAnyOrder(expected.getDependencies().toArray())))
                    .andExpect(jsonPath("$.[" + i + "].bundleGroups").value(
                            Matchers.containsInAnyOrder(expected.getBundleGroups().toArray())));
        }
    }
}
