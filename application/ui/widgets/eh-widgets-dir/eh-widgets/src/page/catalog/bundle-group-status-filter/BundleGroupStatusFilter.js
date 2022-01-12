import React, {useCallback, useEffect, useState} from 'react'
import {Select, SelectItem} from "carbon-components-react"
import {getHigherRole} from "../../../helpers/helpers"
import {getProfiledStatusSelectInfo} from "../../../helpers/profiling"

import './bundle-group-status-filter.scss'
import i18n from '../../../i18n'

const BundleGroupStatusFilter = ({onFilterValueChange, setActiveBundleStatus}) => {
    const [selectedStatus, setSelectedStatus] = useState("")
    const [statusArray, setStatusArray] = useState([])

    const changeSelectedStatus = useCallback((value) => {
        setSelectedStatus(value)
        onFilterValueChange(value)
    }, [onFilterValueChange])

    useEffect(() => {
        const higherRole = getHigherRole()
        const status = getProfiledStatusSelectInfo(higherRole)
        setStatusArray(status)
        //use the first value as the default
        let defaultStatus = status.length > 0 ? status[0].value : ""
        //call the on change to align the result
        changeSelectedStatus(defaultStatus)
    }, [changeSelectedStatus])


    const statusChangeHandler = (e) => {
        const value = e.target.value
        changeSelectedStatus(value)
        setActiveBundleStatus(value)
    }

    const render = () => {
        const itemList = statusArray.map((s, index) => <SelectItem key={index} value={s.value} text={i18n.t(s.text)}/>)
        return (
            <div className="BundleGroupStatusFilter-select">
              <Select labelText={i18n.t('page.catlogPanel.catlogHomePage.select')} value={selectedStatus} id={"category"} onChange={statusChangeHandler}>
                {itemList}
            </Select>
            </div>
        )
    }
    return <div>{render()}</div>
}

export default BundleGroupStatusFilter
