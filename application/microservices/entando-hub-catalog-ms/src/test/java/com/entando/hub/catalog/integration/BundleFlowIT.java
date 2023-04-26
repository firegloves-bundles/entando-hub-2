package com.entando.hub.catalog.integration;

import com.entando.hub.catalog.rest.dto.BundleDto;
import com.entando.hub.catalog.testhelper.AssertionHelper;
import com.entando.hub.catalog.testhelper.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

import java.util.List;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;
import static org.mockito.Mockito.when;

@SpringBootTest
class BundleFlowIT extends BaseFlowIT {

    private static final String BASE_URL = "/api/bundles";

    @BeforeEach
    public void setUp() {
        super.setUpBundleFlowData();
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void anAdminShouldBeAbleToAccessEveryRequestedBundles() throws Exception {

        // given I am an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(true);
        when(securityHelperService.isAdmin()).thenReturn(true);

        // without filters should get everything
        List<BundleDto> expectedList = List.of(
                TestHelper.stubBundleDto(bundle1.getId(), List.of(bundleGroupVersion1)),
                TestHelper.stubBundleDto(bundle2.getId(), List.of(bundleGroupVersion2)),
                TestHelper.stubBundleDto(bundle3.getId(), List.of(bundleGroupVersion3)),
                TestHelper.stubBundleDto(bundle4.getId(), List.of(bundleGroupVersion4)));
        ResultActions resultActions = executeOkGetBundlesRequest(null, null);
        AssertionHelper.assertOnBundles(resultActions, expectedList);

        // filter by bundleGroupVersionId and CatalogId
        expectedList = List.of(TestHelper.stubBundleDto(bundle1.getId(), List.of(bundleGroupVersion1)));
        resultActions = executeOkGetBundlesRequest(bundleGroupVersion1.getId(), catalog1.getId());
        AssertionHelper.assertOnBundles(resultActions, expectedList);

        // filter by bundleGroupVersionId
        expectedList = List.of(TestHelper.stubBundleDto(bundle1.getId(), List.of(bundleGroupVersion1)));
        resultActions = executeOkGetBundlesRequest(bundleGroupVersion1.getId(), null);
        AssertionHelper.assertOnBundles(resultActions, expectedList);

        // filter by CatalogId
        expectedList = List.of(TestHelper.stubBundleDto(bundle3.getId(), List.of(bundleGroupVersion3)));
        resultActions = executeOkGetBundlesRequest(null, catalog2.getId());
        AssertionHelper.assertOnBundles(resultActions, expectedList);
    }


    @Test
    @WithMockUser(roles = {MANAGER})
    void aNonAdminUserShouldBeAbleToAccessTheExpectedBundles() throws Exception {

        // given I am logged but not as an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(true);
        when(securityHelperService.isAdmin()).thenReturn(false);
        when(securityHelperService.getContextAuthenticationUsername()).thenReturn(TestHelper.NON_ADMIN_USERNAME);

        // without filters should get all (and only) public bundles
        List<BundleDto> expectedList = List.of(
                TestHelper.stubBundleDto(bundle1.getId(), List.of(bundleGroupVersion1)),
                TestHelper.stubBundleDto(bundle2.getId(), List.of(bundleGroupVersion2)),
                TestHelper.stubBundleDto(bundle3.getId(), List.of(bundleGroupVersion3)));
        ResultActions resultActions = executeOkGetBundlesRequest(null, null);
        AssertionHelper.assertOnBundles(resultActions, expectedList);

        // filter by bundleGroupVersionId and catalogId
        expectedList = List.of(TestHelper.stubBundleDto(bundle1.getId(), List.of(bundleGroupVersion1)));
        resultActions = executeOkGetBundlesRequest(bundleGroupVersion1.getId(), catalog1.getId());
        AssertionHelper.assertOnBundles(resultActions, expectedList);

        // filter by bundleGroupVersionId will return only public bundles
        expectedList = List.of(TestHelper.stubBundleDto(bundle1.getId(), List.of(bundleGroupVersion1)));
        resultActions = executeOkGetBundlesRequest(bundleGroupVersion1.getId(), null);
        AssertionHelper.assertOnBundles(resultActions, expectedList);

        // filter by bundleGroupVersionId will return anything if no public bundles are available
        executeGetBundlesRequest(bundleGroupVersion4.getId(), null, StatusResultMatchers::isNotFound);

        // filter by CatalogId
        expectedList = List.of(TestHelper.stubBundleDto(bundle1.getId(), List.of(bundleGroupVersion1)),
                TestHelper.stubBundleDto(bundle2.getId(), List.of(bundleGroupVersion2)));
        resultActions = executeOkGetBundlesRequest(null, catalog1.getId());
        AssertionHelper.assertOnBundles(resultActions, expectedList);
    }

    @Test
    void shouldReturnErrorWhileANonAdminUserAsksForProtectedData() throws Exception {

        // given I am logged but not as an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(true);
        when(securityHelperService.isAdmin()).thenReturn(false);
        when(securityHelperService.getContextAuthenticationUsername()).thenReturn(TestHelper.NON_ADMIN_USERNAME);

        // filter by bundleGroupVersionId and CatalogId (catalog to which the user doesn't belong to)
        executeGetBundlesRequest(bundleGroupVersion2.getId(), catalog2.getId(), StatusResultMatchers::isNotFound);

        // filter by CatalogId (catalog to which the user doesn't belong to)
        executeGetBundlesRequest(null, catalog2.getId(), StatusResultMatchers::isNotFound);
    }

    @Test
    void aNonLoggedUserShouldBeAbleToAccessTheExpectedBundle() throws Exception {

        // given I am an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(false);

        // without filters should get every public bundle
        List<BundleDto> expectedList = List.of(
                TestHelper.stubBundleDto(bundle1.getId(), List.of(bundleGroupVersion1)),
                TestHelper.stubBundleDto(bundle2.getId(), List.of(bundleGroupVersion2)),
                TestHelper.stubBundleDto(bundle3.getId(), List.of(bundleGroupVersion3)));
        ResultActions resultActions = executeOkGetBundlesRequest(null, null);
        AssertionHelper.assertOnBundles(resultActions, expectedList);

        // filter by bundleGroupVersionId and public bundle group
        expectedList = List.of(TestHelper.stubBundleDto(bundle1.getId(), List.of(bundleGroupVersion1)));
        resultActions = executeOkGetBundlesRequest(bundleGroupVersion1.getId(), null);
        AssertionHelper.assertOnBundles(resultActions, expectedList);
    }

    @Test
    void shouldNotReturnDataWhileNonLoggedAsksForProtectedData() throws Exception {

        // given I am an admin
        when(securityHelperService.isUserAuthenticated()).thenReturn(false);

        // filter by bundleGroupVersionId and CatalogId
        executeGetBundlesRequest(bundleGroupVersion1.getId(), catalog1.getId(), StatusResultMatchers::isForbidden);

        executeGetBundlesRequest(bundleGroupVersion4.getId(), null,StatusResultMatchers::isNotFound);
        // filter by CatalogId
        executeGetBundlesRequest(null, catalog2.getId(), StatusResultMatchers::isForbidden);
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldNotReturnDataWithInconsistentParameters() throws Exception {

        // given I am an admin but this behaviour should be the same in every case
        when(securityHelperService.isUserAuthenticated()).thenReturn(true);

        // with non existing catalogId
        executeGetBundlesRequest(null, 999L, StatusResultMatchers::isNotFound);

        // with non existing bundleGroupVersionId
        executeGetBundlesRequest(999L, null, StatusResultMatchers::isNotFound);

        // with inconsistent bundleGroupVersionId and catalogId
        executeGetBundlesRequest(bundleGroupVersion1.getId(), catalog2.getId(), StatusResultMatchers::isNotFound);
    }


    private ResultActions executeOkGetBundlesRequest(Long bundleGroupVersionId, Long catalogId) throws Exception {
        return executeGetBundlesRequest(bundleGroupVersionId, catalogId, StatusResultMatchers::isOk);
    }

    private ResultActions executeGetBundlesRequest(Long bundleGroupVersionId, Long catalogId,
            StatusMatcher statusMatcher) throws Exception {

        String url = BASE_URL + "/?";
        url += bundleGroupVersionId != null ? "bundleGroupVersionId=" + bundleGroupVersionId + "&" : "";
        url += catalogId != null ? "catalogId=" + catalogId + "&" : "";
        url = url.substring(0, url.length() - 1);

        return super.executeGetRequest(url, statusMatcher);
    }
}
