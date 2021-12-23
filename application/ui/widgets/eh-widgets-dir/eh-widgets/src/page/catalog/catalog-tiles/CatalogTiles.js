import React from 'react'
import CatalogTile from "../catalog-tile/CatalogTile"

/**
 * renders a list of bundleGroup
 */
const CatalogTiles = ({bundleGroups, categoryDetails, onAfterSubmit, isVersionsPage }) => {
    const listItems = bundleGroups && bundleGroups.map((bundleGroup, index) => <CatalogTile onAfterSubmit={onAfterSubmit} categoryDetails={categoryDetails} descriptionImage={bundleGroup.descriptionImage} key={index} bundleGroup={bundleGroup} isVersionsPage={isVersionsPage} {...bundleGroup}/>)
    return <div>{listItems}</div>
}

export default CatalogTiles
