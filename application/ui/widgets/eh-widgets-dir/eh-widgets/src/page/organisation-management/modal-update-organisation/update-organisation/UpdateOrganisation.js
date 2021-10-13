import {Content, TextInput,} from "carbon-components-react"


/*

Organisation:
{
    "name": "Entando inc.",
    "description": "Entando inc.",
    "bundleGroups": [],
    "organisationId": "1"
}

*/

const UpdateOrganisation = ({organisationObj, onDataChange}) => {

    const changeOrganisation = (field, value) => {
        const newObj = {
            ...organisationObj,
        }
        newObj[field] = value
        onDataChange(newObj)
    }



    const onChangeHandler = (e,fieldName) => {
        changeOrganisation(fieldName, e.target.value)
    }


    return (
        <>
            <Content>
                <TextInput id="name" value={organisationObj.name} labelText="Name" onChange={(e)=>onChangeHandler(e,"name")}/>
                <TextInput id="description" value={organisationObj.description} labelText="Description" onChange={(e)=>onChangeHandler(e,"description")}/>
            </Content>
        </>
    )

}


export default UpdateOrganisation
