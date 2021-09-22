import { getData, postData, deleteData } from "./Http"

// endpoints
const urlOrganisations = `${process.env.REACT_APP_PUBLIC_API_URL}/organisation/`
const urlCategories = `${process.env.REACT_APP_PUBLIC_API_URL}/category/`
const urlBundles = `${process.env.REACT_APP_PUBLIC_API_URL}/bundles/`
const urlBundleGroups = `${process.env.REACT_APP_PUBLIC_API_URL}/bundlegroups/`
const urlUsers = `${process.env.REACT_APP_PUBLIC_API_URL}/users/`

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

/*********************
 * ORGANISATIONS *****
 *********************/

export const getAllOrganisations = async () => {
  const { data, isError } = await getData(urlOrganisations)

  return checkForErrorsAndSendResponse(data, isError, "organisationList")
}

export const getSingleOrganisation = async (id) => {
  const { data, isError } = await getData(urlOrganisations, id)

  return checkForErrorsAndSendResponse(data, isError, "organisation")
}

export const addNewOrganisation = async (organisationData) => {
  const { data, isError } = await postData(urlOrganisations, organisationData)

  return checkForErrorsAndSendResponse(data, isError, "newOrganisation")
}

export const editOrganisation = async (organisationData, id) => {
  const { data, isError } = await postData(
    urlOrganisations,
    organisationData,
    id
  )

  return checkForErrorsAndSendResponse(data, isError, "editedOrganisation")
}

/*********************
 * CATEGORIES ********
 *********************/

export const getAllCategories = async () => {
  const { data, isError } = await getData(urlCategories)

  return checkForErrorsAndSendResponse(data, isError, "categoryList")
}

export const getSingleCategory = async (id) => {
  const { data, isError } = await getData(urlCategories, id)

  return checkForErrorsAndSendResponse(data, isError, "category")
}

export const addNewCategory = async (categoryData) => {
  const { data, isError } = await postData(urlCategories, categoryData)

  return checkForErrorsAndSendResponse(data, isError, "newCategory")
}

export const editCategory = async (categoryData, id) => {
  const { data, isError } = await postData(urlCategories, categoryData, id)

  return checkForErrorsAndSendResponse(data, isError, "editedCategory")
}

/*********************
 * BUNDLES ***********
 *********************/

export const getAllBundles = async () => {
  const { data, isError } = await getData(urlBundles)

  return checkForErrorsAndSendResponse(data, isError, "bundleList")
}

export const getAllBundlesForABundleGroup = async (id) => {
  const newUrl = `${urlBundles}?bundleGroupId=${id}`
  const { data, isError } = await getData(newUrl)

  return checkForErrorsAndSendResponse(data, isError, "bundleList")
}

export const getSingleBundle = async (id) => {
  const { data, isError } = await getData(urlBundles, id)

  return checkForErrorsAndSendResponse(data, isError, "bundleGroup")
}

export const addNewBundle = async (bundleData) => {
  const { data, isError } = await postData(urlBundles, bundleData)

  return checkForErrorsAndSendResponse(data, isError, "newBundle")
}

export const editBundle = async (bundleData, id) => {
  const { data, isError } = await postData(urlBundles, bundleData, id)

  return checkForErrorsAndSendResponse(data, isError, "editedBundle")
}

/*********************
 * BUNDLE GROUPS *****
 *********************/

export const getAllBundleGroups = async () => {
  const { data, isError } = await getData(urlBundleGroups)

  return checkForErrorsAndSendResponse(data, isError, "bundleGroupList")
}

export const getSingleBundleGroup = async (id) => {
  const { data, isError } = await getData(urlBundleGroups, id)

  return checkForErrorsAndSendResponse(data, isError, "bundleGroup")
}

export const addNewBundleGroup = async (bundleGroupData) => {
  const { data, isError } = await postData(urlBundleGroups, bundleGroupData)

  return checkForErrorsAndSendResponse(data, isError, "newBundleGroup")
}

export const editBundleGroup = async (bundleGroupData, id) => {
  const { data, isError } = await postData(urlBundleGroups, bundleGroupData, id)

  return checkForErrorsAndSendResponse(data, isError, "editedBundleGroup")
}

/*********************
 * USERS *************
 *********************/

// POST input: username and organization id -> create a user and assign it to that organization
// path: organization id
// req body: username
export const createAUserForAnOrganisation = async (organisationId, userData) => {
  const newUrl = `${urlUsers}${organisationId}`
  const { data, isError } = await postData(newUrl, userData)

  return checkForErrorsAndSendResponse(data, isError, "newUser")
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

  return data;
}

// DELETE input: organization id and username -> remove the user from that organization
// path: username
// path: organization id
export const removeUserFromOrganisation = async (organisationId, username) => {
  const newUrl = `${urlUsers}${organisationId}/user/${username}`
  const { data } = await deleteData(newUrl)

  return data;
}