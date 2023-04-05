package com.entando.hub.catalog.integration;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionDto;
import com.entando.hub.catalog.testhelper.AssertionHelper;
import com.entando.hub.catalog.testhelper.TestHelper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

@SpringBootTest
class BundleGroupVersionFlowIT extends BaseFlowIT {

    private static final String BASE_URL = "/api/bundlegroupversions";

    @BeforeEach
    public void setUp() {
        super.setUpBundleGroupVersionFlowData();
    }

    @Test
    void shouldGetTheExpectedFilteredData() throws Exception {

        // prepare expected
        BundleGroupVersionFilteredResponseView expected1 = TestHelper.stubBundleGroupVersionFilteredResponseView(
                bundleGroup1.getId(), bundleGroupVersion1.getId(), organisation1.getId(), bundle1.getId());
        BundleGroupVersionFilteredResponseView expected2 = TestHelper.stubSecondBundleGroupVersionFilteredResponseView(
                bundleGroup2.getId(), bundleGroupVersion2.getId(), organisation1.getId(), bundle2.getId());
        BundleGroupVersionFilteredResponseView expected3 = TestHelper.stubBundleGroupVersionFilteredResponseView(
                        bundleGroup3.getId(), bundleGroupVersion3.getId(), organisation2.getId(), bundle3.getId())
                .setVersion(TestHelper.BUNDLE_GROUP_VERSION_2)
                .setAllVersions(List.of(TestHelper.BUNDLE_GROUP_VERSION_2));

        List<BundleGroupVersionFilteredResponseView> expectedList = List.of(expected2, expected1);
        ResultActions resultActions = executeGetFilteredRequest("&organisationId=" + organisation1.getId());
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, expectedList);

