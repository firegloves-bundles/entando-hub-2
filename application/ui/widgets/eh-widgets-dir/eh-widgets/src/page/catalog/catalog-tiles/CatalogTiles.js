import React from 'react'
import CatalogTile from "../catalog-tile/CatalogTile"

/**
 * renders a list of bundleGroup
 */
const CatalogTiles = ({bundleGroups, categoriesDetails, onAfterSubmit}) => {
    const listItems = bundleGroups && bundleGroups.map((bundleGroup, index) => <CatalogTile onAfterSubmit={onAfterSubmit} categoriesDetails={categoriesDetails} descriptionImage={bundleGroup.descriptionImage} key={index} bundleGroup={bundleGroup} {...bundleGroup}/>)
    return <div>{listItems}</div>
}

export default CatalogTiles
