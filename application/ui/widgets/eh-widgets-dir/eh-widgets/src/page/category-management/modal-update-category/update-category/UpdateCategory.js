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

const UpdateCategory = ({categoryObj, onDataChange}) => {

    const changeCategory = (field, value) => {
        const newObj = {
            ...categoryObj,
        }
        newObj[field] = value
        onDataChange(newObj)
    }



    const onChangeHandler = (e,fieldName) => {
        changeCategory(fieldName, e.target.value)
    }


    return (
        <>
            <Content>
                <TextInput id="name" value={categoryObj.name} labelText="Name" onChange={(e)=>onChangeHandler(e,"name")}/>
                <TextInput id="description" value={categoryObj.description} labelText="Description" onChange={(e)=>onChangeHandler(e,"description")}/>
            </Content>
        </>
    )

}


export default UpdateCategory
