import React from 'react'
import CatalogTile from "../catalog-tile/CatalogTile"

/**
 * renders a list of bundleGroup
 */
const CatalogTiles = ({bundleGroups, categoriesDetails, onAfterSubmit}) => {
    const listItems = bundleGroups && bundleGroups.map((bundleGroup) => <CatalogTile onAfterSubmit={onAfterSubmit} categoriesDetails={categoriesDetails} descriptionImage={bundleGroup.descriptionImage} key={bundleGroup.bundleGroupId} {...bundleGroup}/>)
    return <div>{listItems}</div>
}

export default CatalogTiles
