import React, {useEffect, useState} from 'react';
import CatalogPageHeader from "./catalog-page-header/CatalogPageHeader";
import CatalogTiles from "./catalog-tiles/CatalogTiles";
import CatalogFilterTile from "./catalog-filter-tile/CatalogFilterTile";
import {Breadcrumb, BreadcrumbItem, Content} from "carbon-components-react";
import {getAllBundleGroups, getAllCategories} from "../../integration/Integration";

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


const CatalogPage = () => {
    useEffect(()=>{
        const initBGs = async ()=> {
            const data = await getAllBundleGroups()
            setAllBundleGroups(data.bundleGroupList)
            setFilteredBundleGroups(data.bundleGroupList)
        }
        const initCs = async ()=> {
            const data = await getAllCategories()
            setCategories(data.categoryList)
        }

        initBGs()
        initCs()
    },[])






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
            <CatalogPageHeader/>
            <Content>
                <div className="bx--grid bx--grid--full-width catalog-page">
                    <div className="bx--row">
                        <div className="bx--col-lg-16">
                            <Breadcrumb aria-label="Page navigation">
                                <BreadcrumbItem>
                                    <a href="/">Home</a>
                                </BreadcrumbItem>
                            </Breadcrumb>
                        </div>
                    </div>
                    <div className="bx--row">
                        <div className="bx--col-lg-4">
                            Categories
                        </div>
                        <div className="bx--col-lg-6">
                            Catalog
                        </div>
                        <div className="bx--col-lg-6">
                            Search
                        </div>
                    </div>
                    <div className="bx--row">
                        <div className="bx--col-lg-4">
                            {categories.length>0 && <CatalogFilterTile categories={categories} onFilterChange={onFilterChange}/>}
                        </div>
                        <div className="bx--col-lg-12">
                            <CatalogTiles bundleGroups={filteredBundleGroups}/>
                        </div>
                    </div>
                </div>
            </Content>

        </>
    );
};

export default CatalogPage;
