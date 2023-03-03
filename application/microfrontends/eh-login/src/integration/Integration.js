import {  getData } from "./Http"
import { API_RESPONSE_KEY } from "../helpers/constants";

// endpoints
const urlUsers = '/api/users/'

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

/**
 * Get a portal user by username.
 * @param {*} username
 * @returns
 */
 export const getPortalUser = async (apiUrl) => {
  const { data, isError } = await getData(apiUrl + `${urlUsers}details`)

  return checkForErrorsAndSendResponse(data, isError, API_RESPONSE_KEY.PORTAL_USER);
}
