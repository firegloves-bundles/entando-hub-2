import React, {useCallback, useEffect, useState} from 'react'
import CatalogFilterTile from "../catalog-filter-tile/CatalogFilterTile"
import CatalogTiles from "../catalog-tiles/CatalogTiles"
import {
  getAllBundleGroups,
  getAllCategories
} from "../../../integration/Integration"

import "./catalog-page-content.scss"
import {getHigherRole, isHubUser} from "../../../helpers/helpers"
import {getProfiledStatusSelectAllValues} from "../../../helpers/profiling"
import {getCurrentUserOrganisation} from "../../../integration/api-adapters";

/*
const categories = Array.from(Array(3).keys()).map(index => {
    return {name: "name" + index, categoryId: "" + index}
})
*/

/*
{
name	string
description	string
bundleGroups	[...]
categoryId	string
}
 */

/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}
 */

const CatalogPageContent = ({
  reloadToken,
  statusFilterValue = "PUBLISHED",
  onAfterSubmit
}) => {
  const [loading, setLoading] = useState(true)
  const [selectedCategoryIds, setSelectedCategoryIds] = useState(["-1"])

  const loadData = useCallback(async (newSelectedCategoryIds) => {
    const localSelectedCategoryIds = newSelectedCategoryIds
        || selectedCategoryIds

    const userOrganisation = await getCurrentUserOrganisation()
    const organisationId = userOrganisation ? userOrganisation.organisationId
        : undefined

    const initBGs = async (organisationId) => {
      const data = await getAllBundleGroups(organisationId)
      let filtered = data.bundleGroupList
      if (localSelectedCategoryIds && localSelectedCategoryIds.length > 0
          && localSelectedCategoryIds[0] !== "-1") {
        filtered = data.bundleGroupList.filter(
            currBundleGroup => localSelectedCategoryIds.includes(
                currBundleGroup.categories[0]))
      }
      //status filter === -1 is all
      //the BG statuses to filter depend on user role
      if (statusFilterValue !== "-1") {
        filtered = filtered.filter(
            bg => bg.status && bg.status === statusFilterValue)
      } else if (isHubUser()) {
        //the user must be authenticated to get there
        //check for be sure
        const allStatuses = getProfiledStatusSelectAllValues(getHigherRole())
        filtered = filtered.filter(
            bg => bg.status && allStatuses.includes(bg.status))
      }
      setFilteredBundleGroups(filtered)
    }
    const initCs = async () => {
      const data = await getAllCategories()
      setCategories(data.categoryList)
    }
    return Promise.all([initBGs(organisationId), initCs()])
  }, [statusFilterValue, selectedCategoryIds])

  useEffect(() => {
    (async () => {
      setLoading(true)
      await loadData()
      setLoading(false)

    })()
  }, [reloadToken, loadData])

  const [filteredBundleGroups, setFilteredBundleGroups] = useState([])
  const [categories, setCategories] = useState([])

  const onFilterChange = (newSelectedCategoryIds) => {
    (async () => {
      setLoading(true)
      await loadData(newSelectedCategoryIds)
      setSelectedCategoryIds(newSelectedCategoryIds)
      setLoading(false)

    })()
  }

  console.log("loading", loading)
  return (
      <>
        <div className="bx--col-lg-4">
          {categories.length > 0 &&
          <CatalogFilterTile categories={categories}
                             onFilterChange={onFilterChange}/>}
        </div>
        {!loading && <div
            className="bx--col-lg-12 CatalogPageContent-wrapper">
          <CatalogTiles bundleGroups={filteredBundleGroups}
                        onAfterSubmit={onAfterSubmit}/>
        </div>}
      </>
  )
}

export default CatalogPageContent
