import React from 'react';
import {ClickableTile} from "carbon-components-react";
import {useHistory} from "react-router-dom";

const CatalogTile = (CatalogTileProps) => {
    const {bundleGroupId, name, description, categories} = CatalogTileProps

    const history = useHistory()

    const handleClick = (evt) => {
        history.push("/bundlegroup/" + bundleGroupId)
    }

    return <ClickableTile handleClick={handleClick}>CATALOG TILE COMPONENT name: {name}, description:{description},
        category: {categories[0]} </ClickableTile>
}

export default CatalogTile;