        // filter by categoryIds
        expectedList = List.of(expected3, expected2, expected1);
        resultActions = executeGetFilteredRequest("&categoryIds=" + categorySet.iterator().next().getId());
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, expectedList);

        // filter by categoryIds that doesn't match
        resultActions = executeGetFilteredRequest("&categoryIds=99");
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, Collections.emptyList());

        // filter by searchText
        resultActions = executeGetFilteredRequest("&searchText=" + "Group Name");
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, expectedList);

        // no result expected
        resultActions = executeGetFilteredRequest("&searchText=" + "not existing");
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, Collections.emptyList());
    }

    @Test
    void shouldGetTheExpectedBundleGroupVersions() throws Exception {

        // prepare expected
        BundleGroupVersionFilteredResponseView expected = TestHelper.stubSecondBundleGroupVersionFilteredResponseView(
                bundleGroup2.getId(), bundleGroupVersion2.getId(), organisation1.getId(), bundle2.getId());

        // filter by existing bundle group id
        ResultActions resultActions = executeGetVersionsRequest(bundleGroup2.getId(), "");
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions,
                Collections.singletonList(expected));

        // filter by not existing bundle group id
        resultActions = executeGetVersionsRequest(10L, "");
        resultActions.andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldGetTheExpectedPrivateFilteredData() throws Exception {

        // given I am an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(true);
        when(securityHelperService.isAdmin()).thenReturn(true);

        // prepare expected
        BundleGroupVersionFilteredResponseView expected3 = TestHelper.stubBundleGroupVersionFilteredResponseView(
                        bundleGroup3.getId(), bundleGroupVersion3.getId(), organisation2.getId(), bundle3.getId())
                .setVersion(TestHelper.BUNDLE_GROUP_VERSION_2)
                .setAllVersions(List.of(TestHelper.BUNDLE_GROUP_VERSION_2));

        // filter by catalogId
        List<BundleGroupVersionFilteredResponseView> expectedList = List.of(expected3);
        ResultActions resultActions = executeGetPrivateFilteredRequest(bundleGroup3.getCatalogId(), "");
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, expectedList);

        // filter by catalogId that doesn't exist
        resultActions = executeGetPrivateFilteredRequest(99L, "");
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, Collections.emptyList());

        // filter by categoryIds
        resultActions = executeGetPrivateFilteredRequest(bundleGroup3.getCatalogId(), "&categoryIds=" + categorySet.iterator().next().getId());
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, expectedList);

        // filter by categoryIds that doesn't match
        resultActions = executeGetPrivateFilteredRequest(bundleGroup3.getCatalogId(), "&categoryIds=99");
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, Collections.emptyList());

        // filter by searchText
        resultActions = executeGetPrivateFilteredRequest(bundleGroup3.getCatalogId(), "&searchText=" + "Group Name");
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, expectedList);

        // no result expected
        resultActions = executeGetPrivateFilteredRequest(bundleGroup3.getCatalogId(), "&searchText=" + "not existing");
        AssertionHelper.assertOnBundleGroupVersionFilteredResponseViews(resultActions, Collections.emptyList());
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void anAdminShouldBeAbleToAccessAllRequestedBundleGroupVersion() throws Exception {

        // given I am an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(true);
        when(securityHelperService.isAdmin()).thenReturn(true);

        final BundleGroupVersionDto bundleGroupVersionView1 = TestHelper.stubBundleGroupVersionView(
                bundleGroupVersion1,
                bundle1, Status.PUBLISHED);

        // select by only bundleGroupVersionId
        ResultActions resultActions = executeOkGetBundleGroupVersionsRequest(bundleGroupVersion1.getId(), null);
        AssertionHelper.assertOnBundleGroupVersion(resultActions, bundleGroupVersionView1);

        // select by bundleGroupVersionId and CatalogId
        resultActions = executeOkGetBundleGroupVersionsRequest(bundleGroupVersion1.getId(), catalog1.getId());
        AssertionHelper.assertOnBundleGroupVersion(resultActions, bundleGroupVersionView1);
    }

    @Test
    @WithMockUser(roles = {MANAGER})
    void aNonAdminUserShouldBeAbleToAccessTheExpectedBundleGroupVersions() throws Exception {

        // given I am logged but not as an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(true);
        when(securityHelperService.isAdmin()).thenReturn(false);
        when(securityHelperService.getContextAuthenticationUsername()).thenReturn(TestHelper.NON_ADMIN_USERNAME);

        final BundleGroupVersionDto bundleGroupVersionView1 = TestHelper.stubBundleGroupVersionView(
                bundleGroupVersion1,
                bundle1, Status.PUBLISHED);
        final BundleGroupVersionDto bundleGroupVersionView3 = TestHelper.stubBundleGroupVersionView(
                bundleGroupVersion3,
                bundle3, Status.PUBLISHED);
                bundleGroupVersionView3.setVersion(TestHelper.BUNDLE_GROUP_VERSION_2);

        // with a bundleGroupVersionId that does belong to the user org
        ResultActions resultActions = executeOkGetBundleGroupVersionsRequest(bundleGroupVersion1.getId(), null);
        AssertionHelper.assertOnBundleGroupVersion(resultActions, bundleGroupVersionView1);

        // with a bundleGroupVersionId and catalogId that does belong to the user
        resultActions = executeOkGetBundleGroupVersionsRequest(bundleGroupVersion1.getId(), catalog1.getId());
        AssertionHelper.assertOnBundleGroupVersion(resultActions, bundleGroupVersionView1);

        // with a bundleGroupVersionId that does belong to the user but public bundle and no catalogId
        resultActions = executeOkGetBundleGroupVersionsRequest(bundleGroupVersion3.getId(), null);
        AssertionHelper.assertOnBundleGroupVersion(resultActions, bundleGroupVersionView3);

//
//        // filter by bundleGroupVersionId will not return anything if no public bundles are available
//        resultActions = executeOkGetBundlesRequest(bundleGroupVersion4.getId(), null);
//        AssertionHelper.assertOnBundles(resultActions, Collections.emptyList());
//
//        // filter by CatalogId
//        expectedList = List.of(TestHelper.stubBundleDto(bundle1.getId(), List.of(bundleGroupVersion1)),
//                TestHelper.stubBundleDto(bundle2.getId(), List.of(bundleGroupVersion2)));
//        resultActions = executeOkGetBundlesRequest(null, catalog1.getId());
//        AssertionHelper.assertOnBundles(resultActions, expectedList);
    }

    @Test
    @WithMockUser(roles = {MANAGER})
    void shouldReturnErrorWhileANonAdminUserAsksForProtectedData() throws Exception {

        // given I am logged but not as an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(true);
        when(securityHelperService.isAdmin()).thenReturn(false);
        when(securityHelperService.getContextAuthenticationUsername()).thenReturn(TestHelper.NON_ADMIN_USERNAME);

        // filter by bundleGroupVersionId that is NOT public
        executeGetBundleGroupVersionsRequest(bundleGroupVersion4.getId(), null, StatusResultMatchers::isNotFound);

        // with a bundleGroupVersionId and catalogId that does NOT belong to the user
        executeGetBundleGroupVersionsRequest(bundleGroupVersion3.getId(), catalog2.getId(),
                StatusResultMatchers::isNotFound);

        // with a bundleGroupVersionId that does NOT belong to the catalogId
        executeGetBundleGroupVersionsRequest(bundleGroupVersion3.getId(), catalog1.getId(),
                StatusResultMatchers::isNotFound);
    }

    @Test
    void anNonLoggedUserShouldBeAbleToAccessThePublicBundleGroupVersions() throws Exception {

        // given I am an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(false);

        final BundleGroupVersionDto bundleGroupVersionView1 = TestHelper.stubBundleGroupVersionView(
                bundleGroupVersion1,
                bundle1, Status.PUBLISHED);

        // with a bundleGroupVersionId that does belong to the user org
        ResultActions resultActions = executeOkGetBundleGroupVersionsRequest(bundleGroupVersion1.getId(), null);
        AssertionHelper.assertOnBundleGroupVersion(resultActions, bundleGroupVersionView1);
    }

    @Test
    void shouldNotReturnDataWhileNonLoggedAsksForProtectedData() throws Exception {

        // given I am an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(false);

        // filter by bundleGroupVersionId that is NOT public
        executeGetBundleGroupVersionsRequest(bundleGroupVersion4.getId(), null, StatusResultMatchers::isNotFound);

        // filter by bundleGroupVersionId and CatalogId
        executeGetBundleGroupVersionsRequest(bundleGroupVersion1.getId(), catalog1.getId(), StatusResultMatchers::isForbidden);
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldNotReturnDataWithInconsistentParameters() throws Exception {

        // given I am an admin but this behaviour should be the same in every case
        when(securityHelperService.isUserAuthenticated()).thenReturn(true);

        // with non existing catalogId
        executeGetBundleGroupVersionsRequest(bundleGroupVersion1.getId(), 999L, StatusResultMatchers::isNotFound);

        // with non existing bundleGroupVersionId
        executeGetBundleGroupVersionsRequest(999L, null, StatusResultMatchers::isNotFound);

        // with inconsistent bundleGroupVersionId and catalogId
        executeGetBundleGroupVersionsRequest(bundleGroupVersion1.getId(), catalog2.getId(),
                StatusResultMatchers::isNotFound);
    }

    private ResultActions executeOkGetBundleGroupVersionsRequest(Long bundleGroupVersionId, Long catalogId)
            throws Exception {

        return executeGetBundleGroupVersionsRequest(bundleGroupVersionId, catalogId, StatusResultMatchers::isOk);
    }

    private ResultActions executeGetBundleGroupVersionsRequest(Long bundleGroupVersionId, Long catalogId,
            StatusMatcher statusMatcher) throws Exception {

        String url = BASE_URL + "/" + bundleGroupVersionId + "?";
        url += catalogId != null ? "catalogId=" + catalogId + "&" : "";
        url = url.substring(0, url.length() - 1);

        return executeRequest(url, statusMatcher);
    }


    private ResultActions executeGetVersionsRequest(Long bundleGroupId, String url) throws Exception {
        return executeRequest(BASE_URL + "/versions/" + bundleGroupId + "?page=0&pageSize=10" + url,
                StatusResultMatchers::isOk);
    }

    private ResultActions executeGetFilteredRequest(String url) throws Exception {
        return executeRequest(BASE_URL + "/filtered?page=0&pageSize=10" + url, StatusResultMatchers::isOk);
    }

    private ResultActions executeGetPrivateFilteredRequest(Long catalogId, String url) throws Exception {
        return executeRequest(BASE_URL + "/catalog/" + catalogId + "?page=0&pageSize=10" + url, StatusResultMatchers::isOk);
    }

    private ResultActions executeRequest(String url, StatusMatcher statusMatcher) throws Exception {

        return super.executeGetRequest(url, statusMatcher);
    }
}
