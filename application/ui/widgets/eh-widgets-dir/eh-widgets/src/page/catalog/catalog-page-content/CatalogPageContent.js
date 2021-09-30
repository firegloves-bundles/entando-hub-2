import React, {useCallback, useEffect, useState} from 'react'
import CatalogFilterTile from "../catalog-filter-tile/CatalogFilterTile"
import CatalogTiles from "../catalog-tiles/CatalogTiles"
import {getAllBundleGroups, getAllCategories} from "../../../integration/Integration"

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

const CatalogPageContent = ({reloadToken, statusFilterValue, onAfterSubmit}) => {
    const [loading, setLoading] = useState(true)
    const [selectedCategoryIds, setSelectedCategoryIds] = useState(["-1"])


    const loadData = useCallback(async (newSelectedCategoryIds) => {
        const localSelectedCategoryIds = newSelectedCategoryIds || selectedCategoryIds //when the user select new categories ==>newSelectedCategoryIds otherwise no parameter is sent and we get the last filter selection

        const userOrganisation = await getCurrentUserOrganisation()
        const organisationId = userOrganisation ? userOrganisation.organisationId : undefined


        //TODO BE QUERY REFACTORING
        const getBundleGroupsAndFilterThem = async (organisationId, categoryIds, statuses) => {
            const data = await getAllBundleGroups(organisationId)
            let filtered = data.bundleGroupList
            if (categoryIds) {
                filtered = data.bundleGroupList.filter(currBundleGroup => categoryIds.includes(currBundleGroup.categories[0]))
            }
            if (statuses) {
                filtered = filtered.filter(bg => bg.status && statuses.includes(bg.status))
            }
            return filtered
        }

        const initBGs = async (organisationId) => {
            let hubUser = isHubUser();
            console.log("statusFilterValue {} isHubUser() {}", statusFilterValue, hubUser)
            if (hubUser && statusFilterValue === "LOADING") return //skip everything, waiting for status filter loading
            //get the selected categories if -1 no filtering at all on them
            const categoryIds = (localSelectedCategoryIds && localSelectedCategoryIds.length > 0 && localSelectedCategoryIds[0] !== "-1") ? localSelectedCategoryIds : undefined

            let statuses = [] //filter values for the status
            if (!hubUser) { //GUEST user no status filter, only categories one
                statuses = ["PUBLISHED"]
            } else { //authenticated user
                if (statusFilterValue === "-1") { //all the statuses
                    statuses = getProfiledStatusSelectAllValues(getHigherRole())
                } else {
                    statuses = [statusFilterValue]
                }
            }
            const filtered = await getBundleGroupsAndFilterThem(organisationId, categoryIds, statuses)
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
            await loadData() //first load
            setLoading(false)

        })()
    }, [reloadToken, loadData])

    const [filteredBundleGroups, setFilteredBundleGroups] = useState([])
    const [categories, setCategories] = useState([])

    const onFilterChange = (newSelectedCategoryIds) => {
        (async () => {
            setLoading(true)
            await loadData(newSelectedCategoryIds) //filters data based on the selected categories
            setSelectedCategoryIds(newSelectedCategoryIds)
            setLoading(false)

        })()
    }

    return (
        <>
            <div className="bx--col-lg-4">
                {categories.length > 0 &&
                <CatalogFilterTile categories={categories} onFilterChange={onFilterChange}/>}
            </div>
            {!loading && <div className="bx--col-lg-12 CatalogPageContent-wrapper">
                <CatalogTiles bundleGroups={filteredBundleGroups} onAfterSubmit={onAfterSubmit}/>
            </div>}
        </>
    )
}

export default CatalogPageContent
