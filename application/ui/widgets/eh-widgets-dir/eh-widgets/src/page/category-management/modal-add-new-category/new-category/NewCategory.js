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

const NewCategory = ({onDataChange}) => {


    const [category, setCategory] = useState({
        name: "",
        description: ""
    })

    const changeCategory = (field, value) => {
        const newObj = {
            ...category,
        }
        newObj[field] = value
        setCategory(newObj)
        onDataChange(newObj)
    }


    const onChangeHandler = (e,fieldName) => {
        changeCategory(fieldName, e.target.value)
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
export default NewCategory
