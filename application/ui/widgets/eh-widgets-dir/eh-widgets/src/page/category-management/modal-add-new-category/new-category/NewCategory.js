import { useState } from "react"
import { Content, TextInput } from "carbon-components-react"
import { categorySchema } from "../../../../helpers/validation/categorySchema"
import { CHAR_LENGTH, LEAST_CHAR_NAME_MSG, NAME_REQ_MSG } from "../../../../helpers/constants"

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

const NewCategory = ({ onDataChange, validationResult }) => {
  const [category, setCategory] = useState({
    name: "",
    description: "",
  })
  const [validationErrorMsg,setValidationErrorMsg] = useState({message: NAME_REQ_MSG});

  const changeCategory = (field, value) => {
    const newObj = {
      ...category,
    }
    newObj[field] = value
    setCategory(newObj)
    onDataChange(newObj)
  }

  const onChangeHandler = (e, fieldName) => {
    if (fieldName === "name") {
      if (e.target.value.length === 0) {
        setValidationErrorMsg({
          message: NAME_REQ_MSG
        })
      } else if (e.target.value.length < CHAR_LENGTH) {
        setValidationErrorMsg({
          message: LEAST_CHAR_NAME_MSG
        })
      } else {
        setValidationErrorMsg({
          message: ""
        })
      }
    }
    changeCategory(fieldName, e.target.value)
  }

  return (
    <>
      <Content>
        <TextInput
          invalid={!!validationResult["name"] && !!validationErrorMsg.message}
          invalidText={
            validationErrorMsg.message
          }
          id="name"
          labelText={`Name ${categorySchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
        />
        <TextInput
          invalid={!!validationResult["description"]}
          invalidText={
            validationResult["description"] &&
            validationResult["description"].join("; ")
          }
          id="description"
          labelText={`Description ${categorySchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
        />
      </Content>
    </>
  )
}
export default NewCategory
