import {useState} from "react"
import {Content, TextInput,} from "carbon-components-react"

/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}
 */

const NewOrganisation = ({onDataChange}) => {


    const [organisation, setOrganisation] = useState({
        name: "",
        description: ""
    })

    const changeOrganisation = (field, value) => {
        const newObj = {
            ...organisation,
        }
        newObj[field] = value
        setOrganisation(newObj)
        onDataChange(newObj)
    }



    const onChangeHandler = (e,fieldName) => {
        changeOrganisation(fieldName, e.target.value)
    }


    return (
        <>
            <Content>
                <TextInput id="name" labelText="Name" onChange={(e)=>onChangeHandler(e,"name")}/>
                <TextInput id="description" labelText="Description" onChange={(e)=>onChangeHandler(e,"description")}/>
            </Content>
        </>
    )

}
export default NewOrganisation
