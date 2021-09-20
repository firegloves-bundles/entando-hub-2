import React, {useEffect, useState} from 'react';
import {Tag, Tile} from "carbon-components-react";
import {useHistory} from "react-router-dom";
import {getSingleCategory} from "../../../integration/Integration";

import "./catalog-tile.scss"
import CatalogTileOverflowMenu from "./overflow-menu/CatalogTileOverflowMenu";
import {isHubUser} from "../../../api/helpers";

const CatalogTile = ({bundleGroupId, name, description, categories, status, onAfterSubmit}) => {
    const [categoryName, setCategoryName] = useState("")
    useEffect(() => {
        (async () => {
            const data = await getSingleCategory(categories[0])
            setCategoryName(data.category.name)
        })(categories)
    })

    const history = useHistory()

    //manage the bundle group detail
    const handleClick = (evt) => {
        history.push("/bundlegroup/" + bundleGroupId)
    }

    //TODO refactor into an utility function
    const statusToRender = status === "PUBLISHED" ? 'Published' : 'Unpublished'
    return (
        <>
            <Tile className="CatalogTile">
                {isHubUser() && <CatalogTileOverflowMenu bundleGroupId={bundleGroupId} onAfterSubmit={onAfterSubmit}/>}
                <div onClick={handleClick} className="CatalogTile-card-wrapper">
                    <div className="CatalogTile-card-icon">
                        <img src={`${process.env.REACT_APP_PUBLIC_ASSETS_URL}/icon.svg`} alt="Entando logo"/>
                    </div>
                    <div className="CatalogTile-card-title">{name}</div>
                    {isHubUser() && <div className="CatalogTile-card-status">{statusToRender}</div>}
                    <div className="CatalogTile-card-description">
                        {description}
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
