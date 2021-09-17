import React, {useEffect, useState} from 'react';
import {ClickableTile, OverflowMenu, OverflowMenuItem, Tag, Tile} from "carbon-components-react";
import {useHistory} from "react-router-dom";
import {getSingleCategory} from "../../../integration/Integration";

import "./catalog-tile.scss"
import CatalogTileOverflowMenu from "./overflow-menu/CatalogTileOverflowMenu";
import {isHubUser} from "../../../api/helpers";

const CatalogTile = (CatalogTileProps) => {
    const {bundleGroupId, name, description, categories, status} = CatalogTileProps
    const [categoryName, setCategoryName] = useState("")
    useEffect(() => {
        (async () => {
            const data = await getSingleCategory(categories[0])
            setCategoryName(data.category.name)
        })(categories)
    })

    const history = useHistory()

    const handleClick = (evt) => {
        history.push("/bundlegroup/" + bundleGroupId)
    }


    return (
        <>
            <Tile className="CatalogTile">
                {isHubUser() && <CatalogTileOverflowMenu bundleGroupId={bundleGroupId} />}
                <div onClick={handleClick} className="CatalogTile-card-wrapper">
                    <div className="CatalogTile-card-icon">
                        <img src={`${process.env.REACT_APP_PUBLIC_ASSETS_URL}/icon.svg`} alt="Entando logo"/>
                    </div>
                    <div className="CatalogTile-card-title">
                        {name}
                    </div>
                    <div className="CatalogTile-card-description">
                        {description} - {status}
                    </div>
                    <div className="CatalogTile-card-category">
                        <Tag type="blue" title="Clear Filter"> {categoryName} </Tag>
                    </div>
                </div>
            </Tile>
        </>

    )
}

export default CatalogTile;
