import {useEffect, useState} from "react"
import {Content, Select, SelectItem, TextInput,} from "carbon-components-react"
import {getAllOrganisations} from "../../../../integration/Integration"


/*

Organisation:
{
    "name": "Entando inc.",
    "description": "Entando inc.",
    "bundleGroups": [],
    "organisationId": "1"
}
user
{
    "username": "germano",
    "email": "g.giudici@entando.com",
    "organisation": {
        "name": "Entando inc.",
        "description": "Entando inc.",
        "bundleGroups": [],
        "organisationId": "1"
    }
}
*/

const UpdateOrganisation = ({userObj, onDataChange}) => {


    const [user, setUser] = useState({
        username:"",
        email:"",
        organisation:{}
    })
    const [organisations, setOrganisations] = useState([])

    const changeUser = (field, value) => {
        const newObj = {
            ...user,
        }
        newObj[field] = value
        setUser(newObj)
        onDataChange(newObj)
    }

    useEffect(() => {
        (async () => {
            //TODO load the organisation from the db together with the userobj
            const organisations = (await getAllOrganisations()).organisationList
            setOrganisations(organisations)
            const userObjWithDefaultOrganisation = userObj.organisation ? userObj : {
                ...userObj,
                organisation: organisations[0]
            }
            console.log(userObjWithDefaultOrganisation)
            setUser(userObjWithDefaultOrganisation)
            onDataChange(userObjWithDefaultOrganisation) //put the initial object in the father state
        })()
    }, [onDataChange, userObj])

    const selectItems_Organisations = organisations.map((organisation) => {
        return (
            <SelectItem
                key={organisation.organisationId}
                value={organisation.organisationId}
                text={organisation.name}
            />
        )
    })


    const nameChangeHandler = (e) => {
        changeUser("username", e.target.value)
    }

    const organisationChangeHandler = (e) => {
        const selectedOrganisation = organisations.filter(o => o.organisationId === e.target.value)[0]
        changeUser("organisation", selectedOrganisation)
    }


    return (
        <>
            <Content>
                <TextInput disabled={true} value={user.username} onChange={nameChangeHandler} id={"name"}
                           labelText={"Name"}/>
                <TextInput disabled={true} value={user.email} id={"email"}
                           labelText={"Email"}/>
{/*
*/}
                <Select value={user.organisation.organisationId} onChange={organisationChangeHandler}
                        id={"organisation"}
                        labelText={"Organisation"}>{selectItems_Organisations}</Select>
            </Content>
        </>
    )

}

export default UpdateOrganisation
