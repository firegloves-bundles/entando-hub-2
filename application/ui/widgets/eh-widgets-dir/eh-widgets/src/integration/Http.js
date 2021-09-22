import axios from "axios"

// retrieves the data (organisations, categories, bundles and bundles group) from the BE. If an ID is provided it retrieves a single element (the one with the same ID)
export const getData = async (url, id) => {
  url = mergeUrl(url, id)

  const data = await axios
    .get(url)
    .then((res) => {
      return res.data
    })
    .catch((e) => {
      return e
    })

  return errorCheck(data)
}

// creates a new record of any type (organisation, category, bundle or bundle group). If an ID is provided it modifies the record with the same ID
export const postData = async (url, payload, id) => {
  url = mergeUrl(url, id)

  const data = await axios
    .post(url, payload)
    .then((res) => {
      return res
    })
    .catch((e) => {
      return e
    })

  return errorCheck(data)
}

export const deleteData = async (url) => {
  const data = await axios
    .delete(url)
    .then((res) => {
      return res.data
    })
    .catch((e) => {
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

  if (data.hasOwnProperty("toJSON") && data.toJSON().name === "Error") {
    isError = true
  }

  return {
    data,
    isError,
  }
}
