import axios from "axios"
import {getDefaultOptions} from "../helpers/helpers";

const addAuthorizationRequestConfig = (config={})=>{
  let defaultOptions = getDefaultOptions();
    return {
    ...config,
    ...defaultOptions
  }
}



// retrieves the data (organisations, categories, bundles and bundles group) from the BE. If an ID is provided it retrieves a single element (the one with the same ID)
export const getData = async (url, id) => {
  url = mergeUrl(url, id)
  const data = await axios
    .get(url, addAuthorizationRequestConfig())
    .then((res) => {
      return res.data
    })
    .catch((e) => {
      if (e.response) {
        return e.response.data
      }
      return e
    })

  return errorCheck(data)
}

// creates a new record of any type (organisation, category, bundle or bundle group). If an ID is provided it modifies the record with the same ID
export const postData = async (url, payload, id) => {
  url = mergeUrl(url, id)

  const data = await axios
    .post(url, payload, addAuthorizationRequestConfig())
    .then((res) => {
      return res.data
    })
    .catch((e) => {
      if (e.response) {
        return e.response.data
      }
      return e
    })

  return errorCheck(data)
}

export const deleteData = async (url, id) => {
  url = mergeUrl(url, id)

  const data = await axios
    .delete(url, addAuthorizationRequestConfig())
    .then((res) => {
      return res.data
    })
    .catch((e) => {
      if (e.response) {
        return e.response.data
      }
      return e
    })

  return errorCheck(data)
}

export const putData = async (url, payload, id) => {
  url = mergeUrl(url, id)

  const data = await axios
    .put(url, payload, addAuthorizationRequestConfig())
    .then((res) => {
      return res.data
    })
    .catch((e) => {
      if (e.response) {
        return e.response.data
      }
      return e
    })

  return errorCheck(data)
}

// if an ID is present, it modifies the url by merging it with the ID
const mergeUrl = (url, id) => {
  if (id) {
    url = `${url}${id}`
  }

  return url
}

// checks if the input data is an error and returns the data enhanced with a boolean
const errorCheck = (data) => {
  let isError = false

  if ((data.hasOwnProperty("toJSON") && data.toJSON().name === "Error") || data.error) {
    isError = true
  }

  return {
    data,
    isError,
  }
}
