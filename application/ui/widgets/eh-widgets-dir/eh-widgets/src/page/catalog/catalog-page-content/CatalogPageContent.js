import React, {useEffect, useState} from 'react';
import CatalogFilterTile from "../catalog-filter-tile/CatalogFilterTile";
import CatalogTiles from "../catalog-tiles/CatalogTiles";
import {getAllBundleGroups, getAllCategories} from "../../../integration/Integration";


/*
const categories = Array.from(Array(3).keys()).map(index => {
    return {name: "name" + index, categoryId: "" + index};
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
/*
const allBundleGroups = Array.from(Array(10).keys()).map(index => {
    return {
        bundleGroupId: "" + index,
        name: "name" + index,
        description: "description" + index,
        categories: ["" + categories[Math.floor(Math.random() * categories.length)].id],
        image: "image" + index
    };
})
*/


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
