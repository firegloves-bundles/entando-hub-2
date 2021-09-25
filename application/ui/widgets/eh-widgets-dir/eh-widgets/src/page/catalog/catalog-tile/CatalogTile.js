import React, {useEffect, useState} from 'react'
import {Tag, Tile} from "carbon-components-react"
import {useHistory} from "react-router-dom"
import {getSingleCategory} from "../../../integration/Integration"

import "./catalog-tile.scss"
import CatalogTileOverflowMenu from "./overflow-menu/CatalogTileOverflowMenu"
import {isHubUser} from "../../../helpers/helpers"
import {textFromStatus} from "../../../helpers/profiling"

const CatalogTile = ({bundleGroupId, name, description, categories, status, onAfterSubmit}) => {
    const [categoryName, setCategoryName] = useState("")
    useEffect(() => {
        let isMounted = true
        (async () => {
            const data = await getSingleCategory(categories[0])
            if (isMounted) {
                setCategoryName(data.category.name)
            }

        })()

        return () => {
            isMounted = false
        }
    },[categories])

    const history = useHistory()

    //manage the bundle group detail
    const handleClick = (evt) => {
        history.push("/bundlegroup/" + bundleGroupId)
    }

    //TODO refactor into an utility function
    return (
        <>
            <Tile className="CatalogTile">
                {isHubUser() && <CatalogTileOverflowMenu bundleGroupId={bundleGroupId} onAfterSubmit={onAfterSubmit}/>}
                <div onClick={handleClick} className="CatalogTile-card-wrapper">
                    <div className="CatalogTile-card-icon">
                        <img src={`${process.env.REACT_APP_PUBLIC_ASSETS_URL}/icon.svg`} alt="Entando logo"/>
                    </div>
                    <div className="CatalogTile-card-title">{name}</div>
                    {isHubUser() && <div className="CatalogTile-card-status">{textFromStatus(status)}</div>}
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

export default CatalogTile
