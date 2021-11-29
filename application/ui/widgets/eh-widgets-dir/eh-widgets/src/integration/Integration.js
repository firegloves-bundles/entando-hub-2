import { deleteData, getData, postData } from "./Http"
import { fireEvent, SUCCESS, FAIL } from "../helpers/eventDispatcher"
import { API_RESPONSE_KEY } from "../helpers/constants";

// endpoints
const urlOrganisations = `${process.env.REACT_APP_PUBLIC_API_URL}/organisation/`
const urlCategories = `${process.env.REACT_APP_PUBLIC_API_URL}/category/`
const urlBundles = `${process.env.REACT_APP_PUBLIC_API_URL}/bundles/`
const urlBundleGroups = `${process.env.REACT_APP_PUBLIC_API_URL}/bundlegroups/`
const urlBundleGroupsFilteredPaged = `${process.env.REACT_APP_PUBLIC_API_URL}/bundlegroups/filtered`
const urlUsers = `${process.env.REACT_APP_PUBLIC_API_URL}/users/`
const urlKC = `${process.env.REACT_APP_PUBLIC_API_URL}/keycloak/`

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

export const getAllOrganisations = async () => {
  let { data, isError } = await getData(urlOrganisations)

  eventHandler(
    isError,
    `Impossible to load organisations. ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "organisationList")
}

export const getSingleOrganisation = async (id) => {
  const { data, isError } = await getData(urlOrganisations, id)

  eventHandler(
    isError,
    `Impossible to load organisation. ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "organisation")
}

export const addNewOrganisation = async (organisationData) => {
  const { data, isError } = await postData(urlOrganisations, organisationData)

  eventHandler(
    isError,
    `Impossible to create organisation. ${data ? data.message : ""}`,
    `Organisation ${data.data ? data.data.name : ""} created`
  )

  return checkForErrorsAndSendResponse(data, isError, "newOrganisation")
}

export const editOrganisation = async (organisationData, id) => {
  const { data, isError } = await postData(
    urlOrganisations,
    organisationData,
    id
  )

  eventHandler(
    isError,
    `Impossible to update organisation. ${data ? data.message : ""}`,
    `Organisation ${data.data ? data.data.name : ""} updated`
  )

  return checkForErrorsAndSendResponse(data, isError, "editedOrganisation")
}

export const deleteOrganisation = async (id) => {
  const { data, isError } = await deleteData(urlOrganisations, id)

  console.log("HERE", data, isError)
  eventHandler(
    isError,
    `Impossible to delete organisation. ${data ? data.message : ""}`,
    `Organisation ${data.data ? data.data.name : ""} deleted`
  )

  return checkForErrorsAndSendResponse(data, isError, "deletedOrganisation")
}

/*********************
 * CATEGORIES ********
 *********************/

export const getAllCategories = async () => {
  const { data, isError } = await getData(urlCategories)

  eventHandler(
    isError,
    `Impossible to load categories. ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "categoryList")
}

export const getSingleCategory = async (id) => {
  const { data, isError } = await getData(urlCategories, id)

  eventHandler(
    isError,
    `Impossible to load category. ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "category")
}

export const addNewCategory = async (categoryData) => {
  const { data, isError } = await postData(urlCategories, categoryData)

  eventHandler(
    isError,
    `Impossible to create category. ${data ? data.message : ""}`,
    `Category ${data.data ? data.data.name : ""} created`
  )

  return checkForErrorsAndSendResponse(data, isError, "newCategory")
}

export const editCategory = async (categoryData, id) => {
  const { data, isError } = await postData(urlCategories, categoryData, id)

  eventHandler(
    isError,
    `Impossible to update category. ${data ? data.message : ""}`,
    `Category ${data.data ? data.data.name : ""} updated`
  )

  return checkForErrorsAndSendResponse(data, isError, "editedCategory")
}

/*********************
 * BUNDLES ***********
 *********************/

