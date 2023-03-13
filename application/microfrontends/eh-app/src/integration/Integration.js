import { deleteData, getData, postData } from "./Http"
import { fireEvent, SUCCESS, FAIL } from "../helpers/eventDispatcher"
import { API_RESPONSE_KEY, DELETED_BUNDLE, HTTP_STATUS } from "../helpers/constants";
import i18n from "../i18n";

// endpoints
const urlOrganisations = '/api/organisation/'
const urlCategories = '/api/category/'
const urlBundles = '/api/bundles/'
const urlBundleGroups = '/api/bundlegroups/'
const urlCatalogs = '/api/catalog/'
const urlUsers = '/api/users/'
const urlKC = '/api/keycloak/'
const urlBundleGroupVersion = '/api/bundlegroupversions/'

// checks if the input data contain an error and sends back either the error itself or the actual data
const checkForErrorsAndSendResponse = (data, isError, objectLabel) => {
  if (isError) {
    return {
      errorBody: data,
      isError,
    }
  } else {
    return {
      [objectLabel]: data,
      isError,
    }
  }
}

const eventHandler = (isError, failMessage, successMessage) => {
  if (successMessage) {
    if (!isError) {
      fireEvent(SUCCESS, successMessage)
    }
  }

  if (isError) {
    console.error(`[ --- FATAL ERROR --- ] ${failMessage}`)
    fireEvent(FAIL, failMessage)
  }
}

/*********************
 * ORGANISATIONS *****
 *********************/

export const getAllOrganisations = async (apiUrl) => {
  let { data, isError } = await getData(apiUrl + urlOrganisations)
  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToLoadOrganisations')} ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "organisationList")
}

export const getSingleOrganisation = async (apiUrl,id) => {
  const { data, isError } = await getData(apiUrl+urlOrganisations, id)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToLoadOrganisation')} ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "organisation")
}

export const addNewOrganisation = async (apiUrl, organisationData) => {
  const { data, isError } = await postData(apiUrl + urlOrganisations, organisationData)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToCreateOrganisation')} ${data ? data.message : ""}`,
    `${i18n.t('component.bundleModalFields.organisation')} ${data ? data.name : ""} ${i18n.t('toasterMessage.created')}`
  )

  return checkForErrorsAndSendResponse(data, isError, "newOrganisation")
}

export const editOrganisation = async (apiUrl, organisationData, id) => {
    const { data, isError } = await postData(
      apiUrl + urlOrganisations,
    organisationData,
    id
  )

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToUpdateOrganisation')} ${data ? data.message : ""}`,
    `${i18n.t('component.bundleModalFields.organisation')} ${data ? data.name : ""} ${i18n.t('toasterMessage.updated')}`
  )

  return checkForErrorsAndSendResponse(data, isError, "editedOrganisation")
}

export const deleteOrganisation = async (apiUrl, id) => {
  const { data, isError } = await deleteData(apiUrl + urlOrganisations, id)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToDeleteOrganisation')} ${data ? data.message : ""}`,
    `${i18n.t('component.bundleModalFields.organisation')} ${data ? data.name : ""} ${i18n.t('toasterMessage.deleted')}`
  )

  return checkForErrorsAndSendResponse(data, isError, "deletedOrganisation")
}

/*********************
 * CATEGORIES ********
 *********************/

export const getAllCategories = async (apiUrl) => {
  const { data, isError } = await getData(apiUrl + urlCategories)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToLoadCategory')} ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "categoryList")
}

export const getSingleCategory = async (apiUrl, id) => {
  const { data, isError } = await getData(apiUrl + urlCategories, id)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToLoadCategory')} ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "category")
}

export const addNewCategory = async (apiUrl, categoryData) => {
  const { data, isError } = await postData(apiUrl + urlCategories, categoryData)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToCreateCategory')}  ${data ? data.message : ""}`,
    `${i18n.t('component.bundleModalFields.category')} ${data ? data.name : ""} ${i18n.t('toasterMessage.created')}`
  )

  return checkForErrorsAndSendResponse(data, isError, "newCategory")
}

export const editCategory = async (apiUrl, categoryData, id) => {
  const { data, isError } = await postData(apiUrl + urlCategories, categoryData, id)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToUpdateCategory')}  ${data ? data.message : ""}`,
    `${i18n.t('component.bundleModalFields.category')} ${data ? data.name : ""} ${i18n.t('toasterMessage.updated')}`
  )

  return checkForErrorsAndSendResponse(data, isError, "editedCategory")
}

