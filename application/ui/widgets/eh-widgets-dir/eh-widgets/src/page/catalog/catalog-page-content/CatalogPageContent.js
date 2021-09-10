import React, {useEffect, useState} from 'react';
import CatalogFilterTile from "../catalog-filter-tile/CatalogFilterTile";
import CatalogTiles from "../catalog-tiles/CatalogTiles";
import {getAllBundleGroups, getAllCategories} from "../../../integration/Integration";


const CatalogPageContent = () => {
    useEffect(() => {
        const initBGs = async () => {
            const data = await getAllBundleGroups()
            setAllBundleGroups(data.bundleGroupList)
            setFilteredBundleGroups(data.bundleGroupList)
        }
        const initCs = async () => {
            const data = await getAllCategories()
            setCategories(data.categoryList)
        }

        initBGs()
        initCs()
    }, [])

    const [allBundleGroups, setAllBundleGroups] = useState([])
    const [filteredBundleGroups, setFilteredBundleGroups] = useState([])
    const [categories, setCategories] = useState([])

    const onFilterChange = (selectedCategoryIds) => {
        let filtered = allBundleGroups
        if (selectedCategoryIds.length > 0 && selectedCategoryIds !== "-1") {
            filtered = allBundleGroups.filter(currBundleGroup => selectedCategoryIds.includes(currBundleGroup.categories[0]));
        }
        setFilteredBundleGroups(filtered)
    }

    return (
        <>
            <div className="bx--col-lg-4">
                {categories.length > 0 && <CatalogFilterTile categories={categories} onFilterChange={onFilterChange}/>}
            </div>
            <div className="bx--col-lg-12">
                <CatalogTiles bundleGroups={filteredBundleGroups}/>
            </div>
        </>
    )
}

export default CatalogPageContent;
