import React, {useCallback, useEffect, useState} from 'react'
import CatalogFilterTile from "../catalog-filter-tile/CatalogFilterTile"
import CatalogTiles from "../catalog-tiles/CatalogTiles"
import {getAllBundleGroups, getAllCategories} from "../../../integration/Integration"

import "./catalog-page-content.scss"
import {getHigherRole, isHubUser} from "../../../helpers/helpers"
import {getProfiledStatusSelectAllValues} from "../../../helpers/profiling"

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

const CatalogPageContent = ({reloadToken, statusFilterValue = "PUBLISHED", onAfterSubmit}) => {
    const [selectedCategoryIds, setSelectedCategoryIds] = useState(["-1"])

    const loadData = useCallback(async (newSelectedCategoryIds) => {
        const localSelectedCategoryIds = newSelectedCategoryIds || selectedCategoryIds
        const initBGs = async () => {
            const data = await getAllBundleGroups()
            let filtered = data.bundleGroupList
            if (localSelectedCategoryIds && localSelectedCategoryIds.length > 0 && localSelectedCategoryIds[0] !== "-1") {
                filtered = data.bundleGroupList.filter(currBundleGroup => localSelectedCategoryIds.includes(currBundleGroup.categories[0]))
            }
            //status filter === -1 is all
            //the BG statuses to filter depend on user role
            if (statusFilterValue !== "-1") {
                filtered = filtered.filter(bg => bg.status && bg.status === statusFilterValue)
            } else if (isHubUser()) {
                //the user must be authenticated to get there
                //check for be sure
                const allStatuses = getProfiledStatusSelectAllValues(getHigherRole())
                filtered = filtered.filter(bg => bg.status && allStatuses.includes(bg.status))
            }
            setFilteredBundleGroups(filtered)
        }
        const initCs = async () => {
            const data = await getAllCategories()
            setCategories(data.categoryList)
        }
        initBGs()
        initCs()
    }, [statusFilterValue, selectedCategoryIds])


    useEffect(() => loadData(), [reloadToken, loadData])

    const [filteredBundleGroups, setFilteredBundleGroups] = useState([])
    const [categories, setCategories] = useState([])

    const onFilterChange = (newSelectedCategoryIds) => {
        loadData(newSelectedCategoryIds)
        setSelectedCategoryIds(newSelectedCategoryIds)
    }

    return (
        <>
            <div className="bx--col-lg-4">
                {categories.length > 0 &&
                <CatalogFilterTile categories={categories} onFilterChange={onFilterChange}/>}
            </div>
            <div className="bx--col-lg-12 CatalogPageContent-wrapper">
                <CatalogTiles bundleGroups={filteredBundleGroups} onAfterSubmit={onAfterSubmit}/>
            </div>
        </>
    )
}

export default CatalogPageContent