const CATEGORY_APPLIED_ON_BUNDLE_GROUP_MSG = "This category is already in use."
export const deleteCategory = async (apiUrl, id, categoryName) => {
  const { data, isError } = await deleteData(apiUrl + urlCategories, id)
  const dataMessageLength = data.message ? data.message.split(" ").length : null
  const statusCode = dataMessageLength ? data.message.split(" ")[dataMessageLength - 1] : 0

  if (statusCode && statusCode === HTTP_STATUS.EXPECTATION_FAILED) {
    data.message = CATEGORY_APPLIED_ON_BUNDLE_GROUP_MSG
  }

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToDeleteCategory')} ${data ? data.message : ""}`,
    `${i18n.t('component.bundleModalFields.category')} ${categoryName ? categoryName : ""}  ${i18n.t('toasterMessage.deleted')}`
  )

  return checkForErrorsAndSendResponse(data, isError, "deletedCategory")
}


/*********************
 * BUNDLES ***********
 *********************/

export const getAllBundles = async (apiUrl) => {
  const { data, isError } = await getData(apiUrl + urlBundles)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToLoadBundles')} ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleList")
}

export const getAllBundlesForABundleGroup = async (apiUrl, id) => {
  const newUrl = `${apiUrl+urlBundles}?bundleGroupVersionId=${id}`
  const { data, isError } = await getData(newUrl)

  eventHandler(
    isError,
   `${i18n.t('toasterMessage.impossibleToLoadBundles')} ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleList")
}

export const getSingleBundle = async (apiUrl, id) => {
  const { data, isError } = await getData(apiUrl + urlBundles, id)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToLoadBundle')} ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleGroup")
}

export const addNewBundle = async (apiUrl, bundleData) => {
  const { data, isError } = await postData(apiUrl + urlBundles, bundleData)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToCreateBundle')} ${data ? data.message : ""}`,
    `${i18n.t('toasterMessage.bundle')} ${data ? data.name : ""} ${i18n.t('toasterMessage.created')}`
  )

  return checkForErrorsAndSendResponse(data, isError, "newBundle")
}

export const editBundle = async (apiUrl, bundleData, id) => {
  const { data, isError } = await postData(apiUrl + urlBundles, bundleData, id)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToUpdateBundle')} ${data ? data.message : ""}`,
    `${i18n.t('toasterMessage.bundle')} ${data ? data.name : ""} ${i18n.t('toasterMessage.updated')}`
  )

  return checkForErrorsAndSendResponse(data, isError, "editedBundle")
}

/*********************
 * BUNDLE GROUPS *****
 *********************/

export const getAllBundleGroups = async (apiUrl, organisationId) => {
  let url = apiUrl+ urlBundleGroups
  if (organisationId)
    url = apiUrl+urlBundleGroups + "?organisationId=" + organisationId
  const { data, isError } = await getData(url)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToLoadBundleGroups')} ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleGroupList")
}

/**
 * GET bundle groups/versions filtered
 * @param {*} page
 * @param {*} pageSize
 * @param {*} organisationId
 * @param {*} categoryIds
 * @param {*} statuses
 * @returns
 */
export const getAllBundleGroupsFilteredPaged = async (
  apiUrl, {
    page,
    pageSize,
    organisationId,
    categoryIds,
    statuses,
    catalogId,
    searchText = null,
  },
) => {
  let url = `${apiUrl}${urlBundleGroupVersion}`;
  url += catalogId ? `catalog/${catalogId}` : 'filtered';
  url += `?page=${page}&pageSize=${pageSize}`;

  if (categoryIds && categoryIds.length > 0) {
    const categoryIdsQueryParams = categoryIds.map((categoryId) => `categoryIds=${categoryId}`).join('&');
    url += `&${categoryIdsQueryParams}`;
  }

  if (statuses && statuses.length > 0) {
    const statusesQueryParams = statuses.map((status) => `statuses=${status}`).join('&');
    url += `&${statusesQueryParams}`;
  }

  if (organisationId) {
    url += `&organisationId=${organisationId}`;
  }

  if (searchText) {
    url += `&searchText=${searchText}`;
  }

  const { data, isError } = await getData(url);

  eventHandler(
    isError,
    `Impossible to load bundle groups: ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleGroupList")
}

export const getSingleBundleGroup = async (apiUrl,id) => {
  const { data, isError } = await getData(apiUrl+urlBundleGroups, id)

  eventHandler(
    isError,
    `Impossible to load bundle group: ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleGroup")
}

export const addNewBundleGroup = async (apiUrl,bundleGroupData) => {
  const { data, isError } = await postData(apiUrl+urlBundleGroups, bundleGroupData)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToCreateBundleGroup')} ${data ? data.message : ""}`,
    `${i18n.t('toasterMessage.bundleGroup')} ${data ? data.name : ""} ${i18n.t('toasterMessage.created')}`
  )

  return checkForErrorsAndSendResponse(data, isError, "newBundleGroup")
}

