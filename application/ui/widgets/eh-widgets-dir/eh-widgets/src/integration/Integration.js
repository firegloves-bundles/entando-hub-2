import { getData, postData } from "./Http";

// endpoints
const urlOrganisations = `${process.env.REACT_APP_PUBLIC_API_URL}/organisation/`;
const urlCategories = `${process.env.REACT_APP_PUBLIC_API_URL}/category/`;
const urlBundles = `${process.env.REACT_APP_PUBLIC_API_URL}/bundles/`;
const urlBundleGroups = `${process.env.REACT_APP_PUBLIC_API_URL}/bundlegroups/`;

// checks if the input data contain an error and sends back either the error itself or the actual data
const checkForErrorsAndSendResponse = (data, isError, objectLabel) => {
  if (isError) {
    return {
      errorBody: data,
      isError,
    };
  } else {
    return {
      [objectLabel]: data,
      isError,
    };
  }
};

/*********************
 * ORGANISATIONS *****
 *********************/

export const getAllOrganisations = async () => {
  const { data, isError } = await getData(urlOrganisations);

  return checkForErrorsAndSendResponse(data, isError, "organisationList");
};

export const getSingleOrganisation = async (id) => {
  const { data, isError } = await getData(urlOrganisations, id);

  return checkForErrorsAndSendResponse(data, isError, "organisation");
};

export const addNewOrganisation = async (organisationData) => {
  const { data, isError } = await postData(urlOrganisations, organisationData);

  return checkForErrorsAndSendResponse(data, isError, "newOrganisation");
};

export const editOrganisation = async (organisationData, id) => {
  const { data, isError } = await postData(
    urlOrganisations,
    organisationData,
    id
  );

  return checkForErrorsAndSendResponse(data, isError, "editedOrganisation");
};

/*********************
 * CATEGORIES ********
 *********************/

export const getAllCategories = async () => {
  const { data, isError } = await getData(urlCategories);

  return checkForErrorsAndSendResponse(data, isError, "categoryList");
};

export const getSingleCategory = async (id) => {
  const { data, isError } = await getData(urlCategories, id);

  return checkForErrorsAndSendResponse(data, isError, "category");
};

export const addNewCategory = async (categoryData) => {
  const { data, isError } = await postData(urlCategories, categoryData);

  return checkForErrorsAndSendResponse(data, isError, "newCategory");
};

export const editCategory = async (categoryData, id) => {
  const { data, isError } = await postData(urlCategories, categoryData, id);

  return checkForErrorsAndSendResponse(data, isError, "editedCategory");
};

/*********************
 * BUNDLES ***********
 *********************/

export const getAllBundles = async () => {
  const { data, isError } = await getData(urlBundles);

  return checkForErrorsAndSendResponse(data, isError, "bundleList");
};

export const getAllBundlesForABundleGroup = async (id) => {
  const newUrl = `${urlBundles}?bundleGroupId=${id}`;
  const { data, isError } = await getData(newUrl);

  return checkForErrorsAndSendResponse(data, isError, "bundleList");
};

export const getSingleBundle = async (id) => {
  const { data, isError } = await getData(urlBundles, id);

  return checkForErrorsAndSendResponse(data, isError, "bundleGroup");
};

export const addNewBundle = async (bundleData) => {
  const { data, isError } = await postData(urlBundles, bundleData);

  return checkForErrorsAndSendResponse(data, isError, "newBundle");
};

export const editBundle = async (bundleData, id) => {
  const { data, isError } = await postData(urlBundles, bundleData, id);

  return checkForErrorsAndSendResponse(data, isError, "editedBundle");
};

/*********************
 * BUNDLE GROUPS *****
 *********************/

export const getAllBundleGroups = async () => {
  const { data, isError } = await getData(urlBundleGroups);

  return checkForErrorsAndSendResponse(data, isError, "bundleGroupList");
};

export const getSingleBundleGroup = async (id) => {
  const { data, isError } = await getData(urlBundleGroups, id);

  return checkForErrorsAndSendResponse(data, isError, "bundleGroup");
};

export const addNewBundleGroup = async (bundleGroupData) => {
  const { data, isError } = await postData(urlBundleGroups, bundleGroupData);

  return checkForErrorsAndSendResponse(data, isError, "newBundleGroup");
};

export const editBundleGroup = async (bundleGroupData, id) => {
  const { data, isError } = await postData(
    urlBundleGroups,
    bundleGroupData,
    id
  );

  return checkForErrorsAndSendResponse(data, isError, "editedBundleGroup");
};
