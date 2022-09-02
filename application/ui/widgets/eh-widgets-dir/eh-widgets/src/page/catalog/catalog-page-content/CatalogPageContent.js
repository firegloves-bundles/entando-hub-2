import React, {useCallback, useEffect, useState} from 'react'
import CatalogFilterTile from "../catalog-filter-tile/CatalogFilterTile"
import CatalogTiles from "../catalog-tiles/CatalogTiles"
import {getAllBundleGroupsFilteredPaged} from "../../../integration/Integration"

import "./catalog-page-content.scss"
import {getHigherRole, isHubAdmin, isHubUser} from "../../../helpers/helpers"
import {getProfiledStatusSelectAllValues} from "../../../helpers/profiling"
import {Loading, Pagination} from "carbon-components-react";
import i18n from '../../../i18n'

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

const CatalogPageContent = ({reloadToken, statusFilterValue, catList, isError, onAfterSubmit, currentUserOrg, orgList, searchTerm, showFullPage }) => {
    const [page, setPage] = useState(1)
    const [pageSize, setPageSize] = useState(12)
    const [totalItems, setTotalItems] = useState(12)
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
        const userOrganisation = currentUserOrg;
        const organisationId = !isHubAdmin() && userOrganisation ? userOrganisation.organisationId : undefined

        /**
         *Get all the bundle groups having categoryIds and statuses
         */
        const getBundleGroupsAndFilterThem = async (organisationId, categoryIds, statuses, searchTerm) => {
            const data = await getAllBundleGroupsFilteredPaged(page, pageSize, organisationId, categoryIds, statuses, searchTerm)
            if (data.isError) {
                setLoading(false)
            }
            let filtered = null;
            let metadata = null;
            if (data.bundleGroupList) {
                filtered = data.bundleGroupList.payload;
                metadata = data.bundleGroupList.metadata
                setPage(metadata.page)
                setPageSize(metadata.pageSize)
                setTotalItems(metadata.totalItems)
            }
            return filtered
        }

        const initBGs = async (organisationId, statuses) => {
            //get the selected categories if -1 no filtering at all on them
            const categoryIds = (selectedCategoryIds && selectedCategoryIds.length > 0 && selectedCategoryIds[0] !== "-1") ? selectedCategoryIds : undefined

            const filtered = await getBundleGroupsAndFilterThem(organisationId, categoryIds, statuses, searchTerm)
            setFilteredBundleGroups(filtered)
        }
        return Promise.all([initBGs(organisationId, statuses)])
    }, [currentUserOrg, searchTerm])

    useEffect(() => {
        if (isError) {
            setLoading(false);
        }
        setCategories(catList);
    }, [isError, catList])

    useEffect(() => {
        const hubUser = isHubUser();
        let statuses = [] //filter values for the status
        if (!hubUser || !showFullPage) { //GUEST user no status filter, only categories one
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
    }, [reloadToken, page, pageSize, selectedCategoryIds, localStatusFilterValue, loadData, showFullPage])


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
                {categories && categories.length > 0 &&
                <CatalogFilterTile categories={categories} onFilterChange={onFilterChange} />}
            </div>
            <div className="bx--col-lg-12 CatalogPageContent-wrapper">
                <CatalogTiles bundleGroups={filteredBundleGroups} categoryDetails={catList} onAfterSubmit={onAfterSubmit} orgList={orgList} showFullPage={showFullPage}/>
                <Pagination
                    itemsPerPageText={i18n.t("component.pagination.itemsPerPage")}
                    itemRangeText={
                        (min, max, total) => `${min}–${max} ${i18n.t("component.pagination.of")} ${total} ${i18n.t("component.pagination.items")}`
                    }
                    pageSizes={[12, 18, 24]}
                    totalItems={totalItems}
                    onChange={onPaginationChange}
                    backwardText={i18n.t("component.pagination.previousPage")}
                    forwardText={i18n.t("component.pagination.nextPage")}
                    pageRangeText={
                        (_current, total) => `${i18n.t("component.pagination.of")} ${ total }
                        ${ total === 1 ? `${i18n.t("component.pagination.page")}` : `${i18n.t("component.pagination.pages")}` }`
                    }
                />
            </div>
            {loading && <Loading/>}
        </>
    )
}

export default CatalogPageContent
