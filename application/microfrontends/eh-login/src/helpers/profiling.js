//STATUS
/*
public  enum Status {
    NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED
}
*/


//GUEST
//can see
//PUBLISHED
//can modify
//NOTHING

//AUTHOR
//can see
//NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ
//can modify
//NOT_PUBLISHED (=>) PUBLISH_REQ, DELETE_REQ

//MANAGER
//can see
//NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ
//can modify
//NOT_PUBLISHED, PUBLISH_REQ, PUBLISHED, DELETE_REQ, DELETED

//ADMIN


import {ADMIN, AUTHOR, MANAGER} from "./constants"

//SELECT CATALOG FILTER STATUS
let STATUS = (() => {
    let ret = {}
    ret[ADMIN] = [
        {value: ["NOT_PUBLISHED", "PUBLISH_REQ", "PUBLISHED", "DELETE_REQ"], text: "All"},
        {value: "NOT_PUBLISHED", text: "Draft"},
        {value: "PUBLISH_REQ", text: "Publication Request"},
        {value: "PUBLISHED", text: "Published"},
        {value: "DELETE_REQ", text: "Deletion Request"},
        {value: "ARCHIVE", text: "Archived"},
    ]
    ret[MANAGER] = [
        {value: ["NOT_PUBLISHED", "PUBLISH_REQ", "PUBLISHED", "DELETE_REQ"], text: "All"},
        {value: "NOT_PUBLISHED", text: "Draft"},
        {value: "PUBLISH_REQ", text: "Publication Request"},
        {value: "PUBLISHED", text: "Published"},
        {value: "DELETE_REQ", text: "Deletion Request"},
        {value: "ARCHIVE", text: "Archived"},
    ]
    ret[AUTHOR] = [
        {value: ["NOT_PUBLISHED", "PUBLISH_REQ", "PUBLISHED", "DELETE_REQ"], text: "All"},
        {value: "NOT_PUBLISHED", text: "Draft"},
        {value: "PUBLISH_REQ", text: "Publication Request"},
        {value: "PUBLISHED", text: "Published"},
        {value: "DELETE_REQ", text: "Deletion Request"},
        {value: "ARCHIVE", text: "Archived"},
    ]
    return ret
})()


export const getProfiledStatusSelectInfo = (role) => {
    return STATUS[role].map(statusEntry => {
        if (statusEntry.text === "All") {
            return {
                ...statusEntry,
                value: "-1"
            }
        }
        return statusEntry

    })
}

export const getProfiledStatusSelectAllValues = (role) => {
    let map = STATUS[role].filter(statusEntry => statusEntry.text === "All").flatMap(statusEntry => {
        return statusEntry.value
    })
    return map
}


export const getProfiledNewSelectStatusInfo = (higherRole) => {
    if (higherRole === AUTHOR) {
        return {
            values: [
                {value: "NOT_PUBLISHED", text: textFromStatus("NOT_PUBLISHED")},
                {value: "PUBLISH_REQ", text: textFromStatus("PUBLISH_REQ")},
            ]
        }
    }

    if (higherRole === MANAGER) {
        return {
            values: [
                {value: "NOT_PUBLISHED", text: textFromStatus("NOT_PUBLISHED")},
                {value: "PUBLISHED", text: textFromStatus("PUBLISHED")},
                {value: "PUBLISH_REQ", text: textFromStatus("PUBLISH_REQ")},
            ]
        }
    }

    if (higherRole === ADMIN) {
        return {
            values: [
                {value: "NOT_PUBLISHED", text: textFromStatus("NOT_PUBLISHED")},
                {value: "PUBLISHED", text: textFromStatus("PUBLISHED")},
                {value: "PUBLISH_REQ", text: textFromStatus("PUBLISH_REQ")},
            ]
        }
    }

}


export const getProfiledUpdateSelectStatusInfo = (higherRole, bundleGroupStatus) => {
    if (higherRole === AUTHOR) {
        if (bundleGroupStatus === "NOT_PUBLISHED") {
            return {
                disabled: false,
                values: [
                    {value: "NOT_PUBLISHED", text: textFromStatus("NOT_PUBLISHED")},
                    {value: "PUBLISH_REQ", text: textFromStatus("PUBLISH_REQ")},
                    {value: "DELETE_REQ", text: textFromStatus("DELETE_REQ")}
                ]
            }
        } else {
            return {
                disabled: true,
                values: [
                    {value: "NOT_PUBLISHED", text: textFromStatus("NOT_PUBLISHED")},
                    {value: "PUBLISH_REQ", text: textFromStatus("PUBLISH_REQ")},
                    {value: "PUBLISHED", text: textFromStatus("PUBLISHED")},
                    {value: "DELETE_REQ", text: textFromStatus("DELETE_REQ")},
                    {value: "DELETED", text: textFromStatus("DELETED")}
                ]
            }
        }
    }

    if (higherRole === MANAGER || higherRole === ADMIN) {
        return {
            disabled: false,
            values: [
                {value: "NOT_PUBLISHED", text: textFromStatus("NOT_PUBLISHED")},
                {value: "PUBLISH_REQ", text: textFromStatus("PUBLISH_REQ")},
                {value: "PUBLISHED", text: textFromStatus("PUBLISHED")},
                {value: "DELETE_REQ", text: textFromStatus("DELETE_REQ")}
            ]
        }
    }

    return {
        disabled: true,
        values: [
            {value: "NOT_PUBLISHED", text: textFromStatus("NOT_PUBLISHED")},
            {value: "PUBLISH_REQ", text: textFromStatus("PUBLISH_REQ")},
            {value: "PUBLISHED", text: textFromStatus("PUBLISHED")},
            {value: "DELETE_REQ", text: textFromStatus("DELETE_REQ")},
            {value: "DELETED", text: textFromStatus("DELETED")}
        ]
    }

}

export const getProfiledInsertSelectInfo = (role, bundleGroupStatus) => {

}

//    NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED
export const textFromStatus = (bundleGroupStatus) => {
    if (bundleGroupStatus === "NOT_PUBLISHED") return "Draft"
    if (bundleGroupStatus === "PUBLISH_REQ") return "Publication Request"
    if (bundleGroupStatus === "PUBLISHED") return "Published"
    if (bundleGroupStatus === "DELETE_REQ") return "Deletion Request"
    if (bundleGroupStatus === "DELETED") return "Deleted"
    if (bundleGroupStatus === "ARCHIVE") return "Archived"
}
