import React from 'react';
import CatalogTile from "../catalog-tile/CatalogTile";

/**
 * renders a list of bundleGroup
 */
const CatalogTiles = ({bundleGroups}) => {
    const listItems = bundleGroups.map((bundleGroup) => <CatalogTile key={bundleGroup.bundleGroupId} {...bundleGroup}/>);
    return <div>{listItems}</div>
}

export default CatalogTiles;