export const getAllBundles = async () => {
  const { data, isError } = await getData(urlBundles)

  eventHandler(
    isError,
    `Impossible to load bundles. ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleList")
}

export const getAllBundlesForABundleGroup = async (id) => {
  const newUrl = `${urlBundles}?bundleGroupId=${id}`
  const { data, isError } = await getData(newUrl)

  eventHandler(
    isError,
    `Impossible to load bundles. ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleList")
}

export const getSingleBundle = async (id) => {
  const { data, isError } = await getData(urlBundles, id)

  eventHandler(
    isError,
    `Impossible to load bundle. ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleGroup")
}

export const addNewBundle = async (bundleData) => {
  const { data, isError } = await postData(urlBundles, bundleData)

  eventHandler(
    isError,
    `Impossible to create bundle. ${data ? data.message : ""}`,
    `Bundle ${data.data ? data.data.name : ""} created`
  )

  return checkForErrorsAndSendResponse(data, isError, "newBundle")
}

export const editBundle = async (bundleData, id) => {
  const { data, isError } = await postData(urlBundles, bundleData, id)

  eventHandler(
    isError,
    `Impossible to update bundle. ${data ? data.message : ""}`,
    `Bundle ${data.data ? data.data.name : ""} updated`
  )

  return checkForErrorsAndSendResponse(data, isError, "editedBundle")
}

/*********************
 * BUNDLE GROUPS *****
 *********************/

export const getAllBundleGroups = async (organisationId) => {
  let url = urlBundleGroups
  if (organisationId)
    url = urlBundleGroups + "?organisationId=" + organisationId
  const { data, isError } = await getData(url)

  eventHandler(
    isError,
    `Impossible to load bundle groups. ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleGroupList")
}

export const getAllBundleGroupsFilteredPaged = async (
  page,
  pageSize,
  organisationId,
  categoryIds,
  statuses
) => {
  let url = `${urlBundleGroupsFilteredPaged}?page=${page}&pageSize=${pageSize}`
  if (categoryIds && categoryIds.length > 0) {
    url =
      url +
      "&" +
      categoryIds.map((categoryId) => `categoryIds=${categoryId}`).join("&")
  }
  if (statuses && statuses.length > 0) {
    statuses.map((status) => `statuses=${status}`).join("&")
    url = url + "&" + statuses.map((status) => `statuses=${status}`).join("&")
  }

  if (organisationId) url = url + "&organisationId=" + organisationId
  const { data, isError } = await getData(url)

  eventHandler(
    isError,
    `Impossible to load bundle groups: ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleGroupList")
}

export const getSingleBundleGroup = async (id) => {
  const { data, isError } = await getData(urlBundleGroups, id)

  eventHandler(
    isError,
    `Impossible to load bundle group: ${data ? data.message : ""}`
  )

  return checkForErrorsAndSendResponse(data, isError, "bundleGroup")
}

export const addNewBundleGroup = async (bundleGroupData) => {
  const { data, isError } = await postData(urlBundleGroups, bundleGroupData)

  eventHandler(
    isError,
    `Impossible to create bundle group. ${data ? data.message : ""}`,
    `Bundle group ${data.data ? data.data.name : ""} created`
  )

  return checkForErrorsAndSendResponse(data, isError, "newBundleGroup")
}

export const editBundleGroup = async (bundleGroupData, id) => {
  const { data, isError } = await postData(urlBundleGroups, bundleGroupData, id)

  eventHandler(
    isError,
    `Impossible to update bundle group. ${data ? data.message : ""}`,
    `Bundle group ${data.data ? data.data.name : ""} updated`
  )

  return checkForErrorsAndSendResponse(data, isError, API_RESPONSE_KEY.EDITED_BUNDLE_GROUP)
}

export const deleteBundle = async (id, bundleName) => {
  const { data, isError } = await deleteData(urlBundleGroups, id)

  eventHandler(
    isError,
    `Impossible to delete bundle. ${data ? data.message : ""}`,
    `Bundle ${bundleName ? bundleName : ""} deleted`
  )

  return checkForErrorsAndSendResponse(data, isError, "deletedBundle")
}

/*********************
 * USERS *************
 *********************/

// POST input: username and organization id -> create a user and assign it to that organization
// path: organization id
// req body: username
export const createAUserForAnOrganisation = async (
  organisationId,
  userData
) => {
  const newUrl = `${urlUsers}${organisationId}`
  const userDataObject = {
    username: userData,
  }
  const { data, isError } = await postData(newUrl, userDataObject)

  eventHandler(isError, `Impossible to create user`, `User created`)

  return checkForErrorsAndSendResponse(data, isError, "newUserForOrganization")
}

// GET input: nothing -> get all the users
export const getAllUsers = async () => {
  const { data, isError } = await getData(urlUsers)

  eventHandler(isError, `Impossible to load users`)

  return checkForErrorsAndSendResponse(data, isError, "userList")
}

// GET input: organization id -> get all the users for that organization
// query string: organization id
export const getAllUserForAnOrganisation = async (organisationId) => {
  const newUrl = `${urlUsers}?organisationId=${organisationId}`
  const { data, isError } = await getData(newUrl)

  eventHandler(isError, `Impossible to load users`)

  return checkForErrorsAndSendResponse(data, isError, "userList")
}

// DELETE input: username -> delete the user
// path: username
export const deleteUser = async (username) => {
  const newUrl = `${urlUsers}${username}`
  const { data, isError } = await deleteData(newUrl)

  eventHandler(isError, `Impossible to delete user`, `User deleted`)

  return data
}

// DELETE input: organization id and username -> remove the user from that organization
// path: username
// path: organization id
export const removeUserFromOrganisation = async (organisationId, username) => {
  const newUrl = `${urlUsers}${organisationId}/user/${username}`
  const { data, isError } = await deleteData(newUrl)

  eventHandler(isError, `Impossible to remove user`, `User removed`)

  return data
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
export const getAllKCUsers = async () => {
  const newUrl = `${urlKC}users`
  const { data, isError } = await getData(newUrl)

  return checkForErrorsAndSendResponse(data, isError, "kcUsers")
}
