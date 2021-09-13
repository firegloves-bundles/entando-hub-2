import React, {useEffect, useState} from 'react';
import {ClickableTile} from "carbon-components-react";
import {useHistory} from "react-router-dom";
import {getSingleCategory} from "../../../integration/Integration";

const CatalogTile = (CatalogTileProps) => {
    const {bundleGroupId, name, description, categories} = CatalogTileProps
    const [categoryName,setCategoryName] = useState("")
    useEffect(()=>{
        (async ()=>{
            const data = await getSingleCategory(categories[0])
            setCategoryName(data.category.name)
        })(categories)
    })

    const history = useHistory()

    const handleClick = (evt) => {
        history.push("/bundlegroup/" + bundleGroupId)
    }

    return <ClickableTile handleClick={handleClick}>CATALOG TILE COMPONENT name: {name}, description:{description},
        category: {categoryName} </ClickableTile>
}

export default CatalogTile;
