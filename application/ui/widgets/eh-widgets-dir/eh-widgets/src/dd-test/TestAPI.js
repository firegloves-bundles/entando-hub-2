import React from "react"
import { getAllBundleGroups, getAllUsers, getAllUserForAnOrganisation, deleteUser, removeUserFromOrganisation, createAUserForAnOrganisation } from "../integration/Integration"

const TestAPI = () => {
  const runGet = async () => {
    const response = await getAllUserForAnOrganisation(1)
    console.log(response)
  }

  const runPost = async () => {
    const response = await createAUserForAnOrganisation(1, "germano")
    console.log(response)
  }

  const runDelete = async () => {
    const response = await removeUserFromOrganisation(1, "germano")
    console.log(response)
  }

  return (
    <div>
      <button onClick={runGet}>GET</button>
      <button onClick={runPost}>POST</button>
      <button onClick={runDelete}>DELETE</button>
    </div>
  )
}

export default TestAPI
