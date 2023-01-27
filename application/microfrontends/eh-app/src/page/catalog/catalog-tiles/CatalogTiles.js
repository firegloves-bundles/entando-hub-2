import React from 'react'
import CatalogTile from "../catalog-tile/CatalogTile"

/**
 * renders a list of bundleGroup
 */
const CatalogTiles = ({apiUrl, bundleGroups, categoryDetails, onAfterSubmit, isVersionsPage, orgList, showFullPage }) => {
    const listItems = bundleGroups && bundleGroups.map((bundleGroup, index) => <CatalogTile apiUrl={apiUrl} onAfterSubmit={onAfterSubmit} categoryDetails={categoryDetails} descriptionImage={bundleGroup.descriptionImage} key={index} bundleGroup={bundleGroup} isVersionsPage={isVersionsPage} orgList={orgList} showFullPage={showFullPage} {...bundleGroup}/>)
    return <div>{listItems}</div>
}

export default CatalogTiles
