import React, {useCallback, useEffect, useState} from 'react'
import CatalogFilterTile from "../catalog-filter-tile/CatalogFilterTile"
import CatalogTiles from "../catalog-tiles/CatalogTiles"
import {getAllBundleGroupsFilteredPaged, getAllCategories} from "../../../integration/Integration"

import "./catalog-page-content.scss"
import {getHigherRole, isHubUser} from "../../../helpers/helpers"
import {getProfiledStatusSelectAllValues} from "../../../helpers/profiling"
import {getCurrentUserOrganisation} from "../../../integration/api-adapters";
import {Loading, Pagination} from "carbon-components-react";

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
    const [page, setPage] = useState(1)
    const [pageSize, setPageSize] = useState(5)
    const [totalItems, setTotalItems] = useState(10)
    const [loading, setLoading] = useState(true)
    const [selectedCategoryIds, setSelectedCategoryIds] = useState(["-1"])
    const [filteredBundleGroups, setFilteredBundleGroups] = useState([])
    const [categories, setCategories] = useState([])
    const [localStatusFilterValue, setLocalStatusFilerValue] = useState(null)

    //if the statusFilter change value we need to set the page to 1
    //and query the BE
    if(localStatusFilterValue!==statusFilterValue){
        setLocalStatusFilerValue(statusFilterValue)
        setPage(1)
    }



    const loadData = useCallback(async (page, pageSize, statusFilterValue, selectedCategoryIds, statuses) => {

        const userOrganisation = await getCurrentUserOrganisation()
        const organisationId = userOrganisation ? userOrganisation.organisationId : undefined


        /**
         *Get all the bundle groups having categoryIds and statuses
         */
        const getBundleGroupsAndFilterThem = async (organisationId, categoryIds, statuses) => {
            const data = await getAllBundleGroupsFilteredPaged(page, pageSize, organisationId, categoryIds, statuses)
            let filtered = data.bundleGroupList.payload
            const metadata = data.bundleGroupList.metadata
            setPage(metadata.page)
            setPageSize(metadata.pageSize)
            setTotalItems(metadata.totalItems)
            return filtered
        }

        const initBGs = async (organisationId, statuses) => {
            //get the selected categories if -1 no filtering at all on them
            const categoryIds = (selectedCategoryIds && selectedCategoryIds.length > 0 && selectedCategoryIds[0] !== "-1") ? selectedCategoryIds : undefined

            const filtered = await getBundleGroupsAndFilterThem(organisationId, categoryIds, statuses)
            setFilteredBundleGroups(filtered)
        }
        const initCs = async () => {
            const data = await getAllCategories()
            setCategories(data.categoryList)
        }
        return Promise.all([initBGs(organisationId, statuses), initCs()])
    }, [])

    useEffect(() => {
        const hubUser = isHubUser();
        let statuses = [] //filter values for the status
        if (!hubUser) { //GUEST user no status filter, only categories one
            statuses = ["PUBLISHED"]
        } else { //authenticated user
            if (localStatusFilterValue === "-1") { //all the statuses
                statuses = getProfiledStatusSelectAllValues(getHigherRole())
            } else {
                statuses = [localStatusFilterValue]
            }
        }

        (async () => {
            setLoading(true)
            await loadData(page, pageSize, localStatusFilterValue, selectedCategoryIds, statuses)
            setLoading(false)

        })()
    }, [reloadToken, page, pageSize, selectedCategoryIds, localStatusFilterValue, loadData])


    const onFilterChange = (newSelectedCategoryIds) => {
        setPage(1)
        setSelectedCategoryIds(newSelectedCategoryIds)
    }

    const onPaginationChange = ({page, pageSize}) => {
        setPageSize(pageSize)
        setPage(page)
    }

    return (
        <>
            <div className="bx--col-lg-4">
                {categories.length > 0 &&
                <CatalogFilterTile categories={categories} onFilterChange={onFilterChange}/>}
            </div>
            <div className="bx--col-lg-12 CatalogPageContent-wrapper">
                <CatalogTiles bundleGroups={filteredBundleGroups} onAfterSubmit={onAfterSubmit}/>
                <Pagination pageSizes={[5, 10, 15]} page={page} pageSize={pageSize} totalItems={totalItems}
                            onChange={onPaginationChange}/>
            </div>
            {loading && <Loading/>}
        </>
    )
}

export default CatalogPageContent