export const editBundleGroup = async (apiUrl,bundleGroupData, id) => {
  const { data, isError } = await postData(apiUrl+urlBundleGroups, bundleGroupData, id)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToUpdateBundleGroup')} ${data ? data.message : ""}`,
    `${i18n.t('toasterMessage.bundleGroup')} ${data ? data.name : ""} ${i18n.t('toasterMessage.updated')}`
  )

  return checkForErrorsAndSendResponse(data, isError, API_RESPONSE_KEY.EDITED_BUNDLE_GROUP)
}

export const deleteBundle = async (apiUrl,id, bundleName) => {
  const { data, isError } = await deleteData(apiUrl+urlBundleGroups, id)

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToDeleteBundle')}  ${data ? data.message : ""}`,
    `${i18n.t('toasterMessage.bundle')} ${bundleName ? bundleName : ""} ${i18n.t('toasterMessage.deleted')}`
  )

  return checkForErrorsAndSendResponse(data, isError, DELETED_BUNDLE)
}

/*********************
 * USERS *************
 *********************/

// POST input: username and organization id -> create a user and assign it to that organization
// path: organization id
// req body: username
export const createAUserForAnOrganisation = async (
  apiUrl,
  organisationId,
  userData,
  type
) => {
  const newUrl = `${apiUrl+urlUsers}${organisationId}`
  const userDataObject = {
    username: userData,
  }
  const { data, isError } = await postData(newUrl, userDataObject)
  if (type === 'update') {
    debugger
    eventHandler(isError,
      `${i18n.t('toasterMessage.impossibleToCreateUser')}`,
      `${i18n.t('toasterMessage.user')} ${userData ? userData : ""} ${i18n.t('toasterMessage.updated')}`
    )
  } else {
    eventHandler(isError,
      `${i18n.t('toasterMessage.impossibleToCreateUser')}`,
      `${i18n.t('toasterMessage.user')} ${userData ? userData : ""} ${i18n.t('toasterMessage.created')}`
    )
  }

  return checkForErrorsAndSendResponse(data, isError, "newUserForOrganization")
}

// GET input: nothing -> get all the users
export const getAllUsers = async (apiUrl) => {
  const { data, isError } = await getData(apiUrl,urlUsers)

  eventHandler(isError, `${i18n.t('toasterMessage.impossibleToLoadUsers')}`)

  return checkForErrorsAndSendResponse(data, isError, "userList")
}

// GET input: organization id -> get all the users for that organization
// query string: organization id
export const getAllUserForAnOrganisation = async (apiUrl,organisationId) => {
  const newUrl = `${apiUrl+urlUsers}?organisationId=${organisationId}`
  const { data, isError } = await getData(newUrl)

  eventHandler(isError, `${i18n.t('toasterMessage.impossibleToLoadUsers')}`)

  return checkForErrorsAndSendResponse(data, isError, "userList")
}

// DELETE input: username -> delete the user
// path: username
export const deleteUser = async (apiUrl,username) => {
  const newUrl = `${apiUrl+urlUsers}${username}`
  const { data, isError } = await deleteData(newUrl)

  eventHandler(isError, `${i18n.t('toasterMessage.impossibleToDeleteUser')}`, `User deleted`)

  return data
}

// DELETE input: organization id and username -> remove the user from that organization
// path: username
// path: organization id
export const removeUserFromOrganisation = async (apiUrl,organisationId, username, type) => {
  const newUrl = `${apiUrl+urlUsers}${organisationId}/user/${username}`
  const { data, isError } = await deleteData(newUrl)
  // while updating user no need to show 'user removed toaster'
  if (type === 'update') {
    eventHandler(isError, `${i18n.t('toasterMessage.impossibleToRemoveUser')}`, ``);
  } else {
    eventHandler(isError, `${i18n.t('toasterMessage.impossibleToRemoveUser')}`, `${i18n.t('toasterMessage.userRemovedFromTheOrganisation')}`);
  }

  return data
}

/**
 * Get a portal user by username.
 * @param {*} username
 * @returns
 */
 export const getPortalUser = async (apiUrl) => {
  const { data, isError } = await getData(apiUrl, `${urlUsers}details`)

  return checkForErrorsAndSendResponse(data, isError, API_RESPONSE_KEY.PORTAL_USER);
}

/*********************
 * KC *************
 *********************/
/*
        {
            "id": "e7a0ae5d-59ab-40c7-a510-ae4756cc5044",
            "created": "2021-09-02T21:49:36.409+00:00",
            "username": "admin",
            "enabled": true,
            "firstName": null,
            "lastName": null,
            "email": null,
            "organisationIds": []
        }
}
*/
export const getAllKCUsers = async (apiUrl) => {
  const newUrl = `${apiUrl+urlKC}users`
  const { data, isError } = await getData(newUrl)

  return checkForErrorsAndSendResponse(data, isError, "kcUsers")
}

