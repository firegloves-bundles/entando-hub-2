import { deleteData, getData, postData } from "./Http"
import { fireEvent, SUCCESS, FAIL } from "../helpers/eventDispatcher"

// endpoints
const urlOrganisations = `${process.env.REACT_APP_PUBLIC_API_URL}/organisation/`
const urlCategories = `${process.env.REACT_APP_PUBLIC_API_URL}/category/`
const urlBundles = `${process.env.REACT_APP_PUBLIC_API_URL}/bundles/`
const urlBundleGroups = `${process.env.REACT_APP_PUBLIC_API_URL}/bundlegroups/`
const urlBundleGroupsFilteredPaged = `${process.env.REACT_APP_PUBLIC_API_URL}/bundlegroups/filtered`
const urlUsers = `${process.env.REACT_APP_PUBLIC_API_URL}/users/`
const urlKC = `${process.env.REACT_APP_PUBLIC_API_URL}/keycloack/`

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

const eventHandler = (
  data,
  isError,
  objectLabel,
  actionLabel,
  onlyError = true
) => {
  if (isError) {
    console.log(
      `Impossible to ${actionLabel} ${objectLabel}. ${data ? data.message : ""}`
    )
    fireEvent(
      FAIL,
      `Impossible to ${actionLabel} ${objectLabel}. ${data ? data.message : ""}`
    )
    return
  }
  if (!isError && !onlyError) {
    console.log(
      `${objectLabel[0].toUpperCase()}${objectLabel.substring(1)} ${
        data ? data.data.name : ""
      } ${actionLabel}d`
    )
    fireEvent(
      SUCCESS,
      `${objectLabel[0].toUpperCase()}${objectLabel.substring(1)} ${
        data ? data.data.name : ""
      } ${actionLabel}d`
    )
    return
  }
}

/*********************
 * ORGANISATIONS *****
 *********************/

export const getAllOrganisations = async () => {
  let { data, isError } = await getData(urlOrganisations)

  eventHandler(data, isError, "organisations", "retrieve")

  return checkForErrorsAndSendResponse(data, isError, "organisationList")
}

export const getSingleOrganisation = async (id) => {
  const { data, isError } = await getData(urlOrganisations, id)

  eventHandler(data, isError, "organisation", "retrieve")

  return checkForErrorsAndSendResponse(data, isError, "organisation")
}

export const addNewOrganisation = async (organisationData) => {
  const { data, isError } = await postData(urlOrganisations, organisationData)

  eventHandler(data, isError, "organisation", "create", false)

  return checkForErrorsAndSendResponse(data, isError, "newOrganisation")
}

export const editOrganisation = async (organisationData, id) => {
  const { data, isError } = await postData(
    urlOrganisations,
    organisationData,
    id
  )

  eventHandler(data, isError, "organisation", "update", false)

  return checkForErrorsAndSendResponse(data, isError, "editedOrganisation")
}

export const deleteOrganisation = async (id) => {
  const { data, isError } = await deleteData(urlOrganisations, id)

  eventHandler(data, isError, "organisation", "delete", false)

  return checkForErrorsAndSendResponse(data, isError, "deletedOrganisation")
}

/*********************
 * CATEGORIES ********
 *********************/

export const getAllCategories = async () => {
  const { data, isError } = await getData(urlCategories)

  eventHandler(data, isError, "categories", "retrieve")

  return checkForErrorsAndSendResponse(data, isError, "categoryList")
}

export const getSingleCategory = async (id) => {
  const { data, isError } = await getData(urlCategories, id)

  eventHandler(data, isError, "category", "retrieve")

  return checkForErrorsAndSendResponse(data, isError, "category")
}

export const addNewCategory = async (categoryData) => {
  const { data, isError } = await postData(urlCategories, categoryData)

  eventHandler(data, isError, "category", "create", false)

  return checkForErrorsAndSendResponse(data, isError, "newCategory")
}

export const editCategory = async (categoryData, id) => {
  const { data, isError } = await postData(urlCategories, categoryData, id)

  eventHandler(data, isError, "category", "update", false)

  return checkForErrorsAndSendResponse(data, isError, "editedCategory")
}

/*********************
 * BUNDLES ***********
 *********************/

export const getAllBundles = async () => {
  const { data, isError } = await getData(urlBundles)

  eventHandler(data, isError, "bundles", "retrieve")

  return checkForErrorsAndSendResponse(data, isError, "bundleList")
}

export const getAllBundlesForABundleGroup = async (id) => {
  const newUrl = `${urlBundles}?bundleGroupId=${id}`
  const { data, isError } = await getData(newUrl)

  eventHandler(data, isError, "bundles", "retrieve")

  return checkForErrorsAndSendResponse(data, isError, "bundleList")
}

export const getSingleBundle = async (id) => {
  const { data, isError } = await getData(urlBundles, id)

  eventHandler(data, isError, "bundle", "retrieve")

  return checkForErrorsAndSendResponse(data, isError, "bundleGroup")
}

export const addNewBundle = async (bundleData) => {
  const { data, isError } = await postData(urlBundles, bundleData)

  eventHandler(data, isError, "bundle", "create", false)

  return checkForErrorsAndSendResponse(data, isError, "newBundle")
}

export const editBundle = async (bundleData, id) => {
  const { data, isError } = await postData(urlBundles, bundleData, id)

  eventHandler(data, isError, "bundle", "update", false)

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

  eventHandler(data, isError, "bundle groups", "retrieve")

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

  eventHandler(data, isError, "bundle groups", "retrieve")

  return checkForErrorsAndSendResponse(data, isError, "bundleGroupList")
}

export const getSingleBundleGroup = async (id) => {
  const { data, isError } = await getData(urlBundleGroups, id)

  eventHandler(data, isError, "bundle group", "retrieve")

  return checkForErrorsAndSendResponse(data, isError, "bundleGroup")
}

export const addNewBundleGroup = async (bundleGroupData) => {
  const { data, isError } = await postData(urlBundleGroups, bundleGroupData)

  eventHandler(data, isError, "bundle group", "create", false)

  return checkForErrorsAndSendResponse(data, isError, "newBundleGroup")
}

export const editBundleGroup = async (bundleGroupData, id) => {
  const { data, isError } = await postData(urlBundleGroups, bundleGroupData, id)

  eventHandler(data, isError, "bundle group", "update", false)

  return checkForErrorsAndSendResponse(data, isError, "editedBundleGroup")
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

  return checkForErrorsAndSendResponse(data, isError, "newUserForOrganization")
}

// GET input: nothing -> get all the users
export const getAllUsers = async () => {
  const { data, isError } = await getData(urlUsers)

  return checkForErrorsAndSendResponse(data, isError, "userList")
}

// GET input: organization id -> get all the users for that organization
// query string: organization id
export const getAllUserForAnOrganisation = async (organisationId) => {
  const newUrl = `${urlUsers}?organisationId=${organisationId}`
  const { data, isError } = await getData(newUrl)

  return checkForErrorsAndSendResponse(data, isError, "userList")
}

// DELETE input: username -> delete the user
// path: username
export const deleteUser = async (username) => {
  const newUrl = `${urlUsers}${username}`
  const { data } = await deleteData(newUrl)

  return data
}

// DELETE input: organization id and username -> remove the user from that organization
// path: username
// path: organization id
export const removeUserFromOrganisation = async (organisationId, username) => {
  const newUrl = `${urlUsers}${organisationId}/user/${username}`
  const { data } = await deleteData(newUrl)

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
