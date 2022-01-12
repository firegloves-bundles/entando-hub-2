import React, {useState} from 'react'
import {Checkbox, Tile} from "carbon-components-react"

import './catalog-filter-tile.scss'

/**
 * renders a list of categories
 */
const CatalogFilterTile = ({categories, categoryId, onFilterChange, setActiveCategory}) => {
    const init = ()=>{
        let checkBoxInitialStatuses = {}
        categories.forEach((category) => {
            checkBoxInitialStatuses[category.categoryId] = false
        })
        if (categoryId) {
            checkBoxInitialStatuses[categoryId] = true
        } else {
            checkBoxInitialStatuses["-1"] = true
        }
        return checkBoxInitialStatuses
    }

    let [checkboxStatuses, setCheckboxStatuses] = useState(init())

    const onChange = (value, id, event) => {
        if (id === "-1") {
            //all clicked
            Object.keys(checkboxStatuses).forEach((key) => checkboxStatuses[key] = (value === true))
        }else{
            checkboxStatuses[id] = value
            checkboxStatuses["-1"] = false

        }
        setCheckboxStatuses(checkboxStatuses)
        onFilterChange(Object.keys(checkboxStatuses).filter(key => (key!=="-1" && checkboxStatuses[key])))
        setActiveCategory(checkboxStatuses)
    }
    const prefix = "catalog-filter"
    const listItems = categories.map((category) => <Checkbox disabled={categoryId ? true : false} checked={checkboxStatuses[category.categoryId]} onChange={onChange} key={category.categoryId}
                                                             labelText={category.name} id={category.categoryId}/>)
    return (
        <Tile className="CatalogFilterTile">
            <fieldset className={`${prefix}--fieldset`}>
                <Checkbox disabled={categoryId ? true : false} checked={checkboxStatuses["-1"]} onChange={onChange} key={"-1"} labelText={"All"}
                          id={"-1"}/>
                {listItems}
            </fieldset>
        </Tile>
    )
}

export default CatalogFilterTile
