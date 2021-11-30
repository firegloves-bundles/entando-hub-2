import React, { useEffect, useState } from "react"
import { Tag } from "carbon-components-react"
import { useHistory } from "react-router-dom"
import { getSingleCategory } from "../../../integration/Integration"

import "./catalog-tile.scss"
import CatalogTileOverflowMenu from "./overflow-menu/CatalogTileOverflowMenu"
import { isHubUser } from "../../../helpers/helpers"
import { textFromStatus } from "../../../helpers/profiling"

const CatalogTile = ({
  bundleGroupId,
  name,
  organisationName,
  description,
  descriptionImage,
  categories,
  status,
  onAfterSubmit,
  version
}) => {
  const [categoryName, setCategoryName] = useState("")
  let bundleStatus = status

  useEffect(() => {
    let isMounted = true
    ;(async () => {
      const data = await getSingleCategory(categories[0])
      if (isMounted) {
        setCategoryName(data.category.name)
      }
    })()

    return () => {
      isMounted = false
    }
  }, [categories])

  const history = useHistory()

  //manage the bundle group detail
  const handleClick = () => {
    history.push("/bundlegroup/" + bundleGroupId)
  }

  let tagColor
  switch (categoryName) {
    case "Component Collection":
      tagColor = "red"
      break
    case "Solution Template":
      tagColor = "green"
      break
    case "PBC":
      tagColor = "blue"
      break
    default:
      tagColor = "blue"
  }

  //TODO refactor into an utility function
  return (
    <>
      <div className="CatalogTile">
        {isHubUser() && (
          <div className="CatalogTile-dropmenu">
            <CatalogTileOverflowMenu
              bundleGroupId={bundleGroupId}
              bundleStatus={bundleStatus}
              bundleName={name}
              onAfterSubmit={onAfterSubmit}
            />
          </div>
        )}
        <div onClick={handleClick} className="CatalogTile-card-wrapper">
          <div className="CatalogTile-card-icon">
            {descriptionImage ? (
              <img src={descriptionImage} alt="Logo" />
            ) : (
              <img
                src={`${process.env.REACT_APP_PUBLIC_ASSETS_URL}/icon.svg`}
                alt="Logo"
              />
            )}
          </div>
          <div className="CatalogTile-card-title">{name}</div>
          <div className="CatalogTile-card-status">{organisationName}</div>
          <div className="CatalogTile-card-description">{description}</div>
          {isHubUser() && (
            <div className="CatalogTile-card-status">
              {textFromStatus(status)}
            </div>
          )}
          <div className="CatalogTile-card-status">{version}</div>
          <div className="CatalogTile-card-category">
            <Tag type={tagColor} title="Clear Filter">
              {" "}
              {categoryName}{" "}
            </Tag>
          </div>
        </div>
      </div>
    </>
  )
}

export default CatalogTile
