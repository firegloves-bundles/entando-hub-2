import React from 'react';
import CatalogTile from "../catalog-tile/CatalogTile";

/**
 * renders a list of bundleGroup
 */
const CatalogTiles = ({bundleGroups,onAfterSubmit}) => {
    const listItems = bundleGroups.map((bundleGroup) => <CatalogTile onAfterSubmit={onAfterSubmit} key={bundleGroup.bundleGroupId} {...bundleGroup}/>);
    return <div>{listItems}</div>
}

export default CatalogTiles;
