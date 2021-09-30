import axios from "axios"
import React, { useState, useEffect } from "react"
import Organisation from "./Organisation"

const OrganisationList = () => {
  const urlGetOrganisations = `http://hubdev.okd-entando.org/entando-hub-api/api/organisation/`

  const [organisationList, setOrganisationList] = useState([])

  useEffect(() => {
    const fetchOrganisations = async () => {
      const data = await axios
        .get(urlGetOrganisations)
        .then((res) => {
          return res.data
        })
        .catch((e) => {
          console.error(e)
        })

      setOrganisationList(data)
    }

    fetchOrganisations()
  }, [urlGetOrganisations])

  const orgListToRender = organisationList.map((organisation) => {
    return (
      <Organisation
        key={organisation.organisationId}
        id={organisation.organisationId}
        name={organisation.name}
        description={organisation.description}
      />
    )
  })

  return <>{orgListToRender}</>
}

export default OrganisationList