/**************************
 * Bundle Group Version
***************************/

/**
 * Add new bundle group version
 * @param {*} bundleGroupVersionData
 * @param {*} bundleGroupId
 * @returns
 */
 export const addNewBundleGroupVersion = async (apiUrl, bundleGroupVersionData) => {
  const { data, isError } = await postData(apiUrl+ urlBundleGroupVersion, bundleGroupVersionData)
  eventHandler(
    isError,
    `${i18n.t('toasterMessage.unableToAddBundleGroupVersion')} ${data ? data.message : ""}`,
    `${i18n.t('toasterMessage.bundleGroupVersion')} ${data ? data.name : ""} saved`
  )

  return checkForErrorsAndSendResponse(data, isError, API_RESPONSE_KEY.EDITED_BUNDLE_GROUP)
}

/**
 * Get all bundle group versions by bundleGroupId
 * @param {*} bundleGroupId
 */
 export const getAllBundleGroupVersionByBundleGroupId = async (apiUrl, bundleGroupId, page, pageSize, bundleStatuses) => {
  // let url = `${urlBundleGroupVersion}versions/${bundleGroupId}?page=${page}&pageSize=${pageSize}`;
  let url = `${apiUrl + urlBundleGroupVersion}versions/${bundleGroupId}?page=${page}&pageSize=${pageSize}${(!bundleStatuses || bundleStatuses.toString() === '-1') ? '' : `&statuses=${bundleStatuses}`}`;
  const { data, isError } = await getData(url);
  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToLoadBundleGroupVersions')} : ${data ? data.message : ""}`
  )
  return checkForErrorsAndSendResponse(data, isError, "versions")
}

/**
 * Delete a bundle group version
 * @param {*} id
 * @param {*} bundleName
 * @returns
 */
export const deleteBundleGroupVersion = async (apiUrl,bundleGroupVersionId) => {
  const { data, isError } = await deleteData(apiUrl+urlBundleGroupVersion, bundleGroupVersionId);
  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToDeleteBundle')}  ${data ? data.message : ""}`,
    `${i18n.t('toasterMessage.bundleGroupVersion')} ${i18n.t('toasterMessage.deleted')}`
  )
  return checkForErrorsAndSendResponse(data, isError, DELETED_BUNDLE);
}

/**
 * Update a bundle group version
 * @param {*} bundleGroupVersionData
 * @param {*} bundleGroupVersionId
 * @returns
 */
export const editBundleGroupVersion = async (apiUrl,bundleGroupVersionData, bundleGroupVersionId) => {
  const { data, isError } = await postData(apiUrl+urlBundleGroupVersion, bundleGroupVersionData, bundleGroupVersionId)
  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToUpdateBundleGroup')} ${data ? data.message : ""}`,
    `${i18n.t('toasterMessage.bundleGroup')} ${bundleGroupVersionData.name} ${i18n.t('toasterMessage.updated')}`
  )

  return checkForErrorsAndSendResponse(data, isError, API_RESPONSE_KEY.EDITED_BUNDLE_GROUP)
}

/**
 * Get bundle group details by bundle group version id
 * @param {*} bundleGroupVersionId
 * @returns
 */
 export const getBundleGroupDetailsByBundleGroupVersionId = async (apiUrl,bundleGroupVersionId) => {
  let newUrl = `${apiUrl+urlBundleGroupVersion}${bundleGroupVersionId}`;
  const { data, isError } = await getData(newUrl)

  eventHandler(isError, `${i18n.t('toasterMessage.impossibleToLoadUsers')}`)

  return checkForErrorsAndSendResponse(data, isError, "bgVersionDetails")
}

/*******************
 * CATALOGS ********
 *******************/

export const createPrivateCatalog = async (apiUrl, organisationId) => {
  const url = `${apiUrl}${urlCatalogs}`;
  const { data, isError } = await postData(url, null, organisationId);

  eventHandler(
    isError,
    `${i18n.t('toasterMessage.impossibleToCreatePrivateCatalog')}: ${data?.message || ''}`,
    `${i18n.t('toasterMessage.created')} ${data.name}`,
  );

  return {
    data,
    isError,
  };
};

export const getPrivateCatalogs = async (apiUrl) => {
  const url = `${apiUrl}${urlCatalogs}`;
  const { data, isError } = await getData(url);
  eventHandler(isError, `${i18n.t('toasterMessage.impossibleToLoadPrivateCatalogs')}: ${data?.message || ''}`);
  return {
    data,
    isError,
  };
};
