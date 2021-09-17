import React, {useEffect, useState} from 'react';
import {ADMIN, AUTHOR, MANAGER} from "../../../api/constants";
import {Select, SelectItem} from "carbon-components-react";
import {getHigherRole} from "../../../api/helpers";

let STATUS = (() => {
    let ret = {}
    ret[ADMIN] = [{value: "NOT_PUBLISHED", text: "not published"}, {
        value: "PUBLISHED",
        text: "Published"
    }, {value: "REQUEST_FOR_PUBLISHING", text: "Request for Publishing"}, {
        value: "REQUEST_FOR_DELETING",
        text: "Request for Deleting"
    }]
    ret[MANAGER] = [{value: "NOT_PUBLISHED", text: "not published"}, {
        value: "PUBLISHED",
        text: "Published"
    }, {value: "REQUEST_FOR_PUBLISHING", text: "Request for Publishing"}, {
        value: "REQUEST_FOR_DELETING",
        text: "Request for Deleting"
    }]
    ret[AUTHOR] = [{value: "NOT_PUBLISHED", text: "not published"}, {
        value: "PUBLISHED",
        text: "Published"
    }, {value: "REQUEST_FOR_PUBLISHING", text: "Request for Publishing"}]
    return ret
})()


const BundleGroupStatusFilter = ({onFilterValueChange}) => {

    useEffect(()=>{
        const higherRole = getHigherRole();
        //TODO CHECK DAVIDE
        const status = higherRole ? STATUS[higherRole] : []
        setStatusArray(status)
        let defaultStatus = status.length>0 ? status[0].value:"";
        changeSelectedStatus(defaultStatus)
    },[])
    const [selectedStatus, setSelectedStatus] = useState("")
    const [statusArray, setStatusArray] = useState([])


    function changeSelectedStatus(value) {
        setSelectedStatus(value)
        onFilterValueChange(value)
    }

    const statusChangeHandler = (e) => {
        const value = e.target.value;
        changeSelectedStatus(value);
        debugger
    }

    const render = () => {
        const itemList = statusArray.map((s, index) => <SelectItem key={index} value={s.value} text={s.text}/>)

        return (
            <div className="bx--row">
                <div className="bx--col-lg-4 CatalogPage-section">
                </div>
                <div className="bx--col-lg-12 CatalogPage-section">
                    <Select value={selectedStatus} id={"category"} onChange={statusChangeHandler}>
                        {itemList}
                    </Select>
                </div>
            </div>
        )
    }
    return <div>{render()}</div>
}

export default BundleGroupStatusFilter;
