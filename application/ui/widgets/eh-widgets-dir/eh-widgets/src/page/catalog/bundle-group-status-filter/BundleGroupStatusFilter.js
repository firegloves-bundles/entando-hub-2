import React, {useCallback, useEffect, useState} from 'react';
import {ADMIN, AUTHOR, MANAGER} from "../../../api/constants";
import {Select, SelectItem} from "carbon-components-react";
import {getHigherRole} from "../../../api/helpers";

let STATUS = (() => {
    let ret = {}
    ret[ADMIN] = [
        {value: "-1", text: "All"},
        {value: "NOT_PUBLISHED", text: "Not Published"},
        {value: "PUBLISHED", text: "Published"},
        {value: "REQUEST_FOR_PUBLISHING", text: "Request for Publishing"},
        {value: "REQUEST_FOR_DELETING", text: "Request for Deleting"}
    ]
    ret[MANAGER] = [
        {value: "-1", text: "All"},
        {value: "NOT_PUBLISHED", text: "Not Published"},
        {value: "PUBLISHED", text: "Published"},
        {value: "REQUEST_FOR_PUBLISHING", text: "Request for Publishing"},
        {value: "REQUEST_FOR_DELETING", text: "Request for Deleting"}
    ]
    ret[AUTHOR] = [
        {value: "-1", text: "All"},
        {value: "NOT_PUBLISHED", text: "Not Published"},
        {value: "PUBLISHED", text: "Published"},
        {value: "REQUEST_FOR_PUBLISHING", text: "Request for Publishing"}
    ]
    return ret
})()


const BundleGroupStatusFilter = ({onFilterValueChange}) => {
    const changeSelectedStatus = useCallback((value) => {
        setSelectedStatus(value)
        onFilterValueChange(value)
    }, [onFilterValueChange])

    useEffect(() => {
        const higherRole = getHigherRole();
        //TODO CHECK DAVIDE
        const status = higherRole ? STATUS[higherRole] : []
        setStatusArray(status)
        //use the first value as the default
        let defaultStatus = status.length > 0 ? status[0].value : "";
        //call the on change to align the result
        changeSelectedStatus(defaultStatus)
    }, [changeSelectedStatus])
    const [selectedStatus, setSelectedStatus] = useState("")
    const [statusArray, setStatusArray] = useState([])


    const statusChangeHandler = (e) => {
        const value = e.target.value;
        changeSelectedStatus(value);
    }

    const render = () => {
        const itemList = statusArray.map((s, index) => <SelectItem key={index} value={s.value} text={s.text}/>)

        return (
            <Select value={selectedStatus} id={"category"} onChange={statusChangeHandler}>
                {itemList}
            </Select>
        )
    }
    return <div>{render()}</div>
}

export default BundleGroupStatusFilter;
