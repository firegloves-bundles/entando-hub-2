import React from 'react';
import {ClickableTile, Tag} from "carbon-components-react";
import {useHistory} from "react-router-dom";
import "./catalog-tile.scss"

const CatalogTile = (CatalogTileProps) => {
  const {bundleGroupId, name, description, categories} = CatalogTileProps

  const history = useHistory()

  const handleClick = (evt) => {
    history.push("/bundlegroup/" + bundleGroupId)
  }

  return <ClickableTile handleClick={handleClick} className="CatalogTile">
    <div className="CatalogTile-card-wrapper">
      <div className="CatalogTile-card-icon">
        <img src="/../../icon.svg" alt="Entando logo"/>
      </div>
      <div className="CatalogTile-card-title">
        {name}
      </div>
      <div className="CatalogTile-card-description">
        {description}
        Lorem ipsum dolor sit amet, consectetur adipiscing elit.
      </div>
      <div className="CatalogTile-card-category">
        {/*tag list*/}
        <Tag type="blue" title="Clear Filter"> {categories[0]} </Tag>
      </div>
    </div>
  </ClickableTile>
}

export default CatalogTile;
