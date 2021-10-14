import {Content, Select, SelectItem, TextInput,} from "carbon-components-react"


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

const UpdateUser = ({userObj, organisations, onDataChange}) => {

    const changeUser = (field, value) => {
        const newObj = {
            ...userObj,
        }
        newObj[field] = value
        onDataChange(newObj)
    }

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
                <TextInput disabled={true} value={userObj.username} onChange={nameChangeHandler} id={"name"}
                           labelText={"Name"}/>
                <TextInput disabled={true} value={userObj.email} id={"email"}
                           labelText={"Email"}/>
                <Select value={userObj.organisation.organisationId} onChange={organisationChangeHandler}
                        id={"organisation"}
                        labelText={"Organisation"}>{selectItems_Organisations}</Select>
            </Content>
        </>
    )

}

export default UpdateUser
